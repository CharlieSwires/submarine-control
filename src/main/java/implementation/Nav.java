package implementation;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Nav {

	public Integer readBearing() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer setRudder(Integer angle) {
		// TODO Auto-generated method stub
		return null;
	}

}
