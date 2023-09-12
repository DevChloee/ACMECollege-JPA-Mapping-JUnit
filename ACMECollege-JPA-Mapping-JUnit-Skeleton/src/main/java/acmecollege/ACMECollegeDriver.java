/***************************************************************************
 * File:  ACMECollegeDriver.java Course materials (23W) CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @date August 28, 2022
 * 
 * @author Mike Norman
 * @date 2020 04
 */
package acmecollege;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.entity.AcademicStudentClub;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.CourseRegistrationPK;
import acmecollege.entity.DurationAndStatus;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.NonAcademicStudentClub;
import acmecollege.entity.Professor;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;

/**
 * Used as starting point of application to simply create the DB on server or refresh it if needs be.
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @version August 28, 2022
 */
public class ACMECollegeDriver {

	protected static final Logger LOG = LogManager.getLogger();

	public static final String PERSISTENCE_UNIT = "acmecollege-PU";

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		EntityManager em = emf.createEntityManager();
		// Two methods are just to check we can create and read for all tables.
		// They can be commented out if not needed.
		addSampleData(em);
		printData(em);
		cleanData(em);
		em.close();
		emf.close();
	}
	
	private static void cleanData(EntityManager em) {
		/* Clean up data:  Instead of removing the schema, remove all the entities
		 * 
		 * JPQL has a String-based DELETE query, but it gets complicated when an entity
		 * has a composite primary key, like CourseRegistration with CourseRegistrationPK.  The JPQL look like:
		 *   DELETE from cr CourseRegistration cr WHERE cr.courseId = :x and cr.studentId = y
		 * If the WHERE clause gets more complicated, ... not nice!
		 * 
		 * We use the CriteriaBuilder API ... it also is complicated, but the benefit
		 * is that once you figure it out, changing the query later doesn't increase
		 * the complexity
		 * 
		 */
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaDelete<CourseRegistration> q1 = cb.createCriteriaDelete(CourseRegistration.class);
		q1.from(CourseRegistration.class);
		CriteriaDelete<Course> q2 = cb.createCriteriaDelete(Course.class);
		q2.from(Course.class);
		CriteriaDelete<Professor> q3 = cb.createCriteriaDelete(Professor.class);
		q3.from(Professor.class);
		CriteriaDelete<MembershipCard> q4 = cb.createCriteriaDelete(MembershipCard.class);
		q4.from(MembershipCard.class);
		CriteriaDelete<ClubMembership> q5 = cb.createCriteriaDelete(ClubMembership.class);
		q5.from(ClubMembership.class);
		CriteriaDelete<StudentClub> q6 = cb.createCriteriaDelete(StudentClub.class);
		q6.from(StudentClub.class);
		CriteriaDelete<Student> q7 = cb.createCriteriaDelete(Student.class);
		q7.from(Student.class);

		EntityTransaction et = em.getTransaction();
		et.begin();
		em.createQuery(q1).executeUpdate();
		em.createQuery(q2).executeUpdate();
		em.createQuery(q3).executeUpdate();
		em.createQuery(q4).executeUpdate();
		em.createQuery(q5).executeUpdate();
		em.createQuery(q6).executeUpdate();
		em.createQuery(q7).executeUpdate();
		et.commit();
		
		em.close();
	}

	private static void printData(EntityManager em) {

		Student s = em.find(Student.class, 1);
		int sizeSCourseRegistrations = s.getCourseRegistrations().size();
		int sizeMembershipCards = s.getMembershipCards().size();

		MembershipCard mc = em.find(MembershipCard.class, 1);
		int ownerId = mc.getOwner().getId();
		int clubMembershipId = mc.getClubMembership().getId();

		ClubMembership cm = em.find(ClubMembership.class, 1);
		int clubId = cm.getStudentClub().getId();
		int cardId = cm.getCard().getId();

		StudentClub sc = em.find(StudentClub.class, 1);
		int sizeClubMemberships = sc.getClubMemberships().size();

		Course c = em.find(Course.class, 1);
		int sizeCCourseRegistrations = c.getCourseRegistrations().size();

		Professor p = em.find(Professor.class, 1);
		int sizePCourseRegistrations = p.getCourseRegistrations().size();

		CourseRegistration cr = em.find(CourseRegistration.class, new CourseRegistrationPK(1, 1));
		int studentCRId = cr.getStudent().getId();
		int professorCRId = cr.getProfessor().getId();
		int courseCRId = cr.getCourse().getId();

		LOG.info("Student.ID: {} has membership cards: {} and course registrations: {}", s.getId(), sizeMembershipCards, sizeSCourseRegistrations);
		LOG.info("MembershipCard.ID: {} has owner.id: {} and clubMembership.id: {}", mc.getId(), ownerId, clubMembershipId);
		LOG.info("ClubMembership.ID: {} has club.id: {} and card.id: {}", cm.getId(), clubId, cardId);
		LOG.info("StudentClub.ID: {} has memberships: {}", sc.getId(), sizeClubMemberships);
		LOG.info("Course.ID: {} has courseRegistrations: {}", c.getId(), sizeCCourseRegistrations);
		LOG.info("Professor.ID: {} has courseRegistrations: {}", p.getId(), sizePCourseRegistrations);
		LOG.info("CourseRegistration.ID: {} has student.id: {}, professor.id: {}, and course.id: {}", cr.getId(), studentCRId, professorCRId,
				courseCRId);
	}

	private static void addSampleData(EntityManager em) {
		EntityTransaction et = em.getTransaction();
		et.begin();

		StudentClub clubAcademic = new AcademicStudentClub();
		clubAcademic.setName("Computer Programming Club");
		em.persist(clubAcademic);

		StudentClub clubNonAcademic = new NonAcademicStudentClub();
		clubNonAcademic.setName("Student Hiking Club");
		em.persist(clubNonAcademic);

		Course course1 = new Course();
		course1.setCourse("CST8277", "Enterprise Application Programming", 2022, "AUTUMN", 3, (byte) 0);

		Course course2 = new Course();
		course2.setCourse("CST8284", "Object-Oriented Programming in Java", 2022, "SUMMER", 3, (byte) 1);

		Professor professor = new Professor();
		professor.setProfessor("Teddy", "Yap", "Information and Communications Technology");

		Student s = new Student();
		s.setFullName("John", "Smith");

		DurationAndStatus ds = new DurationAndStatus();
		ds.setDurationAndStatus(LocalDateTime.of(2022, 8, 28, 0, 0), LocalDateTime.of(2023, 8, 27, 0, 0) , "+");

		DurationAndStatus ds2 = new DurationAndStatus();
		ds2.setDurationAndStatus(LocalDateTime.of(2021, 1, 1, 0, 0), LocalDateTime.of(2021, 12, 31, 0, 0) , "-");

		CourseRegistration cr1 = new CourseRegistration();
		cr1.setProfessor(professor);
		cr1.setCourse(course1);
		cr1.setLetterGrade("A+");
		cr1.setNumericGrade(100);
		cr1.setStudent(s);
		em.persist(cr1);

		CourseRegistration cr2 = new CourseRegistration();
		cr2.setStudent(s);
		cr2.setCourse(course2);
		cr2.setNumericGrade(85);
		cr2.setStudent(s);
		em.persist(cr2);

		ClubMembership membership = new ClubMembership();
		membership.setDurationAndStatus(ds);
		membership.setStudentClub(clubNonAcademic);
		em.persist(membership);

		ClubMembership membership2 = new ClubMembership();
		membership2.setDurationAndStatus(ds2);
		membership2.setStudentClub(clubAcademic);
		em.persist(membership2);

		MembershipCard card = new MembershipCard();
		card.setOwner(s);
		card.setSigned(true);
		card.setClubMembership(membership);
		em.persist(card);

		MembershipCard card2 = new MembershipCard();
		card2.setOwner(s);
		card2.setSigned(false);
		card2.setClubMembership(membership);
		em.persist(card2);

		MembershipCard card3 = new MembershipCard();
		card3.setOwner(s);
		card3.setSigned(true);
		card3.setClubMembership(membership2);
		em.persist(card3);

		et.commit();
	}

}
