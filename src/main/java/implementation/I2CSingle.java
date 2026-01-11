package implementation;

import org.springframework.context.annotation.Bean;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2CProvider;

public class I2CSingle {
	// @Configuration class (once in your app)
	@Bean(destroyMethod = "shutdown")
	public Context pi4j() { return Pi4J.newAutoContext(); }

	@Bean
	public I2CProvider i2c(Context ctx) { return ctx.provider("linuxfs-i2c"); }

}
