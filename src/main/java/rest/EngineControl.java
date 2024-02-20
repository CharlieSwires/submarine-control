package rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import implementation.Eng;

@RestController
@RequestMapping(path = "/engine")
public class EngineControl {

	@Autowired
	private Eng eng;
	
    @GetMapping(path = "/left/{percentPower}")
    public ResponseEntity<Integer> getLeft (@PathVariable("percentPower") Integer percentPower){
		return new ResponseEntity<Integer>(eng.setPowerLeft(percentPower),HttpStatus.OK);
    }
    @GetMapping(path = "/right/{percentPower}")
    public ResponseEntity<Integer> getRudder (@PathVariable("percentPower") Integer percentPower){
		return new ResponseEntity<Integer>(eng.setPowerRight(percentPower),HttpStatus.OK);
    }

}
