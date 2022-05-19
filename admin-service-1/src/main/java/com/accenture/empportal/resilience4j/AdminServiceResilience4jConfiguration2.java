package com.accenture.empportal.resilience4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.accenture.empportal.entity.Department;
import com.accenture.empportal.entity.Employee;
import com.accenture.empportal.exception.DepartmentNotFoundException;
import com.accenture.empportal.exception.EmployeeNotFoundException;
import com.accenture.empportal.exception.NoEmployeesFoundException;
import com.accenture.empportal.exception.ServerException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AdminServiceResilience4jConfiguration2 {

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	private LoadBalancerClient loadbalancer;

	// private static final ExecutorService executorService =
	// Executors.newCachedThreadPool();

	//private static final Logger logger = Logger.getLogger(AdminServiceResilience4jConfiguration2.class.getName());

	private static Logger logger = LoggerFactory.getLogger(AdminServiceResilience4jConfiguration2.class);
	Employee employee = new Employee();
	Department department = new Department();

	@CircuitBreaker(name = "findByEmpIdCircuitBreaker", fallbackMethod = "fallbackForFindByEmpId")
	@RateLimiter(name = "findByEmpIdRateLimiter", fallbackMethod = "fallbackForRateLimiter")
	public Employee findByEmpId(Long id) {

		ServiceInstance employeeServiceinstance = loadbalancer.choose("EMPLOYEE-SERVICE");
		String employeeServiceURI = employeeServiceinstance.getUri().toString();
		ServiceInstance departmentServiceInstance = loadbalancer.choose("DEPARTMENT-SERVICE");
		String departmentServiceURI = departmentServiceInstance.getUri().toString();
		Employee emp= restTemplate.getForObject(employeeServiceURI+"/api/employee/"+ id, Employee.class);
		
		Department dept = restTemplate.getForObject(departmentServiceURI+"/api/department/" + emp.getDeptId(), Department.class);
		emp.setDept(dept);
		
		return emp;
	
		

	}

	public Employee fallbackForFindByEmpId(Exception e) {
		logger.info("inside fallbackForFindByEmpId" + e.getLocalizedMessage());
		return null;
	}

	
}
