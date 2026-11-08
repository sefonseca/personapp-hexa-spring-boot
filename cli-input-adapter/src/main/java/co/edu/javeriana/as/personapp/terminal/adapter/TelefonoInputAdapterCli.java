package co.edu.javeriana.as.personapp.terminal.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PhoneUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class TelefonoInputAdapterCli {

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
	private TelefonoMapperCli telefonoMapperCli;

	PhoneInputPort phoneInputPort;

	public void setPhoneOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			phoneInputPort = new PhoneUseCase(phoneOutputPortMaria, personOutputPortMaria);
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			phoneInputPort = new PhoneUseCase(phoneOutputPortMongo, personOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public void historial() {
		log.info("Into historial TelefonoEntity in Input Adapter");
		phoneInputPort.findAll().stream().map(telefonoMapperCli::fromDomainToAdapterCli).forEach(System.out::println);
	}

	public void buscar(String numero) {
		try {
			System.out.println(telefonoMapperCli.fromDomainToAdapterCli(phoneInputPort.findOne(numero)));
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void crear(TelefonoModelCli telefono) {
		try {
			System.out.println("Creado: " + telefonoMapperCli
					.fromDomainToAdapterCli(phoneInputPort.create(telefonoMapperCli.fromAdapterCliToDomain(telefono))));
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void editar(TelefonoModelCli telefono) {
		try {
			System.out.println("Editado: " + telefonoMapperCli.fromDomainToAdapterCli(
					phoneInputPort.edit(telefono.getNumero(), telefonoMapperCli.fromAdapterCliToDomain(telefono))));
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void eliminar(String numero) {
		try {
			System.out.println("Eliminado: " + phoneInputPort.drop(numero));
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void contar() {
		System.out.println("Total de telefonos: " + phoneInputPort.count());
	}
}
