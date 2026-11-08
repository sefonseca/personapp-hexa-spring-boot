package co.edu.javeriana.as.personapp.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.TelefonoInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/telefono")
@Tag(name = "Telefono", description = "CRUD de telefonos en MariaDB o MongoDB")
public class TelefonoControllerV1 {

	@Autowired
	private TelefonoInputAdapterRest telefonoInputAdapterRest;

	@Operation(summary = "Lista todos los telefonos de la base indicada (MARIA o MONGO)")
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TelefonoResponse>> telefonos(@PathVariable("database") String database)
			throws InvalidOptionException {
		log.info("Into telefonos REST API");
		return ResponseEntity.ok(telefonoInputAdapterRest.historial(database.toUpperCase()));
	}

	@Operation(summary = "Cuenta los telefonos de la base indicada")
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> contarTelefonos(@PathVariable("database") String database)
			throws InvalidOptionException {
		Integer total = telefonoInputAdapterRest.contarTelefonos(database.toUpperCase());
		return ResponseEntity.ok(new Response("OK", total + "", LocalDateTime.now()));
	}

	@Operation(summary = "Busca un telefono por su numero")
	@GetMapping(path = "/{database}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TelefonoResponse> buscarTelefono(@PathVariable("database") String database,
			@PathVariable("number") String number) throws InvalidOptionException, NoExistException {
		return ResponseEntity.ok(telefonoInputAdapterRest.buscarTelefono(database.toUpperCase(), number));
	}

	@Operation(summary = "Crea un telefono; el dueno (ownerId) debe existir")
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TelefonoResponse> crearTelefono(@RequestBody TelefonoRequest request)
			throws InvalidOptionException, AlreadyExistException, NoExistException {
		return ResponseEntity.status(HttpStatus.CREATED).body(telefonoInputAdapterRest.crearTelefono(request));
	}

	@Operation(summary = "Edita un telefono existente (operador y/o dueno)")
	@PutMapping(path = "/{database}/{number}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TelefonoResponse> editarTelefono(@PathVariable("database") String database,
			@PathVariable("number") String number, @RequestBody TelefonoRequest request)
			throws InvalidOptionException, NoExistException {
		return ResponseEntity.ok(telefonoInputAdapterRest.editarTelefono(database.toUpperCase(), number, request));
	}

	@Operation(summary = "Elimina un telefono")
	@DeleteMapping(path = "/{database}/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> eliminarTelefono(@PathVariable("database") String database,
			@PathVariable("number") String number) throws InvalidOptionException, NoExistException {
		Boolean deleted = telefonoInputAdapterRest.eliminarTelefono(database.toUpperCase(), number);
		return ResponseEntity.ok(new Response("OK", "Phone " + number + " deleted: " + deleted, LocalDateTime.now()));
	}
}
