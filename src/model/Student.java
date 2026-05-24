package model;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String studentId;
    private String name;
    private String major;
    private int admissionYear;
    private List<TakenCourse> takenCourses; // 수강한 과목 리스트

    // 생성자
    public Student(String studentId, String name, String major, int admissionYear) {
        this.studentId = studentId;
        this.name = name;
        this.major = major;
        this.admissionYear = admissionYear;
        this.takenCourses = new ArrayList<>(); // 리스트 초기화
    }

    // 수강 과목 추가 메서드 (편의를 위해 추가)
    public void addTakenCourse(TakenCourse course) {
        this.takenCourses.add(course);
    }

    // Getters & Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public int getAdmissionYear() { return admissionYear; }
    public void setAdmissionYear(int admissionYear) { this.admissionYear = admissionYear; }

    public List<TakenCourse> getTakenCourses() { return takenCourses; }
    public void setTakenCourses(List<TakenCourse> takenCourses) { this.takenCourses = takenCourses; }
}