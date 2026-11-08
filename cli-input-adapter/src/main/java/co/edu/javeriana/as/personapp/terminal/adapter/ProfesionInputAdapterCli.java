package co.edu.javeriana.as.personapp.terminal.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.terminal.mapper.ProfesionMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class ProfesionInputAdapterCli {

	@Autowired
	@Qualifier("professionOutputAdapterMaria")
	private ProfessionOutputPort professionOutputPortMaria;

	@Autowired
	@Qualifier("professionOutputAdapterMongo")
	private ProfessionOutputPort professionOutputPortMongo;

	@Autowired
	private ProfesionMapperCli profesionMapperCli;

	ProfessionInputPort professionInputPort;

	public void setProfessionOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public void historial() {
		log.info("Into historial ProfesionEntity in Input Adapter");
		professionInputPort.findAll().stream().map(profesionMapperCli::fromDomainToAdapterCli)
				.forEach(System.out::println);
	}

	public void buscar(Integer id) {
		try {
			System.out.println(profesionMapperCli.fromDomainToAdapterCli(professionInputPort.findOne(id)));
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void crear(ProfesionModelCli profesion) {
		try {
			System.out.println("Creada: " + profesionMapperCli.fromDomainToAdapterCli(
					professionInputPort.create(profesionMapperCli.fromAdapterCliToDomain(profesion))));
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void editar(ProfesionModelCli profesion) {
		try {
			System.out.println("Editada: " + profesionMapperCli.fromDomainToAdapterCli(professionInputPort
					.edit(profesion.getId(), profesionMapperCli.fromAdapterCliToDomain(profesion))));
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void eliminar(Integer id) {
		try {
			System.out.println("Eliminada: " + professionInputPort.drop(id));
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public void contar() {
		System.out.println("Total de profesiones: " + professionInputPort.count());
	}
}
