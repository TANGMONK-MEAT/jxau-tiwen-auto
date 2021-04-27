package com.github.tangmonkmeat.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StudentGroup {
	
	private String name;
	
	private List<Student> students;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	@Override
	public String toString() {
		return "StudentGroup [name=" + name + ", students=" + students + "]";
	}

	public StudentGroup(String name, List<Student> students) {
		super();
		this.name = name;
		this.students = students;
	}
}
