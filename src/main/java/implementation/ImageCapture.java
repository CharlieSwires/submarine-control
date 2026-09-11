package implementation;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ImageCapture {
	Logger log = LoggerFactory.getLogger(ImageCapture.class);

	public static enum Resolution {
		SMALL,
		HD,
		PHOTO;
	}
	public BufferedImage captureImage(Resolution resolution) {
//		try {
//			// Define the output file name
//			String outputFileName = "image.jpg";
//			Process process;
//			// Set the command to execute
//			switch (resolution) {
//			case HD:
//				log.info("before runner hd");
////				process = Runtime.getRuntime().exec("runner.sh");
////				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
////				String line = "";
////				while ((line = reader.readLine()) != null) {
////				    System.out.println(line);
////				}
////				BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
////				String errorLine = "";
////				while ((errorLine = errorReader.readLine()) != null) {
////				    System.err.println(errorLine);
////				}
////				log.info("runner hd");
//				break;
//			case SMALL:
//				log.info("before runner small");
////				process = Runtime.getRuntime().exec("runner.sh");
////				reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
////				line = "";
////				while ((line = reader.readLine()) != null) {
////				    System.out.println(line);
////				}
////				errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
////				errorLine = "";
////				while ((errorLine = errorReader.readLine()) != null) {
////				    System.err.println(errorLine);
////				}				
////				log.info("runner small");
//				break;
//			case PHOTO:
//				process = Runtime.getRuntime().exec("libcamera-still -o "+ outputFileName);
//				log.info("photo");
//				break;
//			default:
//				throw new RuntimeException("Not implemented:"+resolution.toString());
//
//			}
//
//
//			// Wait for the command to finish
//			if (resolution == Resolution.PHOTO) {
//				int exitVal = process.waitFor();
//				if(exitVal == 0) {
//					log.debug("Image captured successfully");
//
//					// Load the image from the file
//					File imageFile = new File(outputFileName);
//					BufferedImage image = ImageIO.read(imageFile);
//
//					// Return the loaded image
//					return image;
//				} else {
//					log.error("Image capture failed");
//				}
//			}
//		} catch (IOException | InterruptedException e) {
//			e.printStackTrace();
//		}

		// Return null if the image capture or loading failed
		return null;
	}
}
