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
public class TestCRUDStudent extends JUnitBase {

	private EntityManager em;
	private EntityTransaction et;

	private static Student student;
	private static String first_name = "Chloe";
	private static String last_name = "Jun";


	
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
		student = new Student();
		student.setFirstName(first_name);
		student.setLastName(last_name);

		em.persist(student);
		et.commit();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		// Select count(cr) from CourseRegistration cr where cr.id = :id
		Root<Student> root = query.from(Student.class);
		query.select(builder.count(root));
		query.where(builder.equal(root.get(Student_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq = em.createQuery(query);
		tq.setParameter("id", student.getId());
		// Get the result as row count
		long result = tq.getSingleResult();

		// There should only be one row in the DB
		assertThat(result, is(greaterThanOrEqualTo(1L)));
//		assertEquals(result, 1);
	}

	@Test
	void test03_CreateInvalid() {
		et.begin();
		Student student2 = new Student();

		//student2.setFirstName("Jenny");
		student2.setLastName("Kim");

		// We expect a failure because student is part of the composite key
		assertThrows(PersistenceException.class, () -> em.persist(student2));
		et.commit();
	}

	@Test
	void test04_Read() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for CourseRegistration
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		// Select cr from CourseRegistration cr
		Root<Student> root = query.from(Student.class);
		query.select(root);
		// Create query and set the parameter
		TypedQuery<Student> tq = em.createQuery(query);
		// Get the result as row count
		List<Student> students = tq.getResultList();

		assertThat(students, contains(equalTo(student)));
	}


	@Test
	void test05_Update() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for Contact
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		// Select cr from Contact cr
		Root<Student> root = query.from(Student.class);
		query.select(root);
		query.where(builder.equal(root.get(Student_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Student> tq = em.createQuery(query);
		tq.setParameter("id", student.getId());
		// Get the result as row count
		Student returnedStudent = tq.getSingleResult();

		String new_first_name = "Jenny";
		String new_last_name = "Kim";

		et.begin();
		returnedStudent.setFirstName(new_first_name);
		returnedStudent.setLastName(new_last_name);
		
		em.merge(returnedStudent);
		et.commit();

		returnedStudent = tq.getSingleResult();

		assertThat(returnedStudent.getFirstName(), equalTo(new_first_name));
		assertThat(returnedStudent.getLastName(), equalTo(new_last_name));
	}




	@Test
	void test06_Delete() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for Contact
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		// Select cr from CourseRegistration cr
		Root<Student> root = query.from(Student.class);
		query.select(root);
		query.where(builder.equal(root.get(Student_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Student> tq = em.createQuery(query);
		tq.setParameter("id", student.getId());
		// Get the result as row count
		Student returnedStudent = tq.getSingleResult();

		et.begin();
		// Add another row to db to make sure only the correct row is deleted
		Student student2 = new Student();
		student2.setFirstName("Suzy");
		student2.setLastName("Bae");
	
		em.persist(student2);
		et.commit();

		et.begin();
		em.remove(returnedStudent);
		et.commit();

		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query2 = builder.createQuery(Long.class);
		// Select count(p) from Professor p where p.id = :id
		Root<Student> root2 = query2.from(Student.class);
		query2.select(builder.count(root2));
		query2.where(builder.equal(root2.get(Student_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq2 = em.createQuery(query2);
		tq2.setParameter("id", returnedStudent.getId());
		// Get the result as row count
		long result = tq2.getSingleResult();
		assertThat(result, is(equalTo(0L)));

		// Create query and set the parameter
		TypedQuery<Long> tq3 = em.createQuery(query2);
		tq3.setParameter("id", student2.getId());
		// Get the result as row count
		result = tq3.getSingleResult();
		assertThat(result, is(equalTo(1L)));
	}
}
