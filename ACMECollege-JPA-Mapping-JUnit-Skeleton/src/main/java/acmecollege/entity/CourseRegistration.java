/***************************************************************************
 * File:  CourseRegistration.java Course materials (23W) CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @date August 28, 2022
 * 
 */

package acmecollege.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@SuppressWarnings("unused")
/**
 * The persistent class for the course_registration database table.
 */
@Entity
@Table(name = "course_registration")
@Access(AccessType.FIELD)
@NamedQuery(name = "CourseRegistration.findAll", query = "SELECT cr FROM CourseRegistration cr")
public class CourseRegistration extends PojoBaseCompositeKey<CourseRegistrationPK> implements Serializable {
	private static final long serialVersionUID = 1L;

	// Hint - What annotation is used for a composite primary key type?
	@EmbeddedId
	private CourseRegistrationPK id;

	// @MapsId is used to map a part of composite key to an entity.
	@MapsId("studentId")
	@ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
	private Student student;

	//TODO CR01 - Add missing annotations.  Similar to student, this field is a part of the composite key of this entity.  Changes to this class should cascade.  Reference to a course is not optional.
	@MapsId("courseId")
	@ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	//TODO CR02 - Add missing annotations.  Changes to this class should cascade.
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "professor_id")
	private Professor professor;

	@Column(name = "numeric_grade")
	private int numericGrade;

	@Column(length = 3, name = "letter_grade")
	private String letterGrade;


	public CourseRegistration() {
		id = new CourseRegistrationPK();
	}

	@Override
	public CourseRegistrationPK getId() {
		return id;
	}

	@Override
	public void setId(CourseRegistrationPK id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
		//We must manually set the 'other' side of the relationship (JPA does not 'do' auto-management of relationships)
		if (student != null) {
			student.getCourseRegistrations().add(this);
		}
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
		//We must manually set the 'other' side of the relationship (JPA does not 'do' auto-management of relationships)
		if (course != null) {
			course.getCourseRegistrations().add(this);
		}
	}

	public Professor getProfessor() {
		return professor;
	}

	public void setProfessor(Professor professor) {
		this.professor = professor;
		//We must manually set the 'other' side of the relationship (JPA does not 'do' auto-management of relationships)
		if (professor != null) {
			professor.getCourseRegistrations().add(this);
		}
	}

	public int getNumericGrade() {
		return numericGrade;
	}
	
	public void setNumericGrade(int numericGrade) {
		this.numericGrade = numericGrade;
	}

	public String getLetterGrade() {
		return letterGrade;
	}

	public void setLetterGrade(String letterGrade) {
		this.letterGrade = letterGrade;
	}

	//Inherited hashCode/equals is sufficient for this entity class

}