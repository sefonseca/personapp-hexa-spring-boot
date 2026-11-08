package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;

@Mapper
public class ProfesionMapperCli {

	public ProfesionModelCli fromDomainToAdapterCli(Profession profession) {
		return new ProfesionModelCli(profession.getIdentification(), profession.getName(),
				profession.getDescription());
	}

	public Profession fromAdapterCliToDomain(ProfesionModelCli profesionModelCli) {
		Profession profession = new Profession();
		profession.setIdentification(profesionModelCli.getId());
		profession.setName(profesionModelCli.getNombre());
		profession.setDescription(profesionModelCli.getDescripcion());
		return profession;
	}
}
