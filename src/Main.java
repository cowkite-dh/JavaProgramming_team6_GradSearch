package ui;

import model.*;
import service.*;
import notifyEmptySeat.*;

import java.io.File;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.println("=================================================");
		System.out.println("   졸업 요건 시각화 및 빈자리 알림 시스템 시작   ");
		System.out.println("=================================================\n");

		// =================================================================
		// 1. 학생 기본 정보 입력
		// =================================================================
		System.out.println(">>> [1/5] 학생 기본 정보를 입력받습니다.");

		System.out.print("▶ 학번을 입력하세요: ");
		String studentId = scanner.nextLine();

		System.out.print("▶ 이름을 입력하세요: ");
		String name = scanner.nextLine();

		System.out.print("▶ 전공을 입력하세요: ");
		String major = scanner.nextLine();

		System.out.print("▶ 입학년도를 입력하세요 (숫자 4자리): ");
		int admissionYear = scanner.nextInt();
		scanner.nextLine();

		Student student = new Student(studentId, name, major, admissionYear);
		System.out.println("\n✅ 학생 정보 등록 완료.\n");

		// =================================================================
		// 2. 수강 과목 정보 동적 입력
		// =================================================================
		System.out.println(">>> [2/5] 수강 내역(들은 과목)을 입력받습니다.");

		while (true) {
			System.out.println("-------------------------------------------------");
			System.out.print("새로운 수강 과목을 입력하시겠습니까? (Y/N): ");
			String choice = scanner.nextLine().trim();
			if (choice.equalsIgnoreCase("N")) {
				break;
			} else if (!choice.equalsIgnoreCase("Y")) {
				continue;
			}

			System.out.print("▶ 과목 코드 (예: COMP001): ");
			String courseId = scanner.nextLine();
			System.out.print("▶ 과목명: ");
			String courseName = scanner.nextLine();
			System.out.print("▶ 학점 (숫자): ");
			int credits = scanner.nextInt();

			System.out.println("▶ 과목 유형 (1:전필, 2:전선, 3:교필, 4:교선): ");
			System.out.print("  선택: ");
			int typeChoice = scanner.nextInt();
			CourseType type = CourseType.MAJOR_ELECTIVE;
			switch (typeChoice) {
			case 1:
				type = CourseType.MAJOR_REQUIRED;
				break;
			case 2:
				type = CourseType.MAJOR_ELECTIVE;
				break;
			case 3:
				type = CourseType.GENERAL_REQUIRED;
				break;
			case 4:
				type = CourseType.GENERAL_ELECTIVE;
				break;
			}

			System.out.print("▶ 권장 학년 (1~4): ");
			int grade = scanner.nextInt();
			scanner.nextLine();

			System.out.print("▶ 수강 학기 (예: 2025-1): ");
			String semester = scanner.nextLine();

			System.out.print("▶ 취득 성적 (예: A+, F): ");
			String courseGrade = scanner.nextLine();

			// 입력받은 과목이 전필이면 required 속성을 true로 세팅
			boolean isRequired = (type == CourseType.MAJOR_REQUIRED);
			Course course = new Course(courseId, courseName, credits, type, grade, isRequired);
			student.addTakenCourse(new TakenCourse(course, semester, courseGrade));
		}

		// =================================================================
		// 3. 비교과 체크리스트 동적 관리 (신규 추가)
		// =================================================================
		System.out.println("\n>>> [3/5] 비교과 졸업요건 체크리스트 관리\n");
		ChecklistService checklist = new ChecklistService();
		checklist.setToeicTargetScore(800); // 학과 기본 목표 점수 세팅

		while (true) {
			System.out.println("\n--- 현재 체크리스트 현황 ---");
			System.out.println(checklist.printSummary());
			System.out.println("1: 토익 점수 업데이트");
			System.out.println("2: 지도교수 상담 학기 추가 (현재 기준 총 8회 달성 시 완료)");
			System.out.println("3: 자유 체크리스트 항목 추가");
			System.out.println("0: 체크리스트 관리 종료 및 다음 단계로 이동");
			System.out.print("▶ 메뉴 선택: ");

			int chkChoice = scanner.nextInt();
			scanner.nextLine();

			if (chkChoice == 0)
				break;

			switch (chkChoice) {
			case 1:
				System.out.print("현재 보유하신 토익 점수를 입력하세요: ");
				int tScore = scanner.nextInt();
				scanner.nextLine();
				checklist.setToeicActualScore(tScore);
				System.out.println("✅ 토익 점수가 반영되었습니다.");
				break;
			case 2:
				System.out.print("상담을 완료한 학기를 입력하세요 (예: 2025-1): ");
				String advSem = scanner.nextLine();
				checklist.addAdvisorSemester(advSem);
				System.out.println("✅ 지도교수 상담 내역이 추가되었습니다.");
				break;
			case 3:
				System.out.print("추가할 요건의 제목을 입력하세요: ");
				String title = scanner.nextLine();
				System.out.print("요건의 설명을 입력하세요: ");
				String desc = scanner.nextLine();

				// SimpleChecklistItem 생성 후 자유롭게 체크 상태(true) 지정 가능
				SimpleChecklistItem newItem = new SimpleChecklistItem(title, desc);

				System.out.print("이 요건을 이미 달성하셨습니까? (Y/N): ");
				String isComp = scanner.nextLine();
				if (isComp.equalsIgnoreCase("Y")) {
					newItem.setCompleted(true);
				}

				checklist.addItem(newItem);
				System.out.println("✅ 신규 요건이 추가되었습니다.");
				break;
			default:
				System.out.println("잘못된 입력입니다.");
			}
		}

		// =================================================================
		// 4. 졸업 요건 계산 및 분석 리포트 출력
		// =================================================================
		System.out.println("\n>>> [4/5] 입력된 데이터를 기반으로 졸업 요건을 분석합니다...\n");

		GraduationReq req = GraduationReq.generateRequirement(student.getAdmissionYear(), student.getMajor());

		// [문제점 2 해결] 학과 커리큘럼 상 반드시 들어야 하는 '전공 필수' 과목 코드 목록을 하드코딩 주입
		// (실제 프로젝트에서는 DB나 파일에서 읽어와야 하지만, 현재 구조상 메인에서 세팅합니다)
		req.getRequiredCourseIds().add("COMP001"); // 자바프로그래밍
		req.getRequiredCourseIds().add("COMP002"); // 데이터구조
		req.getRequiredCourseIds().add("COMP003"); // 운영체제

		GraduationService gradService = new GraduationService(student, req);
		System.out.println(gradService.generateReport());

		System.out.println("\n[비교과 요건 최종 현황]");
		System.out.println(checklist.printSummary());

		// =================================================================
		// 5. 전체 강좌 조회 및 빈자리 알림 감시 시작
		// =================================================================
		System.out.println(">>> [5/5] 빈자리 알림 시스템 연동 및 가동...\n");

		WatchlistService watchlistService = new WatchlistService();
		System.out.println(watchlistService.printAllCourses());

		// [문제점 1 해결] IDE 환경을 고려하여 현재 작업 디렉토리 기반 절대 경로로 명시
		String filePath = new File("seats.txt").getAbsolutePath();
		System.out.println("\n[감시 대상 파일 절대 경로]: " + filePath);

		SeatManager seatManager = new SeatManager(filePath);
		FileMonitor fileMonitor = new FileMonitor(filePath, seatManager);

		Thread monitorThread = new Thread(fileMonitor);
		monitorThread.setDaemon(true);
		monitorThread.start();

		System.out.println("=================================================");
		System.out.println(" 🚨 백그라운드 빈자리 감시가 동작 중입니다.");
		System.out.println(" 🚨 이클립스/인텔리제이 밖의 메모장 등 외부 에디터로 seats.txt를 열어서");
		System.out.println("    수강 인원을 정원 미만으로 수정한 뒤 저장해보세요.");
		System.out.println(" 🚨 프로그램을 종료하려면 콘솔 창에서 Enter를 누르세요.");
		System.out.println("=================================================");

		try {
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\n프로그램을 안전하게 종료합니다.");
		scanner.close();
	}
}