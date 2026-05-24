/**
 * 
 * @Project: 졸업 요건 확인 서비스 
 * @프로그램 설명: 
 *  - 비교과 졸업요건 체크리스트 관리 서비스
 *  - TOEIC, 지도교수상담 항목은 항상 1개씩 존재(고정)
 *  - 그 외 항목은 사용자가 자유롭게 추가/삭제/체크 가능
 *
 * ChecklistService.java
 *
 * @author Son Seonghoon
 *
 */
package service;

import model.*;
import java.util.*;

public class ChecklistService {
    private List<ChecklistItem> items;

    private static final int TOEIC_INDEX = 0;
    private static final int ADVISOR_INDEX = 1;
    
    /**
	 * 객체 생성 메소드(ChecklistService)
	 * - items[0] = TOEIC (항상 존재)
	 * - items[1] = 지도교수 상담 (항상 존재)
	 */
    public ChecklistService() {
    	this.items = new ArrayList<>();
    	
    	items.add(new ToeicChecklistItem());
    	items.add(new AdvisorChecklistItem());
    }
    
    /**
	 * 아이템 추가 메소드 (addItem)
	 * 
	 * @param item 아이템의 이름, 설명   
	 * @return 
	 * 		 0: 추가 성공
	 * 		-1: 추가 실패 (입력 오류)  
	 */
    public int addItem(ChecklistItem item) {
    	
    	try {
    		items.add(item);
    	} catch(Exception e) {
    		System.out.println("잘못된 입력입니다.");
    		return -1;
    	}
    	
    	return 0;
    }

    /**
	 * 아이템 삭제 메소드 (removeItem)
	 * 
	 * @param index 삭제할 아이템의 인덱스   
	 * @return 
	 * 		 0: 삭제 성공
	 * 		-1: 삭제 실패 (입력 오류)  
	 */
    public int removeItem(int index) {
        if (index < 0 || index >= items.size())
        {
        	System.out.println("잘못된 입력입니다.");
        	return -1;	
        }
        
        if (index == TOEIC_INDEX || index == ADVISOR_INDEX) {
        	System.out.println("TOEIC/지도교수 상담 항목은 삭제할 수 없습니다.");
        	return -1;
        }
        
        items.remove(index);
        return 0;
    }
    
    // -----------------------------
    // TOEIC: index 0
    // -----------------------------
    
    private ToeicChecklistItem toeic() {
    	return (ToeicChecklistItem) items.get(TOEIC_INDEX);
    }

    public int setToeicTargetScore(int targetScore) {
    	toeic().setTargetScore(targetScore);
    	return 0;
    }

    public int setToeicActualScore(int actualScore) {
    	toeic().setActualScore(actualScore);
    	return 0;
    }
    
    // -----------------------------
    // 지도 교수: index 1
    // -----------------------------
    
    private AdvisorChecklistItem advisor() {
    	return (AdvisorChecklistItem) items.get(ADVISOR_INDEX);
    }

    /** 상담 학기 추가 */
    public int addAdvisorSemester(String semester) {
    	if (semester == null || semester.trim().isEmpty()) {
    		System.out.println("잘못된 입력입니다.");
    		return -1;
    	}
    	advisor().setSemester(semester.trim());
    	return 0;
    }

    /** 상담 학기 삭제(학기 리스트 인덱스로 삭제) */
    public int removeAdvisorSemester(int semesterIndex) {
    	int result = advisor().removeSemester(semesterIndex);
    	if (result == -1) {
    		System.out.println("잘못된 학기 인덱스입니다.");
    	}
    	return result;
    }
    
    // -----------------------------
    // 공통 통계/출력
    // -----------------------------
    
    
    /**
	 * 달성한 아이템 갯수 반환 메소드 (getCompleteCount)  
	 * @return cnt 달성한(체크 되어있음) 항목 갯수 
	 */
    public int getCompleteCount() {
        int cnt = 0;
        for (ChecklistItem item : items) {
            if (item != null && item.isCompleted())
            	cnt++;
        }
        return cnt;
    }

    /**
	 * 미달성 아이템 갯수 반환 메소드 (getInCompleteCount)
	 * @return cnt 미달성한(체크 되어있지 않음) 항목 갯수 
	 */
    public int getIncompleteCount() {
        int cnt = 0;
        for (ChecklistItem item : items) {
            if (item != null && !item.isCompleted())
            	cnt++;
        }
        return cnt;
    }
    
    /**
   	 *  미달성 아이템 텍스트로 출력 메소드(getInCompleteItems)
    	 * @return out : 출력할 문자열
   	 */
    public String getInCompleteItems() {
    	StringBuilder out = new StringBuilder();
        out.append("====== 미완료 체크리스트 항목 ======\n");

        int printed = 0;
        for (int i = 0; i < items.size(); i++) {
            ChecklistItem item = items.get(i);
            if (item == null)
            	continue;

            if (!item.isCompleted()) {
                printed++;
                out.append("[" + i + "] " + item.getTitle()
                					+ " - " + item.getDescription() + "\n");
            }
        }

        if (printed == 0) {
            out.append("(미완료 항목이 없습니다)\n");
        }

        out.append("==================================\n");
        return out.toString();
    }

    /**
   	 *  보고서 텍스트로 출력 메소드(printSummary)
   	 	* @return out 출력할 문자열
   	 */
    public String printSummary() {
    	StringBuilder out = new StringBuilder();
        out.append("====== 비교과 졸업요건 체크리스트 ======\n");
        out.append("총 항목: " + items.size() + ", 완료: " + getCompleteCount() 
        				+ ", 미완료: " + getIncompleteCount() + "\n");
        out.append("--------------------------------------\n");

        if (items.isEmpty()) {
            out.append("(등록된 항목이 없습니다)\n");
        } else {
            for (int i = 0; i < items.size(); i++) {
                ChecklistItem item = items.get(i);
                out.append("[" + i + "] ");
                out.append(item.isCompleted() ? "[완료] " : "[미완료] "
                + item.getTitle() + " - " + item.getDescription() + "\n");
            }
        }

        out.append("======================================\n");
        return out.toString();
    }
}
