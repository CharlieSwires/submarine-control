package implementation;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Eng {

	public Integer setPowerLeft(Integer percentPower) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer setPowerRight(Integer percentPower) {
		// TODO Auto-generated method stub
		return null;
	}

}
