package implementation;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.config.ConfigurableBeanFactory;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Component;
//
//import com.pi4j.Pi4J;
//import com.pi4j.context.Context;
//import com.pi4j.io.exception.IOException;
//import com.pi4j.io.i2c.I2C;
//import com.pi4j.io.i2c.I2CConfig;
//import com.pi4j.io.i2c.I2CProvider;
//
//import jakarta.annotation.PostConstruct;
//
//
//@Component
//@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
//public class Dive {
//
//	Logger log = LoggerFactory.getLogger(Dive.class);
//
//	private WatchDog watchDogThread;
//	private static boolean firstTime = true;
//	private I2C deviceAccl;
//	private Context pi4j;
//	private static final int BUFFER_SIZE = 30;
//	private short[] xBuffer = new short[BUFFER_SIZE];
//	private short[] yBuffer = new short[BUFFER_SIZE];
//	private short[] zBuffer = new short[BUFFER_SIZE];
//	private int bufferIndex = 0;
//	private static final int X_ID = 0;
//	private static final int Y_ID = 1;
//	private static final int Z_ID = 2;
//	private static final int MSB = 1;
//	private static final int LSB = 0;
//	
//	@PostConstruct
//	public void init() {
//		try {
//			// Initialize Pi4J with auto context
//			pi4j = Pi4J.newAutoContext();
//			I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
//
//			// Create I2C config for accelerometer (LSM303DLHC)
//			I2CConfig configAccl = I2C.newConfigBuilder(pi4j)
//					.id("LSM303AGR-Accl")
//					.name("LSM303AGR Accelerometer")
//					.bus(1)
//					.device(0x19)
//					.build();
//
//
//			// Get I2C provider and create I2C instances
//			deviceAccl = i2CProvider.create(configAccl);
//
//			// Initialize accelerometer
//			deviceAccl.writeRegister(0x24, (byte) 0x80); //reset no fifo
//			deviceAccl.writeRegister(0x21, (byte) 0x00); 
//			deviceAccl.writeRegister(0x22, (byte) 0x00); 
//			deviceAccl.writeRegister(0x23, (byte) 0x00); //continuous normal SPI disabled
//			deviceAccl.writeRegister(0x20, (byte) 0x67); //normal power 200Hz xyz enabled
//
//
//			Thread.sleep(500); // Wait for settings to take effect
//		} catch (Exception e) {
//			log.error("Error initializing I2C devices", e);
//		}
//	}
//

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

