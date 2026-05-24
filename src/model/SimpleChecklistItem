/**
 * 
 * @Project: 졸업 요건 확인 서비스 
 * @프로그램 설명: 
 * 	- 졸업 요건(수업 관련 제외) 중 지도 교수 및 토익을 제외한 요건을 확인하는 서비스
 *  - 체크리스트 형식으로 만들어 자유롭게 완/미완료 체크 가능
 *
 * AdvisorChecklistItem.java
 *
 * @author Son Seonghoon
 *
 */
package model;

public class SimpleChecklistItem implements ChecklistItem {
    private String title;
    private String description;
    private boolean completed;

    public SimpleChecklistItem(String title, String description) {
        this.title = title;
        this.description = description;
        this.completed = false;
    }

    @Override
    public String getTitle() { return title; }

    @Override
    public String getDescription() { return description; }

    @Override
    public boolean isCompleted() { return completed; }

    // 사용자가 자유롭게 체크/해제
    public void setCompleted(boolean completed) { this.completed = completed; }
}
