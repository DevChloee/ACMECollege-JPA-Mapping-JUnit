/***************************************************************************
 * File:  Student.java Course materials (23W) CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @date August 28, 2022
 * 
 */
package acmecollege.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the STUDENT database table in the acmecollege schema
 * </br></br>
 * 
 * Note:  This is <b>NOT</b> the same Student entity from Lab 1/Assignment 1/Assignment 2.
 * </br>
 * This entity does <b>NOT</b> have member fields email, phoneNumber, level, or program.
 * </br>
 * 
 */
@SuppressWarnings("unused")
//TODO ST01 - Add the missing annotations.
@Entity
@Table(name="student")
@Access(AccessType.FIELD)
//TODO ST02 - Do we need a mapped super class? If so, which one?
@AttributeOverride(name = "id", column = @Column(name="id"))
public class Student extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	// TODO ST03 - Add annotation
    @Column(name = "first_name")
	private String firstName;

	// TODO ST04 - Add annotation
    @Column(name = "last_name")
	private String lastName;

	// TODO ST05 - Add annotations for 1:M relation.  Changes should not cascade.
    @OneToMany(mappedBy="owner")
	private Set<MembershipCard> membershipCards = new HashSet<>();

	// TODO ST06 - Add annotations for 1:M relation.  Changes should not cascade.
    @OneToMany(mappedBy="student")
	private Set<CourseRegistration> courseRegistrations = new HashSet<>();

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Set<MembershipCard> getMembershipCards() {
		return membershipCards;
	}

	public void setMembershipCards(Set<MembershipCard> membershipCards) {
		this.membershipCards = membershipCards;
	}

	public Set<CourseRegistration> getCourseRegistrations() {
		return courseRegistrations;
	}

	public void setCourseRegistrations(Set<CourseRegistration> courseRegistrations) {
		this.courseRegistrations = courseRegistrations;
	}

	public void setFullName(String firstName, String lastName) {
		setFirstName(firstName);
		setLastName(lastName);
	}
	
	//Inherited hashCode/equals is sufficient for this entity class

}
