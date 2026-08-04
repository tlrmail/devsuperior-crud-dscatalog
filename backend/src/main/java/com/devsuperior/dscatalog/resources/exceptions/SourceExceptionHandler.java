package com.devsuperior.dscatalog.resources.exceptions;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoudException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class SourceExceptionHandler {

	@ExceptionHandler(ResourceNotFoudException.class)
	public ResponseEntity<StandartError> entityNotFound(ResourceNotFoudException e, HttpServletRequest http){
		StandartError err = new StandartError();
		err.setTimestamp(Instant.now());
		err.setStatus(HttpStatus.NOT_FOUND.value());
		err.setError("Resource not found.");
		err.setMessage(e.getMessage());
		err.setPath(http.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
		
	}
	
}
