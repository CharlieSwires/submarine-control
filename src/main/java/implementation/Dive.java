package implementation;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Dive {

    private WatchDog watchDogThread;
 
    public Dive() {
        // Initialize the watch dog thread
        watchDogThread = new WatchDog();
        watchDogThread.start();
    }

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
        // Stop the watch dog thread
        watchDogThread.interrupt();
        // Restart the watch dog thread
        watchDogThread = new WatchDog();
        watchDogThread.start();
        // TODO: Implement getDepth method logic here
        return null;
    }

    public Integer getDiveAngle() {
        // TODO: Implement getDiveAngle method logic here
        return null;
    }

    // Watch Dog thread class
    private class WatchDog extends Thread {
 
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Sleep for 200 milliseconds
                    Thread.sleep(200);

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
        setFillTank(false);
    }
}
