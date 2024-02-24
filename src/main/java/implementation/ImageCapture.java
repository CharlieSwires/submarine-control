package implementation;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ImageCapture {


	public Image captureImage() {
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
				System.out.println("Image captured successfully");

				// Load the image from the file
				File imageFile = new File(outputFileName);
				Image image = ImageIO.read(imageFile);

				// Return the loaded image
				return image;
			} else {
				System.out.println("Image capture failed");
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		// Return null if the image capture or loading failed
		return null;
	}

}
