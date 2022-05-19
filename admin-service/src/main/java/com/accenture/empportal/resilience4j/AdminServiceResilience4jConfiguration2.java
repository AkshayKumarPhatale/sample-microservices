package com.accenture.empportal.resilience4j;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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
	private WebClient webClient;
	
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
	public Mono<Employee> findByEmpId(Long id) {

		ServiceInstance employeeServiceinstance = loadbalancer.choose("EMPLOYEE-SERVICE");
		String employeeServiceURI = employeeServiceinstance.getUri().toString();
		ServiceInstance departmentServiceInstance = loadbalancer.choose("DEPARTMENT-SERVICE");
		String departmentServiceURI = departmentServiceInstance.getUri().toString();
		
		logger.info( "Request from uri " + employeeServiceURI);
		logger.info( "Request from uri " + departmentServiceURI);
		
				
		return CompletableFuture.supplyAsync(() -> {
			logger.info( "Request from service " + id);

			return webClient.get().uri(employeeServiceURI+"/api/employee/" + id).retrieve()
					.onStatus(HttpStatus::is5xxServerError,
							error -> Mono.error(new EmployeeNotFoundException("Employee not Found with this id")))
					.onStatus(HttpStatus::is4xxClientError, error -> Mono.error(new RuntimeException("API not found")))
					.bodyToMono(Employee.class);

		}).thenApplyAsync(

				(employeedto) -> {
					Employee emp = employeedto.block();
					logger.info( "Response from employee service" + emp);
					BeanUtils.copyProperties(emp, employee);
					Department dept = webClient.get().uri(departmentServiceURI+"/api/department/" + emp.getDeptId())
							.retrieve()
							.onStatus(HttpStatus::is4xxClientError, error -> Mono.error(new RuntimeException("API not found")))
							.onStatus(HttpStatus::is5xxServerError,
									error -> Mono.error(
											new DepartmentNotFoundException("Department not Found with this id")))
							.bodyToMono(Department.class).block();

					BeanUtils.copyProperties(dept, department);
					employee.setDept(department);
					logger.info( " Final Response" + emp);
					return Mono.just(employee);

				}).join();

	}

	public Mono<Employee> fallbackForFindByEmpId(Exception e) {
		logger.info("inside fallbackForFindByEmpId" + e.getLocalizedMessage());
		return Mono.error(new ServerException(e.getLocalizedMessage()));
	}

	public Mono<Employee> fallbackForRateLimiter(Exception e) {
		logger.info( "inside rateLimiterfallback method" + e.getLocalizedMessage());
		return Mono.error(new ServerException("Server Exception " + e.getLocalizedMessage()));
	}

	@CircuitBreaker(name = "findAllCircuitBreaker", fallbackMethod = "fallbackForfindAll")
	public Flux<Employee> findAll() {
		logger.info( "From findAll method");
		return webClient.get().uri("http://localhost:2020/api/employee/findall").retrieve()
				.onStatus(HttpStatus::is4xxClientError, error -> Mono.error(new RuntimeException("API not found")))
				.onStatus(HttpStatus::is5xxServerError,
						error -> Mono.error(new NoEmployeesFoundException("No Employees found")))
				.bodyToFlux(Employee.class);

	}

	public Flux<Employee> fallbackForfindAll(Throwable e) {
		logger.info( "From fallbackForfindAll method" + e.getLocalizedMessage());
		Flux<Employee> f = Flux.error(new ServerException("Server Exception" + e.getLocalizedMessage()));

		return f;
	}

}
