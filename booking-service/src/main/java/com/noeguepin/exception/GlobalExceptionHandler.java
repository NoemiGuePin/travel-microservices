package com.noeguepin.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
		
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());	
		problemDetail.setTitle("Resource Not Found");
		problemDetail.setProperty("resource", ex.getResource());
		problemDetail.setProperty("field", ex.getField());
		problemDetail.setProperty("value", ex.getValue());
		
		return problemDetail;
	}
	
	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ProblemDetail handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
		
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());	
		problemDetail.setTitle("Resource Already Exists");
		problemDetail.setProperty("resource", ex.getResource());
		problemDetail.setProperty("field", ex.getField());
		problemDetail.setProperty("value", ex.getValue());
		
		return problemDetail;
	}
	
	@ExceptionHandler(SeatsException.class)
	public ProblemDetail handleSeatsUnavailableException(SeatsException ex) {
		
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
		problemDetail.setTitle("Seat Rule Violation");
		problemDetail.setProperty("errorCode", ex.getErrorCode().name());
		problemDetail.setDetail(ex.getMessage());
		
		return problemDetail;
	}
     
}
