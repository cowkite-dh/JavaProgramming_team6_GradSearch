package ui;

import model.Student;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * 학과별 전공/교양 기준 취득 요건 수치 제어 다이얼로그
 */
public class GraduationSettingsDialog extends JDialog {
    private final Student student;
    private final Runnable onSaved;

    private JTextField txtTotal, txtMajor, txtGeneral, txtReqAdvisor;

    public GraduationSettingsDialog(JFrame parent, Student student, Runnable onSaved) {
        super(parent, "학과별 졸업 요건 설정", true);
        this.student = student;
        this.onSaved = onSaved;

        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(AppColors.BG_MAIN);
        setSize(380, 300);
        setLocationRelativeTo(parent);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        centerWrap.add(buildForm(), BorderLayout.CENTER);

        add(centerWrap, BorderLayout.CENTER);
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridLayout(5, 2, 10, 16));
        p.setBackground(AppColors.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        txtTotal = createTextField(String.valueOf(student.getReqTotalCredits()));
        txtMajor = createTextField(String.valueOf(student.getReqMajorCredits()));
        txtGeneral = createTextField(String.valueOf(student.getReqGeneralCredits()));
        txtReqAdvisor = createTextField(String.valueOf(student.getReqAdvisorCount()));

        p.add(createLabel("총 필요 학점:"));        p.add(txtTotal);
        p.add(createLabel("전공 필요 학점:"));      p.add(txtMajor);
        p.add(createLabel("교양 필요 학점:"));      p.add(txtGeneral);
        p.add(createLabel("상담 필요 횟수(기준):")); p.add(txtReqAdvisor);

        JButton btnSave = makeBtn("요건 수정 적용", new Color(142, 68, 173));
        btnSave.addActionListener(e -> saveSettings());

        p.add(new JLabel("")); 
        p.add(btnSave);

        return p;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppColors.bold(13));
        lbl.setForeground(AppColors.TEXT_PRIMARY);
        return lbl;
    }

    private JTextField createTextField(String text) {
        JTextField tf = new JTextField(text);
        tf.setFont(AppColors.plain(13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        return tf;
    }

    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(AppColors.bold(12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void saveSettings() {
        try {
            student.setReqTotalCredits(Integer.parseInt(txtTotal.getText().trim()));
            student.setReqMajorCredits(Integer.parseInt(txtMajor.getText().trim()));
            student.setReqGeneralCredits(Integer.parseInt(txtGeneral.getText().trim()));
            student.setReqAdvisorCount(Integer.parseInt(txtReqAdvisor.getText().trim()));

            JOptionPane.showMessageDialog(this, "학과 요건이 성공적으로 변경되었습니다.");
            onSaved.run(); 
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "숫자만 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}