package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.model.request.EstudioRequest;
import co.edu.javeriana.as.personapp.model.response.EstudioResponse;

@Mapper
public class EstudioMapperRest {

	public EstudioResponse fromDomainToAdapterRest(Study study, String database) {
		Person person = study.getPerson();
		Profession profession = study.getProfession();
		return new EstudioResponse(
				person.getIdentification() + "",
				person.getFirstName() + " " + person.getLastName(),
				profession.getIdentification() + "",
				profession.getName(),
				study.getGraduationDate() != null ? study.getGraduationDate().toString() : null,
				study.getUniversityName(),
				database,
				"OK");
	}

	public Study fromAdapterToDomain(EstudioRequest request) {
		Person person = new Person();
		person.setIdentification(RestValidator.requiredInteger(request.getPersonId(), "personId"));
		Profession profession = new Profession();
		profession.setIdentification(RestValidator.requiredInteger(request.getProfessionId(), "professionId"));
		Study study = new Study();
		study.setPerson(person);
		study.setProfession(profession);
		study.setGraduationDate(RestValidator.optionalDate(request.getGraduationDate(), "graduationDate"));
		study.setUniversityName(request.getUniversityName());
		return study;
	}
}
