package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraduationReq {
    private Map<CourseType, Integer> minCredits; // 영역별 최소 이수 학점
    private int totalRequired;                   // 총 졸업 학점
    private List<String> requiredCourseIds;      // 필수 수강 과목 ID 리스트
    private String major;                        // 기준 전공

    // 사진의 요건을 반영하기 위해 다이어그램 외에 추가로 필요한 필드
    private double minGpa;                       // 졸업 최소 평점평균

    // 기본 생성자
    public GraduationReq(String major) {
        this.major = major;
        this.minCredits = new HashMap<>();
        this.requiredCourseIds = new ArrayList<>();
    }

    /**
     * 첨부된 학사 규정(사진)을 바탕으로 학번(입학년도)과 학과에 따른 
     * 졸업 요건 객체를 생성하여 반환하는 팩토리 메서드입니다.
     */
    public static GraduationReq generateRequirement(int admissionYear, String major) {
        GraduationReq req = new GraduationReq(major);

        // 1. 총 졸업 학점 (2008학번 기준)
        if (admissionYear >= 2008) {
            req.setTotalRequired(130);
        } else {
            req.setTotalRequired(140);
        }

        // 2. 전공 학점 (2018, 2012, 2011 기준 - 최소 전공 기준치로 합산)
        int majorCredits = 0;
        if (admissionYear >= 2018) {
            majorCredits = 51;
        } else if (admissionYear >= 2012) {
            majorCredits = 45;
        } else {
            majorCredits = 35;
        }
        // 다이어그램 구조상 전공 이수 학점을 MAJOR_REQUIRED 맵에 저장해둡니다. (필요시 분리 가능)
        req.getMinCredits().put(CourseType.MAJOR_REQUIRED, majorCredits); 

        // 3. 교양 학점 (2018~2022학번 / 그 외)
        int generalCredits = 0;
        if (admissionYear >= 2018 && admissionYear <= 2022) {
            generalCredits = 24; // 24학점 이상 42학점 이하이므로 최소 요건은 24
        } else {
            generalCredits = 30; // 2017 이전, 2023 이후 최소 30학점
        }
        req.getMinCredits().put(CourseType.GENERAL_REQUIRED, generalCredits);

        // 4. 평점평균 (기본 1.7 이상)
        double gpa = 1.7;
        if (major.contains("사범") && admissionYear >= 2009) {
            gpa = 1.9;
        } else if (major.contains("약학")) {
            gpa = 2.0;
        } else if (major.contains("경영") && admissionYear >= 2013) {
            gpa = 2.0;
        }
        req.setMinGpa(gpa);

        return req;
    }

    // --- Getters & Setters ---

    public Map<CourseType, Integer> getMinCredits() { return minCredits; }
    public void setMinCredits(Map<CourseType, Integer> minCredits) { this.minCredits = minCredits; }

    public int getTotalRequired() { return totalRequired; }
    public void setTotalRequired(int totalRequired) { this.totalRequired = totalRequired; }

    public List<String> getRequiredCourseIds() { return requiredCourseIds; }
    public void setRequiredCourseIds(List<String> requiredCourseIds) { this.requiredCourseIds = requiredCourseIds; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public double getMinGpa() { return minGpa; }
    public void setMinGpa(double minGpa) { this.minGpa = minGpa; }
}