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
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Dive {

	Logger log = LoggerFactory.getLogger(Dive.class);

	private I2C deviceGyro;
	private I2C deviceDepth;
	private Context pi4j;
	private WatchDog watchDogThread;
	private static boolean firstTime = true;
	private int[] calibrationCoefficients = new int[6];

	private static boolean disabled = true;
	private static int offsetDepth = 0;
	private static int offsetPitch = 0;

	public Dive() {
		try {
			log.info("Starting Dive method.");
			pi4j = Pi4J.newAutoContext();
			log.debug("Pi4J context initialized.");
			I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
			log.debug("I2C provider obtained.");


			//			I2CConfig configGyro = I2C.newConfigBuilder(pi4j)
			//					.id("LSM6DSO32-Gyro")
			//					.name("LSM6DSO32 Gyroscope")
			//					.bus(1)
			//					.device(0x6B)  // Adjust if using a different I2C address
			//					.build();
			//
			//			deviceGyro = i2CProvider.create(configGyro);
			//
			//			// Gyroscope initialization
			//			deviceGyro.writeRegister(0x10, (byte) 0x4C); // CTRL2_G: 104 Hz, 2000 dps, gyro full-scale
			//			Thread.sleep(100); // Wait for gyro settings to take effect

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
			Thread.sleep(20);
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

			Thread.sleep(100); // Wait for sensor settings to take effect
			disabled  = true;
//			for (int i = 0; i < 5; i++) {
//				try {
//					getDepth();
//					Thread.sleep(50);
//					log.info("getDepth() called");
//
//				}catch (Exception e) {
//					log.info("exception raised ignoring!!");
//				}
//			}
//			disabled = false;
		} catch (Exception e) {
			log.error("Error initializing I2C devices", e);
			throw new RuntimeException("Error initializing I2C devices", e);
		}
	}

	public Integer getDiveAngle() {
		//		try {
		//			byte[] gyroData = new byte[6];
		//			deviceGyro.readRegister(0x22, gyroData, 0, 6); // OUTX_L_G register address
		//
		//			short gyroX = (short) ((gyroData[0] & 0xFF) | (gyroData[1] << 8));
		//			short gyroY = (short) ((gyroData[2] & 0xFF) | (gyroData[3] << 8));
		//			short gyroZ = (short) ((gyroData[4] & 0xFF) | (gyroData[5] << 8));
		//
		//			Double pitch = Math.atan2(gyroZ, gyroX) * (180 / Math.PI);
		//			log.debug("Pitch: " + pitch);
		//			return pitch.intValue() - offsetPitch;
		//		} catch (IOException e) {
		//			log.error("Error reading gyroscope data", e);
		//			throw new RuntimeException("Error reading gyroscope data", e);
		//		}
		return 10 - offsetPitch;
	}

	// Watch Dog thread class
	private class WatchDog extends Thread {

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					// Sleep for 5seconds
					Thread.sleep(5000);

					// Check if getDepth method hasn't been called within 1000ms
					if (!Thread.currentThread().isInterrupted()) {
						// Trigger emergency surface event
						emergencySurface();
					}
				} catch (InterruptedException e) {
					// Thread interrupted, exit the loop
					break;
				}
			}
		}
	}
	// Method to trigger emergency surface event
	private void emergencySurface() {
		log.error("emergencySurface");
		setFillTank(false);
	}
	public Integer setFrontAngle(Integer angle) {
		log.debug("setFrontAngle:"+angle);
		return angle;
	}

	public Integer setBackAngle(Integer angle) {
		log.debug("setBackAngle:"+angle);
		return angle;
	}

	public Integer setFillTank(Boolean action) {
		log.debug("setFillTank:"+action);

		return action?1:0;
	}

	public Integer getDepth() {
		log.info("getDepth");
		try {
			if (!disabled) {

				if (!firstTime) {
					// Stop the watch dog thread
					watchDogThread.interrupt();
				}
				// Restart the watch dog thread
				watchDogThread = new WatchDog();
				watchDogThread.start();
				firstTime = false;
			}

			// Depth and temperature reading sequence...
			// Initiate pressure and temperature reading sequence
//			deviceDepth.writeRegister(0x1E, (byte)0x78); // Reset command
//			Thread.sleep(50); // Wait for reset to complete
			deviceDepth.writeRegister(0x40, (byte)0x12); // Start pressure conversion was 0x02
			Thread.sleep(20); // Wait for conversion to complete
			byte[] pressureData = new byte[3];
			deviceDepth.readRegister(0x00, pressureData, 0, 3); // Read pressure data
			long D1 = ((pressureData[0] & 0xFF) << 16) | ((pressureData[1] & 0xFF) << 8) | (pressureData[2] & 0xFF);// Read pressure data as before...
			deviceDepth.writeRegister(0x50, (byte)0x1C); // Start temperature conversion was 0x0A
			Thread.sleep(20); // Wait for conversion to complete
			byte[] tempData = new byte[3];
			deviceDepth.readRegister(0x00, tempData, 0, 3); // Read temperature data

			int temperature = ((tempData[0] & 0xFF) << 16) | ((tempData[1] & 0xFF) << 8) | (tempData[2] & 0xFF);
			long D2 = ((tempData[0] & 0xFF) << 16) | ((tempData[1] & 0xFF) << 8) | (tempData[2] & 0xFF);


			// Apply conversion formulae using calibration coefficients
			double dT = D2 - calibrationCoefficients[4] * Math.pow(2, 8);
			double TEMP = 2000 + dT * calibrationCoefficients[5] / Math.pow(2, 23);
			double OFF = calibrationCoefficients[1] * Math.pow(2, 17) + (calibrationCoefficients[3] * dT) / Math.pow(2, 6);
			double SENS = calibrationCoefficients[0] * Math.pow(2, 16) + (calibrationCoefficients[2] * dT) / Math.pow(2, 7);
			double P = (D1 * SENS / Math.pow(2, 21) - OFF) / Math.pow(2, 15);

			// Depth calculation using the corrected pressure value...

			log.debug("Pressure: " + P + " mbar, Temperature: " + TEMP + " °C");

			double pressure = 1025.0*P/ 109995.54106639235; //mPa

			// Convert temperature to degrees Celsius
			double tempCelsius = 19.0 * TEMP/8143.355641007423; //Celcius

			// Apply temperature compensation to pressure
			double density = 999.842594 + 6.793952e-2 * tempCelsius - 9.09529e-3 * Math.pow(tempCelsius, 2)
			+ 1.001685e-4 * Math.pow(tempCelsius, 3) - 1.120083e-6 * Math.pow(tempCelsius, 4)
			+ 6.536332e-9 * Math.pow(tempCelsius, 5);

			double depthMeters = pressure / (density * 9.80665 * 1000.0);//mPa /1000
			log.debug("pressure = "+pressure+" tempCelsius = "+tempCelsius+" depth = "+depthMeters+" density = "+density);

			return (int) (-depthMeters * 1000 - offsetDepth); // Convert meters to millimeters
		} catch (Exception e) {
			log.error("Error reading depth sensor data", e);
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

}
