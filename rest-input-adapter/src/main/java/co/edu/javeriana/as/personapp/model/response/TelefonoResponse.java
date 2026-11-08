package co.edu.javeriana.as.personapp.model.response;

import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;

public class TelefonoResponse extends TelefonoRequest {

	private String ownerName;
	private String status;

	public TelefonoResponse(String number, String company, String ownerId, String ownerName, String database,
			String status) {
		super(number, company, ownerId, database);
		this.ownerName = ownerName;
		this.status = status;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
