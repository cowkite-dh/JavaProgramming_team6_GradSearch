/**
 * 
 * @Project: 졸업 요건 확인 서비스 
 * @프로그램 설명: 
 * 	- 졸업 요건 확인(수업 관련)을 위한 서비스
 *  - 수업 관련 요건만 확인하기에 지도교수 상담, 토익 점수와 같은 요소는 제외
 *
 * GraduationService.java
 *
 * @author Son Seonghoon
 *
 */
package service;

import model.*;
import java.util.*;

public class GraduationService {
    private Student student;
    private GraduationReq req;

    /**
	 * 객체 생성 메소드(GraduationService)
	 * @param student 학생 
	 * @param req 필요 사항 
	 */
    public GraduationService(Student student, GraduationReq req) {
        this.student = student;
        this.req = req;
    }

    /**
	 * 과목 유형별 취득 학점 계산 메소드(calcCredits) 단, 재수강처럼 pass를 안 했을 경우에는 제외.
	 * @param type 취득한 학점을 계산할 과목 유형(교양, 전공 등등...)
	 * @return sum 취득한 학점 
	 */
    public int calcCredits(CourseType type) {
        int sum = 0;
        
        for (TakenCourse tc : student.getTakenCourses()) {
            Course c = tc.getCourse();
            
            if (c.getType() == type && tc.isPassed()) {
                sum += c.getCredits();
            }
        }
        return sum;
    }
    /**
	 * 과목 유형별 미달 학점 계산 메소드(getMissingCredits)
 	 * @param missing 미달한 학점
 	 * @param minCredit 필요한 과목 유형별 최소 학점
 	 * @return 
	 * 		 학점 유형, 0이 아닌 정수 A (Map): 해당 학점 유형은 부족하지 않음
	 * 		 학점 유형, 0 초과인 정수 A (Map): 해당 학점 유형은 A만큼 부족함
	 */
    public Map<CourseType, Integer> getMissingCredits() {
        Map<CourseType, Integer> missing = new HashMap<>();
        Map<CourseType, Integer> minCredit = req.getMinCredits();
        int obtained, needed, miss;
        
        for (CourseType type : minCredit.keySet()) {
            obtained = calcCredits(type);
            needed = minCredit.get(type);
            
            miss = needed - obtained;
            
            if (miss < 0) {
            	miss = 0;
            }
            missing.put(type, miss);
        }
        return missing;
    }

    /**
	 * 아직 듣지 않은 전공필수 과목 반환 메소드(getMissingRequired)
 	 * @param missing 듣지 않은 전공필수 과목
 	 * @param takenCourseIds 이미 들은 과목들
 	 * @return missing 듣지 않은 전공필수 과목
	 */
    public List<String> getMissingRequired() {
    	List<String> missing = new ArrayList<>();
        Set<String> takenCourseIds = new HashSet<>();
        
        for (TakenCourse tc : student.getTakenCourses()) {
            if ((tc.isPassed()) && (tc.getCourse().getType() == CourseType.MAJOR_REQUIRED)) {
                takenCourseIds.add(tc.getCourse().getCourseId());
            }
        }
        
        for (String reqId : req.getRequiredCourseIds()) {
            if (!takenCourseIds.contains(reqId)) {
                missing.add(reqId);
            }
        }
        return missing;
    }

    /**
   	 *  전체 이수율 계산 메소드(getCompletionRate)
    	 * @param sumObtained 들은 학점
    	 * @param sumMinimum 들어야 하는 최소 학점
    	 * @return 이수율(0~1). 초과해서 이수했더라도 1.
   	 */
    public double getCompletionRate() {
        int sumObtained = 0;
        int sumMinimum = 0;
        Map<CourseType, Integer> minCredits = req.getMinCredits();
        
        for (CourseType type : minCredits.keySet()) {
            sumMinimum += minCredits.get(type);
            
            if(calcCredits(type) > minCredits.get(type))
            {
            	sumObtained += minCredits.get(type);
            }
            else
            {
            	sumObtained += calcCredits(type);
            }
        }
        return (double)sumObtained / sumMinimum;
    }

    /**
   	 *  보고서 텍스트로 출력 메소드(generateReport)
    	 * @return out 출력할 문자열
   	 */
    public String generateReport() {
    	StringBuilder out = new StringBuilder();
    	Map<CourseType, Integer> minCredits = req.getMinCredits();
    	List<String> missingReq = getMissingRequired();
    	
        out.append("====== 졸업 이수 현황 보고서 ======\n");
        out.append("이름: " + student.getName() + ", 학번: " + student.getStudentId() + "\n");
        out.append("전공: " + student.getMajor() + ", 입학년도: " + student.getAdmissionYear() + "\n\n");

        out.append("[이수 구분별 취득/필요 학점]\n");
        for (CourseType type : minCredits.keySet()) {
            int obtained = calcCredits(type);
            int needed = minCredits.get(type);
            
            out.append("- " + type + ": " + obtained + " / " + needed);
            if (obtained < needed) {
            	out.append(" !부족!");	
            }
            out.append("\n");
        }

        out.append("\n[미이수 필수 과목]\n");
        if (missingReq.isEmpty())
        {
            out.append("없음 (필수과목 모두 이수)\n");
        }
        else
        {
            for (String id : missingReq) {
                out.append("- " + id + "\n");
            }
        }

        out.append("\n전체 이수율 : ");
        out.append(String.format("%.1f%%", 100 * getCompletionRate()));
        out.append("\n================================\n");
        return out.toString();
    }
}
