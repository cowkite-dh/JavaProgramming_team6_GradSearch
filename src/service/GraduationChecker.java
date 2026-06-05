package service;

import model.Student;
import model.TakenCourse;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 학생 정보를 분석하여 일반 및 조기 졸업 기준 도달 여부를 감정/판정하는 연산 모듈
 */
public class GraduationChecker {

    private final Student student;
    public static final int REQ_ADVISOR = 8; // 기본 디폴트 지도교수 상담 필수 기준 횟수

    public GraduationChecker(Student student) {
        this.student = student;
    }

    public int getReqTotal()   { return student.getReqTotalCredits(); }
    public int getReqMajor()   { return student.getReqMajorCredits(); }
    public int getReqGeneral() { return student.getReqGeneralCredits(); }
    public int getReqAdvisor() { return student.getReqAdvisorCount(); }

    public int getTotalCredits()   { return student.getTotalCredits(); }
    public int getMajorCredits()   { return student.getMajorCredits(); }
    public int getGeneralCredits() { return student.getGeneralCredits(); }
    public double getGpa()         { return student.getGpa(); }

    /** 교내 필수 지정 영어(실용영어1, 실용영어2) 교과 이수 여부 체크 */
    public boolean hasPracticalEnglish() {
        boolean eng1 = false;
        boolean eng2 = false;
        for (TakenCourse tc : student.getTakenCourses()) {
            if (tc.isPassed()) {
                String name = tc.getCourse().getCourseName().replaceAll(" ", "");
                if (name.contains("실용영어1")) eng1 = true;
                if (name.contains("실용영어2")) eng2 = true;
            }
        }
        return eng1 && eng2;
    }

    /** 공인 어학시험 통과(토익 기준 점수 이상 획득) 또는 실용영어 필수 교과 이수 여부 통합 판단 */
    public boolean isEnglishReqMet() {
        boolean hasToeicPassed = student.getToeicScore() >= student.getToeicTarget();
        return hasToeicPassed || hasPracticalEnglish();
    }

    /** 영역별 달성 비율 통계를 기반으로 전체 종합 진척률 산출 (최대 100%) */
    public double getCompletionRate() {
        double total   = Math.min(1.0, (double) getTotalCredits() / getReqTotal());
        double major   = Math.min(1.0, (double) getMajorCredits() / getReqMajor());
        double general = Math.min(1.0, (double) getGeneralCredits() / getReqGeneral());
        double advisor = Math.min(1.0, (double) student.getAdvisorCount() / getReqAdvisor());
        double english = isEnglishReqMet() ? 1.0 : 0.0;
        
        double semester = Math.min(1.0, (double) student.getRegisteredSemesters() / 8.0);

        return (total + major + general + advisor + english + semester) / 6.0;
    }

    /** 학점, 영어, 상담 등의 기본 비즈니스 정량적 충족 여부 판정 */
    private boolean hasMetBaseRequirements() {
        return getTotalCredits() >= getReqTotal()
            && getMajorCredits() >= getReqMajor()
            && getGeneralCredits() >= getReqGeneral()
            && student.getAdvisorCount() >= getReqAdvisor()
            && isEnglishReqMet();
    }

    /** 일반 졸업 여부 판정 (기본 조건 충족 및 8학기 이상 정규 등록 조건 만족 여부 확인) */
    public boolean canGraduate() {
        return hasMetBaseRequirements() 
            && student.getRegisteredSemesters() >= 8;
    }

    /** 조기 졸업 여부 판정 (기본 요건 조기 달성 및 평점 3.7 이상, 6학기 이상 등록 요건 충족 여부 확인) */
    public boolean canGraduateEarly() {
        return hasMetBaseRequirements() 
            && student.getGpa() >= 3.7 
            && student.getRegisteredSemesters() >= 6;
    }

    /** 대시보드 표(JTable) 구성용 통합 요약 맵 생성 및 반환 */
    public Map<String, int[]> getSummary() {
        Map<String, int[]> map = new LinkedHashMap<>();
        map.put("총 학점",   new int[]{ getTotalCredits(),   getReqTotal() });
        map.put("전공 학점", new int[]{ getMajorCredits(),   getReqMajor() });
        map.put("교양 학점", new int[]{ getGeneralCredits(), getReqGeneral() });
        return map;
    }
}