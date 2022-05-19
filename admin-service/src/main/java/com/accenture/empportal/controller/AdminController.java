package com.accenture.empportal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accenture.empportal.entity.Employee;
import com.accenture.empportal.service.IAdminService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(value = "/api/admin")
@Api(value = "Admin API", tags = { "Admin Controller" })
public class AdminController {
	@Autowired
	private IAdminService iAdminService;
	
	//private static final Logger logger = Logger.getLogger(AdminServiceResilience4jConfiguration2.class.getName());
	private static Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@GetMapping("/{id}")
	@ApiOperation(value = "get emp details", notes = "This method is for get emp object to the db")
	public ResponseEntity<Mono<Employee>> findByEmpId(@NotNull @PathVariable(value = "id") Long id) {
		logger.info("Admin api called findByEmpId"+id);
		Mono<Employee> emp=iAdminService.findEmpById(id);
		logger.info( "Response" + emp);
		return new ResponseEntity<Mono<Employee>>(emp, HttpStatus.OK);
		
	}
	
	

	@GetMapping("/findall")
	@ApiOperation(value = "get all employess", notes = "This method is for get All employees")
	public ResponseEntity<Flux<Employee>> findAll() {
		System.out.println("findall");
	
		Flux<Employee> listEmployees = iAdminService.findAll();

		return new ResponseEntity<Flux<Employee>>(listEmployees, HttpStatus.OK);

	
	
	}
}
