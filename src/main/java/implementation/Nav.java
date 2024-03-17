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
import com.pi4j.io.i2c.I2CConfigBuilder;
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

			// Create I2C config for accelerometer (LSM303DLHC)
			I2CConfigBuilder configAccl = I2C.newConfigBuilder(pi4j)
					.id("LSM303DLHC-Accl")
					.name("LSM303DLHC Accelerometer")
					.bus(1)
					.device(0x19)
					.provider("pigpio-i2c");
;

			// Create I2C config for magnetometer (LSM303DLHC)
			I2CConfigBuilder configMag = I2C.newConfigBuilder(pi4j)
					.id("LSM303DLHC-Mag")
					.name("LSM303DLHC Magnetometer")
					.bus(1)
					.device(0x1E)
					.provider("pigpio-i2c");

			// Get I2C provider and create I2C instances
			deviceAccl = pi4j.create(configAccl.build());
			deviceMag = pi4j.create(configMag.build());

			// Initialize accelerometer
			deviceAccl.writeRegister(0x20, (byte) 0x27); // X, Y and Z-axis enable, power on mode, o/p data rate 10 Hz
			deviceAccl.writeRegister(0x23, (byte) 0x00); // Full scale +/- 2g, continuous update

			// Initialize magnetometer
			deviceMag.writeRegister(0x02, (byte) 0x00); // Continuous conversion mode
			deviceMag.writeRegister(0x00, (byte) 0x10); // Data output rate = 15Hz
			deviceMag.writeRegister(0x01, (byte) 0x20); // Set gain = +/- 1.3g

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
		try {
			byte[] magData = new byte[6];
			deviceMag.readRegister(0x03, magData, 3, 6);

			short xMag = (short) (((magData[0] & 0xFF) << 8) | (magData[1] & 0xFF));
			short yMag = (short) (((magData[4] & 0xFF) << 8) | (magData[5] & 0xFF));
			log.info("readBearing: x = " + xMag + " y = " + yMag);

			// Adjust values if they are negative
			if (xMag > 32767) xMag -= 65536;
			if (yMag > 32767) yMag -= 65536;

			// Calculate bearing
			double bearing = Math.atan2(yMag, xMag) * (180 / Math.PI);

			if (bearing < 0) bearing += 360.0;
			
			return (int) bearing;
		} catch (IOException e) {
			log.error("Error reading magnetometer data", e);
			throw new RuntimeException("Error reading magnetometer data", e);
		}
	}

	public Integer setRudder(Integer angle) {
		log.debug("setRudder:" + angle + " degrees");
		// Code to set the rudder angle, if applicable
		return angle;
	}


	public Integer getDiveAngle() {
		log.debug("getDiveAngle");
		try {
			byte[] acclData = new byte[6];
			deviceAccl.readRegister(0x28, acclData, 0, 6);

			int xAccl = ((acclData[1] & 0xFF) << 8) | (acclData[0] & 0xFF);
			int zAccl = ((acclData[5] & 0xFF) << 8) | (acclData[4] & 0xFF);

			// Adjust values if they are negative
			if (xAccl > 32767) xAccl -= 65536;
			if (zAccl > 32767) zAccl -= 65536;

			// Calculate dive angle
			double diveAngle = Math.atan2(xAccl, zAccl) * (180 / Math.PI);

			return (int) diveAngle;
		} catch (IOException e) {
			log.error("Error reading accelerometer data", e);
			throw new RuntimeException("Error reading accelerometer data", e);
		}
	}

	// Additional methods for interacting with the LSM303DLHC sensor could be added here
}


