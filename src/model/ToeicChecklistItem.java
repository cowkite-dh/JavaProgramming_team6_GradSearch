package model;

public class ToeicChecklistItem implements ChecklistItem {
    private int targetScore;
    private int actualScore;
    private boolean isManuallyCompleted; // setCompleted(true) 호출 시 강제 완료 처리를 위한 변수

    // 생성자
    public ToeicChecklistItem(int targetScore, int actualScore) {
        this.targetScore = targetScore;
        this.actualScore = actualScore;
        this.isManuallyCompleted = false;
    }

    // Getter & Setter for scores
    public int getTargetScore() { return targetScore; }
    public void setTargetScore(int targetScore) { this.targetScore = targetScore; }

    public int getActualScore() { return actualScore; }
    public void setActualScore(int actualScore) { this.actualScore = actualScore; }

    // 인터페이스 구현 메서드들
    @Override
    public String getTitle() {
        return "TOEIC 졸업 요건";
    }

    @Override
    public boolean isCompleted() {
        // 실제 점수가 목표 점수 이상이거나, 강제로 완료 처리된 경우 true 반환
        return (actualScore >= targetScore) || isManuallyCompleted;
    }

    @Override
    public String getDescription() {
        return "목표 점수: " + targetScore + " / 현재 점수: " + actualScore;
    }

    @Override
    public void setCompleted(boolean completed) {
        this.isManuallyCompleted = completed;
    }
}