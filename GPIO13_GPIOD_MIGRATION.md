# GPIO13 SSS migration (Raspberry Pi OS Trixie)

- High-power solid-state switch (12 V / 100 A, driven by transistor on BCM GPIO13) is owned by `implementation.Eng` rather than `Dive`.
- The existing `/submarine/dive/power/{0|1}` URL remains unchanged; `rest.DiveControl` delegates to `Eng`.
- GPIO13 uses Pi4J `gpiod-digital-output` (GpioD) instead of pigpio. All Pi4J modules have been updated together from 2.4.0 to 2.8.0.
- The pin initializes LOW and shuts down LOW. `Eng` sets both propulsion ESC hardware PWM channels to neutral (7.5% at 50 Hz) before enabling the SSS. This does **not** replace the five-second neutral-command calibration sequence in Engine Room UI and does not validate the pump ESC's neutral signal.
- LinuxFS I2C and LinuxFS hardware PWM remain unchanged. The prior PWM error `/sys/class/pwm/pwmchip0/npwm` must be separately resolved by enabling/verifying the PWM device-tree overlay and channel mapping.
- Test on a bench with motor/propeller power isolated first. GpioD requires a compatible GPIO character device and native library for the target Pi/OS; GPIO13 pin-to-chip mapping must be verified on Trixie.
- The GPIO output is software-controlled, not a guaranteed hardware power-off mechanism if the Pi freezes. Provide an independent hardware failsafe before water trials.

Build on the laptop with `mvn clean package -DskipTests`; deploy the new JAR, then check boot logs and the GPIO/PWM interfaces before energising the 12 V switch.