//
//	public Integer getDiveAngle() {
//		log.info("getDiveAngle");
//		short xAccl = 0;
//		short yAccl = 0;
//		short zAccl = 0;
//		int count = 0;
//		while (xAccl == 0 && yAccl == 0 && zAccl == 0 && count++ < 20) {
//			try {
//				byte[] ready = new byte[1];
//				deviceAccl.readRegister(0x27, ready, 0, 1);
//				log.info("ready = " + ready[0]);
//				if ((ready[0] & 7) > 0) {
//					byte[] acclDataX = new byte[2];
//					byte[] acclDataY = new byte[2];
//					byte[] acclDataZ = new byte[2];
//					Set<String> set = new HashSet<String>();
//					while(true) {
//						if ((ready[0] & 1) == 1) {
//							deviceAccl.readRegister(0x28, acclDataX, 0, 2);
//							set.add("X");
//						} 
//						if ((ready[0] & 2) == 2) {
//							deviceAccl.readRegister(0x2A, acclDataY, 0, 2);
//							set.add("Y");
//						}
//						if ((ready[0] & 4) == 4) {
//							deviceAccl.readRegister(0x2C, acclDataZ, 0, 2);
//							set.add("Z");
//						}
//						if (set.size() == 3) {
//							break;
//						}
//						try {
//							Thread.sleep(10);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						ready = new byte[1];
//						deviceAccl.readRegister(0x27, ready, 0, 1);
//
//					}
//
//					xAccl = (short) (((acclDataX[MSB] & 0xFF) << 8) | (acclDataX[LSB] & 0xFF));
//					yAccl = (short) (((acclDataY[MSB] & 0xFF) << 8) | (acclDataY[LSB] & 0xFF));
//					zAccl = (short) (((acclDataZ[MSB] & 0xFF) << 8) | (acclDataZ[LSB] & 0xFF));
//					// Read and process each axis data
//					updateBuffer(xBuffer, xAccl);
//					updateBuffer(yBuffer, yAccl);
//					updateBuffer(zBuffer, zAccl);
//					bufferIndex++;
//					if (bufferIndex == BUFFER_SIZE) {
//						bufferIndex = 0;
//					}
//					// Calculate averages
//					short[] average = calculateAverage(xBuffer, yBuffer, zBuffer);
//
//					log.info("Average: x = " + average[X_ID] + " y = " + average[Y_ID] + " z = " + average[Z_ID]);
//
//					// Calculate dive angle using averages
//					double diveAngle = Math.atan2(-average[Z_ID], -average[X_ID]) * (180 / Math.PI);
//
//
//					// Calculate dive angle
//					return (int) diveAngle;
//
//				} else {
//					try {
//						Thread.sleep(10);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					continue;
//				}
//			} catch (IOException e) {
//				log.error("Error reading accelerometer data", e);
//				throw new RuntimeException("Error reading accelerometer data", e);
//			}
//		}
//		return 0;
//	}
//	private void updateBuffer(short[] buffer, short data) {
//		short value = data;
//		buffer[bufferIndex] = value;
//	}
//
//	private short[] calculateAverage(short[] bufferX,short[] bufferY,short[] bufferZ) {
//		int sumX = 0;
//		int sumY = 0;
//		int sumZ = 0;
//		int count = 0;
//		for (int i = 0; i < bufferX.length; i++) {
//			if (bufferX[i] != 0 || bufferY[i] != 0 || bufferZ[i] != 0) {
//				sumX += bufferX[i];
//				sumY += bufferY[i];
//				sumZ += bufferZ[i];
//				count++;
//			}
//		}
//		if (count != 0) {
//			short[] result = {(short)(sumX/count), (short)(sumY/count), (short)(sumZ/count)};
//			return result;
//		}
//		short[] result = {(short)(1), (short)(1), (short)(1)};
//		return result;
//
//	}

//}

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

