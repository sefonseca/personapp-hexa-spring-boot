package co.edu.javeriana.as.personapp.mariadb.mapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Study;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.EstudiosEntityPK;

@Mapper
public class EstudiosMapperMaria {

	@Autowired
	private PersonaMapperMaria personaMapperMaria;

	@Autowired
	private ProfesionMapperMaria profesionMapperMaria;

	public EstudiosEntity fromDomainToAdapter(Study study) {
		EstudiosEntityPK estudioPK = new EstudiosEntityPK();
		estudioPK.setCcPer(study.getPerson().getIdentification());
		estudioPK.setIdProf(study.getProfession().getIdentification());
		EstudiosEntity estudio = new EstudiosEntity();
		estudio.setEstudiosPK(estudioPK);
		// Se asignan tambien las relaciones para que la entidad quede completa
		estudio.setPersona(personaMapperMaria.fromDomainToAdapterBasic(study.getPerson()));
		estudio.setProfesion(profesionMapperMaria.fromDomainToAdapterBasic(study.getProfession()));
		estudio.setFecha(validateFecha(study.getGraduationDate()));
		estudio.setUniver(validateUniver(study.getUniversityName()));
		return estudio;
	}

	private Date validateFecha(LocalDate graduationDate) {
		return graduationDate != null
				? Date.from(graduationDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
				: null;
	}

	private String validateUniver(String universityName) {
		return universityName != null ? universityName : "";
	}

	public Study fromAdapterToDomain(EstudiosEntity estudiosEntity) {
		Study study = new Study();
		study.setPerson(personaMapperMaria.fromAdapterToDomainBasic(estudiosEntity.getPersona()));
		study.setProfession(profesionMapperMaria.fromAdapterToDomainBasic(estudiosEntity.getProfesion()));
		study.setGraduationDate(validateGraduationDate(estudiosEntity.getFecha()));
		study.setUniversityName(validateUniversityName(estudiosEntity.getUniver()));
		return study;
	}

	private LocalDate validateGraduationDate(Date fecha) {
		if (fecha == null)
			return null;
		// Hibernate entrega un java.sql.Date para @Temporal(DATE), y
		// java.sql.Date.toInstant() lanza UnsupportedOperationException
		if (fecha instanceof java.sql.Date sqlDate)
			return sqlDate.toLocalDate();
		return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	private String validateUniversityName(String univer) {
		return univer != null ? univer : "";
	}
}