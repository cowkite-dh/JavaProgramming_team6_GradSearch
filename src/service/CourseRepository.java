package service;

import model.Course;
import model.CourseType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 데이터 디렉토리 내부의 시간표 개설 정보 수집 및 다건 CSV 파일 통합 인메모리 저장소
 */
public class CourseRepository {

    private final String dataDir;
    private final List<Course> allCourses = new ArrayList<>();

    // CSV 원본 데이터 정렬 배치 순서 인덱스 (0-based)
    private static final int COL_YEAR       = 1;
    private static final int COL_SEMESTER   = 2;
    private static final int COL_COLLEGE    = 3;
    private static final int COL_DEPT       = 4;
    private static final int COL_TYPE       = 5;
    private static final int COL_GRADE      = 6;
    private static final int COL_CODE       = 7;
    private static final int COL_SECTION    = 8;
    private static final int COL_NAME       = 9;
    private static final int COL_CREDITS    = 10;
    private static final int COL_PROFESSOR  = 11;
    private static final int COL_SCHEDULE   = 12;
    private static final int COL_ROOM       = 13;
    private static final int COL_MAX        = 14;
    private static final int COL_CURRENT    = 15;

    public CourseRepository(String dataDir) {
        this.dataDir = dataDir;
        reload();
    }

    /** 데이터 디렉토리에서 CSV 파일을 순회하여 파싱 작업 진행 (초기화 및 새로고침 연동) */
    public synchronized void reload() {
        allCourses.clear();
        File dir = new File(dataDir);
        if (!dir.exists() || !dir.isDirectory()) return;

        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".csv"));
        if (files == null) return;

        for (File f : files) {
            parseCsvFile(f);
        }
    }

    private void parseCsvFile(File file) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String line = br.readLine(); // 헤더 행 무시 처리
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (tokens.length <= COL_CURRENT) continue;

                Course c = new Course(
                    safe(tokens, COL_CODE),
                    safe(tokens, COL_SECTION),
                    safe(tokens, COL_NAME),
                    parseInt(tokens, COL_CREDITS, 0),
                    CourseType.fromCsvLabel(safe(tokens, COL_TYPE)),
                    parseInt(tokens, COL_GRADE, 0),
                    safe(tokens, COL_PROFESSOR),
                    safe(tokens, COL_SCHEDULE),
                    safe(tokens, COL_ROOM),
                    parseInt(tokens, COL_MAX, 0),
                    parseInt(tokens, COL_CURRENT, 0),
                    safe(tokens, COL_DEPT),
                    safe(tokens, COL_COLLEGE),
                    safe(tokens, COL_YEAR),
                    safe(tokens, COL_SEMESTER)
                );
                allCourses.add(c);
            }
        } catch (IOException e) {
            System.err.println("[CourseRepository] 파일 로딩 실패: " + file.getName() + " -> " + e.getMessage());
        }
    }

    public synchronized List<Course> getAll() {
        return new ArrayList<>(allCourses);
    }

    /** 과목코드를 바탕으로 1차 선행 정보 매칭 조회 */
    public synchronized Optional<Course> findById(String courseId) {
        return allCourses.stream()
                .filter(c -> c.getCourseId().equalsIgnoreCase(courseId))
                .findFirst();
    }

    /** 과목코드와 분반 조합 키(Key)를 이용해 유일한 개설 강좌 정밀 조회 */
    public synchronized Optional<Course> findByKey(String key) {
        return allCourses.stream()
                .filter(c -> c.getKey().equals(key))
                .findFirst();
    }

    public synchronized List<String> getLoadedYears() {
        return allCourses.stream()
                .map(Course::getYear).distinct().sorted()
                .collect(Collectors.toList());
    }

    /** 문자열 전처리 유틸: 큰따옴표 이스케이프 및 불필요한 HTML 태그 안전 청소 */
    private String safe(String[] arr, int idx) {
        if (idx >= arr.length) return "";
        String s = arr[idx].trim();
        
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1).trim();
        }
        return s.replaceAll("<[^>]+>", "").trim();
    }

    private int parseInt(String[] arr, int idx, int def) {
        try { 
            return Integer.parseInt(safe(arr, idx)); 
        } catch (NumberFormatException e) { 
            return def; 
        }
    }
}