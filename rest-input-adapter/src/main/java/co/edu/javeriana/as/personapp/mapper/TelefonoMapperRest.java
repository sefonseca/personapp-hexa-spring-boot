package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;

@Mapper
public class TelefonoMapperRest {

	public TelefonoResponse fromDomainToAdapterRest(Phone phone, String database) {
		Person owner = phone.getOwner();
		return new TelefonoResponse(
				phone.getNumber(),
				phone.getCompany(),
				owner != null ? owner.getIdentification() + "" : null,
				owner != null ? owner.getFirstName() + " " + owner.getLastName() : null,
				database,
				"OK");
	}

	public Phone fromAdapterToDomain(TelefonoRequest request) {
		Person owner = new Person();
		owner.setIdentification(RestValidator.requiredInteger(request.getOwnerId(), "ownerId"));
		Phone phone = new Phone();
		phone.setNumber(RestValidator.requiredText(request.getNumber(), "number"));
		phone.setCompany(RestValidator.requiredText(request.getCompany(), "company"));
		phone.setOwner(owner);
		return phone;
	}
}
