package co.edu.javeriana.as.personapp.terminal.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.terminal.mapper.EstudioMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.PersonaMapperCli;
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.PersonaModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PersonaInputAdapterCli {

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private PersonaMapperCli personaMapperCli;

	@Autowired
	private TelefonoMapperCli telefonoMapperCli;

	@Autowired
	private EstudioMapperCli estudioMapperCli;

	PersonInputPort personInputPort;

	public void setPersonOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMaria);
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public void historial1() {
		log.info("Into historial PersonaEntity in Input Adapter");
		List<PersonaModelCli> persona = personInputPort.findAll().stream().map(personaMapperCli::fromDomainToAdapterCli)
					.collect(Collectors.toList());
		persona.forEach(p -> System.out.println(p.toString()));
	}
	public void historial() {
	    log.info("Into historial PersonaEntity in Input Adapter");
	    personInputPort.findAll().stream()
	        .map(personaMapperCli::fromDomainToAdapterCli)
	        .forEach(System.out::println);
	}

	public void buscar(Integer cc) {
		try {
			System.out.println(personaMapperCli.fromDomainToAdapterCli(personInputPort.findOne(cc)));
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void crear(PersonaModelCli persona) {
		try {
			System.out.println("Creada: " + personaMapperCli
					.fromDomainToAdapterCli(personInputPort.create(personaMapperCli.fromAdapterCliToDomain(persona))));
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void editar(PersonaModelCli persona) {
		try {
			System.out.println("Editada: " + personaMapperCli.fromDomainToAdapterCli(
					personInputPort.edit(persona.getCc(), personaMapperCli.fromAdapterCliToDomain(persona))));
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void eliminar(Integer cc) {
		try {
			System.out.println("Eliminada: " + personInputPort.drop(cc));
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void contar() {
		System.out.println("Total de personas: " + personInputPort.count());
	}

	public void telefonos(Integer cc) {
		try {
			personInputPort.getPhones(cc).stream().map(telefonoMapperCli::fromDomainToAdapterCli)
					.forEach(System.out::println);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void estudios(Integer cc) {
		try {
			personInputPort.getStudies(cc).stream().map(estudioMapperCli::fromDomainToAdapterCli)
					.forEach(System.out::println);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

}
