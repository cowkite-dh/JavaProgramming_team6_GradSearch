/**
 * 
 * @Project: 졸업 요건 확인 서비스 
 * @프로그램 설명: 
 * 	- 졸업 요건 확인(수업 관련 제외)을 위한 서비스
 *  - 수업 관련을 제외한 요건(지도교수 상담, 토익 점수 등)만 확인
 *  - 해당 요소는 체크리스트 방식으로 자율적으로 만들 수 있게 되어 있음
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

    /**
	 * 객체 생성 메소드(ChecklistService)
	 */
    public ChecklistService() {
    	this.items = new ArrayList<>();
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
	 * @param title 삭제할 아이템의 이름   
	 * @return 
	 * 		 0: 삭제 성공
	 * 		-1: 삭제 실패 (입력 오류)  
	 */
    public int removeItem(String title) {
    	if (title == null)
    	{
    		System.out.println("잘못된 입력입니다.");
    		return -1;	
    	}
    	
    	String target = title.trim();
    	if (target.isEmpty())
    	{
    		System.out.println("잘못된 입력입니다.");
    		return -1;
    	}
    	
    	for(int i = 0; i < items.size(); i++) {
    		ChecklistItem item = items.get(i);
    		String itemTitle = item.getTitle();
    		
    		if(target.equals(itemTitle.trim()))
    		{
    			items.remove(i);
    			return 0;
    		}
    	}
    	
    	return -1;
    }
    
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
