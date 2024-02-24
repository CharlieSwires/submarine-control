package rest;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import implementation.ImageCapture;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "image")
public class ImageCaptureControl {

    @Autowired
    private ImageCapture ic;

    @GetMapping(path = "capture", produces = "image/jpeg")
    public void getImage(HttpServletResponse response) {
        try {
            BufferedImage image = ic.captureImage(); // Assuming captureImage() returns a BufferedImage
            if (image != null) {
                response.setContentType("image/jpeg");
                ImageIO.write(image, "JPEG", response.getOutputStream());
                response.getOutputStream().close();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
