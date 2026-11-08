package co.edu.javeriana.as.personapp.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.mapper.PersonaMapperRest;
import co.edu.javeriana.as.personapp.mapper.TelefonoMapperRest;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PersonaInputAdapterRest {

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private PersonaMapperRest personaMapperRest;

	@Autowired
	private TelefonoMapperRest telefonoMapperRest;

	@Autowired
	private EstudioMapperRest estudioMapperRest;

	/**
	 * Antes el caso de uso se guardaba en un atributo del adaptador (singleton),
	 * por lo que dos peticiones simultaneas a bases distintas se pisaban entre si.
	 * Ahora se crea y se retorna en una variable local por peticion.
	 */
	private PersonInputPort setPersonOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption == null) {
			throw new InvalidOptionException("The database option is required (MARIA or MONGO)");
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			return new PersonUseCase(personOutputPortMaria);
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			return new PersonUseCase(personOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	private String databaseName(String dbOption) {
		return dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString()) ? "MariaDB" : "MongoDB";
	}

	public List<PersonaResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial PersonaEntity in Input Adapter");
		PersonInputPort personInputPort = setPersonOutputPortInjection(database);
		return personInputPort.findAll().stream()
				.map(person -> personaMapperRest.fromDomainToAdapterRest(person, databaseName(database)))
				.collect(Collectors.toList());
	}

	public PersonaResponse buscarPersona(String database, Integer identification)
			throws InvalidOptionException, NoExistException {
		PersonInputPort personInputPort = setPersonOutputPortInjection(database);
		return personaMapperRest.fromDomainToAdapterRest(personInputPort.findOne(identification),
				databaseName(database));
	}

	public PersonaResponse crearPersona(PersonaRequest request) throws InvalidOptionException, AlreadyExistException {
		log.info("Into crearPersona in Input Adapter");
		PersonInputPort personInputPort = setPersonOutputPortInjection(request.getDatabase());
		Person person = personInputPort.create(personaMapperRest.fromAdapterToDomain(request));
		return personaMapperRest.fromDomainToAdapterRest(person, databaseName(request.getDatabase()));
	}

	public PersonaResponse editarPersona(String database, Integer identification, PersonaRequest request)
			throws InvalidOptionException, NoExistException {
		PersonInputPort personInputPort = setPersonOutputPortInjection(database);
		// el identificador lo manda la URL, el del body se ignora
		request.setDni(identification + "");
		Person person = personInputPort.edit(identification, personaMapperRest.fromAdapterToDomain(request));
		return personaMapperRest.fromDomainToAdapterRest(person, databaseName(database));
	}

	public Boolean eliminarPersona(String database, Integer identification)
			throws InvalidOptionException, NoExistException {
		PersonInputPort personInputPort = setPersonOutputPortInjection(database);
		return personInputPort.drop(identification);
	}

	public Integer contarPersonas(String database) throws InvalidOptionException {
		return setPersonOutputPortInjection(database).count();
	}

	public List<TelefonoResponse> telefonosPersona(String database, Integer identification)
			throws InvalidOptionException, NoExistException {
		PersonInputPort personInputPort = setPersonOutputPortInjection(database);
		return personInputPort.getPhones(identification).stream()
				.map(phone -> telefonoMapperRest.fromDomainToAdapterRest(phone, databaseName(database)))
				.collect(Collectors.toList());
	}

	public List<EstudioResponse> estudiosPersona(String database, Integer identification)
			throws InvalidOptionException, NoExistException {
		PersonInputPort personInputPort = setPersonOutputPortInjection(database);
		return personInputPort.getStudies(identification).stream()
				.map(study -> estudioMapperRest.fromDomainToAdapterRest(study, databaseName(database)))
				.collect(Collectors.toList());
	}

}
