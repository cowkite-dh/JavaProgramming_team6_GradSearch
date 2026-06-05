package ui;

import javax.swing.*;
import java.awt.*;

/**
 * 졸업 요건 만족 비율을 스무스 라운드 형태로 시각화하는 커스텀 게이지 바
 */
public class GaugeBar extends JComponent {

    private double value = 0.0; // 0.0 ~ 1.0 범위 비율

    public GaugeBar() {
        setPreferredSize(new Dimension(300, 28));
    }

    public void setValue(double v) {
        this.value = Math.max(0.0, Math.min(1.0, v));
        repaint();
    }

    public double getValue() { return value; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int arc = 12;

        // 트랙 배경 렌더링
        g2.setColor(AppColors.GAUGE_BG);
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        // 달성량 채우기
        int fillW = (int) (w * value);
        if (fillW > 0) {
            Color fill = value >= 1.0 ? AppColors.GAUGE_DONE : AppColors.GAUGE_FILL;
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, fillW, h, arc, arc);
        }

        // 중앙 퍼센트 문자열 정밀 중앙 배치 계산
        String text = String.format("%.1f%%", value * 100);
        g2.setFont(AppColors.bold(12));
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
        
        // 가독성을 고려하여 게이지가 절반 이상 채워지면 글자 색상을 흰색으로 전향
        g2.setColor(fillW > w / 2 ? Color.WHITE : AppColors.TEXT_PRIMARY);
        g2.drawString(text, tx, ty);

        g2.dispose();
    }
}