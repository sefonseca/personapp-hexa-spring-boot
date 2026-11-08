package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Gender;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;

@Mapper
public class PersonaMapperRest {
	
	public PersonaResponse fromDomainToAdapterRestMaria(Person person) {
		return fromDomainToAdapterRest(person, "MariaDB");
	}
	public PersonaResponse fromDomainToAdapterRestMongo(Person person) {
		return fromDomainToAdapterRest(person, "MongoDB");
	}
	
	public PersonaResponse fromDomainToAdapterRest(Person person, String database) {
		return new PersonaResponse(
				person.getIdentification()+"", 
				person.getFirstName(), 
				person.getLastName(), 
				person.getAge() != null ? person.getAge() + "" : null,
				person.getGender().toString(), 
				database,
				"OK");
	}

	/**
	 * Antes retornaba "new Person()" vacio (metodo sin implementar), por lo que el
	 * POST nunca podia crear una persona. Ahora convierte y valida cada campo.
	 */
	public Person fromAdapterToDomain(PersonaRequest request) {
		Person person = new Person();
		person.setIdentification(RestValidator.requiredInteger(request.getDni(), "dni"));
		person.setFirstName(RestValidator.requiredText(request.getFirstName(), "firstName"));
		person.setLastName(RestValidator.requiredText(request.getLastName(), "lastName"));
		person.setGender(toGender(request.getSex()));
		person.setAge(RestValidator.optionalInteger(request.getAge(), "age"));
		if (!person.isValidAge())
			throw new IllegalArgumentException("age must be greater or equal than 0");
		return person;
	}

	private Gender toGender(String sex) {
		String value = RestValidator.requiredText(sex, "sex").toUpperCase();
		switch (value) {
		case "M":
		case "MALE":
			return Gender.MALE;
		case "F":
		case "FEMALE":
			return Gender.FEMALE;
		default:
			throw new IllegalArgumentException("sex must be M or F");
		}
	}
		
}
