package com.oop.util.student;

import com.oop.model.Student;

import java.util.Comparator;

public interface StudentSortStrategy extends Comparator<Student> {

    static Comparator<Student> studentNameAscSort(){
        return Comparator.comparing(Student::getName);
    }

    static Comparator<Student> studentNameDescSort(){
        return Comparator.comparing(Student::getName).reversed();
    }

    static Comparator<Student> studentGPAAscSort(){
        return Comparator.comparing(Student::getGpa);
    }

    static Comparator<Student> studentGPADescSort(){
        return Comparator.comparing(Student::getGpa).reversed();
    }
}
