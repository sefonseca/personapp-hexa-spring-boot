package co.edu.javeriana.as.personapp.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudioRequest {
	private String personId;
	private String professionId;
	/** Formato ISO yyyy-MM-dd */
	private String graduationDate;
	private String universityName;
	private String database;
}
