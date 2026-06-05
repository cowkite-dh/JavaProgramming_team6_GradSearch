package ui;

import java.awt.*;

/**
 * 어플리케이션 전역에서 사용되는 테마 색상 및 폰트 유틸 상수 클래스
 */
public final class AppColors {
    private AppColors() {}

    public static final Color BG_MAIN      = new Color(245, 247, 250);
    public static final Color BG_CARD      = Color.WHITE;
    public static final Color BG_HEADER    = new Color(52, 73, 94);
    public static final Color BG_ACCENT    = new Color(41, 128, 185);
    public static final Color BG_SUCCESS   = new Color(39, 174, 96);
    public static final Color BG_WARN      = new Color(230, 126, 34);
    public static final Color BG_DANGER    = new Color(192, 57, 43);
    public static final Color BG_TABLE_ALT = new Color(248, 249, 252);

    public static final Color TEXT_PRIMARY   = new Color(44,  62,  80);
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    public static final Color TEXT_WHITE     = Color.WHITE;
    public static final Color TEXT_LINK      = new Color(41, 128, 185);

    public static final Color BORDER     = new Color(220, 220, 225);
    public static final Color GAUGE_BG   = new Color(220, 220, 225);
    public static final Color GAUGE_FILL = new Color(41, 128, 185);
    public static final Color GAUGE_DONE = new Color(39, 174, 96);

    // 시스템 폰트 설정
    public static final String FONT_NAME = "맑은 고딕";

    public static Font font(int size, int style) {
        Font f = new Font(FONT_NAME, style, size);
        if (!f.getFamily().equals(FONT_NAME)) {
            f = new Font("SansSerif", style, size);
        }
        return f;
    }
    public static Font bold(int size)   { return font(size, Font.BOLD); }
    public static Font plain(int size)  { return font(size, Font.PLAIN); }
}