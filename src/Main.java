import model.Student;
import service.*;
import ui.MainWindow;

import javax.swing.*;
import java.util.Scanner;

/**
 * 어플리케이션 구동용 메인 엔트리 클래스
 */
public class Main {

    private static final String DATA_DIR = "src/data";

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("  경북대 졸업 요건 확인 시스템");
        System.out.println("====================================");
        System.out.println("[시스템] 데이터 초기화 및 로딩 중... (" + DATA_DIR + ")");
        
        CourseRepository repo = new CourseRepository(DATA_DIR);
        StudentStorage storage = new StudentStorage(DATA_DIR, repo);
        Student student;

        // 파일 스토리지 검증 및 계정 로드 흐름 구성
        if (storage.exists()) {
            student = storage.load();
            if (student == null) {
                System.out.println("[시스템] 세션 파일이 손상되었습니다. 새로 구축합니다.");
                student = inputNewStudent(repo, storage);
            } else {
                System.out.println("[시스템] 기존 복원 성공, 환영합니다: " + student.getName() + "님");
            }
        } else {
            System.out.println("[시스템] 등록 기록이 없습니다. 콘솔 초기 등록 작업을 수행합니다.");
            student = inputNewStudent(repo, storage);
        }

        // GUI 운영체제 일치형 룩앤필 바인딩 및 대시보드 메인 윈도우 스레드 오픈
        Student finalStudent = student;
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            
            SeatMonitor monitor = new SeatMonitor(repo, finalStudent);
            MainWindow win = new MainWindow(finalStudent, repo, storage, monitor);
            win.setVisible(true);
        });
    }

    /** 최초 사용자 콘솔 텍스트 기반 계정 설정 헬퍼 */
    private static Student inputNewStudent(CourseRepository repo, StudentStorage storage) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- 신규 학생 계정 생성 절차 ---");
        
        System.out.print("이름 입력: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) name = "기본학생";

        System.out.print("현재 학년 (1~4): ");
        int grade = parseInt(sc, 1, 1, 4);

        System.out.print("입학 연도 (예: 2022): ");
        int admissionYear = parseInt(sc, 2022, 2000, 2030);

        System.out.print("지도교수 상담 완료 횟수 (현재까지): ");
        int advisorCount = parseInt(sc, 0, 0, 100);

        System.out.print("TOEIC 목표 기준 점수 (기본 800): ");
        String toeicTargetStr = sc.nextLine().trim();
        int toeicTarget = toeicTargetStr.isEmpty() ? 800 : parseIntStr(toeicTargetStr, 800);

        System.out.print("현재 취득 완료 토익 점수 (없으면 0): ");
        int toeicScore = parseInt(sc, 0, 0, 990);

        Student student = new Student(name, grade, admissionYear, advisorCount);
        student.setToeicTarget(toeicTarget);
        student.setToeicScore(toeicScore);

        storage.save(student); // 영속성 저장 기록 완료
        System.out.printf("[알림] %s님의 마스터 프로필 파일 생성이 정상 완료되었습니다.%n%n", name);
        return student;
    }

    private static int parseInt(Scanner sc, int def, int min, int max) {
        String line = sc.nextLine().trim();
        return parseIntStr(line, def, min, max);
    }

    private static int parseIntStr(String s, int def) {
        try { 
            return Integer.parseInt(s); 
        } catch (NumberFormatException e) { 
            return def; 
        }
    }

    private static int parseIntStr(String s, int def, int min, int max) {
        try {
            int v = Integer.parseInt(s);
            if (v < min || v > max) return def;
            return v;
        } catch (NumberFormatException e) {
            return def;
        }
    }
}