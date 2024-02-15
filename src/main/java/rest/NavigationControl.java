package rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import implementation.Nav;

@RestController
@RequestMapping(path = "/navigation")
public class NavigationControl {

	@Autowired
	private Nav nav;
	
    @GetMapping(path = "/bearing", produces = "application/json")
    public ResponseEntity<Integer> getBearing (){
		return new ResponseEntity<Integer>(nav.readBearing(),HttpStatus.OK);
    }
    @GetMapping(path = "/rudder/{angle}" ,produces = "application/json")
    public ResponseEntity<Integer> getRudder (@PathVariable("angle") Integer angle){
		return new ResponseEntity<Integer>(nav.setRudder(angle),HttpStatus.OK);
    }

}
