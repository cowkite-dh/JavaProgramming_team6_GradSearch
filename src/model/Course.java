package model;

/**
 * 교과목 정보(분반 단위)를 관리하는 도메인 모델
 * - 과목코드와 분반의 조합(courseId-section)을 고유 키로 사용합니다.
 */
public class Course {
    private final String courseId;        // 과목코드 (예: COMP0217)
    private final String section;         // 분반 (예: 001)
    private final String courseName;      // 과목명
    private final int    credits;         // 학점
    private final CourseType type;        // 이수구분
    private final int    grade;           // 권장 학년 (0: 미지정)
    private final String professor;       // 담당교수
    private final String schedule;        // 강의시간
    private final String room;            // 강의실
    private final int    maxStudents;     // 수강정원
    private int          currentStudents; // 수강신청인원 (실시간 변동)
    private final String department;      // 개설학과
    private final String college;         // 개설대학
    private final String year;            // 개설년도
    private final String semester;        // 개설학기

    public Course(String courseId, String section, String courseName, int credits,
                  CourseType type, int grade, String professor, String schedule,
                  String room, int maxStudents, int currentStudents,
                  String department, String college, String year, String semester) {
        this.courseId        = courseId;
        this.section         = section;
        this.courseName      = courseName;
        this.credits         = credits;
        this.type            = type;
        this.grade           = grade;
        this.professor       = professor;
        this.schedule        = schedule;
        this.room            = room;
        this.maxStudents     = maxStudents;
        this.currentStudents = currentStudents;
        this.department      = department;
        this.college         = college;
        this.year            = year;
        this.semester        = semester;
    }

    /** 과목코드와 분반을 조합한 고유 식별 키 생성 */
    public String getKey() { 
        return courseId + "-" + section; 
    }

    /** 현재 수강 여석 존재 여부 확인 */
    public boolean hasVacancy() { 
        return currentStudents < maxStudents; 
    }

    /** 잔여 여석 수 계산 */
    public int getVacancy() { 
        return Math.max(0, maxStudents - currentStudents); 
    }

    public String     getCourseId()        { return courseId; }
    public String     getSection()         { return section; }
    public String     getCourseName()      { return courseName; }
    public int        getCredits()         { return credits; }
    public CourseType getType()            { return type; }
    public int        getGrade()           { return grade; }
    public String     getProfessor()       { return professor; }
    public String     getSchedule()        { return schedule; }
    public String     getRoom()            { return room; }
    public int        getMaxStudents()     { return maxStudents; }
    public int        getCurrentStudents() { return currentStudents; }
    public String     getDepartment()      { return department; }
    public String     getCollege()         { return college; }
    public String     getYear()            { return year; }
    public String     getSemester()        { return semester; }

    public void setCurrentStudents(int n)  { this.currentStudents = n; }

    @Override
    public String toString() {
        return String.format("[%s-%s] %s (%s, %d학점)", courseId, section, courseName, type.getDisplayName(), credits);
    }
}