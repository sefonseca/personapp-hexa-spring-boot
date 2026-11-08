package co.edu.javeriana.as.personapp.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.StudyUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mapper.EstudioMapperRest;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class EstudioInputAdapterRest {

	@Autowired
	@Qualifier("studyOutputAdapterMaria")
	private StudyOutputPort studyOutputPortMaria;

	@Autowired
	@Qualifier("studyOutputAdapterMongo")
	private StudyOutputPort studyOutputPortMongo;

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	@Qualifier("professionOutputAdapterMaria")
	private ProfessionOutputPort professionOutputPortMaria;

	@Autowired
	@Qualifier("professionOutputAdapterMongo")
	private ProfessionOutputPort professionOutputPortMongo;

	@Autowired
	private EstudioMapperRest estudioMapperRest;

	private StudyInputPort setStudyOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption == null) {
			throw new InvalidOptionException("The database option is required (MARIA or MONGO)");
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			return new StudyUseCase(studyOutputPortMaria, personOutputPortMaria, professionOutputPortMaria);
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			return new StudyUseCase(studyOutputPortMongo, personOutputPortMongo, professionOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	private String databaseName(String dbOption) {
		return dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString()) ? "MariaDB" : "MongoDB";
	}

	public List<EstudioResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial EstudiosEntity in Input Adapter");
		StudyInputPort studyInputPort = setStudyOutputPortInjection(database);
		return studyInputPort.findAll().stream()
				.map(study -> estudioMapperRest.fromDomainToAdapterRest(study, databaseName(database)))
				.collect(Collectors.toList());
	}

	public EstudioResponse buscarEstudio(String database, Integer personId, Integer professionId)
			throws InvalidOptionException, NoExistException {
		StudyInputPort studyInputPort = setStudyOutputPortInjection(database);
		return estudioMapperRest.fromDomainToAdapterRest(studyInputPort.findOne(personId, professionId),
				databaseName(database));
	}

	public EstudioResponse crearEstudio(EstudioRequest request)
			throws InvalidOptionException, AlreadyExistException, NoExistException {
		StudyInputPort studyInputPort = setStudyOutputPortInjection(request.getDatabase());
		Study study = studyInputPort.create(estudioMapperRest.fromAdapterToDomain(request));
		return estudioMapperRest.fromDomainToAdapterRest(study, databaseName(request.getDatabase()));
	}

	public EstudioResponse editarEstudio(String database, Integer personId, Integer professionId,
			EstudioRequest request) throws InvalidOptionException, NoExistException {
		StudyInputPort studyInputPort = setStudyOutputPortInjection(database);
		request.setPersonId(personId + "");
		request.setProfessionId(professionId + "");
		Study study = studyInputPort.edit(personId, professionId, estudioMapperRest.fromAdapterToDomain(request));
		return estudioMapperRest.fromDomainToAdapterRest(study, databaseName(database));
	}

	public Boolean eliminarEstudio(String database, Integer personId, Integer professionId)
			throws InvalidOptionException, NoExistException {
		return setStudyOutputPortInjection(database).drop(personId, professionId);
	}

	public Integer contarEstudios(String database) throws InvalidOptionException {
		return setStudyOutputPortInjection(database).count();
	}
}
