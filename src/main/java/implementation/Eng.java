package implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmConfigBuilder;
import com.pi4j.io.pwm.PwmType;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Eng {
	private static final Logger log = LoggerFactory.getLogger(Eng.class);

	// Motor 1 (Left Motor) Pins
	private static final int MOTOR_1_PIN_E = 12; // PWM pin for left motor speed
	private static final int MOTOR_1_PIN_A = 4; // Direction pin A for left motor
	private static final int MOTOR_1_PIN_B = 5; // Direction pin B for left motor

	// Motor 2 (Right Motor) Pins
	private static final int MOTOR_2_PIN_E = 18; // PWM pin for right motor speed
	private static final int MOTOR_2_PIN_A = 0; // Direction pin A for right motor
	private static final int MOTOR_2_PIN_B = 2; // Direction pin B for right motor

	private DigitalOutput motor1pinA, motor1pinB, motor2pinA, motor2pinB;
	private Pwm motor1pinE, motor2pinE;
	private Context pi4j;

	public Eng() {
		try {
			// Initialize Pi4J with auto context
			pi4j = Pi4J.newAutoContext();

			// Initialize GPIO digital output pins for motor direction control
			motor1pinA = pi4j.create(buildDigitalOutputConfig(MOTOR_1_PIN_A, "M1A"));
			motor1pinB = pi4j.create(buildDigitalOutputConfig(MOTOR_1_PIN_B, "M1B"));
			motor2pinA = pi4j.create(buildDigitalOutputConfig(MOTOR_2_PIN_A, "M2A"));
			motor2pinB = pi4j.create(buildDigitalOutputConfig(MOTOR_2_PIN_B, "M2B"));

			// Initialize PWM pins for motor speed control
			motor1pinE = pi4j.create(buildPwmConfig(MOTOR_1_PIN_E, "M1E"));
			motor2pinE = pi4j.create(buildPwmConfig(MOTOR_2_PIN_E, "M2E"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Abort");
		}
	}

	private DigitalOutputConfig buildDigitalOutputConfig(int address, String id) {
		return DigitalOutput.newConfigBuilder(pi4j)
				.address(address)
				.id(id)
				.shutdown(DigitalState.LOW)
				.initial(DigitalState.LOW)
				.build();
	}

	private PwmConfig buildPwmConfig(int address, String id) {
		return Pwm.newConfigBuilder(pi4j)
				.address(address)
				.id(id)
				.pwmType(PwmType.SOFTWARE)
				.frequency(10) // Set the PWM frequency if necessary
				.dutyCycle(0) // Start with 0% duty cycle
				.build();
	}

	public Integer setPowerLeft(Integer percentPower) {
		log.info("setPowerLeft:" + percentPower + "%");

		// Set direction based on the sign of percentPower
		motor1pinA.setState(percentPower >= 0);
		motor1pinB.setState(percentPower < 0);

		// Set speed using the absolute value of percentPower
		motor1pinE.on(Math.abs(percentPower));
		log.info("type : " +motor1pinE.pwmType());
		return percentPower;
	}

	public Integer setPowerRight(Integer percentPower) {
		log.info("setPowerRight:" + percentPower + "%");

		// Set direction based on the sign of percentPower
		motor2pinA.setState(percentPower >= 0);
		motor2pinB.setState(percentPower < 0);

		// Set speed using the absolute value of percentPower
		motor2pinE.on(Math.abs(percentPower));
		log.info("type : " +motor2pinE.pwmType());

		return percentPower;
	}
}
