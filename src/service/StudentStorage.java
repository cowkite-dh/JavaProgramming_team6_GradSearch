package service;

import model.Student;
import model.TakenCourse;
import model.Course;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 텍스트 파일(user.txt)에 매핑된 학생 프로필 및 학적 상태 세션 정보를 영구 보존/파싱하는 영속성 입출력 클래스
 * * [포맷 정의 가이드]
 * NAME=홍길동
 * GRADE=2
 * ...
 * TAKEN=과목키|학기|성적
 * WATCH=과목키
 */
public class StudentStorage {

    private static final String FILE_NAME = "user.txt";
    private final String dataDir;
    private final CourseRepository repo;

    public StudentStorage(String dataDir, CourseRepository repo) {
        this.dataDir = dataDir;
        this.repo    = repo;
    }

    public boolean exists() {
        return new File(dataDir, FILE_NAME).exists();
    }

    /** 파일 데이터를 스트림으로 로드하여 학생 객체 정보 재구성 */
    public Student load() {
        File f = new File(dataDir, FILE_NAME);
        Map<String, String> kv = new HashMap<>();
        List<String> takenLines = new ArrayList<>();
        List<String> watchKeys  = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                int idx = line.indexOf('=');
                if (idx < 0) continue;

                String key = line.substring(0, idx).trim();
                String val = line.substring(idx + 1).trim();

                if ("TAKEN".equals(key)) {
                    takenLines.add(val);
                } else if ("WATCH".equals(key)) {
                    watchKeys.add(val);
                } else {
                    kv.put(key, val);
                }
            }

            if (kv.isEmpty()) return null;

            String name = kv.getOrDefault("NAME", "미명");
            int grade   = parseInt(kv, "GRADE", 1);
            int admin   = parseInt(kv, "ADMISSION", 2026);
            int adv     = parseInt(kv, "ADVISOR", 0);

            Student s = new Student(name, grade, admin, adv);
            s.setToeicTarget(parseInt(kv, "TOEIC_TARGET", 800));
            s.setToeicScore(parseInt(kv, "TOEIC_SCORE", 0));
            
            s.setReqTotalCredits(parseInt(kv, "REQ_TOTAL", 130));
            s.setReqMajorCredits(parseInt(kv, "REQ_MAJOR", 65));
            s.setReqGeneralCredits(parseInt(kv, "REQ_GENERAL", 30));
            s.setReqAdvisorCount(parseInt(kv, "REQ_ADVISOR", 8));
            s.setRegisteredSemesters(parseInt(kv, "REGISTERED_SEMESTERS", 0));

            // 이수 완료 수강 과목 연동 복구 복원
            for (String tl : takenLines) {
                String[] p = tl.split("\\|");
                if (p.length < 3) continue;
                String key = p[0];
                String sem = p[1];
                String grd = p[2];

                repo.findByKey(key).ifPresent(c -> s.addTakenCourse(new TakenCourse(c, sem, grd)));
            }

            // 알림 찜 목록 정보 리스트 바인딩 복원
            for (String wk : watchKeys) {
                repo.findByKey(wk).ifPresent(s::addWatchlist);
            }

            return s;

        } catch (IOException e) {
            System.err.println("[StudentStorage] 불러오기 실패: " + e.getMessage());
            return null;
        }
    }

    /** 런타임 상의 학생 인스턴스 최신 요건 값들을 타겟 포맷 텍스트 파일로 안전 덤프 저장 */
    public void save(Student s) {
        File dir = new File(dataDir);
        if (!dir.exists()) dir.mkdirs();

        File f = new File(dir, FILE_NAME);
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
            
            pw.println("# 학생 계정 개인정보 프로필");
            pw.println("NAME="            + s.getName());
            pw.println("GRADE="           + s.getGrade());
            pw.println("ADMISSION="       + s.getAdmissionYear());
            pw.println("ADVISOR="         + s.getAdvisorCount());
            pw.println("TOEIC_TARGET="    + s.getToeicTarget());
            pw.println("TOEIC_SCORE="     + s.getToeicScore());
            
            pw.println("REQ_TOTAL="       + s.getReqTotalCredits());
            pw.println("REQ_MAJOR="       + s.getReqMajorCredits());
            pw.println("REQ_GENERAL="     + s.getReqGeneralCredits());
            pw.println("REQ_ADVISOR="     + s.getReqAdvisorCount());
            pw.println("REGISTERED_SEMESTERS=" + s.getRegisteredSemesters());
            
            pw.println();
            pw.println("# 이수 과목 내역 세션 (과목키|학기|성적)");
            for (TakenCourse tc : s.getTakenCourses()) {
                pw.printf("TAKEN=%s|%s|%s%n", tc.getCourse().getKey(), tc.getSemester(), tc.getGrade());
            }
            
            pw.println();
            pw.println("# 모니터링 타겟 찜 과목 목록");
            for (Course c : s.getWatchlist()) {
                pw.println("WATCH=" + c.getKey());
            }
        } catch (IOException e) {
            System.err.println("[StudentStorage] 파일 스토리지 저장 오류: " + e.getMessage());
        }
    }

    private int parseInt(Map<String, String> map, String key, int def) {
        try { 
            return Integer.parseInt(map.getOrDefault(key, String.valueOf(def))); 
        } catch (Exception e) { 
            return def; 
        }
    }
}