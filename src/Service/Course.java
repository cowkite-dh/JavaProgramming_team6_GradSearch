/*
 * 기본적인 학생 정보, 수강, 성적 관련 정보 선언
 * 조건이 되는 getter들 다른 코드에서 호출하는 방식 
 */

package model;

public class Course {

    // 과목 코드
    private String courseId;

    // 과목명
    private String courseName;

    // 학점
    private int credits;

    // 과목 종류 (전공필수, 교양 등)
    private CourseType type;

    // 권장 학년 (1~4)
    private int grade;

    // 필수 여부
    private boolean required;

    // 생성자
    public Course(String courseId, String courseName,
                  int credits, CourseType type,
                  int grade, boolean required) {

        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
        this.type = type;
        this.grade = grade;
        this.required = required;
    }

    // getter
    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCredits() {
        return credits;
    }

    public CourseType getType() {
        return type;
    }

    public int getGrade() {
        return grade;
    }

    public boolean isRequired() {
        return required;
    }

    // setter
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setType(CourseType type) {
        this.type = type;
    }

    public void setGrade(int grade) {
        if (grade < 1 || grade > 4) {
            throw new IllegalArgumentException("학년은 1~4만 가능합니다.");
        }

    this.grade = grade;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    // 과목 정보 출력
    public void printCourseInfo() {

        System.out.println("===== 과목 정보 =====");
        System.out.println("과목 코드 : " + courseId);
        System.out.println("과목명 : " + courseName);
        System.out.println("학점 : " + credits);
        System.out.println("과목 유형 : " + type);
        System.out.println("권장 학년 : " + grade + "학년");
        System.out.println("필수 여부 : " + required);
    }
}
