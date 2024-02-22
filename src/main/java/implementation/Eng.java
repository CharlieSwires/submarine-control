package implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Eng {
	Logger log = LoggerFactory.getLogger(Eng.class);

	public Integer setPowerLeft(Integer percentPower) {
		log.debug("setPowerLeft:"+percentPower+"%");
		// TODO Auto-generated method stub
		return null;
	}

	public Integer setPowerRight(Integer percentPower) {
		log.debug("setPowerRight:"+percentPower+"%");
		// TODO Auto-generated method stub
		return null;
	}

}
