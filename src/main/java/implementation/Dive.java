package implementation;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Dive {

	public Integer setFrontAngle(Integer angle) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer setBackAngle(Integer angle) {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean setFillTank(Boolean action) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getDepth() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getDiveAngle() {
		// TODO Auto-generated method stub
		return null;
	}

}
