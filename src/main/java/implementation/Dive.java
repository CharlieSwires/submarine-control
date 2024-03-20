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

//
//	public Integer getDiveAngle() {
//		log.debug("getDiveAngle");
//		short xAccl = 0;
//		short yAccl = 0;
//		short zAccl = 0;
//		int count = 0;
//		while (xAccl == 0 && yAccl == 0 && zAccl == 0 && count++ < 20) {
//			try {
//				byte[] ready = new byte[1];
//				deviceAccl.readRegister(0x27, ready, 0, 1);
//				log.debug("ready = " + ready[0]);
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
//					log.debug("Average: x = " + average[X_ID] + " y = " + average[Y_ID] + " z = " + average[Z_ID]);
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
import com.pi4j.io.exception.IOException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

import jakarta.annotation.PostConstruct;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Dive {

	Logger log = LoggerFactory.getLogger(Dive.class);

	private I2C deviceGyro;
	private I2C deviceDepth;
	private Context pi4j;
	private WatchDog watchDogThread;
	private static boolean firstTime = true;

	@PostConstruct
	public void init() {
		try {
			pi4j = Pi4J.newAutoContext();
			I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");

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
			// Assuming deviceDepth is already calibrated, if not, implement calibration here

			Thread.sleep(100); // Wait for sensor settings to take effect
		} catch (Exception e) {
			log.error("Error initializing I2C devices", e);
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
//			Double pitch = Math.atan2(gyroY, gyroZ) * (180 / Math.PI);
//			log.info("Pitch: " + pitch);
//			return pitch.intValue();
//		} catch (IOException e) {
//			log.error("Error reading gyroscope data", e);
//			throw new RuntimeException("Error reading gyroscope data", e);
//		}
		return 0;
	}

	// Watch Dog thread class
	private class WatchDog extends Thread {

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					// Sleep for 1000 milliseconds
					Thread.sleep(2000);

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
		log.debug("emergencySurface");
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
	    log.debug("getDepth");
	    try {
	        if (!firstTime) {
	            // Stop the watch dog thread
	            watchDogThread.interrupt();
	        }
	        // Restart the watch dog thread
	        watchDogThread = new WatchDog();
	        watchDogThread.start();
	        firstTime = false;
	        
	        // Initiate pressure and temperature reading sequence
	        deviceDepth.writeRegister(0x1E, (byte)0x78); // Reset command
	        Thread.sleep(10); // Wait for reset to complete
	        deviceDepth.writeRegister(0x40, (byte)0x02); // Start pressure conversion
	        Thread.sleep(20); // Wait for conversion to complete
	        byte[] pressureData = new byte[3];
	        deviceDepth.readRegister(0x00, pressureData, 0, 3); // Read pressure data
	        deviceDepth.writeRegister(0x50, (byte)0x0A); // Start temperature conversion
	        Thread.sleep(20); // Wait for conversion to complete
	        byte[] tempData = new byte[3];
	        deviceDepth.readRegister(0x00, tempData, 0, 3); // Read temperature data

	        int pressure = ((pressureData[0] & 0xFF) << 16) | ((pressureData[1] & 0xFF) << 8) | (pressureData[2] & 0xFF);
	        int temperature = ((tempData[0] & 0xFF) << 16) | ((tempData[1] & 0xFF) << 8) | (tempData[2] & 0xFF);

	        // Convert temperature to degrees Celsius
	        double tempCelsius = temperature / 100.0;

	        // Apply temperature compensation to pressure
	        double density = 999.842594 + 6.793952e-2 * tempCelsius - 9.09529e-3 * Math.pow(tempCelsius, 2)
	                + 1.001685e-4 * Math.pow(tempCelsius, 3) - 1.120083e-6 * Math.pow(tempCelsius, 4)
	                + 6.536332e-9 * Math.pow(tempCelsius, 5);
	        
	        double depthMeters = pressure / (density * 9.80665);
		    log.info("pressure = "+pressure+" temperature = "+temperature+" depth = "+depthMeters+" density = "+density);

	        return (int) (-depthMeters * 1000); // Convert meters to millimeters
	    } catch (IOException | InterruptedException e) {
	        log.error("Error reading depth sensor data", e);
	        throw new RuntimeException("Error reading depth sensor data", e);
	    }
	}

}
