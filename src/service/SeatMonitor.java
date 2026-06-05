package service;

import model.Course;
import model.Student;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * 찜 목록에 등록된 강좌들의 실시간 빈자리 현황을 관측하고 이벤트 알림을 분배하는 스케줄러 서비스
 */
public class SeatMonitor {

    /** 빈자리 감지 발생 알림 이벤트 전송용 DTO 내부 클래스 */
    public static class VacancyEvent {
        public final Course course;
        public final int    vacancy;
        public VacancyEvent(Course c, int v) { 
            this.course = c; 
            this.vacancy = v; 
        }
    }

    private final CourseRepository repo;
    private final Student          student;
    private final List<Consumer<List<VacancyEvent>>> listeners = new ArrayList<>();

    // 스레드 안전성을 위해 ConcurrentHashMap 활용해 과거 인원 상태 버퍼 보존
    private final Map<String, Integer> prevStudents = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;
    private static final int INTERVAL_SECONDS = 60; // 1분 간격 폴링 주기

    public SeatMonitor(CourseRepository repo, Student student) {
        this.repo    = repo;
        this.student = student;
        snapshotWatchlist();
    }

    public void addListener(Consumer<List<VacancyEvent>> listener) {
        listeners.add(listener);
    }

    /** 백그라운드 1분 타이머 자동 동기화 모니터링 기동 */
    public synchronized void startAuto() {
        if (scheduler != null) return;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                repo.reload(); // 레포지토리 새로고침 진행
                checkNow();
            } catch (Exception e) {
                System.err.println("[SeatMonitor] 자동 폴링 예외 처리 발생: " + e.getMessage());
            }
        }, INTERVAL_SECONDS, INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    /** 자동 폴링 모니터링 안전 중지 및 스케줄러 폐기 */
    public synchronized void stopAuto() {
        if (scheduler == null) return;
        scheduler.shutdown();
        scheduler = null;
    }

    /** 실시간 수동 수강 상태 비교 연산 및 데이터 무결성 갱신 */
    public synchronized List<VacancyEvent> checkNow() {
        List<VacancyEvent> events = new ArrayList<>();

        for (Course watched : student.getWatchlist()) {
            repo.findByKey(watched.getKey()).ifPresent(latest -> {
                int prev = prevStudents.getOrDefault(latest.getKey(), latest.getCurrentStudents());
                boolean wasFullOrHigher = prev >= latest.getMaxStudents();
                boolean nowVacant       = latest.hasVacancy();

                // 만석이었다가 여석이 발생했거나, 자리가 있는 상태에서 추가 여석이 늘어난 경우 검출
                if (nowVacant && (wasFullOrHigher || latest.getCurrentStudents() < prev)) {
                    events.add(new VacancyEvent(latest, latest.getVacancy()));
                }
                prevStudents.put(latest.getKey(), latest.getCurrentStudents());
                watched.setCurrentStudents(latest.getCurrentStudents()); // 동기화 반영
            });
        }

        if (!events.isEmpty()) {
            notifyListeners(events);
        }
        return events;
    }

    public synchronized List<Course> getWatchlistStatus() {
        return Collections.unmodifiableList(student.getWatchlist());
    }

    private void snapshotWatchlist() {
        for (Course c : student.getWatchlist()) {
            prevStudents.put(c.getKey(), c.getCurrentStudents());
        }
    }

    private void notifyListeners(List<VacancyEvent> events) {
        for (Consumer<List<VacancyEvent>> l : listeners) {
            try { 
                l.accept(events); 
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
        }
    }
}