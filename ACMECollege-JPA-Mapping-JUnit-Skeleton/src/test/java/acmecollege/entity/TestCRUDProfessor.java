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
public class TestCRUDProfessor extends JUnitBase {

	private EntityManager em;
	private EntityTransaction et;

	private static Professor professor;
	private static String first_name = "Asim";
	private static String last_name = "Butt";
	private static String department= "lab";

	
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
		Root<Professor> root = query.from(Professor.class);
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
		professor = new Professor();
		professor.setFirstName(first_name);
		professor.setLastName(last_name);
		professor.setDepartment(department);

		em.persist(professor);
		et.commit();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		// Select count(cr) from CourseRegistration cr where cr.id = :id
		Root<Professor> root = query.from(Professor.class);
		query.select(builder.count(root));
		query.where(builder.equal(root.get(Professor_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq = em.createQuery(query);
		tq.setParameter("id", professor.getId());
		// Get the result as row count
		long result = tq.getSingleResult();

		// There should only be one row in the DB
		assertThat(result, is(greaterThanOrEqualTo(1L)));
//		assertEquals(result, 1);
	}

	@Test
	void test03_CreateInvalid() {
		et.begin();
		Professor professor2 = new Professor();
		//course2.setCourseCode(course_code);
		//professor2.setFirstName(first_name);
		professor2.setLastName(last_name);
		professor2.setDepartment(department);
		
		// We expect a failure because professor is part of the composite key
		assertThrows(PersistenceException.class, () -> em.persist(professor2));
		et.commit();
	}

	@Test
	void test04_Read() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for CourseRegistration
		CriteriaQuery<Professor> query = builder.createQuery(Professor.class);
		// Select cr from CourseRegistration cr
		Root<Professor> root = query.from(Professor.class);
		query.select(root);
		// Create query and set the parameter
		TypedQuery<Professor> tq = em.createQuery(query);
		// Get the result as row count
		List<Professor> professors = tq.getResultList();

		assertThat((professors), contains(equalTo(professor)));
	}


	@Test
	void test05_Update() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for Contact
		CriteriaQuery<Professor> query = builder.createQuery(Professor.class);
		// Select cr from Contact cr
		Root<Professor> root = query.from(Professor.class);
		query.select(root);
		query.where(builder.equal(root.get(Professor_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Professor> tq = em.createQuery(query);
		tq.setParameter("id", professor.getId());
		// Get the result as row count
		Professor returnedProfessor = tq.getSingleResult();

		String new_first_name = "Douglas";
		String new_last_name = "King";
		String new_department = "Database";

		et.begin();
		returnedProfessor.setFirstName(new_first_name);
		returnedProfessor.setLastName(new_last_name);
		returnedProfessor.setDepartment(new_department);
		em.merge(returnedProfessor);
		et.commit();

		returnedProfessor = tq.getSingleResult();

		assertThat(returnedProfessor.getFirstName(), equalTo(new_first_name));
		assertThat(returnedProfessor.getLastName(), equalTo(new_last_name));
		assertThat(returnedProfessor.getDepartment(), equalTo(new_department));
	}




	@Test
	void test06_Delete() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// Create query for Contact
		CriteriaQuery<Professor> query = builder.createQuery(Professor.class);
		// Select cr from CourseRegistration cr
		Root<Professor> root = query.from(Professor.class);
		query.select(root);
		query.where(builder.equal(root.get(Professor_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Professor> tq = em.createQuery(query);
		tq.setParameter("id", professor.getId());
		// Get the result as row count
		Professor returnedProfessor = tq.getSingleResult();

		et.begin();
		// Add another row to db to make sure only the correct row is deleted
		Professor professor2 = new Professor();
		professor2.setFirstName("David");
		professor2.setLastName("Kim");
		professor2.setDepartment("Entertainment");
		em.persist(professor2);
		et.commit();

		et.begin();
		em.remove(returnedProfessor);
		et.commit();

		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query2 = builder.createQuery(Long.class);
		// Select count(p) from Professor p where p.id = :id
		Root<Professor> root2 = query2.from(Professor.class);
		query2.select(builder.count(root2));
		query2.where(builder.equal(root2.get(Professor_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq2 = em.createQuery(query2);
		tq2.setParameter("id", returnedProfessor.getId());
		// Get the result as row count
		long result = tq2.getSingleResult();
		assertThat(result, is(equalTo(0L)));

		// Create query and set the parameter
		TypedQuery<Long> tq3 = em.createQuery(query2);
		tq3.setParameter("id", professor2.getId());
		// Get the result as row count
		result = tq3.getSingleResult();
		assertThat(result, is(equalTo(1L)));
	}
}