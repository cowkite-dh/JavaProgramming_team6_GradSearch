package model;

import java.util.ArrayList;
import java.util.List;

/**
 * 학생 개인 정보, 수강 이수 정보 및 찜 목록 세션을 관리하는 통합 데이터 모델
 */
public class Student {
    private String name;
    private int    grade;               // 현재 학년 (1~4)
    private int    admissionYear;       // 입학 연도
    private int    advisorCount;        // 누적 지도교수 상담 완료 횟수
    
    // 학생별 동적 조정 가능 졸업 기준 학점 / 요건 (기본 표준값 초기화)
    private int reqTotalCredits = 130; 
    private int reqMajorCredits = 65;  
    private int reqGeneralCredits = 30; 
    private int reqAdvisorCount = 8;    
    private int registeredSemesters = 0; // 현재까지의 누적 등록 학기 수

    private final List<TakenCourse> takenCourses = new ArrayList<>(); // 이수한 과목 리스트
    private final List<Course>      watchlist    = new ArrayList<>(); // 빈자리 알림 찜 리스트

    private int toeicTarget = 800; // 목표 어학 성적
    private int toeicScore  = 0;   // 취득 어학 성적

    public Student(String name, int grade, int admissionYear, int advisorCount) {
        this.name          = name;
        this.grade         = grade;
        this.admissionYear = admissionYear;
        this.advisorCount  = advisorCount;
    }

    // 이수 과목 제어 메서드
    public void addTakenCourse(TakenCourse tc)     { takenCourses.add(tc); }
    public void removeTakenCourse(TakenCourse tc)  { takenCourses.remove(tc); }
    public List<TakenCourse> getTakenCourses()     { return takenCourses; }

    // 찜 목록(모니터링 대상) 제어 메서드
    public void addWatchlist(Course c) {
        if (!watchlist.contains(c)) {
            watchlist.add(c);
        }
    }
    public void removeWatchlist(Course c) { watchlist.remove(c); }
    public List<Course> getWatchlist()    { return watchlist; }

    // 취득 누적 총 학점 계산 (F등급 제외 반영)
    public int getTotalCredits() {
        return takenCourses.stream().filter(TakenCourse::isPassed).mapToInt(tc -> tc.getCourse().getCredits()).sum();
    }

    // 취득 누적 전공 학점 계산
    public int getMajorCredits() {
        return takenCourses.stream().filter(TakenCourse::isPassed).filter(tc -> tc.getCourse().getType().isMajor()).mapToInt(tc -> tc.getCourse().getCredits()).sum();
    }

    // 취득 누적 교양 학점 계산
    public int getGeneralCredits() {
        return takenCourses.stream().filter(TakenCourse::isPassed).filter(tc -> tc.getCourse().getType().isGeneral()).mapToInt(tc -> tc.getCourse().getCredits()).sum();
    }

    // 전체 수강 과목 평점 평균(GPA) 정밀 계산
    public double getGpa() {
        double totalPoints = 0;
        int creditCount = 0;
        for (TakenCourse tc : takenCourses) {
            String g = tc.getGrade().trim().toUpperCase();
            if ("P".equals(g)) continue; // Pass 과목은 평점 산출 계산식에서 제외
            
            int credits = tc.getCourse().getCredits();
            totalPoints += tc.getGradePoint() * credits;
            creditCount += credits;
        }
        return creditCount == 0 ? 0.0 : totalPoints / creditCount;
    }

    // Getters & Setters
    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }

    public int  getGrade()                     { return grade; }
    public void setGrade(int g)                { this.grade = g; }

    public int  getAdmissionYear()             { return admissionYear; }
    public void setAdmissionYear(int y)        { this.admissionYear = y; }

    public int  getAdvisorCount()              { return advisorCount; }
    public void setAdvisorCount(int c)         { this.advisorCount = c; }

    public int  getToeicTarget()               { return toeicTarget; }
    public void setToeicTarget(int t)          { this.toeicTarget = t; }

    public int  getToeicScore()                { return toeicScore; }
    public void setToeicScore(int s)           { this.toeicScore = s; }
    
    public int getReqTotalCredits()            { return reqTotalCredits; }
    public void setReqTotalCredits(int r)      { this.reqTotalCredits = r; }

    public int getReqMajorCredits()            { return reqMajorCredits; }
    public void setReqMajorCredits(int r)      { this.reqMajorCredits = r; }

    public int getReqGeneralCredits()          { return reqGeneralCredits; }
    public void setReqGeneralCredits(int r)    { this.reqGeneralCredits = r; }

    public int getReqAdvisorCount()            { return reqAdvisorCount; }
    public void setReqAdvisorCount(int r)      { this.reqAdvisorCount = r; }

    public int getRegisteredSemesters()        { return registeredSemesters; }
    public void setRegisteredSemesters(int s)  { this.registeredSemesters = s; }
}