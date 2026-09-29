package com.rideshare.ridesharelite.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message, Map<String, String> errors) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now().toString());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		if (errors != null) {
			body.put("errors", errors);
		}
		return ResponseEntity.status(status).body(body);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> notFound(ResourceNotFoundException ex) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
	}

	@ExceptionHandler(BusinessRuleException.class)
	public ResponseEntity<Map<String, Object>> businessRule(BusinessRuleException ex) {
		return build(ex.getStatus(), ex.getMessage(), null);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
		return build(HttpStatus.BAD_REQUEST, "Validation failed", errors);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<Map<String, Object>> missingParam(MissingServletRequestParameterException ex) {
		return build(HttpStatus.BAD_REQUEST, "Missing required parameter: " + ex.getParameterName(), null);
	}

	@ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class })
	public ResponseEntity<Map<String, Object>> badInput(Exception ex) {
		return build(HttpStatus.BAD_REQUEST, "Malformed request: check field types and date format (yyyy-MM-ddTHH:mm)", null);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, Object>> integrity(DataIntegrityViolationException ex) {
		return build(HttpStatus.CONFLICT, "Operation violates a data constraint (for example a duplicate email)", null);
	}

	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	public ResponseEntity<Map<String, Object>> locking(ObjectOptimisticLockingFailureException ex) {
		return build(HttpStatus.CONFLICT, "The ride was modified by someone else. Please retry.", null);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> generic(Exception ex) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong on the server", null);
	}
}
