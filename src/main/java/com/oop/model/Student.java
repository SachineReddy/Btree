package com.oop.model;

import java.util.Objects;

public class Student implements Comparable<Student> {

    private String name;
    private int redId;
    private float gpa;

    public Student(String name, int redId, float gpa) {
        this.name = name;
        this.redId = redId;
        this.gpa = gpa;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRedId() {
        return redId;
    }

    public void setRedId(int redId) {
        this.redId = redId;
    }

    public float getGpa() {
        return gpa;
    }

    public void setGpa(float gpa) {
        this.gpa = gpa;
    }

    @Override
    //to set default ordering of Students based on redId
    public int compareTo(Student student) {
        return this.getRedId() - student.getRedId();
    }

    @Override
    public String toString() {
        return "Student{" + "name='" + name + '\''
                + ", redId=" + redId + ", gpa=" + gpa + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return redId == student.redId
                && Float.compare(student.gpa, gpa) == 0
                && name.equals(student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, redId, gpa);
    }
}
