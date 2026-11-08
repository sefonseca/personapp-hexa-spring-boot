package co.edu.javeriana.as.personapp.controller;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.response.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Traduce las excepciones del dominio/aplicacion a codigos HTTP. Antes la API
 * respondia siempre 200 "OK" (o null) aunque la operacion fallara.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NoExistException.class)
	public ResponseEntity<Response> handleNoExist(NoExistException e) {
		return build(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(AlreadyExistException.class)
	public ResponseEntity<Response> handleAlreadyExist(AlreadyExistException e) {
		return build(HttpStatus.CONFLICT, e.getMessage());
	}

	@ExceptionHandler({ InvalidOptionException.class, IllegalArgumentException.class,
			MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class })
	public ResponseEntity<Response> handleBadRequest(Exception e) {
		return build(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Response> handleDataIntegrity(DataIntegrityViolationException e) {
		return build(HttpStatus.CONFLICT, "Data integrity violation: " + e.getMostSpecificCause().getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response> handleGeneric(Exception e) {
		log.error("Unexpected error", e);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
	}

	private ResponseEntity<Response> build(HttpStatus status, String message) {
		log.warn("{} -> {}", status, message);
		return ResponseEntity.status(status).body(new Response(status.getReasonPhrase(), message, LocalDateTime.now()));
	}
}
