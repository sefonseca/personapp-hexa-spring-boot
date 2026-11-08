package co.edu.javeriana.as.personapp.terminal.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;

@Mapper
public class TelefonoMapperCli {

	public TelefonoModelCli fromDomainToAdapterCli(Phone phone) {
		Person owner = phone.getOwner();
		return new TelefonoModelCli(phone.getNumber(), phone.getCompany(),
				owner != null ? owner.getIdentification() : null,
				owner != null ? owner.getFirstName() + " " + owner.getLastName() : null);
	}

	public Phone fromAdapterCliToDomain(TelefonoModelCli telefonoModelCli) {
		Person owner = new Person();
		owner.setIdentification(telefonoModelCli.getCcDuenio());
		Phone phone = new Phone();
		phone.setNumber(telefonoModelCli.getNumero());
		phone.setCompany(telefonoModelCli.getOperador());
		phone.setOwner(owner);
		return phone;
	}
}
