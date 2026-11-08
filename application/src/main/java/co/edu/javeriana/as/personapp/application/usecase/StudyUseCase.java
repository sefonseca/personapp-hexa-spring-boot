package co.edu.javeriana.as.personapp.application.usecase;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.StudyInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.StudyOutputPort;
import co.edu.javeriana.as.personapp.common.annotations.UseCase;
import co.edu.javeriana.as.personapp.common.exceptions.AlreadyExistException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.domain.Study;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
public class StudyUseCase implements StudyInputPort {

	private StudyOutputPort studyPersintence;

	private PersonOutputPort personPersintence;

	private ProfessionOutputPort professionPersintence;

	public StudyUseCase(@Qualifier("studyOutputAdapterMaria") StudyOutputPort studyPersintence,
			@Qualifier("personOutputAdapterMaria") PersonOutputPort personPersintence,
			@Qualifier("professionOutputAdapterMaria") ProfessionOutputPort professionPersintence) {
		this.studyPersintence = studyPersintence;
		this.personPersintence = personPersintence;
		this.professionPersintence = professionPersintence;
	}

	@Override
	public void setPersintence(StudyOutputPort studyPersintence, PersonOutputPort personPersintence,
			ProfessionOutputPort professionPersintence) {
		this.studyPersintence = studyPersintence;
		this.personPersintence = personPersintence;
		this.professionPersintence = professionPersintence;
	}

	@Override
	public Study create(Study study) throws AlreadyExistException, NoExistException {
		log.debug("Into create on Application Domain");
		Integer personId = study.getPerson().getIdentification();
		Integer professionId = study.getProfession().getIdentification();
		if (studyPersintence.findById(personId, professionId) != null)
			throw new AlreadyExistException("The study of person " + personId + " in profession " + professionId
					+ " already exists into db, cannot be created");
		study.setPerson(findPerson(personId));
		study.setProfession(findProfession(professionId));
		validateGraduationDate(study.getGraduationDate());
		return studyPersintence.save(study);
	}

	@Override
	public Study edit(Integer personIdentification, Integer professionIdentification, Study study)
			throws NoExistException {
		Study oldStudy = studyPersintence.findById(personIdentification, professionIdentification);
		if (oldStudy != null) {
			study.setPerson(oldStudy.getPerson());
			study.setProfession(oldStudy.getProfession());
			validateGraduationDate(study.getGraduationDate());
			return studyPersintence.save(study);
		}
		throw new NoExistException("The study of person " + personIdentification + " in profession "
				+ professionIdentification + " does not exist into db, cannot be edited");
	}

	@Override
	public Boolean drop(Integer personIdentification, Integer professionIdentification) throws NoExistException {
		Study oldStudy = studyPersintence.findById(personIdentification, professionIdentification);
		if (oldStudy != null)
			return studyPersintence.delete(personIdentification, professionIdentification);
		throw new NoExistException("The study of person " + personIdentification + " in profession "
				+ professionIdentification + " does not exist into db, cannot be dropped");
	}

	@Override
	public List<Study> findAll() {
		log.info("Output: " + studyPersintence.getClass());
		return studyPersintence.find();
	}

	@Override
	public Study findOne(Integer personIdentification, Integer professionIdentification) throws NoExistException {
		Study oldStudy = studyPersintence.findById(personIdentification, professionIdentification);
		if (oldStudy != null)
			return oldStudy;
		throw new NoExistException("The study of person " + personIdentification + " in profession "
				+ professionIdentification + " does not exist into db, cannot be found");
	}

	@Override
	public Integer count() {
		return findAll().size();
	}

	private Person findPerson(Integer identification) throws NoExistException {
		Person person = personPersintence.findById(identification);
		if (person == null)
			throw new NoExistException(
					"The person with id " + identification + " does not exist into db, cannot have a study");
		return person;
	}

	private Profession findProfession(Integer identification) throws NoExistException {
		Profession profession = professionPersintence.findById(identification);
		if (profession == null)
			throw new NoExistException(
					"The profession with id " + identification + " does not exist into db, cannot have a study");
		return profession;
	}

	private void validateGraduationDate(LocalDate graduationDate) {
		if (graduationDate != null && graduationDate.isAfter(LocalDate.now()))
			throw new IllegalArgumentException("The graduation date cannot be in the future");
	}
}
