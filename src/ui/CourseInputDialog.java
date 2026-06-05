package ui;

import model.*;
import service.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 과목 입력 및 관리 다이얼로그
 * - 검색을 통해 학생의 수강 과목(성적 포함) 추가 및 삭제
 * - 관심 과목 목록(찜 목록) 추가
 */
public class CourseInputDialog extends JDialog {

    private final Student          student;
    private final CourseRepository repo;
    private final Runnable         onSaved; // 데이터 변경 시 메인 창을 갱신하기 위한 콜백

    private JTextField     searchField;
    private JTable         searchTable;
    private DefaultTableModel searchModel;
    private JComboBox<String> searchCriteriaBox;

    private JTable         takenTable;
    private DefaultTableModel takenModel;

    // 취득 성적 선택을 위한 상수 배열
    private static final String[] GRADES =
        {"A+","A0","A-","B+","B0","B-","C+","C0","C-","D+","D0","D-","F","P"};

    public CourseInputDialog(JFrame parent, Student student, CourseRepository repo, Runnable onSaved) {
        super(parent, "과목 입력", true);
        this.student = student;
        this.repo    = repo;
        this.onSaved = onSaved;
        buildUI();
        setSize(900, 650);
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(AppColors.BG_MAIN);

        // 상단 탭 구성 (과목 검색 및 이수 목록 분리)
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppColors.bold(13));
        tabs.addTab(" 과목 검색 · 추가", buildSearchPanel());
        tabs.addTab(" 이수 과목 목록",    buildTakenPanel());

        add(tabs, BorderLayout.CENTER);

