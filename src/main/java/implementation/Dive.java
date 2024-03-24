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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Dive {

	Logger log = LoggerFactory.getLogger(Dive.class);

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
	private static final int PWM_MIN = 1024; // Minimum PWM value for 0 degrees was 150
	private static final int PWM_MAX = 600; // Maximum PWM value for 180 degrees
	private static final int PCA9685_MODE1 = 0x00;
	private static final int LED0_ON_L = 0x06;
	private static final int LED0_ON_H = 0x07;
	private static final int LED0_OFF_L = 0x08;
	private static final int LED0_OFF_H = 0x09;
	
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
			log.info("Starting Depth method.");
			// Initialize Depth Sensor
			I2CConfig configDepth = I2C.newConfigBuilder(pi4j)
					.id("MS5837")
					.name("MS5837 Depth Sensor")
					.bus(1)
					.device(0x76)  // MS5837 default I2C address
					.build();
			deviceDepth = i2CProvider.create(configDepth);
			log.debug("Depth sensor initialized.");

			// Reset the device
			log.debug("Sending reset command to the depth sensor.");
			deviceDepth.writeRegister(0x1E, (byte)0x78);
			try { Thread.sleep(20); } catch (Exception e) {} // Wait for conversion to complete
			log.debug("Depth sensor reset.");

			// Read calibration coefficients
			log.debug("Reading calibration coefficients.");
			for (int i = 0; i < calibrationCoefficients.length; i++) {
				byte[] data = new byte[2];
				deviceDepth.readRegister(0xA0 + (i * 2), data, 0, 2); // Each coefficient is 2 bytes, starting at 0xA0
				calibrationCoefficients[i] = ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);
				log.info("Coefficient C" + (i+1) + ": " + calibrationCoefficients[i]);
			}

			log.debug("Calibration Coefficients: " + Arrays.toString(calibrationCoefficients));

			try { Thread.sleep(100); } catch (Exception e) {} // Wait for conversion to complete
			disabled = (System.getenv("WATCHDOG_ENABLED") != null && 
					System.getenv("WATCHDOG_ENABLED").equals("true")? false: true);
			log.info("Watchdog disabled = " + disabled);
			log.info("WATCHDOG_ENABLED = " + System.getenv("WATCHDOG_ENABLED"));
			watchDogThread = new WatchDog();
			watchDogThread.start();
		} catch (Exception e) {
			log.error("Error initializing I2C devices Depth", e);
		}
		try {
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
		    Thread.sleep(5);
	        setPWMFreq(60); // 50Hz for servos
		    Thread.sleep(5);

		} catch (Exception e) {
			log.error("Error initializing I2C devices Servo", e);
		}
	}
	private void setPWMFreq(int freq) throws Exception {
	    int prescale = calculatePrescale(freq);
	    byte oldmode = (byte) devicePCA9685.readRegister(0x00); // Read MODE1 register
	    byte newmode = (byte) ((oldmode & 0x7F) | 0x10); // sleep
	    devicePCA9685.writeRegister(0x00, newmode); // go to sleep
	    devicePCA9685.writeRegister(0xFE, (byte) prescale); // set the prescaler
	    devicePCA9685.writeRegister(0x00, oldmode);
	    Thread.sleep(5);
	    devicePCA9685.writeRegister(0x00, (byte) (oldmode | 0x80)); //  This sets the RESTART bit to wake up the PCA9685
	}
	private int calculatePrescale(double freq) {
	    double prescaleval = 25000000.0; // 25,000,000 Hz
	    prescaleval /= 4096.0;           // 12-bit
	    prescaleval /= freq;
	    prescaleval -= 1.0;
	    return (int) Math.ceil(prescaleval);
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
		return 10 - offsetPitch;
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
		setFillTank(false);
	}
	// Method to convert servo angle to PWM value
	private double angleToDutyCycle(int angle) {
	    return 100.0 * (angle / 90.0);
	}

	// Set the angle for the front servo
	public Integer setFrontAngle(int angle) {
		angle += 45;
	    double dutyCycle = angleToDutyCycle(angle);
	    // Assuming channel 0 for the front servo
	    setPWM(0, dutyCycle);
	    log.debug("setFrontAngle: " + angle);
	    return angle;
	}

	// Set the angle for the back servo
	public Integer setBackAngle(int angle) {
		angle += 45;
	    double dutyCycle = angleToDutyCycle(angle);
	    // Assuming channel 1 for the back servo
	    setPWM(1, dutyCycle);
	    log.debug("setBackAngle: " + angle);
	    return angle;
	}

	// Assuming you have a method like this to send PWM signals
	private void setPWM(int channel, double dutyCycle) {
	    int onCount = (int) (PWM_MIN);
	    int offCount = (int) (PWM_MIN + ((4096)* ((dutyCycle) / 100.0)));
	    if (offCount >= 4096) {
	        offCount -= 4096; // Adjust for next frame if necessary
	    }

	    int onLow = onCount & 0xFF;
	    int onHigh = (onCount >> 8) & 0xFF;
	    int offLow = offCount & 0xFF;
	    int offHigh = (offCount >> 8) & 0xFF;

	    log.info("onCount = " + onCount + " offCount = " + offCount);
	    devicePCA9685.writeRegister(LED0_ON_L + 4 * channel, onLow);
	    devicePCA9685.writeRegister(LED0_ON_H + 4 * channel, onHigh);
	    devicePCA9685.writeRegister(LED0_OFF_L + 4 * channel, offLow);
	    devicePCA9685.writeRegister(LED0_OFF_H + 4 * channel, offHigh);
	}

	public Integer setFillTank(Boolean action) {
		log.debug("setFillTank:"+action);

		return action?1:0;
	}

	public Integer getDepth() {
		log.debug("getDepth");
		try {
			if (!disabled) {
				if (watchDogThread != null) {
					// Stop the watch dog thread
					startTimer.set(true);
					watchDogThread.interrupt();
				}

			} else {
				if (watchDogThread != null) {
					startTimer.set(false);
					watchDogThread.interrupt();
				}
			}


			// Depth and temperature reading sequence...
			// Initiate pressure and temperature reading sequence
			//			deviceDepth.writeRegister(0x1E, (byte)0x78); // Reset command
			//			Thread.sleep(50); // Wait for reset to complete
			deviceDepth.writeRegister(0x40, (byte)0x12); // Start pressure conversion was 0x02
			try { Thread.sleep(20); } catch (Exception e) {} // Wait for conversion to complete
			byte[] pressureData = new byte[3];
			deviceDepth.readRegister(0x00, pressureData, 0, 3); // Read pressure data
			long D1 = ((pressureData[0] & 0xFF) << 16) | ((pressureData[1] & 0xFF) << 8) | (pressureData[2] & 0xFF);// Read pressure data as before...
			deviceDepth.writeRegister(0x50, (byte)0x1C); // Start temperature conversion was 0x0A
			try { Thread.sleep(20); } catch (Exception e) {} // Wait for conversion to complete
			byte[] tempData = new byte[3];
			deviceDepth.readRegister(0x00, tempData, 0, 3); // Read temperature data

			long D2 = ((tempData[0] & 0xFF) << 16) | ((tempData[1] & 0xFF) << 8) | (tempData[2] & 0xFF);

			long dT = D2 - calibrationCoefficients[4] * 256;
			long TEMP = 2000 + dT * calibrationCoefficients[5] / (long)8388608;
			long OFF = calibrationCoefficients[1] * 65536L + (calibrationCoefficients[3] * dT) / 128L;
			long SENS = calibrationCoefficients[0] * 32768L + (calibrationCoefficients[2] * dT) / 256L;
			long T2 = 0;
			long OFF2 = 0;
			long SENS2 = 0;

			if(TEMP >= 2000)
			{
				T2 = 2 * (dT * dT) / 137438953472L;
				OFF2 = ((TEMP - 2000) * (TEMP - 2000)) / 16;
				SENS2 = 0;
			}
			else if(TEMP < 2000)
			{
				T2 = 3 * (dT * dT) / 8589934592L;
				OFF2 = 3 * ((TEMP - 2000) * (TEMP - 2000)) / 2;
				SENS2 = 5 * ((TEMP - 2000) * (TEMP - 2000)) / 8;
				if(TEMP < -1500)
				{
					OFF2 = OFF2 + 7 * ((TEMP + 1500) * (TEMP + 1500));
					SENS2 = SENS2 + 4 * ((TEMP + 1500) * (TEMP + 1500));
				}
			}

			TEMP = TEMP - T2;
			OFF = OFF - OFF2;
			SENS = SENS - SENS2;
			double pressure = ((((D1 * SENS) / 2097152) - OFF) / 8192) / 10.0;
			double tempCelsius = TEMP / 100.0;
			// Depth calculation using the corrected pressure value...
			double correctedPressure = 1025.0*pressure/ 22093.3; //mPa
			//
			//			// Convert temperature to degrees Celsius
			tempCelsius = 19.0 * tempCelsius/82.18; //Celcius

			// Apply temperature compensation to pressure
			double density = 999.842594 + 6.793952e-2 * tempCelsius - 9.09529e-3 * Math.pow(tempCelsius, 2)
			+ 1.001685e-4 * Math.pow(tempCelsius, 3) - 1.120083e-6 * Math.pow(tempCelsius, 4)
			+ 6.536332e-9 * Math.pow(tempCelsius, 5);

			double depthmm = 1000.0 * pressure / (density * 9.80665 * 1000.0);//mPa /1000
			log.debug("pressure = "+pressure+" pressure(mPa) = "+correctedPressure+" tempCelsius = "+tempCelsius+" depth(mm) = "+depthmm+" density(Kg/m^3) = "+density);

			return (int) (-depthmm - offsetDepth); // Convert meters to millimeters
		} catch (Exception e) {
			log.error("Error reading depth sensor data");
			return -999 - offsetDepth;
		}
	}

	public Integer zeroOffsets() {
		Dive.offsetDepth = 0;
		Dive.offsetDepth = getDepth();
		Dive.offsetPitch = 0;
		Dive.offsetPitch = getDiveAngle();
		return 0;
	}
	public Integer setRudder(Integer angle) {
		angle += 45;
	    double dutyCycle = angleToDutyCycle(angle);
	    // Assuming channel 1 for the back servo
	    setPWM(2, dutyCycle);
	    log.debug("setRudder: " + angle);
	    return angle;
	}

}
