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

import co.edu.javeriana.as.personapp.adapter.ProfesionInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/profesion")
@Tag(name = "Profesion", description = "CRUD de profesiones en MariaDB o MongoDB")
public class ProfesionControllerV1 {

	@Autowired
	private ProfesionInputAdapterRest profesionInputAdapterRest;

	@Operation(summary = "Lista todas las profesiones de la base indicada (MARIA o MONGO)")
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ProfesionResponse>> profesiones(@PathVariable("database") String database)
			throws InvalidOptionException {
		log.info("Into profesiones REST API");
		return ResponseEntity.ok(profesionInputAdapterRest.historial(database.toUpperCase()));
	}

	@Operation(summary = "Cuenta las profesiones de la base indicada")
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> contarProfesiones(@PathVariable("database") String database)
			throws InvalidOptionException {
		Integer total = profesionInputAdapterRest.contarProfesiones(database.toUpperCase());
		return ResponseEntity.ok(new Response("OK", total + "", LocalDateTime.now()));
	}

	@Operation(summary = "Busca una profesion por su id")
	@GetMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProfesionResponse> buscarProfesion(@PathVariable("database") String database,
			@PathVariable("id") Integer id) throws InvalidOptionException, NoExistException {
		return ResponseEntity.ok(profesionInputAdapterRest.buscarProfesion(database.toUpperCase(), id));
	}

	@Operation(summary = "Crea una profesion; la base va en el campo database del body")
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProfesionResponse> crearProfesion(@RequestBody ProfesionRequest request)
			throws InvalidOptionException, AlreadyExistException {
		return ResponseEntity.status(HttpStatus.CREATED).body(profesionInputAdapterRest.crearProfesion(request));
	}

	@Operation(summary = "Edita una profesion existente")
	@PutMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ProfesionResponse> editarProfesion(@PathVariable("database") String database,
			@PathVariable("id") Integer id, @RequestBody ProfesionRequest request)
			throws InvalidOptionException, NoExistException {
		return ResponseEntity.ok(profesionInputAdapterRest.editarProfesion(database.toUpperCase(), id, request));
	}

	@Operation(summary = "Elimina una profesion (y en cascada sus estudios)")
	@DeleteMapping(path = "/{database}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> eliminarProfesion(@PathVariable("database") String database,
			@PathVariable("id") Integer id) throws InvalidOptionException, NoExistException {
		Boolean deleted = profesionInputAdapterRest.eliminarProfesion(database.toUpperCase(), id);
		return ResponseEntity.ok(new Response("OK", "Profession " + id + " deleted: " + deleted, LocalDateTime.now()));
	}
}
