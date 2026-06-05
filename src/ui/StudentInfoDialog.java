package ui;

import model.Student;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * 학생 개인 인포메이션 변수(토익, 학기, 상담 진행 상태) 업데이트 수정 창
 */
public class StudentInfoDialog extends JDialog {
    private final Student student;
    private final Runnable onSaved;

    private JTextField txtToeicScore, txtToeicTarget, txtAdvisorCount, txtSemesters;

    public StudentInfoDialog(JFrame parent, Student student, Runnable onSaved) {
        super(parent, "내 정보 설정", true);
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

        txtToeicScore = createTextField(String.valueOf(student.getToeicScore()));
        txtToeicTarget = createTextField(String.valueOf(student.getToeicTarget()));
        txtAdvisorCount = createTextField(String.valueOf(student.getAdvisorCount()));
        txtSemesters = createTextField(String.valueOf(student.getRegisteredSemesters()));

        p.add(createLabel("현재 토익 점수:"));      p.add(txtToeicScore);
        p.add(createLabel("목표 토익 점수:"));      p.add(txtToeicTarget);
        p.add(createLabel("현재 진행 상담 횟수:")); p.add(txtAdvisorCount);
        p.add(createLabel("현재 등록 학기 수:"));   p.add(txtSemesters);

        JButton btnSave = makeBtn("정보 저장", new Color(52, 152, 219));
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
            student.setToeicScore(Integer.parseInt(txtToeicScore.getText().trim()));
            student.setToeicTarget(Integer.parseInt(txtToeicTarget.getText().trim()));
            student.setAdvisorCount(Integer.parseInt(txtAdvisorCount.getText().trim()));
            student.setRegisteredSemesters(Integer.parseInt(txtSemesters.getText().trim()));

            JOptionPane.showMessageDialog(this, "내 정보가 성공적으로 업데이트되었습니다.");
            onSaved.run(); 
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "숫자만 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}