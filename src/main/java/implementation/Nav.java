package implementation;


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
public class Nav {
	private static final Logger log = LoggerFactory.getLogger(Nav.class);

	private I2C deviceAccl;
	private I2C deviceMag;
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
			;

			// Create I2C config for magnetometer (LSM303DLHC)
			I2CConfig configMag = I2C.newConfigBuilder(pi4j)
					.id("LSM303AGR-Mag")
					.name("LSM303AGR Magnetometer")
					.bus(1)
					.device(0x1E)
					.build();

			// Get I2C provider and create I2C instances
			deviceAccl = i2CProvider.create(configAccl);
			deviceMag = i2CProvider.create(configMag);

			// Initialize accelerometer
			deviceAccl.writeRegister(0x21, (byte) 0x00); // X, Y and Z-axis enable, power on mode, o/p data rate 10 Hz
			deviceAccl.writeRegister(0x22, (byte) 0x00); // Full scale +/- 2g, continuous update
			deviceAccl.writeRegister(0x23, (byte) 0x81); // X, Y and Z-axis enable, power on mode, o/p data rate 10 Hz
			deviceAccl.writeRegister(0x20, (byte) 0x57); // Full scale +/- 2g, continuous update

			// Initialize magnetometer
			deviceMag.writeRegister(0x60, (byte) 0x8C); // Continuous conversion mode
			deviceMag.writeRegister(0x61, (byte) 0x02); // Data output rate = 15Hz
			deviceMag.writeRegister(0x62, (byte) 0x10); // Set gain = +/- 1.3g

			Thread.sleep(500); // Wait for settings to take effect
		} catch (Exception e) {
			log.error("Error initializing I2C devices", e);
		}
	}

	// readBearing(), setRudder(), and getDiveAngle() methods remain the same...

	// Additional methods for interacting with the LSM303DLHC sensor could be added here

	// Don't forget to properly close the Pi4J context when the application is stopped
	public void shutdown() {
		if (pi4j != null) {
			pi4j.shutdown();
		}
	}


	public Integer readBearing() {
		log.info("readBearing");
		short xMag = 0;
		short yMag = 0;
		short zMag = 0;
		int count = 0;
		while (xMag == 0 && yMag == 0 && zMag == 0 && count++ < 20) {
			try {
				byte[] magData = new byte[6];
				deviceMag.readRegister(0x68, magData, 0, 6);

				xMag = (short) ((magData[0] & 0xFF) | ((magData[1] & 0xFF) << 8));
				yMag = (short) ((magData[2] & 0xFF) | ((magData[3] & 0xFF) << 8));
				zMag = (short) ((magData[4] & 0xFF) | ((magData[5] & 0xFF) << 8));
				log.info("readBearing: x = " + xMag + " y = " + yMag);

				// Adjust values if they are negative
				if (xMag > 32767) xMag -= 65536;
				if (yMag > 32767) yMag -= 65536;
				if (zMag > 32767) zMag -= 65536;

				// Calculate bearing
				double bearing = Math.atan2(yMag, xMag) * (180 / Math.PI);

				if (xMag != 0 || yMag != 0 || zMag != 0) {
					return (int) -bearing;
				}
			} catch (IOException e) {
				log.error("Error reading magnetometer data", e);
				throw new RuntimeException("Error reading magnetometer data", e);
			} finally{
				try {
					if (xMag == 0 && yMag == 0 && zMag == 0) Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

	public Integer setRudder(Integer angle) {
		log.debug("setRudder:" + angle + " degrees");
		// Code to set the rudder angle, if applicable
		return angle;
	}


	public Integer getDiveAngle() {
		log.debug("getDiveAngle");
		short xAccl = 0;
		short yAccl = 0;
		short zAccl = 0;
		int count = 0;
		while (xAccl == 0 && yAccl == 0 && zAccl == 0 && count++ < 20) {
			try {
				byte[] acclData = new byte[6];
				deviceAccl.readRegister(0x28, acclData, 0, 6);

				xAccl = (short) (((acclData[1] & 0xFF) << 8) | (acclData[0] & 0xFF));
				yAccl = (short) (((acclData[3] & 0xFF) << 8) | (acclData[2] & 0xFF));
				zAccl = (short) (((acclData[5] & 0xFF) << 8) | (acclData[4] & 0xFF));

				// Adjust values if they are negative
				if (xAccl > 32767) xAccl -= 65536;
				if (zAccl > 32767) zAccl -= 65536;

				// Calculate dive angle
				double diveAngle = Math.atan2(xAccl, zAccl) * (180 / Math.PI);

				return (int) diveAngle;
			} catch (IOException e) {
				log.error("Error reading accelerometer data", e);
				throw new RuntimeException("Error reading accelerometer data", e);
			}finally{
				try {
					if (xAccl == 0 && yAccl == 0 && zAccl == 0) Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

	// Additional methods for interacting with the LSM303DLHC sensor could be added here
}


