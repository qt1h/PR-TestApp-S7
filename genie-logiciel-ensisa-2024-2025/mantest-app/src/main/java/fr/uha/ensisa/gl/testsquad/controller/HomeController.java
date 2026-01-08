package fr.uha.ensisa.gl.testsquad.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

	@GetMapping(value="/")
	public ResponseEntity<String> index(){
		return ResponseEntity.status(HttpStatus.SEE_OTHER)
				.header("Location", "./home").build();
	}

	@GetMapping(value="/home")
	public ModelAndView home() {
        return new ModelAndView("home");
	}

}
