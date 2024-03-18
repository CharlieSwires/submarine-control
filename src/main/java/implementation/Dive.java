package implementation;

import java.util.HashSet;
import java.util.Set;

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

	private WatchDog watchDogThread;
	private static boolean firstTime = true;
	private I2C deviceAccl;
	private Context pi4j;

	@PostConstruct
	public void init() {
		try {
			// Initialize Pi4J with auto context
			pi4j = Pi4J.newAutoContext();
			I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");

			// Create I2C config for accelerometer (LSM303DLHC)
			I2CConfig configAccl = I2C.newConfigBuilder(pi4j)
					.id("LSM303AGR-Accl")
					.name("LSM303AGR Accelerometer")
					.bus(1)
					.device(0x19)
					.build();


			// Get I2C provider and create I2C instances
			deviceAccl = i2CProvider.create(configAccl);

			// Initialize accelerometer
			deviceAccl.writeRegister(0x21, (byte) 0x00); 
			deviceAccl.writeRegister(0x22, (byte) 0x00); 
			deviceAccl.writeRegister(0x23, (byte) 0x00); //continuous normal SPI disabled
			deviceAccl.writeRegister(0x20, (byte) 0x67); //normal power 200Hz xyz enabled


			Thread.sleep(500); // Wait for settings to take effect
		} catch (Exception e) {
			log.error("Error initializing I2C devices", e);
		}
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

		if (!firstTime) {
			// Stop the watch dog thread
			watchDogThread.interrupt();
		}
		// Restart the watch dog thread
		watchDogThread = new WatchDog();
		watchDogThread.start();
		firstTime = false;
		return 0;
	}

	public Integer getDiveAngle() {
		log.debug("getDiveAngle");
		short xAccl = 0;
		short yAccl = 0;
		short zAccl = 0;
		int count = 0;
		while (xAccl == 0 && yAccl == 0 && zAccl == 0 && count++ < 20) {
			try {
				byte[] ready = new byte[1];
				deviceAccl.readRegister(0x27, ready, 0, 1);
				log.info("ready = " + ready[0]);
				if ((ready[0] & 7) > 0) {
//					byte[] acclData = new byte[6];
//					deviceAccl.readRegister(0x28, acclData, 0, 6);

					byte[] acclDataX = new byte[2];
					byte[] acclDataY = new byte[2];
					byte[] acclDataZ = new byte[2];
					Set<String> set = new HashSet<String>();
					while(true) {
						ready = new byte[1];
						deviceAccl.readRegister(0x27, ready, 0, 1);
						if ((ready[0] & 1) == 1) {
							deviceAccl.readRegister(0x28, acclDataX, 0, 2);
							set.add("X");
						} else if ((ready[0] & 2) == 2) {
							deviceAccl.readRegister(0x2A, acclDataY, 0, 2);
							set.add("Y");
						} else if ((ready[0] & 4) == 4) {
							deviceAccl.readRegister(0x2C, acclDataZ, 0, 2);
							set.add("Z");
						}
						if (set.size() == 3) {
							break;
						}
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					xAccl = (short) (((acclDataX[1] & 0xFF) << 8) | (acclDataX[0] & 0xFF));
					yAccl = (short) (((acclDataY[1] & 0xFF) << 8) | (acclDataY[0] & 0xFF));
					zAccl = (short) (((acclDataZ[1] & 0xFF) << 8) | (acclDataZ[0] & 0xFF));
					log.info("getDiveAngle: x = " + xAccl + " y = " + yAccl + " z = " + zAccl );

					// Calculate dive angle
					double diveAngle = Math.atan2(xAccl, zAccl) * (180 / Math.PI);
					if (xAccl != 0 || yAccl != 0 || zAccl != 0) {
						return (int) diveAngle;
					}

				} else {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
			} catch (IOException e) {
				log.error("Error reading accelerometer data", e);
				throw new RuntimeException("Error reading accelerometer data", e);
			}finally{
				try {
					if (xAccl == 0 && yAccl == 0 && zAccl == 0) Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
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
}
