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
public class TestCRUDCourse extends JUnitBase {

	private EntityManager em;
	private EntityTransaction et;

	private static Course course;
	private static String course_code = "CST8277";
	private static String course_title = "Enterprise Application Programming";
	private static Integer year = 2022;
	private static String semester = "AUTUMN";
	private static Integer credit_units = 3;
	private static Byte online = 0;

	
	@BeforeAll
	static void setupAllInit() {

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
		Root<Course> root = query.from(Course.class);
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
		course = new Course();
		course.setCourseCode(course_code);
		course.setCourseTitle(course_title);
		course.setYear(year);
		course.setSemester(semester);
		course.setCreditUnits(credit_units);
		course.setOnline(online);
		em.persist(course);
		et.commit();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		// Select count(cr) from CourseRegistration cr where cr.id = :id
		Root<Course> root = query.from(Course.class);
		query.select(builder.count(root));
		query.where(builder.equal(root.get(Course_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq = em.createQuery(query);
		tq.setParameter("id", course.getId());
		// Get the result as row count
		long result = tq.getSingleResult();

		// There should only be one row in the DB
		assertThat(result, is(greaterThanOrEqualTo(1L)));
//		assertEquals(result, 1);
	}

	@Test
	void test03_CreateInvalid() {
		et.begin();
		Course course2 = new Course();
		//course2.setCourseCode(course_code);
		course2.setCourseTitle(course_title);
		course2.setYear(year);
		course2.setSemester(semester);
		course2.setCreditUnits(credit_units);
		course2.setOnline(online);
		// We expect a failure because course is part of the composite key
		assertThrows(PersistenceException.class, () -> em.persist(course2));
		et.commit();
	}

	@Test
	void test04_Read() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for CourseRegistration
		CriteriaQuery<Course> query = builder.createQuery(Course.class);
		// Select cr from CourseRegistration cr
		Root<Course> root = query.from(Course.class);
		query.select(root);
		// Create query and set the parameter
		TypedQuery<Course> tq = em.createQuery(query);
		// Get the result as row count
		List<Course> courses = tq.getResultList();

		assertThat(courses, contains(equalTo(course)));
	}


	@Test
	void test05_Update() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for Contact
		CriteriaQuery<Course> query = builder.createQuery(Course.class);
		// Select cr from Contact cr
		Root<Course> root = query.from(Course.class);
		query.select(root);
		query.where(builder.equal(root.get(Course_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Course> tq = em.createQuery(query);
		tq.setParameter("id", course.getId());
		// Get the result as row count
		Course returnedCourse = tq.getSingleResult();

		String new_course_title = "Music";
		int new_year = 2023;

		et.begin();
		returnedCourse.setCourseTitle(new_course_title);
		returnedCourse.setYear(new_year);
		em.merge(returnedCourse);
		et.commit();

		returnedCourse = tq.getSingleResult();

		assertThat(returnedCourse.getCourseTitle(), equalTo(new_course_title));
		assertThat(returnedCourse.getYear(), equalTo(new_year));
	}




	@Test
	void test06_Delete() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for Contact
		CriteriaQuery<Course> query = builder.createQuery(Course.class);
		// Select cr from CourseRegistration cr
		Root<Course> root = query.from(Course.class);
		query.select(root);
		query.where(builder.equal(root.get(Course_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Course> tq = em.createQuery(query);
		tq.setParameter("id", course.getId());
		// Get the result as row count
		Course returnedCourse = tq.getSingleResult();

		et.begin();
		// Add another row to db to make sure only the correct row is deleted
		Course course2 = new Course();
		course2.setCourse("CST8288", "Design Patters in Java", 2022, "SPRING", 3, (byte) 0);
		course2.setCourseTitle("Dance");
		course2.setYear(returnedCourse.getYear());
		em.persist(course2);
		et.commit();

		et.begin();
		em.remove(returnedCourse);
		et.commit();

		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query2 = builder.createQuery(Long.class);
		// Select count(p) from Professor p where p.id = :id
		Root<Course> root2 = query2.from(Course.class);
		query2.select(builder.count(root2));
		query2.where(builder.equal(root2.get(Course_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq2 = em.createQuery(query2);
		tq2.setParameter("id", returnedCourse.getId());
		// Get the result as row count
		long result = tq2.getSingleResult();
		assertThat(result, is(equalTo(0L)));

		// Create query and set the parameter
		TypedQuery<Long> tq3 = em.createQuery(query2);
		tq3.setParameter("id", course2.getId());
		// Get the result as row count
		result = tq3.getSingleResult();
		assertThat(result, is(equalTo(1L)));
	}
}

