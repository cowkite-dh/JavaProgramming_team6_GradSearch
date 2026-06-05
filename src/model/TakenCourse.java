package model;

/**
 * 학생이 실제로 수강 완료한 교과목 정보 및 취득한 성적 레코드를 매칭하는 모델
 */
public class TakenCourse {
    private final Course course;   
    private final String semester; // 수강 학기 (예: "2025-1학기")
    private String grade;          // 취득 성적 (A+, B0, F, P 등)

    public TakenCourse(Course course, String semester, String grade) {
        this.course   = course;
        this.semester = semester;
        this.grade    = grade;
    }

    /** F학점을 받지 않았다면 정상 이수로 간주 */
    public boolean isPassed() {
        return !"F".equalsIgnoreCase(grade.trim());
    }

    /** 취득 문자 성적을 대학 표준 4.3 만점 기준 가중치 점수로 환산 */
    public double getGradePoint() {
        switch (grade.trim().toUpperCase()) {
            case "A+": return 4.3;
            case "A0": case "A": return 4.0;
            case "A-": return 3.7;
            case "B+": return 3.3;
            case "B0": case "B": return 3.0;
            case "B-": return 2.7;
            case "C+": return 2.3;
            case "C0": case "C": return 2.0;
            case "C-": return 1.7;
            case "D+": return 1.3;
            case "D0": case "D": return 1.0;
            case "D-": return 0.7;
            default: return 0.0; // F 또는 Pass(P) 과목은 평점 0.0 처리
        }
    }

    public Course getCourse()   { return course; }
    public String getSemester() { return semester; }
    public String getGrade()    { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
}