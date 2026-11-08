package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.terminal.model.EstudioModelCli;

@Mapper
public class EstudioMapperCli {

	public EstudioModelCli fromDomainToAdapterCli(Study study) {
		Person person = study.getPerson();
		Profession profession = study.getProfession();
		return new EstudioModelCli(person.getIdentification(), person.getFirstName() + " " + person.getLastName(),
				profession.getIdentification(), profession.getName(), study.getGraduationDate(),
				study.getUniversityName());
	}

	public Study fromAdapterCliToDomain(EstudioModelCli estudioModelCli) {
		Person person = new Person();
		person.setIdentification(estudioModelCli.getCcPersona());
		Profession profession = new Profession();
		profession.setIdentification(estudioModelCli.getIdProfesion());
		Study study = new Study();
		study.setPerson(person);
		study.setProfession(profession);
		study.setGraduationDate(estudioModelCli.getFechaGrado());
		study.setUniversityName(estudioModelCli.getUniversidad());
		return study;
	}
}
