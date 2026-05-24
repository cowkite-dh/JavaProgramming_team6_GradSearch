package model;

public interface ChecklistItem {
    String getTitle();
    boolean isCompleted();
    String getDescription();
    void setCompleted(boolean completed);
}