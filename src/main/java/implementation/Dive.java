package implementation;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

import Const.Constant;
import jakarta.annotation.PostConstruct;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Dive {

	Logger log = LoggerFactory.getLogger(Dive.class);

	@Autowired
	private Eng eng;

	private I2C deviceGyro;
	private I2C deviceDepth;
	private I2C devicePCA9685;
	private Context pi4j = I2CSingle.pi4j;
	private static WatchDog watchDogThread = null;
	public AtomicBoolean startTimer = new AtomicBoolean(false);

	private static boolean disabled = true;
	private static int offsetPitch = 0;
	// Constants for PWM range mapping
	private static final int PCA9685_MODE1 = 0x00;
	private static final int PRE_SCALE = 0xFE;
	private static final int LED0_ON_L = 0x06;
	private static final int LED0_ON_H = 0x07;
	private static final int LED0_OFF_L = 0x08;
	private static final int LED0_OFF_H = 0x09;
	private static final int  MIN_PULSE_WIDTH = 500;//us
	private static final int  MAX_PULSE_WIDTH = 6000;//us
	private static final double TICK_PER_MICRO= 50.0 * 4096.0  / ( 1000000.0);
	private static final int PWM_MIN = (int) Math.round(TICK_PER_MICRO * MIN_PULSE_WIDTH); // Minimum PWM value for 0 degrees was 150
	private static final int PWM_MAX = (int) Math.round(TICK_PER_MICRO * MAX_PULSE_WIDTH); // Maximum PWM value for 180 degrees
	public static final Integer EMERGENCY_PUMP_POWER = 100;
	// --- MS5837 (Blue Robotics Bar02) constants ---
	private static final int MS5837_ADDR = 0x76;           // change to 0x77 if i2cdetect shows that
	private static final int CMD_RESET   = 0x1E;
	private static final int CMD_ADC_READ = 0x00;
	private static final int PROM_BASE   = 0xA0;           // PROM words 0..7 at A0..AE
	private static final int CMD_CONVERT_D1 = 0x40;
	private static final int CMD_CONVERT_D2 = 0x50;
	private static final int OSR_8192       = 0x0A;

	// OSR=8192 conversion commands (explicit bytes are clearest)
	private static final int CMD_CONV_D1_OSR8192 = 0x4A;   // pressure
	private static final int CMD_CONV_D2_OSR8192 = 0x5A;   // temperature
	private static final int OSR_8192_DELAY_MS   = 20;

	private static final double G = 9.80665;
	private static final double DEFAULT_FLUID_DENSITY = 997.0; // fresh water; ~1025 for sea

	private final int[] prom = new int[8];                 // prom[1]..prom[6] => C1..C6
	private double p0Mbar = Double.NaN;                    // baseline pressure (tare)
	private double fluidDensity = DEFAULT_FLUID_DENSITY;
	private volatile boolean depthReady = false;
	private I2CProvider i2c=I2CSingle.i2c;
	private static final Object I2C_LOCK = new Object();

	private static final class PressureTemp {
		final double pressureMbar;
		final double temperatureC;

		private PressureTemp(double pressureMbar, double temperatureC) {
			this.pressureMbar = pressureMbar;
			this.temperatureC = temperatureC;
		}
	}
	@Autowired
	public Dive(Context pi4j, I2CProvider i2c) {
		this.pi4j = java.util.Objects.requireNonNull(pi4j, "pi4j context is null");
		this.i2c  = java.util.Objects.requireNonNull(i2c,  "i2c provider is null");
		watchDogThread = new WatchDog();
		synchronized (I2C_LOCK) {
			try {
				log.info("Starting Dive method.");
				pi4j = Pi4J.newAutoContext();
				log.debug("Pi4J context initialized.");
				i2c = pi4j.provider("linuxfs-i2c");
				log.debug("I2C provider obtained.");

				log.info("Starting Gyro method.");

				I2CConfig configGyro = I2C.newConfigBuilder(pi4j)
						.id("LSM6DSO32-Gyro")
						.name("LSM6DSO32 Gyroscope")
						.bus(1)
						.device(0x6A)  // Adjust if using a different I2C address
						.build();

				deviceGyro = i2c.create(configGyro);
				// Gyroscope initialization
				deviceGyro.writeRegister(0x10, (byte) 0xA2); // CTRL2_A: 6.66kHz, 4g, gyro full-scale
				deviceGyro.writeRegister(0x11, (byte) 0xA2); // CTRL2_G: 6.66kHz, 2000 dps, gyro full-scale
				Thread.sleep(100); // Wait for gyro settings to take effect
			} catch (Exception e) {
				log.error("Error initializing I2C devices Gyro", e);
			}

			try {
				log.info("Starting Servos method. PWM_MIN, PWM_MAX:" +PWM_MIN + ", " +PWM_MAX);
				// Assuming you've already initialized 'pi4j' and 'i2CProvider' like you did for the other devices
				// Here, we're setting up the I2C configuration for the PCA9685
				I2CConfig configPCA9685 = I2C.newConfigBuilder(pi4j)
						.id("PCA9685")
						.name("PCA9685 PWM Controller")
						.bus(1)  // This is the I2C bus. The Raspberry Pi has multiple I2C buses (0, 1), check which one you're using.
						.device(0x40)  // This is the default I2C address for the PCA9685, change if you've set a different address.
						.build();

				// Create the I2C device for the PCA9685 using the configuration
				devicePCA9685= i2c.create(configPCA9685);
				devicePCA9685.writeRegister(PCA9685_MODE1, 0x81);
				Thread.sleep(50);
				setPWMFreq(50.0); // 50Hz for servos
				Thread.sleep(40);

			} catch (Exception e) {
				log.error("Error initializing I2C devices Servo", e);
			}
		}
	}
	@PostConstruct
	public void init() {
		this.pi4j = java.util.Objects.requireNonNull(pi4j, "pi4j context is null");
		this.i2c  = java.util.Objects.requireNonNull(i2c,  "i2c provider is null");
		synchronized (I2C_LOCK) {
			// init other devices...
			boolean FAIL = true;
			int count = 0;
			while (FAIL && count < 5) {
				initMs5837();            // try to bring up depth

				if (depthReady) {
					zeroPressureBaseline(); // now it’s safe to zero
					FAIL = false;
				} else {
					log.warn("MS5837 not ready; skipping zero until device comes up.");
					try {
						Thread.sleep(100);
						count++;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	private static final int MS5837_RESET     = 0x1E;
	private static final int PROM_WORD_COUNT  = 7;

	private void initMs5837() throws IOException {
		log.info("Initialising MS5837 at 0x76");

		// Lazily create the I2C device if needed
		if (deviceDepth == null) {
			I2CConfig depthConfig = I2C.newConfigBuilder(pi4j)
					.id("ms5837")
					.name("MS5837 Depth Sensor")
					.bus(1)
					.device(MS5837_ADDR) // 0x76
					.build();

			deviceDepth = i2c.create(depthConfig);
			log.info("MS5837 I2C device created on bus 1, addr 0x{}",
					Integer.toHexString(MS5837_ADDR));
		}

		try {
			// 1) Send reset command
			try {
				deviceDepth.write(new byte[] { (byte) MS5837_RESET });
				log.info("MS5837 reset command sent (0x1E)");
			} catch (Exception e) {
				log.error("Failed to send MS5837 reset command", e);
				throw new IOException("MS5837 reset failed", e);
			}

			// ... rest of your PROM read loop + CRC as you already have ...
			// Give the sensor time to reset (datasheet says ~2.8ms; we’ll be generous)
			try {
				Thread.sleep(5);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}

			// 2) Read PROM words 0..7 using explicit write(cmd) + read(2),
			//    to mirror: i2cget -y 1 0x76 0xA0 w  etc.
			byte[] two = new byte[2];
			log.info("Reading MS5837 PROM words with write+read sequence");

			for (int i = 0; i < PROM_WORD_COUNT; i++) {
				int cmd = PROM_BASE + (i * 2);  // 0xA0, 0xA2, ..., 0xAE

				for (int attempt = 0; attempt < 3; attempt++) {
					try {
						log.debug("PROM read {}: sending command 0x{}", i, Integer.toHexString(cmd));

						// Set PROM address
						deviceDepth.write(new byte[] { (byte) cmd });

						// Tiny delay to let chip prepare data (a couple of ms is plenty)
						try {
							Thread.sleep(2);
						} catch (InterruptedException ie) {
							Thread.currentThread().interrupt();
						}

						// Now read 2 bytes (16-bit word), like `i2cget ... w`
						int readBytes = deviceDepth.read(two, 0, 2);
						if (readBytes != 2) {
							throw new IOException("Expected 2 bytes from PROM, got " + readBytes);
						}

						int value = ((two[0] & 0xFF) << 8) | (two[1] & 0xFF);
						prom[i] = value;

						log.info("PROM[{}] = 0x{} ({}), raw bytes [{}, {}]",
								i,
								Integer.toHexString(value & 0xFFFF),
								value,
								two[0] & 0xFF,
								two[1] & 0xFF);

						// Success, move to next word
						break;

					} catch (Exception e) {
						if (attempt >= 2) {
							log.error("PROM read {} failed after {} attempts", i, attempt + 1, e);
							throw new IOException("Failed to read MS5837 PROM word " + i, e);
						} else {
							log.warn("Retrying PROM read {}, attempt {}/3", i, attempt + 1, e);
							try {
								Thread.sleep(5);
							} catch (InterruptedException ie) {
								Thread.currentThread().interrupt();
							}
						}
					}
				}
			}

			// (Optional) very basic sanity check
			if (prom[1] == 0 && prom[2] == 0) {
				log.warn("MS5837 PROM words look suspiciously zero-ish: {}", Arrays.toString(prom));
			} else {
				log.info("MS5837 PROM read OK: {}", Arrays.toString(prom));
				depthReady = true;
				return;
			}

		} catch (IOException e) {
			log.error("MS5837 init failed", e);
			throw e;
		}
	}

	private void requireDepth() {
		if (!depthReady || deviceDepth == null) {
			throw new IllegalStateException("MS5837 not initialized");
		}
	}

	private PressureTemp readPressureTemp() throws Exception {
		// --- Read D1 (pressure raw) ---
		long D1 = readADC(CMD_CONVERT_D1);

		// --- Read D2 (temperature raw) ---
		long D2 = readADC(CMD_CONVERT_D2);

		// --- Calibration coefficients C1..C6 ---
		long C1 = prom[1], C2 = prom[2], C3 = prom[3],
				C4 = prom[4], C5 = prom[5], C6 = prom[6];

		// --- First-order compensation ---
		long dT   = D2 - (C5 << 8);
		long TEMP = 2000L + (dT * C6) / 8_388_608L;     // 0.01 °C

		long OFF  = (C2 * 65_536L) + ((C4 * dT) / 128L);
		long SENS = (C1 * 32_768L) + ((C3 * dT) / 256L);

		// --- Second-order compensation ---
		long T2 = 0, OFF2 = 0, SENS2 = 0;

		if (TEMP < 2000L) {
			long t = TEMP - 2000L;
			long t2 = t * t;

			T2    = (3L * dT * dT) / 8_589_934_592L;    // 2^33
			OFF2  = (3L * t2) / 2L;
			SENS2 = (5L * t2) / 8L;

			if (TEMP < -1500L) {
				long tt = TEMP + 1500L;
				long tt2 = tt * tt;
				OFF2  += 7L * tt2;
				SENS2 += 4L * tt2;
			}
		} else {
			T2 = (2L * dT * dT) / 137_438_953_472L;     // 2^37
			long t = TEMP - 2000L;
			OFF2 = (t * t) / 16L;
			// SENS2 stays 0
		}

		TEMP -= T2;
		OFF  -= OFF2;
		SENS -= SENS2;

		// --- Pressure (MS5837): result is 0.01 mbar ---
		long P01mbar = ((((D1 * SENS) / 2_097_152L) - OFF) / 32_768L);

		double pressureMbar = P01mbar / 100.0;
		double temperatureC = TEMP / 100.0;

		return new PressureTemp(pressureMbar, temperatureC);
	}

	private void setPWMFreq(double freq) throws Exception {
		int prescale = calculatePrescale(freq);
		byte oldmode = (byte) devicePCA9685.readRegister(PCA9685_MODE1); // Read MODE1 register
		byte newmode = (byte) ((oldmode & 0x7F) | 0x10); // sleep
		//	    devicePCA9685.writeRegister(PCA9685_MODE1, newmode); // go to sleep
		devicePCA9685.writeRegister(PRE_SCALE, (byte) prescale); // set the prescaler
		//	    devicePCA9685.writeRegister(PCA9685_MODE1, oldmode);
		//	    Thread.sleep(5);
		//	    devicePCA9685.writeRegister(PCA9685_MODE1, (byte) (oldmode | 0x80)); //  This sets the RESTART bit to wake up the PCA9685
	}
	private int calculatePrescale(double freq) {
		double prescaleval = 25000000.0; // 25,000,000 Hz
		prescaleval /= 4096.0;           // 12-bit
		prescaleval /= freq;
		return (int) Math.round(prescaleval) - 1;
	}

	public Integer getDiveAngle() {
		try {
			byte[] gyroData = new byte[6];
			byte[] accelData = new byte[6];
			long sumGyroX = 0;
			long sumGyroZ = 0;
			for(int i = 0; i < 32; i++) {
				deviceGyro.readRegister(0x22, gyroData, 0, 6); // OUTX_L_G register address
				deviceGyro.readRegister(0x28, accelData, 0, 6); // OUTX_L_A register address
				sumGyroX += (short) ((gyroData[0] & 0xFF) | (gyroData[1] << 8)) + 
						(short) ((accelData[0] & 0xFF) | (accelData[1] << 8));
				sumGyroZ += (short) ((gyroData[4] & 0xFF) | (gyroData[5] << 8))+ 
						(short) ((accelData[4] & 0xFF) | (accelData[5] << 8));
			}
			Double pitch = Math.atan2(-sumGyroZ/32.0, sumGyroX/32.0) * (180 / Math.PI);
			log.debug("Pitch: " + pitch);
			return pitch.intValue() - offsetPitch;
		} catch (Exception e) {
			log.error("Error reading gyroscope data", e);
		}
		return Constant.ERROR;
	}
	private void zeroPressureBaseline(int samples) throws Exception {
		double sum = 0.0;
		int good = 0;

		for (int i = 0; i < samples; i++) {
			PressureTemp r = readPressureTemp();
			sum += r.pressureMbar;
			good++;
			Thread.sleep(25);
		}

		if (good > 0) {
			p0Mbar = sum / good;
			log.info(String.format("MS5837 baseline set: P0=%.2f mbar (%d samples)", p0Mbar, good));
		} else {
			throw new IllegalStateException("No valid MS5837 samples for baseline");
		}
	}

	// Watch Dog thread class
	private class WatchDog extends Thread {

		@Override
		public void run() {
			while (true) {
				if (startTimer.get()) {
					try {
						// Sleep for 10seconds
						WatchDog.sleep(10000);
						emergencySurface();

					} catch (InterruptedException e) {
						log.debug("InterruptedException true");
						continue;
					}

				} else {
					try {
						WatchDog.sleep(500);
					} catch (InterruptedException e) {
						log.debug("InterruptedException false");
						continue;
					}
				}
			}
		}
	}
	// Method to trigger emergency surface event
	private void emergencySurface() {
		log.error("emergencySurface");
		setFillTank(-EMERGENCY_PUMP_POWER);
	}
	// Method to convert servo angle to PWM value
	private int angleToPWM(int angle) {
		return (int)Math.round(PWM_MIN + (angle * (PWM_MAX - PWM_MIN)) / 180.0);
	}

	// Set the angle for the front servo
	public Integer setFrontAngle(int angle) {
		angle += 90;
		int pwm = angleToPWM(angle);
		// Assuming channel 0 for the front servo
		if (setPWM(0, pwm) != Constant.ERROR) {
			log.debug("setFrontAngle: " + angle);
			return angle;
		} else return Constant.ERROR;
	}

	// Set the angle for the back servo
	public Integer setBackAngle(int angle) {
		angle += 90;
		int pwm = angleToPWM(angle);
		// Assuming channel 1 for the back servo
		if (setPWM(1, pwm) != Constant.ERROR) {
			log.debug("setBackAngle: " + angle);
			return angle;
		} else return Constant.ERROR;
	}

	// Assuming you have a method like this to send PWM signals
	private int setPWM(int channel, int pwm) {
		try {
			int onCount = (int) 0;
			int offCount = (int)  pwm;
			if (offCount >= 4096) {
				offCount -= 4096; // Adjust for next frame if necessary
			}

			byte onLow = (byte)(onCount & 0xFF);
			byte onHigh = (byte)((onCount >> 8) & 0xFF);
			byte offLow = (byte)(offCount & 0xFF);
			byte offHigh = (byte)((offCount >> 8) & 0xFF);

			log.debug("onCount = " + onCount + " offCount = " + offCount);
			int offset = 4 * channel;
			devicePCA9685.writeRegister(LED0_ON_L + offset, onLow);
			devicePCA9685.writeRegister(LED0_ON_H + offset, onHigh);
			devicePCA9685.writeRegister(LED0_OFF_L + offset, offLow);
			devicePCA9685.writeRegister(LED0_OFF_H + offset, offHigh);
			return 0;
		} catch (Exception e) {
			log.error("Error setting Servo = " + channel + " duty cycle = " + pwm);
			return Constant.ERROR;
		}
	}

	public Integer setFillTank(Integer percent) {
		log.debug("setFillTank:"+percent);
		int pwm = (int) (4095*Eng.setDuty(percent)/100);
		if (setPWM(3, pwm) != Constant.ERROR)
			return percent;
		else
			return Constant.ERROR;
	}


	public synchronized Integer getDepth() {
		requireDepth();
		int attempts = 0;

		while (attempts++ < 5) {
			try {
				// Kick watchdog exactly as you already do
				if (!disabled) {
					if (watchDogThread != null) {
						startTimer.set(true);
						watchDogThread.interrupt();
					}
				} else {
					if (watchDogThread != null) {
						startTimer.set(false);
						watchDogThread.interrupt();
					}
				}

				PressureTemp r = readPressureTemp();

				if (Double.isNaN(p0Mbar)) {
					p0Mbar = r.pressureMbar; // first-ever baseline
				}

				double depthM = ((r.pressureMbar - p0Mbar) * 100.0) / (fluidDensity * G);
				int depthMm = (int) Math.round(depthM * 1000.0);

				log.debug(String.format("P=%.2f mbar T=%.2f C depth=%d mm P0=%.2f rho=%.1f",
						r.pressureMbar, r.temperatureC, depthMm, p0Mbar, fluidDensity));

				return -depthMm; // your convention: down negative

			} catch (Exception e) {
				log.warn("getDepth attempt " + attempts + " failed", e);
				try { Thread.sleep(50); } catch (InterruptedException ie) { /* ignore */ }
			}
		}

		return Constant.ERROR;
	}
	public Integer zeroPressureBaseline() {
		try {
			// Make sure the MS5837 device is up before trying to zero it
			requireDepth();

			// Re-use the detailed baseline logic below
			// (12 samples matches zeroOffsets(); bump if you want smoother baseline)
			zeroPressureBaseline(12);

			return 0;
		} catch (Exception e) {
			log.error("zeroPressureBaseline failed", e);
			return Constant.ERROR;
		}
	}

	private static final int OSR_4096 = 0x08;   // instead of 0x0A

	private long readADC(int cmd) throws Exception {
		requireDepth();

		final int maxAttempts = 3;

		for (int attempt = 1; attempt <= maxAttempts; attempt++) {
			try {
				// 1. Start conversion
				synchronized (I2C_LOCK) {
					// Send conversion command (e.g. 0x48 or 0x58)
					deviceDepth.write(new byte[] {(byte)  cmd});
				}

				// 2. Wait for conversion to complete
				// For OSR=4096 the datasheet says ~9 ms; be generous
				Thread.sleep(20);

				// 3. Issue ADC read command, then read 3 bytes
				byte[] b = new byte[3];

				synchronized (I2C_LOCK) {
					// Send "ADC read" command 0x00
					deviceDepth.write(new byte[] {(byte) CMD_ADC_READ});

					int read = deviceDepth.read(b, 0, 3);
					if (read != 3) {
						throw new IOException("Expected 3 bytes from ADC, got " + read);
					}
				}

				long value = ((b[0] & 0xFFL) << 16)
						| ((b[1] & 0xFFL) << 8)
						|  (b[2] & 0xFFL);

				if (log.isDebugEnabled()) {
					log.debug("MS5837 ADC cmd=0x{} raw={}, bytes={}",
							String.format("%02X", cmd),
							value,
							Arrays.toString(b));
				}

				return value;

			} catch (Exception e) {
				if (attempt == maxAttempts) {
					log.error("readADC cmd=0x{} failed after {} attempts",
							String.format("%02X", cmd), maxAttempts, e);
					throw e;
				} else {
					log.warn("readADC cmd=0x{} attempt {}/{} failed: {}",
							String.format("%02X", cmd),
							attempt,
							maxAttempts,
							e.toString());
					Thread.sleep(10);
				}
			}
		}

		// Should never get here
		throw new IOException("readADC unreachable");
	}


	public Integer zeroOffsets() {
		try {
			offsetPitch = getDiveAngle();
			if (offsetPitch == Constant.ERROR) offsetPitch = 0;
			log.info("Zeroed offsets: pitch0=" + offsetPitch);
			return 0;
		} catch (Exception e) {
			log.error("zeroOffsets failed", e);
			return Constant.ERROR;
		}
	}

	public Integer setRudder(Integer angle) {
		angle += 90;
		int pwm = angleToPWM(angle);
		// Assuming channel 1 for the back servo
		setPWM(2, pwm);
		if (setPWM(2, pwm) != Constant.ERROR) {
			log.debug("setRudder: " + angle);
			return angle;
		} else
			return Constant.ERROR;

	}

}
