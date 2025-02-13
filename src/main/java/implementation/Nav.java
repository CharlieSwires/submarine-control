package implementation;


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
public class Nav {
	private static final Logger log = LoggerFactory.getLogger(Nav.class);

	private I2C deviceMag;
	private Context pi4j;

	@Autowired
	private Dive dive;
	
	public Nav() {
		try {
	        log.info("Starting Nav method.");
			// Initialize Pi4J with auto context
			pi4j = Pi4J.newAutoContext();
			I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");


			// Create I2C config for magnetometer (LSM303DLHC)
			I2CConfig configMag = I2C.newConfigBuilder(pi4j)
					.id("LSM303AGR-Mag")
					.name("LSM303AGR Magnetometer")
					.bus(1)
					.device(0x1E)
					.build();

			// Get I2C provider and create I2C instances
			deviceMag = i2CProvider.create(configMag);

			// Initialize magnetometer
			deviceMag.writeRegister(0x60, (byte) 0x8C); // Continuous conversion mode
			deviceMag.writeRegister(0x61, (byte) 0x02); // Data output rate = 100Hz
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
		log.debug("readBearing");
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
				log.debug("readBearing: x = " + xMag + " y = " + yMag+ " z = " + zMag);

				// Calculate bearing
				double bearing = Math.atan2(yMag, xMag) * (180 / Math.PI);

				if (xMag != 0 || yMag != 0 || zMag != 0) {
					return (int) -bearing+16;
				}
			} catch (Exception e) {
				log.error("Error reading magnetometer data", e.getMessage());
				continue;
			} finally {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
				}
			}
		}
		return Constant.ERROR;
	}

	public Integer setRudder(Integer angle) {
		log.debug("setRudder:" + angle + " degrees");
		return dive.setRudder(angle);
	}



	// Additional methods for interacting with the LSM303DLHC sensor could be added here
}


