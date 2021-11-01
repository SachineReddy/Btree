package com.oop.util;

import com.oop.model.Student;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.oop.util.student.StudentSortStrategy.studentGPADescSort;
import static com.oop.util.student.StudentSortStrategy.studentNameAscSort;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class StudentBTreeTest {

    Tree<Student> studentBTree;

    @Before
    public void setUp() {
        studentBTree = new BTree<>(studentNameAscSort());
        studentBTree.add(new Student("Megan", 1, 2.85F));
        studentBTree.add(new Student("Karolina", 2, 2.7F));
        studentBTree.add(new Student("Alex", 3, 4.0F));
        studentBTree.add(new Student("Jorge", 4, 2.5F));
        studentBTree.add(new Student("Andrea", 5, 4.0F));
        studentBTree.add(new Student("Xavier", 6, 3.5F));
        studentBTree.add(new Student("Simona", 7, 4.0F));
        studentBTree.add(new Student("Sachin", 8, 2.2F));
    }

    @Test
    @DisplayName("Positive scenario: Retrieve element at k'th index from b-tree")
    public void testElementAt() {
        Student student = studentBTree.elementAt(2);
        Assert.assertEquals("Jorge", student.getName());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    @DisplayName("throws exception when we retrieve element at k'th index " +
            "and 'k' is out of range")
    public void testElementAtIndexOutOfBoundsException() {
        studentBTree.elementAt(11);
    }

    @Test
    @DisplayName("Retrieve redId of students on probation based on gpa, ascending order")
    public void testRetrieveRedIds() {
        Comparator<Student> studentNameComparator = Comparator.comparing(Student::getName);
        Predicate<Student> gpaLessThan2_85 = student -> student.getGpa() < 2.85F;
        Function<Student, Integer> redIdInfo = Student::getRedId;

        List<Integer> redIds =
                (List<Integer>) TreeUtils.toList(studentBTree, studentNameComparator,
                        gpaLessThan2_85, redIdInfo);
        List<Integer> expected = Arrays.asList(4, 2, 8);

        assertThat(redIds, is(expected));
        assertThat(redIds, hasItems(2, 4));
        assertThat(redIds.size(), is(3));
    }

    @Test
    @DisplayName("Retrieve name of students with gpa 4.0, desc order")
    public void testRetrieveNames() {
        Comparator<Student> studentNameComparator =
                Comparator.comparing(Student::getName, Comparator.reverseOrder());
        Predicate<Student> gpaEqualTo4 = student -> student.getGpa() == 4.0F;
        Function<Student, String> studentNameInfo = Student::getName;

        List<String> studentNames =
                (List<String>) TreeUtils.toList(studentBTree, studentNameComparator,
                        gpaEqualTo4, studentNameInfo);
        List<String> expected = Arrays.asList("Simona", "Andrea", "Alex");

        assertThat(studentNames, is(expected));
        assertThat(studentNames, hasItems("Andrea"));
        assertThat(studentNames.size(), is(3));
    }


    @Test
    @DisplayName("Test desc ordering of elements by GPA")
    public void testDescOrderingByGPA() {
        studentBTree = new BTree<>(studentGPADescSort());
        studentBTree.add(new Student("Megan", 1, 2.85F));
        studentBTree.add(new Student("Karolina", 2, 2.7F));
        studentBTree.add(new Student("Alex", 3, 4.0F));
        studentBTree.add(new Student("Jorge", 4, 2.5F));

        List<String> expectedStudentList =
                Arrays.asList("Alex,4.0", "Megan,2.85", "Karolina,2.7", "Jorge,2.5");
        List<String> studentDescOrderedByGpa = new ArrayList<>();

        for (Student o : studentBTree) {
            studentDescOrderedByGpa.add(o.getName() + "," + o.getGpa());
        }
        Assert.assertEquals(expectedStudentList, studentDescOrderedByGpa);
    }

}
