package konradn24.tml.gfx;

import java.awt.Color;
import java.awt.Font;

public final class Presets {
	
	// Fonts
	public static final Font FONT_GLOBAL = new Font("Gill Sans", 0, 16);
	public static final Font FONT_INVENTORY = new Font("Andale Mono", 0, 16);
	
	// Colors
	public static final Color COLOR_PRIMARY = new Color(94, 145, 0);
	public static final Color COLOR_SECONDARY = new Color(165, 255, 0);
	public static final Color COLOR_LIGHT = new Color(255, 255, 255, 48);
	public static final Color COLOR_SHADE_1 = new Color(0, 0, 0, 48);
	public static final Color COLOR_SHADE_2 = new Color(0, 0, 0, 128);
	public static final Color COLOR_SHADE_3 = new Color(0, 0, 0, 192);
	public static final Color COLOR_BACKGROUND = new Color(0, 0, 0, 176);
	public static final Color COLOR_COOLDOWN = new Color(180, 180, 180, 180);
	public static final Color COLOR_TEXT_LIGHT = new Color(202, 202, 202);
	public static final Color COLOR_TEXT_DARK = new Color(18, 18, 18);
	
	public static final Color COLOR_WINDOW_BAR_BACKGROUND = new Color(46, 46, 46);
	public static final Color COLOR_WINDOW_TEXT = new Color(212, 212, 212);
	public static final Color COLOR_WINDOW_BACKGROUND = new Color(88, 88, 88);
	
	public static final Color COLOR_EXP_WINDOW_BAR = new Color(0, 100, 199);
	public static final Color COLOR_EXP_WINDOW = new Color(0, 150, 249);
	public static final Color COLOR_EXP_WINDOW_TEXT = new Color(50, 200, 255);
	public static final Color COLOR_EXP_WINDOW_BUTTON = new Color(0, 130, 229);
	
	public static final Color COLOR_BUTTON = new Color(64, 64, 64);
	
	public static final Color COLOR_GREEN_LIGHT = new Color(0, 255, 0, 64);
	public static final Color COLOR_RED_LIGHT = new Color(255, 0, 0, 64);
	
	public static Color brighten(Color color, double fraction) {

        int red = (int) Math.round(Math.min(255, color.getRed() + 255 * fraction));
        int green = (int) Math.round(Math.min(255, color.getGreen() + 255 * fraction));
        int blue = (int) Math.round(Math.min(255, color.getBlue() + 255 * fraction));

        int alpha = color.getAlpha();

        return new Color(red, green, blue, alpha);

    }
}
