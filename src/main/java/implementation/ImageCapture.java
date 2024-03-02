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

	public static enum Resolution {
		SMALL,
		HD,
		PHOTO;
	}
	public BufferedImage captureImage(Resolution resolution) {
		try {
			// Define the output file name
			String outputFileName = "image.jpg";
			Process process;
			// Set the command to execute
			switch (resolution) {
			case HD:
				process = Runtime.getRuntime().exec("pkill vlc&");
				break;
			case SMALL:
				process = Runtime.getRuntime().exec("libcamera-vid -t 0 --width 640 --height 480 --codec yuv420 --framerate 10 --inline --listen -o - | cvlc -vvv stream:///dev/stdin --sout '#transcode{vcodec=h264,vb=800,scale=Auto,acodec=none}:rtp{sdp=rtsp://192.168.137.205:8554/}' --demux=rawvideo --rawvid-fps=10 --rawvid-width=640 --rawvid-height=480 --rawvid-chroma=I420&");
				break;
			case PHOTO:
				process = Runtime.getRuntime().exec("libcamera-still -o "+ outputFileName);
				break;
			default:
				throw new RuntimeException("Not implemented:"+resolution.toString());

			}


			// Wait for the command to finish
			if (resolution == Resolution.PHOTO) {
				int exitVal = process.waitFor();
				if(exitVal == 0) {
					log.debug("Image captured successfully");

					// Load the image from the file
					File imageFile = new File(outputFileName);
					BufferedImage image = ImageIO.read(imageFile);

					// Return the loaded image
					return image;
				} else {
					log.error("Image capture failed");
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		// Return null if the image capture or loading failed
		return null;
	}
}
