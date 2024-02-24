package implementation;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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


	public BufferedImage captureImage() {
		try {
			// Define the output file name
			String outputFileName = "image.jpg";

			// Create a ProcessBuilder
			ProcessBuilder builder = new ProcessBuilder();
			// Set the command to execute
			builder.command("libcamera-still", "-o", outputFileName);

			// Start the process
			Process process = builder.start();

			// Wait for the command to finish
			int exitVal = process.waitFor();
			if (exitVal == 0) {
				log.debug("Image captured successfully");

				// Load the image from the file
				File imageFile = new File(outputFileName);
				BufferedImage image = ImageIO.read(imageFile);

				// Return the loaded image
				return image;
			} else {
				log.error("Image capture failed");
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		// Return null if the image capture or loading failed
		return null;
	}

}
