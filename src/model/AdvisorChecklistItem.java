package model;

public class AdvisorChecklistItem implements ChecklistItem {
    private boolean consulted;
    private String semester;

    // 생성자
    public AdvisorChecklistItem(String semester, boolean consulted) {
        this.semester = semester;
        this.consulted = consulted;
    }

    // Getter & Setter
    public boolean isConsulted() { return consulted; }
    public void setConsulted(boolean consulted) { this.consulted = consulted; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    // 인터페이스 구현 메서드들
    @Override
    public String getTitle() {
        return semester + " 지도교수 상담";
    }

    @Override
    public boolean isCompleted() {
        return consulted; // 상담을 받았으면 true
    }

    @Override
    public String getDescription() {
        return semester + " 학기 지도교수 상담 완료 여부 (상태: " + (consulted ? "완료" : "미완료") + ")";
    }

    @Override
    public void setCompleted(boolean completed) {
        this.consulted = completed;
    }
}