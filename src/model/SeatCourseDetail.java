/**
 * 
 * @Project: 졸업 요건 확인 서비스 
 * @프로그램 설명: 
 *  - Course보다 더 많은 자료를 저장하는 클래스
 *  - 년도, 학년, 학기, 태그, 대학명, 학과명, 코드, 강의명, 강의 학점
        , 실습 학점, 교수명, 시간, 건물명, 강의실, 최대학생, 현재 학생, 강의 타입
 *
 * SeatCourseDetail.java
 *
 * @author Son Seonghoon
 *
 */

package model;

/**
 * seats.txt 한 줄(강의 1개)의 전체 정보를 담는 모델
 * "과목 자체 정보"는 Course를 상속하여 재사용.
 */
public class SeatCourseDetail extends Course {

    private final int year;             // 년도
    private final int semesterNo;        // 학기
    private final String requirementTag; // 전필/전선/교필 등 원본 문자열(전공이면 출력에서 "전공"으로 묶을 수 있음)

    private final String collegeName;    // 세부대학명
    private final String departmentName; // 학과(학부)명

    private final int lectureCredits;    // 강의 학점
    private final int labCredits;        // 실습 학점

    private final String professorName;
    private final String time;
    private final String building;
    private final String room;

    private final int maxStudents;
    private final int currentStudents;

    public SeatCourseDetail(
            int year,
            int recommendedGrade,     // 권장 학년(= Course.grade로도 사용)
            int semesterNo,
            String requirementTag,
            String collegeName,
            String departmentName,
            String courseCode,
            String courseName,
            int lectureCredits,
            int labCredits,
            String professorName,
            String time,
            String building,
            String room,
            int maxStudents,
            int currentStudents,
            CourseType type
    ) {
        super(
                courseCode,
                courseName,
                lectureCredits + labCredits, // 학점 규칙
                type,
                recommendedGrade,            // 권장 학년
                false                         // required: 전공이면 필수/선택 구분을 안 쓰므로 일단 false
        );

        this.year = year;
        this.semesterNo = semesterNo;
        this.requirementTag = requirementTag;
        this.collegeName = collegeName;
        this.departmentName = departmentName;
        this.lectureCredits = lectureCredits;
        this.labCredits = labCredits;
        this.professorName = professorName;
        this.time = time;
        this.building = building;
        this.room = room;
        this.maxStudents = maxStudents;
        this.currentStudents = currentStudents;
    }

    public int getYear() { return year; }
    public int getSemesterNo() { return semesterNo; }
    public String getRequirementTag() { return requirementTag; }
    public String getCollegeName() { return collegeName; }
    public String getDepartmentName() { return departmentName; }

    public int getLectureCredits() { return lectureCredits; }
    public int getLabCredits() { return labCredits; }

    public String getProfessorName() { return professorName; }
    public String getTime() { return time; }
    public String getBuilding() { return building; }
    public String getRoom() { return room; }

    public int getMaxStudents() { return maxStudents; }
    public int getCurrentStudents() { return currentStudents; }

    public boolean hasVacancy() { return currentStudents < maxStudents; }
}
