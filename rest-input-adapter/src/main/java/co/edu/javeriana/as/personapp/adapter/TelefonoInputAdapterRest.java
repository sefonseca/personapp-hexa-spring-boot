package co.edu.javeriana.as.personapp.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PhoneUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mapper.TelefonoMapperRest;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class TelefonoInputAdapterRest {

	@Autowired
	@Qualifier("phoneOutputAdapterMaria")
	private PhoneOutputPort phoneOutputPortMaria;

	@Autowired
	@Qualifier("phoneOutputAdapterMongo")
	private PhoneOutputPort phoneOutputPortMongo;

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private TelefonoMapperRest telefonoMapperRest;

	private PhoneInputPort setPhoneOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption == null) {
			throw new InvalidOptionException("The database option is required (MARIA or MONGO)");
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			return new PhoneUseCase(phoneOutputPortMaria, personOutputPortMaria);
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			return new PhoneUseCase(phoneOutputPortMongo, personOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	private String databaseName(String dbOption) {
		return dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString()) ? "MariaDB" : "MongoDB";
	}

	public List<TelefonoResponse> historial(String database) throws InvalidOptionException {
		log.info("Into historial TelefonoEntity in Input Adapter");
		PhoneInputPort phoneInputPort = setPhoneOutputPortInjection(database);
		return phoneInputPort.findAll().stream()
				.map(phone -> telefonoMapperRest.fromDomainToAdapterRest(phone, databaseName(database)))
				.collect(Collectors.toList());
	}

	public TelefonoResponse buscarTelefono(String database, String number)
			throws InvalidOptionException, NoExistException {
		PhoneInputPort phoneInputPort = setPhoneOutputPortInjection(database);
		return telefonoMapperRest.fromDomainToAdapterRest(phoneInputPort.findOne(number), databaseName(database));
	}

	public TelefonoResponse crearTelefono(TelefonoRequest request)
			throws InvalidOptionException, AlreadyExistException, NoExistException {
		PhoneInputPort phoneInputPort = setPhoneOutputPortInjection(request.getDatabase());
		Phone phone = phoneInputPort.create(telefonoMapperRest.fromAdapterToDomain(request));
		return telefonoMapperRest.fromDomainToAdapterRest(phone, databaseName(request.getDatabase()));
	}

	public TelefonoResponse editarTelefono(String database, String number, TelefonoRequest request)
			throws InvalidOptionException, NoExistException {
		PhoneInputPort phoneInputPort = setPhoneOutputPortInjection(database);
		request.setNumber(number);
		Phone phone = phoneInputPort.edit(number, telefonoMapperRest.fromAdapterToDomain(request));
		return telefonoMapperRest.fromDomainToAdapterRest(phone, databaseName(database));
	}

	public Boolean eliminarTelefono(String database, String number) throws InvalidOptionException, NoExistException {
		return setPhoneOutputPortInjection(database).drop(number);
	}

	public Integer contarTelefonos(String database) throws InvalidOptionException {
		return setPhoneOutputPortInjection(database).count();
	}
}
