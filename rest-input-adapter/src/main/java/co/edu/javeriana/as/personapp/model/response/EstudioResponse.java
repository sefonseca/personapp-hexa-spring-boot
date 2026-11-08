package co.edu.javeriana.as.personapp.model.response;

import co.edu.javeriana.as.personapp.model.request.EstudioRequest;

public class EstudioResponse extends EstudioRequest {

	private String personName;
	private String professionName;
	private String status;

	public EstudioResponse(String personId, String personName, String professionId, String professionName,
			String graduationDate, String universityName, String database, String status) {
		super(personId, professionId, graduationDate, universityName, database);
		this.personName = personName;
		this.professionName = professionName;
		this.status = status;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getProfessionName() {
		return professionName;
	}

	public void setProfessionName(String professionName) {
		this.professionName = professionName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
