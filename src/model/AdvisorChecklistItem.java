/**
 * 
 * @Project: 졸업 요건 확인 서비스 
 * @프로그램 설명: 
 * 	- 졸업 요건(수업 관련 제외) 중 지도 교수 상담을 확인하는 서비스
 *  - 목표치를 넘겼을 경우 달성으로 판정
 *
 * AdvisorChecklistItem.java
 *
 * @author Son Seonghoon
 *
 */
package model;
import java.util.*;

public class AdvisorChecklistItem implements ChecklistItem {
	private String title;
    private String description;
    private List<String> semester;
    private int completedCount;

    public AdvisorChecklistItem() {
    	this.title = "지도교수 상담";
    	this.description = "지도교수 상담 횟수 충족";
    	this.semester = new ArrayList<>();
    	this.completedCount = 0;
    }
    
    public AdvisorChecklistItem(String semester) {
    	this.title = "지도교수 상담";
    	this.description = "지도교수 상담 횟수 충족";
    	this.semester = new ArrayList<>();
    	this.semester.add(semester);
    	this.completedCount = 1;
    }

    @Override
    public String getTitle() { return title; }

    @Override
    public String getDescription() { return description; }

    @Override
    public boolean isCompleted() { return completedCount >= 8; }
 
    public List<String> getSemester() { return this.semester; }
    
    public void setSemester(String semester) {
    	this.semester.add(semester);
    	completedCount++;
    }
    
    public int removeSemester(int index) {
    	if (index < 0 || index >= this.semester.size()) {
    		return -1;
    	}
    	this.semester.remove(index);
    	completedCount--;
    	return 0;
    }
}
