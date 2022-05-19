package com.accenture.empportal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.accenture.empportal.entity.Employee;
import com.accenture.empportal.resilience4j.AdminServiceResilience4jConfiguration2;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AdminService implements IAdminService {
	
	  @Autowired
	  private AdminServiceResilience4jConfiguration2 adminServiceResilience4j;
	  private static Logger logger = LoggerFactory.getLogger(AdminService.class);
	
	 
	/* Multiple service call */
	@Override
	public Employee findEmpById(Long id) {
		logger.info("in service call");
		return adminServiceResilience4j.findByEmpId(id);
		
	}



	@Override
	public Flux<Employee> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