import Const.Constant;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Dive {

	Logger log = LoggerFactory.getLogger(Dive.class);

	@Autowired
	private Eng eng;
	
	private I2C deviceGyro;
	private I2C deviceDepth;
	private I2C devicePCA9685;
	private Context pi4j;
	private static WatchDog watchDogThread = null;
	private int[] calibrationCoefficients = new int[6];

	public AtomicBoolean startTimer = new AtomicBoolean(false);

	private static boolean disabled = true;
	private static int offsetDepth = 0;
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
	// --- MS5837 (Bar02) constants ---
	private static final int MS5837_ADDR = 0x76;
	private static final int CMD_RESET = 0x1E;
	private static final int CMD_ADC_READ = 0x00;
	private static final int CMD_CONVERT_D1 = 0x40; // Pressure base
	private static final int CMD_CONVERT_D2 = 0x50; // Temperature base
	private static final int OSR_8192 = 0x0A;       // Highest OSR for best precision
	private static final int PROM_BASE = 0xA0;      // PROM word 0..7 at A0..AE

	private int[] prom = new int[8];                // PROM words (prom[1]..prom[6] => C1..C6)
	private double p0Mbar = Double.NaN;             // session baseline pressure (zero at surface)
	private static final double G = 9.80665;        // m/s^2

	// Choose your fluid density. Fresh water ~ 997 kg/m^3 at ~25°C, seawater ~1025 kg/m^3.
	// You can refine with temperature if you want; for now pick one:
	private double fluidDensity = 997.0;           // kg/m^3 (set 997.0 for fresh 1025 for salt water)
	public Dive() {
		I2CProvider i2CProvider = null;
		try {
			log.info("Starting Dive method.");
			pi4j = Pi4J.newAutoContext();
			log.debug("Pi4J context initialized.");
			i2CProvider = pi4j.provider("linuxfs-i2c");
			log.debug("I2C provider obtained.");

			log.info("Starting Gyro method.");

			I2CConfig configGyro = I2C.newConfigBuilder(pi4j)
					.id("LSM6DSO32-Gyro")
					.name("LSM6DSO32 Gyroscope")
					.bus(1)
					.device(0x6A)  // Adjust if using a different I2C address
					.build();

			deviceGyro = i2CProvider.create(configGyro);
			// Gyroscope initialization
			deviceGyro.writeRegister(0x10, (byte) 0xA2); // CTRL2_A: 6.66kHz, 4g, gyro full-scale
			deviceGyro.writeRegister(0x11, (byte) 0xA2); // CTRL2_G: 6.66kHz, 2000 dps, gyro full-scale
			Thread.sleep(100); // Wait for gyro settings to take effect
		} catch (Exception e) {
			log.error("Error initializing I2C devices Gyro", e);
		}
		try {
		    log.info("Starting Depth (MS5837) init...");
		    I2CConfig configDepth = I2C.newConfigBuilder(pi4j)
		            .id("MS5837")
		            .name("MS5837 Depth Sensor")
		            .bus(1)
		            .device(MS5837_ADDR)
		            .build();
		    deviceDepth = i2CProvider.create(configDepth);

		    // RESET: send single command byte (no data)
		    deviceDepth.write((byte) CMD_RESET);
		    Thread.sleep(5); // datasheet: ~2.8ms; 5ms is safe

		    // Read PROM words 0..7 (A0..AE). C1..C6 are words 1..6.
		    for (int i = 0; i < 8; i++) {
		        byte[] two = new byte[2];
		        deviceDepth.readRegister(PROM_BASE + (i * 2), two, 0, 2);
		        prom[i] = ((two[0] & 0xFF) << 8) | (two[1] & 0xFF);
		        log.debug("PROM[" + i + "] = " + prom[i]);
		    }
		    // (Optional) CRC check on PROM here if you want extra robustness.
		} catch (Exception e) {
		    log.error("Error initializing MS5837", e);
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
			devicePCA9685 = i2CProvider.create(configPCA9685);
			devicePCA9685.writeRegister(PCA9685_MODE1, 0x81);
			Thread.sleep(50);
			setPWMFreq(50.0); // 50Hz for servos
			Thread.sleep(40);

		} catch (Exception e) {
			log.error("Error initializing I2C devices Servo", e);
		}
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
		int pwm = 4095*Math.abs(percent)/100;
		eng.setDirection(percent > 0 ? true :false);
		if (setPWM(3, pwm) != Constant.ERROR)
			return percent;
		else
			return Constant.ERROR;
	}


	public Integer getDepth() {
	    log.debug("getDepth()");
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

	            // --- Start pressure conversion (D1) @ OSR=8192
	            deviceDepth.write((byte) (CMD_CONVERT_D1 | OSR_8192));
	            Thread.sleep(20); // per datasheet max conv time for OSR=8192 ~20ms

	            byte[] buf = new byte[3];
	            deviceDepth.readRegister(CMD_ADC_READ, buf, 0, 3);
	            long D1 = ((long) (buf[0] & 0xFF) << 16) | ((long) (buf[1] & 0xFF) << 8) | (long) (buf[2] & 0xFF);

	            // --- Start temperature conversion (D2) @ OSR=8192
	            deviceDepth.write((byte) (CMD_CONVERT_D2 | OSR_8192));
	            Thread.sleep(20);

	            deviceDepth.readRegister(CMD_ADC_READ, buf, 0, 3);
	            long D2 = ((long) (buf[0] & 0xFF) << 16) | ((long) (buf[1] & 0xFF) << 8) | (long) (buf[2] & 0xFF);

	            // --- Calibration coefficients (C1..C6)
	            long C1 = prom[1], C2 = prom[2], C3 = prom[3], C4 = prom[4], C5 = prom[5], C6 = prom[6];

	            // --- First-order calculations (datasheet)
	            long dT   = D2 - (C5 << 8);
	            long TEMP = 2000 + (dT * C6) / 8388608L;                 // hundredths of °C
	            long OFF  = (C2 * 65536L) + ((C4 * dT) / 128L);
	            long SENS = (C1 * 32768L) + ((C3 * dT) / 256L);

	            // --- Second-order temperature compensation
	            long T2 = 0, OFF2 = 0, SENS2 = 0;
	            if (TEMP < 2000) {
	                // low temp
	                long t = (TEMP - 2000);
	                T2 = (3L * (dT * dT)) / 8589934592L;                 // 2^33
	                OFF2 = (3L * t * t) / 2L;
	                SENS2 = (5L * t * t) / 8L;
	                if (TEMP < -1500) {
	                    long tt = (TEMP + 1500);
	                    OFF2 += 7L * tt * tt;
	                    SENS2 += 4L * tt * tt;
	                }
	            } else {
	                // high temp
	                T2 = (2L * (dT * dT)) / 137438953472L;               // 2^37
	                OFF2 = ((TEMP - 2000) * (TEMP - 2000)) / 16L;
	            }

	            TEMP -= T2;
	            OFF  -= OFF2;
	            SENS -= SENS2;

	            // Pressure in 0.1 mbar (per MS5837/5611 family scaling)
	            long P = ((D1 * SENS) / 2097152L - OFF) / 8192L;         // 2^21 and 2^13
	            double pressure_mbar = P / 10.0;                         // mbar (hPa)
	            double temp_C = TEMP / 100.0;                             // °C

	            // Capture baseline if needed
	            if (Double.isNaN(p0Mbar)) {
	                p0Mbar = pressure_mbar;
	            }

	            // Depth in meters: ΔP (Pa) / (ρ g); 1 mbar = 100 Pa
	            double depth_m = ((pressure_mbar - p0Mbar) * 100.0) / (fluidDensity * G);
	            int depth_mm = (int) Math.round(depth_m * 1000.0);

	            log.debug(String.format("P=%.2f mbar, T=%.2f °C, depth=%d mm (ρ=%.1f)", 
	                    pressure_mbar, temp_C, depth_mm, fluidDensity));

	            // If you prefer "down is negative", flip the sign here:
	            return -depth_mm; // or: return -depth_mm;

	        } catch (Exception e) {
	            log.warn("Depth read attempt " + attempts + " failed", e);
	            try { Thread.sleep(50); } catch (InterruptedException ie) { /* ignore */ }
	        }
	    }
	    log.error("Error reading depth sensor: retries exhausted");
	    return Constant.ERROR;
	}

	public Integer zeroOffsets() {
	    // Average a few samples for a clean P0 baseline
	    try {
	        double sum = 0;
	        int n = 10;
	        for (int i = 0; i < n; i++) {
	            // take a raw pressure sample without changing p0Mbar mid-loop
	            // Temporarily read once via getDepth() path but protect baseline.
	            double saved = p0Mbar;
	            p0Mbar = Double.NaN;                 // force fresh capture on next read
	            getDepth();                          // primes p0Mbar to current pressure
	            double p0 = p0Mbar;                  // captured
	            p0Mbar = saved;                      // restore
	            sum += p0;
	            Thread.sleep(25);
	        }
	        p0Mbar = sum / n;
	        offsetPitch = getDiveAngle();            // keep your pitch zeroing
	        if (offsetPitch == Constant.ERROR) offsetPitch = 0;
	        offsetDepth = 0;                         // no longer used for pressure; kept for compatibility
	        log.info(String.format("Zeroed: P0=%.2f mbar, pitch0=%d", p0Mbar, offsetPitch));
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
