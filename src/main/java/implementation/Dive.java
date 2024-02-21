package implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Dive {

	Logger log = LoggerFactory.getLogger(Dive.class);
	
    private WatchDog watchDogThread;
    private static boolean firstTime = true;
 
    public Integer setFrontAngle(Integer angle) {
    	log.info("setFrontAngle:"+angle);
        return angle;
    }

    public Integer setBackAngle(Integer angle) {
    	log.info("setBackAngle:"+angle);
        return angle;
    }

    public Integer setFillTank(Boolean action) {
    	log.info("setFillTank:"+action);

        return action?1:0;
    }

    public Integer getDepth() {
    	log.info("getDepth");

    	if (!firstTime) {
    		// Stop the watch dog thread
    		watchDogThread.interrupt();
    	}
        // Restart the watch dog thread
        watchDogThread = new WatchDog();
        watchDogThread.start();
        firstTime = false;
        return 0;
    }

    public Integer getDiveAngle() {
    	log.info("getDiveAngle");

        return 0;
    }

    // Watch Dog thread class
    private class WatchDog extends Thread {
 
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Sleep for 500 milliseconds
                    Thread.sleep(1000);

                    // Check if getDepth method hasn't been called within 200ms
                    if (!Thread.currentThread().isInterrupted()) {
                        // Trigger emergency surface event
                        emergencySurface();
                    }
                } catch (InterruptedException e) {
                    // Thread interrupted, exit the loop
                    break;
                }
            }
        }
    }
    // Method to trigger emergency surface event
    private void emergencySurface() {
    	log.info("emergencySurface");
        setFillTank(false);
    }
}
