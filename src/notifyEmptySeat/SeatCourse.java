// 임시소스파일
// 과목정보저장

package notifyEmptySeat;

public class SeatCourse {
	private final String courseId; // 강좌번호 (예: "CLTR003")
	private final String courseName; // 교과목명 (예: "자바프로그래밍")
	private final int maxStudents; // 수강정원 (예: 40)
	private final int currentStudents; // 수강신청 인원 (예: 39)

	// 과목 주머니를 만들 때 정보를 채워 넣는 곳입니다.
	public SeatCourse(String courseId, String courseName, int maxStudents, int currentStudents) {
		this.courseId = courseId;
		this.courseName = courseName;
		this.maxStudents = maxStudents;
		this.currentStudents = currentStudents;
	}

	// 다른 클래스에서 정보를 꺼내갈 수 있게 통로(Getter)를 열어줍니다.
	public String getCourseId() {
		return courseId;
	}

	public String getCourseName() {
		return courseName;
	}

	public int getMaxStudents() {
		return maxStudents;
	}

	public int getCurrentStudents() {
		return currentStudents;
	}

	// 자리가 남아있는지 확인하는 편리한 기능입니다.
	public boolean hasVacancy() {
		return currentStudents < maxStudents;
	}
}
