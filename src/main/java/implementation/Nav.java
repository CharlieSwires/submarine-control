package implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Nav {
	Logger log = LoggerFactory.getLogger(Nav.class);

	public Integer readBearing() {
		log.debug("readBearing");
		// TODO Auto-generated method stub
		return 0;
	}

	public Integer setRudder(Integer angle) {
		log.debug("setRudder:"+angle+"degrees");
		// TODO Auto-generated method stub
		return angle;
	}

}
