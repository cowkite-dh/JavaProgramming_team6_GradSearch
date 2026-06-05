package ui;

import model.Student;
import service.GraduationChecker;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * 조기졸업 가능 대상 여부 상세 검증 다이얼로그
 */
public class EarlyGraduationDialog extends JDialog {

    private final Student student;

    public EarlyGraduationDialog(JFrame parent, Student student) {
        super(parent, "조기졸업 판정 현황", true);
        this.student = student;

        setLayout(new BorderLayout());
        getContentPane().setBackground(AppColors.BG_MAIN);
        setSize(420, 320);
        setLocationRelativeTo(parent);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        centerWrap.add(buildPanel(), BorderLayout.CENTER);

        add(centerWrap, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottom.setOpaque(false);
        JButton btnClose = makeBtn("닫기", AppColors.BG_HEADER);
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnClose);

        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel buildPanel() {
        JPanel p = new JPanel(new GridLayout(5, 1, 5, 5));
        p.setBackground(AppColors.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GraduationChecker checker = new GraduationChecker(student);

        // 조기졸업 조건 검증: 일반 정규 졸업요건인 '8학기 등록' 규정을 명시적으로 제외한 순수 요건 평가
        boolean isGenMet = checker.getTotalCredits() >= checker.getReqTotal()
                && checker.getMajorCredits() >= checker.getReqMajor()
                && checker.getGeneralCredits() >= checker.getReqGeneral()
                && student.getAdvisorCount() >= checker.getReqAdvisor()
                && checker.isEnglishReqMet();
                
        double gpa = student.getGpa();
        boolean isGpaMet = gpa >= 3.7;

        int semesters = student.getRegisteredSemesters();
        boolean isSemMet = semesters >= 6;

        boolean canEarlyGrad = checker.canGraduateEarly();

        p.add(createStatusLabel("1. 일반 졸업 요건 (학점/영어/상담):", isGenMet));

        String gpaText = String.format("2. 현재 평점평균: %.2f / 4.30 (기준: 3.70 이상)", gpa);
        p.add(createStatusLabel(gpaText, isGpaMet));

        String semText = String.format("3. 등록 학기 수: %d학기 (기준: 6학기 이상)", semesters);
        p.add(createStatusLabel(semText, isSemMet));

        JSeparator sep = new JSeparator();
        sep.setForeground(AppColors.BORDER);
        p.add(sep);

        JLabel lblResult = new JLabel(canEarlyGrad ? "★ 조기졸업 가능 대상자입니다 ★" : "조기졸업 불가능 (조건 미달)");
        lblResult.setHorizontalAlignment(SwingConstants.CENTER);
        lblResult.setFont(AppColors.bold(16));
        lblResult.setForeground(canEarlyGrad ? AppColors.BG_ACCENT : AppColors.BG_DANGER);
        p.add(lblResult);

        return p;
    }

    private JLabel createStatusLabel(String text, boolean isMet) {
        JLabel lbl = new JLabel(text + (isMet ? "  [충족]" : "  [미충족]"));
        lbl.setFont(AppColors.bold(13));
        lbl.setForeground(isMet ? AppColors.BG_SUCCESS : AppColors.BG_DANGER);
        return lbl;
    }

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