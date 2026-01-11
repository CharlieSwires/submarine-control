package implementation;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2CProvider;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class I2CSingle {
	public static Context pi4j = null;
	public static I2CProvider i2c = null;
	
	// @Configuration class (once in your app)
	@Bean(destroyMethod = "shutdown")
	public static Context pi4j() { return Pi4J.newAutoContext(); }

	@Bean
	public static I2CProvider i2c(Context ctx) { return ctx.provider("linuxfs-i2c"); }

	static {
		if (i2c == null) {
		pi4j = pi4j();
		i2c = i2c(pi4j);
		}
	}
}
