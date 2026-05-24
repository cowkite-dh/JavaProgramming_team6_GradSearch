/**
 * 
 * @Project: 졸업 요건 확인 서비스 
 * @프로그램 설명: 
 * 	- 졸업 요건(수업 관련 제외) 중 토익 점수를 확인하는 서비스
 *  - 목표치를 넘겼을 경우 달성으로 판정
 *
 * ToeicChecklistItem.java
 *
 * @author Son Seonghoon
 *
 */
package model;

public class ToeicChecklistItem implements ChecklistItem {
	private String title;
	private String description;
	private int targetScore;
	private int actualScore;

	/**
	 * 객체 생성 메소드(ToeicChecklistItem)
	 */
	public ToeicChecklistItem() {
		this.title = "TOEIC";
		this.description = "토익 목표 점수 이상 달성";
		this.targetScore = 0;
		this.actualScore = 0;
	}
	
	public ToeicChecklistItem(int targetScore) {
		this.title = "TOEIC";
		this.description = "토익 목표 점수 이상 달성";
		this.targetScore = targetScore;
		this.actualScore = 0;
	}

	@Override
	public String getTitle() { return title; }

	@Override
	public boolean isCompleted() { return actualScore >= targetScore; }

	@Override
	public String getDescription() { return description; }

	public int getTargetScore() { return targetScore; }
	public void setTargetScore(int targetScore) { this.targetScore = targetScore; }

	public int getActualScore() { return actualScore; }
	public void setActualScore(int actualScore) { this.actualScore = actualScore; }
}
