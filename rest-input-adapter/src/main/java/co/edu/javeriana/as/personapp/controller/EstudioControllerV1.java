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

import co.edu.javeriana.as.personapp.adapter.EstudioInputAdapterRest;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/estudio")
@Tag(name = "Estudio", description = "CRUD de estudios (persona + profesion) en MariaDB o MongoDB")
public class EstudioControllerV1 {

	@Autowired
	private EstudioInputAdapterRest estudioInputAdapterRest;

	@Operation(summary = "Lista todos los estudios de la base indicada (MARIA o MONGO)")
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EstudioResponse>> estudios(@PathVariable("database") String database)
			throws InvalidOptionException {
		log.info("Into estudios REST API");
		return ResponseEntity.ok(estudioInputAdapterRest.historial(database.toUpperCase()));
	}

	@Operation(summary = "Cuenta los estudios de la base indicada")
	@GetMapping(path = "/{database}/count", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> contarEstudios(@PathVariable("database") String database)
			throws InvalidOptionException {
		Integer total = estudioInputAdapterRest.contarEstudios(database.toUpperCase());
		return ResponseEntity.ok(new Response("OK", total + "", LocalDateTime.now()));
	}

	@Operation(summary = "Busca un estudio por cedula de la persona e id de la profesion")
	@GetMapping(path = "/{database}/{personId}/{professionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EstudioResponse> buscarEstudio(@PathVariable("database") String database,
			@PathVariable("personId") Integer personId, @PathVariable("professionId") Integer professionId)
			throws InvalidOptionException, NoExistException {
		return ResponseEntity.ok(estudioInputAdapterRest.buscarEstudio(database.toUpperCase(), personId, professionId));
	}

	@Operation(summary = "Crea un estudio; la persona y la profesion deben existir")
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EstudioResponse> crearEstudio(@RequestBody EstudioRequest request)
			throws InvalidOptionException, AlreadyExistException, NoExistException {
		return ResponseEntity.status(HttpStatus.CREATED).body(estudioInputAdapterRest.crearEstudio(request));
	}

	@Operation(summary = "Edita la fecha de grado y la universidad de un estudio")
	@PutMapping(path = "/{database}/{personId}/{professionId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EstudioResponse> editarEstudio(@PathVariable("database") String database,
			@PathVariable("personId") Integer personId, @PathVariable("professionId") Integer professionId,
			@RequestBody EstudioRequest request) throws InvalidOptionException, NoExistException {
		return ResponseEntity
				.ok(estudioInputAdapterRest.editarEstudio(database.toUpperCase(), personId, professionId, request));
	}

	@Operation(summary = "Elimina un estudio")
	@DeleteMapping(path = "/{database}/{personId}/{professionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> eliminarEstudio(@PathVariable("database") String database,
			@PathVariable("personId") Integer personId, @PathVariable("professionId") Integer professionId)
			throws InvalidOptionException, NoExistException {
		Boolean deleted = estudioInputAdapterRest.eliminarEstudio(database.toUpperCase(), personId, professionId);
		return ResponseEntity.ok(new Response("OK",
				"Study " + personId + "-" + professionId + " deleted: " + deleted, LocalDateTime.now()));
	}
}
