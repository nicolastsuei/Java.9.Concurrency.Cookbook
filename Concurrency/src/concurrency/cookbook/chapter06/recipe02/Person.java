package concurrency.cookbook.chapter06.recipe02;

import java.util.Date;

public class Person implements Comparable<Person>{
	private int id;
	private String firstName;
	private String lastName;
	private Date birthDate;
	private int salary;
	private double coeficient;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	public double getCoeficient() {
		return coeficient;
	}

	public void setCoeficient(double coeficient) {
		this.coeficient = coeficient;
	}
	
	public int compareTo(Person otherPerson) {
		int compareLastNames = this.getLastName().compareTo(otherPerson.getLastName());
		if (compareLastNames != 0) {
			return compareLastNames;
		} else {
			return this.getFirstName().compareTo(otherPerson.getFirstName());
		}
	}

	
}
