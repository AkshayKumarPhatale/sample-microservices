package com.accenture.empportal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class Sample2Controller {
	
	private static Logger logger = LoggerFactory.getLogger(Sample2Controller.class);
	
	@GetMapping("/message")
	public ResponseEntity<String> getMessage() {
		logger.info("from sample-1 service");
		
		return new ResponseEntity<String>("hello", HttpStatus.OK);
		
	}

}