        // 하단 닫기 버튼
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        bottom.setBackground(AppColors.BG_MAIN);
        JButton btnClose = makeBtn("닫기", AppColors.BG_HEADER);
        btnClose.addActionListener(e -> { onSaved.run(); dispose(); });
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);
    }

    // 과목 검색 및 추가 패널 빌드
    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(AppColors.BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));

        // 검색 바 (조건 선택 콤보박스 및 입력 필드)
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchBar.setBackground(AppColors.BG_MAIN);
        
        String[] criteria = {"과목명", "과목코드", "담당교수"};
        searchCriteriaBox = new JComboBox<>(criteria);
        searchCriteriaBox.setFont(AppColors.plain(13));

        searchField = new JTextField(20);
        searchField.setFont(AppColors.plain(13));
        searchField.setToolTipText("검색어를 입력하세요.");
        
        JButton btnSearch = makeBtn("검색", AppColors.BG_ACCENT);
        
        searchBar.add(new JLabel("검색 조건: "));
        searchBar.add(searchCriteriaBox);
        searchBar.add(searchField);
        searchBar.add(btnSearch);
        panel.add(searchBar, BorderLayout.NORTH);

        // 검색 결과 테이블 설정
        String[] cols = {"과목코드","분반","과목명","이수구분","학점","학년","교수","여석","수강신청/정원"};
        searchModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        searchTable = new JTable(searchModel);
        styleTable(searchTable);
        searchTable.getColumnModel().getColumn(0).setPreferredWidth(85);
        searchTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        searchTable.getColumnModel().getColumn(6).setPreferredWidth(70);
        JScrollPane scroll = new JScrollPane(searchTable);
        panel.add(scroll, BorderLayout.CENTER);

        // 기능 버튼
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btns.setBackground(AppColors.BG_MAIN);
        JButton btnAddTaken = makeBtn("수강 과목으로 추가", AppColors.BG_SUCCESS);
        JButton btnAddWatch = makeBtn("찜 목록에 추가",    AppColors.BG_WARN);
        btns.add(btnAddTaken);
        btns.add(btnAddWatch);
        panel.add(btns, BorderLayout.SOUTH);

        // 이벤트 리스너 바인딩
        ActionListener doSearch = e -> performSearch();
        btnSearch.addActionListener(doSearch);
        searchField.addActionListener(doSearch);

        btnAddTaken.addActionListener(e -> addSelectedToTaken());
        btnAddWatch.addActionListener(e -> addSelectedToWatch());

        // 초기 화면 진입 시 전체 목록 로드
        performSearch();
        return panel;
    }

    // 이미 이수한 과목 목록 패널 빌드
    private JPanel buildTakenPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(AppColors.BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));

        String[] cols = {"과목코드","과목명","이수구분","학점","학기","성적","이수여부"};
        takenModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        takenTable = new JTable(takenModel);
        styleTable(takenTable);
        takenTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        refreshTakenTable();

        JScrollPane scroll = new JScrollPane(takenTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btns.setBackground(AppColors.BG_MAIN);
        JButton btnDel = makeBtn("선택 삭제", AppColors.BG_DANGER);
        btns.add(btnDel);
        panel.add(btns, BorderLayout.SOUTH);

        // 선택 과목 삭제 이벤트
        btnDel.addActionListener(e -> {
            int row = takenTable.getSelectedRow();
            if (row < 0) { 
                JOptionPane.showMessageDialog(this, "삭제할 과목을 선택하세요."); 
                return; 
            }
            student.removeTakenCourse(student.getTakenCourses().get(row));
            refreshTakenTable();
            onSaved.run(); // 메인 화면 실시간 동기화
        });
        return panel;
    }

    // 선택된 조건 및 키워드를 기반으로 레포지토리 검색 진행
    private void performSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        String criteria = (String) searchCriteriaBox.getSelectedItem();
        searchModel.setRowCount(0);
        
        for (Course c : repo.getAll()) { 
            boolean match = false;
            
            if (keyword.isEmpty()) {
                match = true;
            } else {
                if ("과목명".equals(criteria) && c.getCourseName().toLowerCase().contains(keyword)) match = true;
                else if ("과목코드".equals(criteria) && c.getCourseId().toLowerCase().contains(keyword)) match = true;
                else if ("담당교수".equals(criteria) && c.getProfessor() != null && c.getProfessor().toLowerCase().contains(keyword)) match = true;
            }
            
            if (match) {
                searchModel.addRow(new Object[]{
                    c.getCourseId(), 
                    c.getSection(), 
                    c.getCourseName(), 
                    c.getType().getDisplayName(),
                    c.getCredits(), 
                    c.getGrade() == 0 ? "전학년" : c.getGrade() + "학년", 
                    c.getProfessor(), 
                    c.getVacancy() + "석", 
                    c.getCurrentStudents() + " / " + c.getMaxStudents()
                });
            }
        }
    }

    // 선택한 과목을 학기 및 성적과 함께 이수 과목으로 등록
    private void addSelectedToTaken() {
        int row = searchTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "추가할 과목을 먼저 선택하세요.", "안내", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 수강 정보 입력을 위한 커스텀 다이얼로그 패널 생성
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        String[] defaultSemesters = {"2026-1학기","2025-2학기","2025-1학기","2024-2학기", "2024-1학기", "2023-2학기", "2023-1학기"};
        JComboBox<String> semesterCombo = new JComboBox<>(defaultSemesters);
        semesterCombo.setEditable(true); 

        JComboBox<String> gradeCombo = new JComboBox<>(GRADES);

        inputPanel.add(new JLabel("  수강 학기:"));
        inputPanel.add(semesterCombo);
        inputPanel.add(new JLabel("  취득 성적:"));
        inputPanel.add(gradeCombo);

        int result = JOptionPane.showConfirmDialog(
            this, inputPanel, "수강 정보 입력", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String selectedSemester = (String) semesterCombo.getSelectedItem();
            String selectedGrade = (String) gradeCombo.getSelectedItem();
            
            String code = (String) searchModel.getValueAt(row, 0);
            String section = (String) searchModel.getValueAt(row, 1);
            String key = code + "-" + section;
            
            Course course = repo.findByKey(key).orElse(null);
            if (course != null) {
                // 동일 학기 내 중복 수강 등록 방지 검증
                boolean alreadyTaken = student.getTakenCourses().stream()
                        .anyMatch(tc -> tc.getCourse().getKey().equals(key) && tc.getSemester().equals(selectedSemester));
                
                if (alreadyTaken) {
                    JOptionPane.showMessageDialog(this, "이미 해당 학기에 수강한 과목입니다.", "경고", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                student.addTakenCourse(new TakenCourse(course, selectedSemester, selectedGrade));
                refreshTakenTable();
                onSaved.run(); // 메인 화면 및 파일 저장 연동
                JOptionPane.showMessageDialog(this, course.getCourseName() + "이(가) 이수 목록에 추가되었습니다.");
            }
        }
    }

    // 선택한 과목을 관심 과목(빈자리 알림) 목록에 추가
    private void addSelectedToWatch() {
        int row = searchTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "과목을 선택하세요."); return; }
        String code    = (String) searchModel.getValueAt(row, 0);
        String section = (String) searchModel.getValueAt(row, 1);
        String key     = code + "-" + section;
        Course course  = repo.findByKey(key).orElse(null);
        if (course == null) return;
        
        student.addWatchlist(course);
        JOptionPane.showMessageDialog(this, course.getCourseName() + " 과목이 찜 목록에 추가되었습니다.", "찜 추가", JOptionPane.INFORMATION_MESSAGE);
        onSaved.run();
    }

    // 이수 목록 테이블 데이터 최신화
    public void refreshTakenTable() {
        takenModel.setRowCount(0);
        for (TakenCourse tc : student.getTakenCourses()) {
            Course c = tc.getCourse();
            takenModel.addRow(new Object[]{
                c.getCourseId(), c.getCourseName(),
                c.getType().getDisplayName(), c.getCredits(),
                tc.getSemester(), tc.getGrade(),
                tc.isPassed() ? "이수" : "미이수"
            });
        }
    }

    // 테이블 일관 스타일 적용 유틸 메서드
    private void styleTable(JTable table) {
        table.setFont(AppColors.plain(12));
        table.setRowHeight(26); 
        table.setGridColor(AppColors.BORDER);
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setShowGrid(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(AppColors.bold(12));
        header.setReorderingAllowed(false); 
        
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(AppColors.BG_HEADER); 
        renderer.setForeground(Color.WHITE);         
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        renderer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, AppColors.BORDER));
        
        header.setDefaultRenderer(renderer);
    }

    // 버튼 생성 및 공통 템플릿 적용 유틸 메서드
    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(AppColors.bold(12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return b;
    }
}