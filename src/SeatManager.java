// 이전상태와 현재 상태를 대조

package notifyEmptySeat;

import java.io.*;
import java.util.*;

public class SeatManager {
	private Map<String, Course> previousCourses = new HashMap<>(); // 바뀌기전과목 상태를 저장해놓기
	private final String filePath; // 파일경로 들어갈곳

	// 생성자
	public SeatManager(String filePath) {
		this.filePath = filePath;
		this.previousCourses = readCourseFile();
		// 지금 현황 보여주기
		System.out.println("\n[최초 안내] 현재 자리가 비어있는 강좌 목록:");
		for (Course course : previousCourses.values()) { //
			if (course.hasVacancy()) { // 수업에 빈자리가 있으면 출력
				System.out.printf(" -> %s(%s) : 현재 %d명 / 정원 %d명 (여유 있음)\n", course.getCourseName(),
						course.getCourseId(), course.getCurrentStudents(), course.getMaxStudents());
			}
		}
	}

	// 쉼표나 탭으로 구분된 텍스트줄 읽어서 Course클래스에 저장
	public Map<String, Course> readCourseFile() {
		Map<String, Course> courses = new HashMap<>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}
				String tokens[] = line.split(",");

				if (tokens.length >= 17) { // 임시 인풋 텍스트에서 정보를 17개로 정함
					String courseId = tokens[6].trim(); // 7번째 칸: 강좌번호
					String courseName = tokens[7].trim(); // 8번째 칸: 교과목명
					int maxStudents = Integer.parseInt(tokens[15].trim()); // 16번째 칸: 수강정원
					int currentStudents = Integer.parseInt(tokens[16].trim()); // 17번째 칸: 수강신청

					Course course = new Course(courseId, courseName, maxStudents, currentStudents);
					courses.put(courseId, course);
				}
			} // while
		} // try
		catch (IOException e) {
			System.err.println("파일 읽기 실패: " + e.getMessage());
		} catch (NumberFormatException e) {
			System.err.println("숫자 변환 에러 (정원이나 신청 인원이 숫자가 아닙니다): " + e.getMessage());
		}
		return courses;
	} // readCourseFile

	// txt파일이 바뀐걸 감지했을때 작동
	public synchronized void checkChanges() {
		Map<String, Course> currentCourses = readCourseFile(); // 최신파일 읽기

		// 최신파일이랑 옛날 정보랑 비교
		for (Course cur : currentCourses.values()) {
			Course prev = previousCourses.get(cur.getCourseId()); // 옛날 과목코드 조회

			if (prev != null) { // 과목이 있으면
				// 옛날에는 꽉 찼었는데 (신청 >= 정원), 지금은 자리가 빈 경우 (신청 < 정원)
				if (prev.getCurrentStudents() >= prev.getMaxStudents() && cur.hasVacancy()) {
					System.out.printf("🔥 [빈자리 알림] %s(%s) 과목에 취소자가 발생했습니다! (현재 %d/%d)\n", cur.getCourseName(),
							cur.getCourseId(), cur.getCurrentStudents(), cur.getMaxStudents());
				}
				// 원래 남는자리 있었는데 인원이 변하긴 했지만 여전히 남는자리가 있을 때
				else if (prev.getCurrentStudents() != cur.getCurrentStudents() && cur.hasVacancy()) {
					System.out.printf("✨ [인원 변동] %s(%s) 과목 수강신청 인원 변경 (현재 %d/%d)\n", cur.getCourseName(),
							cur.getCourseId(), cur.getCurrentStudents(), cur.getMaxStudents());
				}
			}
		}
		// 대조 끝 -> 정보 갱신
		this.previousCourses = currentCourses;
	} // checkChanges

} // SeatManager
