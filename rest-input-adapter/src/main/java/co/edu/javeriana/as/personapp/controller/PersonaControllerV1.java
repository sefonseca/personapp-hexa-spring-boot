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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.PersonaInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/persona")
@Tag(name = "Persona", description = "CRUD de personas en MariaDB o MongoDB")
public class PersonaControllerV1 {
	
	@Autowired
	private PersonaInputAdapterRest personaInputAdapterRest;
	
	@ResponseBody
	@Operation(summary = "Lista todas las personas de la base indicada (MARIA o MONGO)")
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PersonaResponse> personas(@PathVariable("database") String database) throws InvalidOptionException {
		log.info("Into personas REST API");
		return personaInputAdapterRest.historial(database.toUpperCase());
	}

	@Operation(summary = "Cuenta las personas de la base indicada")
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> contarPersonas(@PathVariable("database") String database)
			throws InvalidOptionException {
		Integer total = personaInputAdapterRest.contarPersonas(database.toUpperCase());
		return ResponseEntity.ok(new Response("OK", total + "", LocalDateTime.now()));
	}

	@Operation(summary = "Busca una persona por su cedula")
	@GetMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PersonaResponse> buscarPersona(@PathVariable("database") String database,
			@PathVariable("id") Integer id) throws InvalidOptionException, NoExistException {
		return ResponseEntity.ok(personaInputAdapterRest.buscarPersona(database.toUpperCase(), id));
	}

	@Operation(summary = "Telefonos de una persona")
	@GetMapping(path = "/{database}/{id}/telefonos", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TelefonoResponse>> telefonosPersona(@PathVariable("database") String database,
			@PathVariable("id") Integer id) throws InvalidOptionException, NoExistException {
		return ResponseEntity.ok(personaInputAdapterRest.telefonosPersona(database.toUpperCase(), id));
	}

	@Operation(summary = "Estudios de una persona")
	@GetMapping(path = "/{database}/{id}/estudios", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EstudioResponse>> estudiosPersona(@PathVariable("database") String database,
			@PathVariable("id") Integer id) throws InvalidOptionException, NoExistException {
		return ResponseEntity.ok(personaInputAdapterRest.estudiosPersona(database.toUpperCase(), id));
	}
	
	@ResponseBody
	@Operation(summary = "Crea una persona; la base va en el campo database del body")
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PersonaResponse> crearPersona(@RequestBody PersonaRequest request)
			throws InvalidOptionException, AlreadyExistException {
		log.info("Into crearPersona REST API");
		return ResponseEntity.status(HttpStatus.CREATED).body(personaInputAdapterRest.crearPersona(request));
	}

	@Operation(summary = "Edita una persona existente")
	@PutMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PersonaResponse> editarPersona(@PathVariable("database") String database,
			@PathVariable("id") Integer id, @RequestBody PersonaRequest request)
			throws InvalidOptionException, NoExistException {
		return ResponseEntity.ok(personaInputAdapterRest.editarPersona(database.toUpperCase(), id, request));
	}

	@Operation(summary = "Elimina una persona (y en cascada sus telefonos y estudios)")
	@DeleteMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> eliminarPersona(@PathVariable("database") String database,
			@PathVariable("id") Integer id) throws InvalidOptionException, NoExistException {
		Boolean deleted = personaInputAdapterRest.eliminarPersona(database.toUpperCase(), id);
		return ResponseEntity.ok(new Response("OK", "Person " + id + " deleted: " + deleted, LocalDateTime.now()));
	}
}
