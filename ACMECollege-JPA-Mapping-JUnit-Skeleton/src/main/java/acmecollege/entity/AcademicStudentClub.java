/***************************************************************************
 * File:  AcademicStudentClub.java Course materials (23W) CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @date August 28, 2022
 * 
 */
package acmecollege.entity;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

//TODO ASC01 - Add missing annotations, please see Week 9 slides page 15.  Value 1 is academic and value 0 is non-academic.
@Entity
@DiscriminatorValue("1")
public class AcademicStudentClub extends StudentClub implements Serializable {
	private static final long serialVersionUID = 1L;

	public AcademicStudentClub() {
	}
}
