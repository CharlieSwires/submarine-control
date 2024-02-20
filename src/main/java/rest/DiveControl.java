package rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import implementation.Dive;

@RestController
@RequestMapping(path = "/dive")
public class DiveControl {

	@Autowired
	private Dive dive;
	
    @GetMapping(path = "/front/{angle}")
    public ResponseEntity<Integer> getFront (@PathVariable("angle") Integer angle){
		return new ResponseEntity<Integer>(dive.setFrontAngle(angle),HttpStatus.OK);
    }
    @GetMapping(path = "/back/{angle}")
    public ResponseEntity<Integer> getBack (@PathVariable("angle") Integer angle){
		return new ResponseEntity<Integer>(dive.setBackAngle(angle),HttpStatus.OK);
    }
    @GetMapping(path = "/fill-tank/true")
    public ResponseEntity<Integer> getFillTankTrue (){
		return new ResponseEntity<Integer>(dive.setFillTank(true),HttpStatus.OK);
    }
    @GetMapping(path = "/fill-tank/false")
    public ResponseEntity<Integer> getFillTankFalse (){
		return new ResponseEntity<Integer>(dive.setFillTank(false),HttpStatus.OK);
    }
    @GetMapping(path = "/dive-angle")
    public ResponseEntity<Integer> getDiveAngle (){
		return new ResponseEntity<Integer>(dive.getDiveAngle(),HttpStatus.OK);
    }
    @GetMapping(path = "/depth")
    public ResponseEntity<Integer> getDepth (){
		return new ResponseEntity<Integer>(dive.getDepth(),HttpStatus.OK);
    }

    
    
}
