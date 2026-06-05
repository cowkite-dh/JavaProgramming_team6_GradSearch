package model;

/**
 * 대학 교과목 이수구분 분류 및 매칭 열거형
 */
public enum CourseType {

    // 전공 영역 분류
    MAJOR_REQUIRED("전공필수"),
    MAJOR_CORE("공학전공"),        // IT대학 심화공학 전공선택군
    MAJOR_ELECTIVE("전공"),        // 일반 전공선택
    MAJOR_BASIC("전공기초"),
    MAJOR_ADVANCED("전공심화"),
    MAJOR_BASE("전공기반"),

    // 교양 영역 분류
    GENERAL("교양"),
    BASIC_LITERACY("기본소양"),
    COMMON_BASIC("기초공통"),

    // 기타 영역 분류
    TEACHER("교직"),
    LINKED_MAJOR("연계전공"),
    CONVERGENCE("융합전공"),
    FREE_ELECTIVE("일반선택");

    private final String csvLabel; // CSV 원본 텍스트 매칭 레이블

    CourseType(String csvLabel) {
        this.csvLabel = csvLabel;
    }

    public String getCsvLabel() { return csvLabel; }
    public String getDisplayName() { return csvLabel; }

    /**
     * CSV 원본 한글 레이블 문자열을 매칭되는 열거형 상수로 변환
     * @param label 원본 이수구분 텍스트
     * @return 매칭 실패 시 기본값으로 FREE_ELECTIVE(일반선택) 반환
     */
    public static CourseType fromCsvLabel(String label) {
        if (label == null) return FREE_ELECTIVE;
        String trimmed = label.trim();
        for (CourseType t : values()) {
            if (t.csvLabel.equals(trimmed)) return t;
        }
        return FREE_ELECTIVE;
    }

    /** 전공 계열에 속하는지 판정 */
    public boolean isMajor() {
        return this == MAJOR_REQUIRED || this == MAJOR_CORE || this == MAJOR_ELECTIVE 
            || this == MAJOR_BASIC || this == MAJOR_ADVANCED || this == MAJOR_BASE;
    }

    /** 교양 계열에 속하는지 판정 */
    public boolean isGeneral() {
        return this == GENERAL || this == BASIC_LITERACY || this == COMMON_BASIC;
    }
}