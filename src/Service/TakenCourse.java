/*
 * 
 * 
*/

package service;

public class TakenCourse {

    // 수강한 과목
    private Course course;

    // 수강 학기
    private String semester;

    // 성적
    private String grade;

    // 패스 여부
    private boolean passed;

    // 생성자
    public TakenCourse(Course course,
                       String semester,
                       String grade) {

        this.course = course;
        this.semester = semester;
        this.grade = grade;

        // 성적 기준으로 자동 판별
        this.passed = isPassGrade();
    }

    // 성적 기준 패스 여부 판단
    public boolean isPassGrade() {

        // F만 실패 처리
        if (grade.equalsIgnoreCase("F")) {
            return false;
        }

        return true;
    }

    // getter
    public Course getCourse() {
        return course;
    }

    public String getSemester() {
        return semester;
    }

    public String getGrade() {
        return grade;
    }

    public boolean isPassed() {
        return passed;
    }

    // setter
    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setGrade(String grade) {
        this.grade = grade;

        // 성적 변경 시 다시 판별
        this.passed = isPassGrade();
    }

    // 수강 정보 출력
    public void printTakenCourseInfo() {

        System.out.println("===== 수강 정보 =====");

        System.out.println("과목명 : "
                + course.getCourseName());

        System.out.println("수강 학기 : "
                + semester);

        System.out.println("성적 : "
                + grade);

        System.out.println("이수 여부 : "
                + passed);
    }
}