package rest;

import java.awt.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import implementation.ImageCapture;


@RestController
@RequestMapping(path = "image")
public class ImageCapureControl {

	@Autowired
	private ImageCapture ic;

	@GetMapping(path = "capture", consumes = "application/json", produces = "image/jpeg")
	public ResponseEntity<Image> getImage (){
		return new ResponseEntity<Image>(ic.captureImage(),HttpStatus.OK);
	}
}
