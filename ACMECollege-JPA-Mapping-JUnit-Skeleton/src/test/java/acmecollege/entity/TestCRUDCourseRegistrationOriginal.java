package acmecollege.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import common.JUnitBase;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestCRUDCourseRegistrationOriginal extends JUnitBase {

	private EntityManager em;
	private EntityTransaction et;

	private static Course course;
	private static Professor professor;
	private static Student student;
	private static CourseRegistration courseRegistration;
	private static final String LETTER_GRADE = "A+";
	private static final int NUMERIC_GRADE = 100;

	
	@BeforeAll
	static void setupAllInit() {
		course = new Course();
		course.setCourse("CST8277", "Enterprise Application Programming", 2022, "AUTUMN", 3, (byte) 0);

		professor = new Professor();
		professor.setProfessor("Teddy", "Yap", "Information and Communications Technology");

		student = new Student();
		student.setFullName("John", "Smith");
	}

	@BeforeEach
	void setup() {
		em = getEntityManager();
		et = em.getTransaction();
	}

	@AfterEach
	void tearDown() {
		em.close();
	}

	@Test
	void test01_Empty() {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		// Select count(cr) from CourseRegistration cr
		Root<CourseRegistration> root = query.from(CourseRegistration.class);
		query.select(builder.count(root));
		// Create query and set the parameter
		TypedQuery<Long> tq = em.createQuery(query);
		// Get the result as row count
		long result = tq.getSingleResult();

		assertThat(result, is(comparesEqualTo(0L)));

	}

	@Test
	void test02_Create() {
		et.begin();
		courseRegistration = new CourseRegistration();
		courseRegistration.setProfessor(professor);
		courseRegistration.setCourse(course);
		courseRegistration.setStudent(student);
		courseRegistration.setLetterGrade("A+");
		courseRegistration.setNumericGrade(100);
		em.persist(courseRegistration);
		et.commit();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		// Select count(cr) from CourseRegistration cr where cr.id = :id
		Root<CourseRegistration> root = query.from(CourseRegistration.class);
		query.select(builder.count(root));
		query.where(builder.equal(root.get(CourseRegistration_.id), builder.parameter(CourseRegistrationPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq = em.createQuery(query);
		tq.setParameter("id", courseRegistration.getId());
		// Get the result as row count
		long result = tq.getSingleResult();

		// There should only be one row in the DB
		assertThat(result, is(greaterThanOrEqualTo(1L)));
//		assertEquals(result, 1);
	}

	@Test
	void test03_CreateInvalid() {
		et.begin();
		CourseRegistration courseRegistration2 = new CourseRegistration();
		courseRegistration2.setProfessor(professor);
//		courseRegistration2.setCourse(course);
		courseRegistration2.setStudent(student);
		courseRegistration2.setNumericGrade(85);
		courseRegistration2.setLetterGrade("A");
		// We expect a failure because course is part of the composite key
		assertThrows(PersistenceException.class, () -> em.persist(courseRegistration2));
		et.commit();
	}

	@Test
	void test04_Read() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for CourseRegistration
		CriteriaQuery<CourseRegistration> query = builder.createQuery(CourseRegistration.class);
		// Select cr from CourseRegistration cr
		Root<CourseRegistration> root = query.from(CourseRegistration.class);
		query.select(root);
		// Create query and set the parameter
		TypedQuery<CourseRegistration> tq = em.createQuery(query);
		// Get the result as row count
		List<CourseRegistration> courseRegistrations = tq.getResultList();

		assertThat(courseRegistrations, contains(equalTo(courseRegistration)));
	}

	@Test
	void test05_ReadDependencies() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for CourseRegistration
		CriteriaQuery<CourseRegistration> query = builder.createQuery(CourseRegistration.class);
		// Select cr from CourseRegistration cr
		Root<CourseRegistration> root = query.from(CourseRegistration.class);
		query.select(root);
		query.where(builder.equal(root.get(CourseRegistration_.id), builder.parameter(CourseRegistrationPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<CourseRegistration> tq = em.createQuery(query);
		tq.setParameter("id", courseRegistration.getId());
		// Get the result as row count
		CourseRegistration returnedCourseRegistration = tq.getSingleResult();

		assertThat(returnedCourseRegistration.getStudent(), equalTo(student));
		assertThat(returnedCourseRegistration.getLetterGrade(), equalTo(LETTER_GRADE));
		assertThat(returnedCourseRegistration.getNumericGrade(), equalTo(NUMERIC_GRADE));
		assertThat(returnedCourseRegistration.getCourse(), equalTo(course));
		assertThat(returnedCourseRegistration.getProfessor(), equalTo(professor));
	}

	@Test
	void test06_Update() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for Contact
		CriteriaQuery<CourseRegistration> query = builder.createQuery(CourseRegistration.class);
		// Select cr from Contact cr
		Root<CourseRegistration> root = query.from(CourseRegistration.class);
		query.select(root);
		query.where(builder.equal(root.get(CourseRegistration_.id), builder.parameter(CourseRegistrationPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<CourseRegistration> tq = em.createQuery(query);
		tq.setParameter("id", courseRegistration.getId());
		// Get the result as row count
		CourseRegistration returnedCourseRegistration = tq.getSingleResult();

		String newLetterGrade = "A";
		int newNumericGrade = 85;

		et.begin();
		returnedCourseRegistration.setLetterGrade(newLetterGrade);
		returnedCourseRegistration.setNumericGrade(newNumericGrade);
		em.merge(returnedCourseRegistration);
		et.commit();

		returnedCourseRegistration = tq.getSingleResult();

		assertThat(returnedCourseRegistration.getLetterGrade(), equalTo(newLetterGrade));
		assertThat(returnedCourseRegistration.getNumericGrade(), equalTo(newNumericGrade));
	}

	@Test
	void test07_UpdateDependencies() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for CourseRegistration
		CriteriaQuery<CourseRegistration> query = builder.createQuery(CourseRegistration.class);
		// Select cr from Contact cr where cr.id = :id
		Root<CourseRegistration> root = query.from(CourseRegistration.class);
		query.select(root);
		query.where(builder.equal(root.get(CourseRegistration_.id), builder.parameter(CourseRegistrationPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<CourseRegistration> tq = em.createQuery(query);
		tq.setParameter("id", courseRegistration.getId());
		// Get the result as row count
		CourseRegistration returnedCourseRegistration = tq.getSingleResult();

		course = returnedCourseRegistration.getCourse();
		course.setCourse("CST8116", "Introduction to Computer Programming", 2021, "WINTER", 3, (byte) 0);

		professor = returnedCourseRegistration.getProfessor();
		professor.setProfessor("Charles", "Xavier", "Physics");

		student = returnedCourseRegistration.getStudent();
		student.setFullName("Jack", "Jackson");

		et.begin();
		returnedCourseRegistration.setProfessor(professor);
		returnedCourseRegistration.setCourse(course);
		returnedCourseRegistration.setStudent(student);
		em.merge(returnedCourseRegistration);
		et.commit();

		returnedCourseRegistration = tq.getSingleResult();

		assertThat(returnedCourseRegistration.getStudent(), equalTo(student));
		assertThat(returnedCourseRegistration.getCourse(), equalTo(course));
		assertThat(returnedCourseRegistration.getProfessor(), equalTo(professor));
	}

	@Test
	void test08_DeleteDependecy() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for CourseRegistration
		CriteriaQuery<CourseRegistration> query = builder.createQuery(CourseRegistration.class);
		// Select cr from CourseRegistration cr
		Root<CourseRegistration> root = query.from(CourseRegistration.class);
		query.select(root);
		query.where(builder.equal( root.get(CourseRegistration_.id), builder.parameter(CourseRegistrationPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<CourseRegistration> tq = em.createQuery(query);
		tq.setParameter("id", courseRegistration.getId());
		// Get the result as row count
		CourseRegistration returnedCourseRegistration = tq.getSingleResult();

		int professorId = returnedCourseRegistration.getProfessor().getId();

		et.begin();
		returnedCourseRegistration.setProfessor(null);
		em.merge(returnedCourseRegistration);
		et.commit();

		returnedCourseRegistration = tq.getSingleResult();

		assertThat(returnedCourseRegistration.getProfessor(), is(nullValue()));

		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query2 = builder.createQuery(Long.class);
		// Select count(p) from Professor p where p.id = :id
		Root<Professor> root2 = query2.from(Professor.class);
		query2.select(builder.count(root2));
		query2.where(builder.equal(root2.get(Professor_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq2 = em.createQuery(query2);
		tq2.setParameter("id", professorId);
		// Get the result as row count
		long result = tq2.getSingleResult();
		// Because it can be null so it is not removed
		assertThat(result, is(equalTo(1L)));
	}

	@Test
	void test09_Delete() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for Contact
		CriteriaQuery<CourseRegistration> query = builder.createQuery(CourseRegistration.class);
		// Select cr from CourseRegistration cr
		Root<CourseRegistration> root = query.from(CourseRegistration.class);
		query.select(root);
		query.where(builder.equal(root.get(CourseRegistration_.id), builder.parameter(CourseRegistrationPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<CourseRegistration> tq = em.createQuery(query);
		tq.setParameter("id", courseRegistration.getId());
		// Get the result as row count
		CourseRegistration returnedCourseRegistration = tq.getSingleResult();

		et.begin();
		// Add another row to db to make sure only the correct row is deleted
		CourseRegistration courseRegistration2 = new CourseRegistration();
		courseRegistration2.setCourse(new Course().setCourse("CST8288", "Design Patters in Java", 2022, "SPRING", 3, (byte) 0));
		courseRegistration2.setNumericGrade(99);
		courseRegistration2.setStudent(returnedCourseRegistration.getStudent());
		em.persist(courseRegistration2);
		et.commit();

		et.begin();
		em.remove(returnedCourseRegistration);
		et.commit();

		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query2 = builder.createQuery(Long.class);
		// Select count(p) from Professor p where p.id = :id
		Root<CourseRegistration> root2 = query2.from(CourseRegistration.class);
		query2.select(builder.count(root2));
		query2.where(builder.equal(root2.get(CourseRegistration_.id), builder.parameter(CourseRegistrationPK.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq2 = em.createQuery(query2);
		tq2.setParameter("id", returnedCourseRegistration.getId());
		// Get the result as row count
		long result = tq2.getSingleResult();
		assertThat(result, is(equalTo(0L)));

		// Create query and set the parameter
		TypedQuery<Long> tq3 = em.createQuery(query2);
		tq3.setParameter("id", courseRegistration2.getId());
		// Get the result as row count
		result = tq3.getSingleResult();
		assertThat(result, is(equalTo(1L)));
	}
}

