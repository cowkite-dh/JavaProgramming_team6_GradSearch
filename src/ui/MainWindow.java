package ui;

import model.*;
import service.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;

/**
 * 시스템의 메인 대시보드 윈도우
 * - 상단: 학생 요약 정보 및 제어 메뉴
 * - 중앙: 대시보드 스크롤 카드 형태의 레이아웃 (달성도 게이지, 학점 현황, 기타 요건, 실시간 여석 알림)
 */
public class MainWindow extends JFrame {

    private final Student          student;
    private final CourseRepository repo;
    private final StudentStorage   storage;
    private final SeatMonitor      monitor;
    private GraduationChecker      checker;

    // 실시간 UI 리프레시가 필요한 컴포넌트 레퍼런스
    private JLabel      lblName, lblGrade, lblYear;
    private GaugeBar    gaugeBar;
    private JTable      creditTable;
    private DefaultTableModel creditModel;
    private JTable      watchTable;
    private DefaultTableModel watchModel;
    private JLabel      lblToeic, lblAdvisorReq, lblGpa, lblSemesterReq;
    private JLabel      lblLastRefresh;
    private JLabel      lblGraduate;

    public MainWindow(Student student, CourseRepository repo, StudentStorage storage, SeatMonitor monitor) {
        super("경북대 졸업 요건 확인 시스템");
        this.student = student;
        this.repo    = repo;
        this.storage = storage;
        this.monitor = monitor;
        this.checker = new GraduationChecker(student);

        // 실시간 빈자리 감지 스레드용 리스너 바인딩
        monitor.addListener(events -> SwingUtilities.invokeLater(() -> showVacancyAlert(events)));

        buildUI();
        refresh();
        monitor.startAuto();

        // 창 닫기 시 자동 모니터링 종료 및 학생 세션 데이터 안전 파일 백업
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                monitor.stopAuto();
                storage.save(student);
                System.out.println("[저장 완료] 학생 정보가 저장되었습니다.");
                dispose();
                System.exit(0);
            }
        });
        setSize(960, 750);
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        getContentPane().setBackground(AppColors.BG_MAIN);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
    }

    // 메인 상단 컨트롤 및 상태바 빌드
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(5, 0)); 
        header.setBackground(AppColors.BG_HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        leftPanel.setOpaque(false);

        lblName    = new JLabel("안녕하세요 " + student.getName() + "님");
        lblGrade   = new JLabel("(" + student.getGrade() + "학년)");
        lblYear    = new JLabel("등록학기: " + student.getRegisteredSemesters() + "학기");

        lblName.setFont(AppColors.bold(14));
        lblName.setForeground(Color.WHITE);
        
        lblGrade.setFont(AppColors.plain(13));
        lblGrade.setForeground(new Color(200, 200, 200));
        
        lblYear.setFont(AppColors.bold(13));
        lblYear.setForeground(new Color(241, 196, 15));
        
        leftPanel.add(lblName);
        leftPanel.add(lblGrade);
        leftPanel.add(new JLabel("  "));
        leftPanel.add(lblYear);
        
        header.add(leftPanel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        btnPanel.setOpaque(false);

        JButton btnCourse    = makeBtn("과목 입력", AppColors.BG_ACCENT);
        JButton btnRefresh   = makeBtn("새로고침", AppColors.BG_SUCCESS); 
        JButton btnInfo      = makeBtn("내 정보", new Color(52, 152, 219));  
        JButton btnSettings  = makeBtn("요건 설정", new Color(142, 68, 173));
        JButton btnEarlyGrad = makeBtn("조기졸업", new Color(243, 156, 18)); 
        JButton btnSave      = makeBtn("저장", new Color(100, 100, 100));

        btnPanel.add(btnCourse);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnInfo);
        btnPanel.add(btnSettings);
        btnPanel.add(btnEarlyGrad);
        btnPanel.add(btnSave);
        
        header.add(btnPanel, BorderLayout.EAST);

        btnCourse.addActionListener(e -> openCourseInputDialog());
        btnRefresh.addActionListener(e -> manualRefreshSeats());
        btnInfo.addActionListener(e -> openInfoDialog());
        btnSettings.addActionListener(e -> openSettingsDialog());
        btnEarlyGrad.addActionListener(e -> openEarlyGradDialog());
        btnSave.addActionListener(e -> {
            storage.save(student);
            JOptionPane.showMessageDialog(this, "저장 완료!", "저장", JOptionPane.INFORMATION_MESSAGE);
        });

        return header;
    }

    private JScrollPane buildCenter() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(AppColors.BG_MAIN);
        center.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        center.add(buildGaugeCard());
        center.add(Box.createVerticalStrut(14));
        center.add(buildCreditCard());
        center.add(Box.createVerticalStrut(14));
        center.add(buildEtcCard());
        center.add(Box.createVerticalStrut(14));
        center.add(buildWatchlistCard());

        JScrollPane sp = new JScrollPane(center);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private JPanel buildGaugeCard() {
        JPanel card = card("졸업 달성도");
        card.setLayout(new BorderLayout(10, 8));

        gaugeBar = new GaugeBar();
        gaugeBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 32));

        lblGraduate = new JLabel("판정 중...");
        lblGraduate.setFont(AppColors.bold(14));

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.add(gaugeBar, BorderLayout.CENTER);
        row.add(lblGraduate, BorderLayout.EAST);
        card.add(row, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildCreditCard() {
        JPanel card = card("졸업 필요 학점 현황");
        card.setLayout(new BorderLayout(0, 6));

        String[] cols = {"구분", "이수 학점", "필요 학점", "부족 학점", "달성도"};
        creditModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        // 부족 학점 유무에 따라 동적 컬러링을 부여하기 위한 렌더러 정의
        creditTable = new JTable(creditModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component comp = super.prepareRenderer(renderer, row, col);
                comp.setBackground(row % 2 == 0 ? Color.WHITE : AppColors.BG_TABLE_ALT);
                if (col == 3) {
                    int missing = 0;
                    try { missing = Integer.parseInt(getValueAt(row, col).toString()); }
                    catch (Exception ignored) {}
                    comp.setForeground(missing > 0 ? AppColors.BG_DANGER : AppColors.BG_SUCCESS);
                } else {
                    comp.setForeground(AppColors.TEXT_PRIMARY);
                }
                return comp;
            }
        };
        styleTable(creditTable);
        creditTable.setPreferredScrollableViewportSize(new Dimension(700, 60));

        card.add(new JScrollPane(creditTable), BorderLayout.CENTER);
        return card;
    }

    private JPanel buildEtcCard() {
        JPanel card = card("기타 졸업 요건");
        card.setLayout(new GridLayout(1, 4, 14, 0));

        lblGpa         = etcLabel("평점평균", "-");
        lblToeic       = etcLabel("TOEIC",   "-");
        lblAdvisorReq   = etcLabel("지도교수 상담", "-");
        lblSemesterReq  = etcLabel("등록 학기", "-");

        card.add(wrapEtc(lblGpa, "평점평균 (4.3 만점)"));
        card.add(wrapEtc(lblToeic, "TOEIC 점수"));
        card.add(wrapEtc(lblAdvisorReq, "지도교수 상담 횟수"));
        card.add(wrapEtc(lblSemesterReq, "등록 학기 수"));
        return card;
    }

    private JPanel buildWatchlistCard() {
        JPanel card = card("찜한 과목 (빈자리 알림 목록)");
        card.setLayout(new BorderLayout(0, 6));

        String[] cols = {"과목코드","분반","과목명","교수","수강신청","정원","여석","상태"};
        watchModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        watchTable = new JTable(watchModel);
        styleTable(watchTable);
        watchTable.setPreferredScrollableViewportSize(new Dimension(700, 100));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        lblLastRefresh = new JLabel("마지막 확인: -");
        lblLastRefresh.setFont(AppColors.plain(11));
        lblLastRefresh.setForeground(AppColors.TEXT_SECONDARY);
        top.add(lblLastRefresh, BorderLayout.EAST);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btns.setOpaque(false);
        JButton btnRemove = makeBtn("찜 해제", AppColors.BG_DANGER);
        btns.add(btnRemove);
        top.add(btns, BorderLayout.WEST);

        card.add(top,                        BorderLayout.NORTH);
        card.add(new JScrollPane(watchTable), BorderLayout.CENTER);

        btnRemove.addActionListener(e -> {
            int row = watchTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "해제할 과목을 선택하세요."); return; }
            Course c = student.getWatchlist().get(row);
            student.removeWatchlist(c);
            refreshWatchlist();
        });
        return card;
    }

    // 내부 데이터 변경 시 연동된 하위 모든 패널 컴포넌트의 텍스트와 게이지를 일괄 새로고침
    public void refresh() {
    	checker = new GraduationChecker(student);

        lblName.setText("안녕하세요 " + student.getName() + "님");
        lblGrade.setText("(" + student.getGrade() + "학년)");
        lblYear.setText("등록학기: " + student.getRegisteredSemesters() + "학기");
        
        double rate = checker.getCompletionRate();
        gaugeBar.setValue(rate);
        
        if (checker.canGraduate()) {
            lblGraduate.setText("★ 졸업 가능");
            lblGraduate.setForeground(AppColors.BG_SUCCESS);
        } else {
            lblGraduate.setText("! 미충족");
            lblGraduate.setForeground(AppColors.BG_DANGER);
        }

        creditModel.setRowCount(0);
        for (Map.Entry<String, int[]> e : checker.getSummary().entrySet()) {
            int got  = e.getValue()[0];
            int req  = e.getValue()[1];
            int miss = Math.max(0, req - got);
            String pct = String.format("%.0f%%", Math.min(100.0, (double) got / req * 100));
            creditModel.addRow(new Object[]{e.getKey(), got, req, miss, pct});
        }

        double gpa = checker.getGpa();
        lblGpa.setText(String.format("%.2f / 4.30", gpa));
        lblGpa.setForeground(gpa >= 1.7 ? AppColors.BG_SUCCESS : AppColors.BG_DANGER);

        int tScore  = student.getToeicScore();
        int tTarget = student.getToeicTarget();
        lblToeic.setText(tScore + " / " + tTarget + "점");
        lblToeic.setForeground(tScore >= tTarget ? AppColors.BG_SUCCESS : AppColors.BG_DANGER);

        int adv    = student.getAdvisorCount();
        int advReq = student.getReqAdvisorCount(); 
        lblAdvisorReq.setText(adv + " / " + advReq + "회");
        lblAdvisorReq.setForeground(adv >= advReq ? AppColors.BG_SUCCESS : AppColors.BG_DANGER);
        
        int currentSem = student.getRegisteredSemesters();
        lblSemesterReq.setText(currentSem + " / 8학기");
        lblSemesterReq.setForeground(currentSem >= 8 ? AppColors.BG_SUCCESS : AppColors.BG_DANGER);

        refreshWatchlist();
    }

    public void refreshWatchlist() {
        watchModel.setRowCount(0);
        for (Course c : student.getWatchlist()) {
            String status = c.hasVacancy() ? "여석 있음" : "마감";
            watchModel.addRow(new Object[]{
                c.getCourseId(), c.getSection(), c.getCourseName(),
                c.getProfessor(), c.getCurrentStudents(), c.getMaxStudents(),
                c.getVacancy(), status
            });
        }
    }

    private void openCourseInputDialog() {
        CourseInputDialog dlg = new CourseInputDialog(this, student, repo, this::refresh);
        dlg.setVisible(true);
    }

    // 서버 데이터를 동기 수동 로드하여 변경된 수강 정보를 가공 후 팝업 출력
    private void manualRefreshSeats() {
        repo.reload();
        List<SeatMonitor.VacancyEvent> events = monitor.checkNow();
        refreshWatchlist();
        
        lblLastRefresh.setText("마지막 확인: " + java.time.LocalTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
                
        if (events.isEmpty()) {
            JOptionPane.showMessageDialog(this, "새로운 빈자리가 없습니다.", "새로고침 완료", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showVacancyAlert(events);
        }
    }

    private void showVacancyAlert(List<SeatMonitor.VacancyEvent> events) {
        if (events.isEmpty()) return;
        StringBuilder sb = new StringBuilder("!!빈자리 알림!!\n\n");
        for (SeatMonitor.VacancyEvent ev : events) {
            sb.append(String.format("• %s (%s) — 여석 %d석\n", ev.course.getCourseName(), ev.course.getCourseId(), ev.vacancy));
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "빈자리 발생!", JOptionPane.WARNING_MESSAGE);
        refreshWatchlist();
        lblLastRefresh.setText("마지막 확인: " + java.time.LocalTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    private void openSettingsDialog() {
        GraduationSettingsDialog dlg = new GraduationSettingsDialog(this, student, this::refresh);
        dlg.setVisible(true);
    }

    private void openEarlyGradDialog() {
        EarlyGraduationDialog dlg = new EarlyGraduationDialog(this, student);
        dlg.setVisible(true);
    }
    
    private void openInfoDialog() {
        StudentInfoDialog dlg = new StudentInfoDialog(this, student, this::refresh);
        dlg.setVisible(true);
    }
    
    // 카드 형태 패널 레이아웃 적용 유틸
    private JPanel card(String title) {
        JPanel p = new JPanel();
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        p.setBackground(AppColors.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), title, TitledBorder.LEFT, TitledBorder.TOP,
                AppColors.bold(13), AppColors.BG_ACCENT);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(6, 12, 12, 12), tb)));
        return p;
    }

    private JLabel etcLabel(String key, String val) {
        JLabel l = new JLabel(val);
        l.setFont(AppColors.bold(20));
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    private JPanel wrapEtc(JLabel value, String title) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(AppColors.plain(11));
        lbl.setForeground(AppColors.TEXT_SECONDARY);
        p.add(lbl,   BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

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

    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(AppColors.bold(11)); 
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8)); 
        return b;
    }
}