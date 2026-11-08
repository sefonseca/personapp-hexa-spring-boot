package co.edu.javeriana.as.personapp.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mapper.ProfesionMapperRest;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class ProfesionInputAdapterRest {

	@Autowired
	@Qualifier("professionOutputAdapterMaria")
	private ProfessionOutputPort professionOutputPortMaria;

	@Autowired
	@Qualifier("professionOutputAdapterMongo")
	private ProfessionOutputPort professionOutputPortMongo;

	@Autowired
	private ProfesionMapperRest profesionMapperRest;

	private ProfessionInputPort setProfessionOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption == null) {
			throw new InvalidOptionException("The database option is required (MARIA or MONGO)");
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			return new ProfessionUseCase(professionOutputPortMaria);
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			return new ProfessionUseCase(professionOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	private String databaseName(String dbOption) {
		return dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString()) ? "MariaDB" : "MongoDB";
	}

	public List<ProfesionResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial ProfesionEntity in Input Adapter");
		ProfessionInputPort professionInputPort = setProfessionOutputPortInjection(database);
		return professionInputPort.findAll().stream()
				.map(profession -> profesionMapperRest.fromDomainToAdapterRest(profession, databaseName(database)))
				.collect(Collectors.toList());
	}

	public ProfesionResponse buscarProfesion(String database, Integer identification)
			throws InvalidOptionException, NoExistException {
		ProfessionInputPort professionInputPort = setProfessionOutputPortInjection(database);
		return profesionMapperRest.fromDomainToAdapterRest(professionInputPort.findOne(identification),
				databaseName(database));
	}

	public ProfesionResponse crearProfesion(ProfesionRequest request)
			throws InvalidOptionException, AlreadyExistException {
		ProfessionInputPort professionInputPort = setProfessionOutputPortInjection(request.getDatabase());
		Profession profession = professionInputPort.create(profesionMapperRest.fromAdapterToDomain(request));
		return profesionMapperRest.fromDomainToAdapterRest(profession, databaseName(request.getDatabase()));
	}

	public ProfesionResponse editarProfesion(String database, Integer identification, ProfesionRequest request)
			throws InvalidOptionException, NoExistException {
		ProfessionInputPort professionInputPort = setProfessionOutputPortInjection(database);
		request.setId(identification + "");
		Profession profession = professionInputPort.edit(identification,
				profesionMapperRest.fromAdapterToDomain(request));
		return profesionMapperRest.fromDomainToAdapterRest(profession, databaseName(database));
	}

	public Boolean eliminarProfesion(String database, Integer identification)
			throws InvalidOptionException, NoExistException {
		return setProfessionOutputPortInjection(database).drop(identification);
	}

	public Integer contarProfesiones(String database) throws InvalidOptionException {
		return setProfessionOutputPortInjection(database).count();
	}
}
