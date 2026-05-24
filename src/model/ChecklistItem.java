/**
 * 
 * @Project: 졸업 요건 확인 서비스 
 * @프로그램 설명: 
 * 	- 졸업 요건 확인(수업 관련 제외)을 위한 서비스
 *  - 수업 관련을 제외한 요건(지도교수 상담, 토익 점수 등)만 확인
 *  - 지도교수 상담과 토익 점수를 제외한 요소는 체크리스트 방식으로 자율적으로 만들 수 있게 되어 있음
 *
 * ChecklistItem.java
 *
 * @author Son Seonghoon
 *
 */

package model;

public interface ChecklistItem {
	/**
	 * 제목을 입력받는 메소드(getTitle)
	 */
	public abstract String getTitle();
	/**
	 * 완료 여부를 판정하는 메소드(isCompleted)
	 */
	public abstract boolean isCompleted();
	/**
	 * 설명을 입력받는 메소드(getDescription)
	 */
	public abstract String getDescription();
}
