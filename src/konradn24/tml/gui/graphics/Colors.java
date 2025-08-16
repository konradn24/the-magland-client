package konradn24.tml.gui.graphics;

import static org.lwjgl.nanovg.NanoVG.*;
import org.lwjgl.nanovg.NVGColor;

import konradn24.tml.utils.Utils;

public class Colors {
	
	public static final NVGColor COLOR_PRIMARY = rgba(94, 145, 0, 255);
    public static final NVGColor COLOR_SECONDARY = rgba(165, 255, 0, 255);
    public static final NVGColor COLOR_LIGHT = rgba(255, 255, 255, 48);
    public static final NVGColor COLOR_SHADE_1 = rgba(0, 0, 0, 48);
    public static final NVGColor COLOR_SHADE_2 = rgba(0, 0, 0, 128);
    public static final NVGColor COLOR_SHADE_3 = rgba(0, 0, 0, 192);
    public static final NVGColor COLOR_BACKGROUND = rgba(32, 38, 48, 255);
    public static final NVGColor COLOR_BACKGROUND_DARK = rgba(21, 25, 32, 255);
    public static final NVGColor COLOR_BACKGROUND_LIGHT = rgba(64, 76, 96, 255);
    public static final NVGColor COLOR_OUTLINE = rgba(102, 108, 108, 255);
    public static final NVGColor COLOR_TEXT = rgba(232, 232, 232, 255);
    public static final NVGColor COLOR_TEXT_PLACEHOLDER = rgba(178, 178, 178, 255);
    public static final NVGColor COLOR_COOLDOWN = rgba(180, 180, 180, 180);
    public static final NVGColor COLOR_TEXT_LIGHT = rgba(202, 202, 202, 255);
    public static final NVGColor COLOR_TEXT_DARK = rgba(18, 18, 18, 255);

    public static final NVGColor COLOR_WINDOW_BAR_BACKGROUND = rgba(46, 46, 46, 255);
    public static final NVGColor COLOR_WINDOW_TEXT = rgba(212, 212, 212, 255);
    public static final NVGColor COLOR_WINDOW_BACKGROUND = rgba(88, 88, 88, 255);

    public static final NVGColor COLOR_EXP_WINDOW_BAR = rgba(0, 100, 199, 255);
    public static final NVGColor COLOR_EXP_WINDOW = rgba(0, 150, 249, 255);
    public static final NVGColor COLOR_EXP_WINDOW_TEXT = rgba(50, 200, 255, 255);
    public static final NVGColor COLOR_EXP_WINDOW_BUTTON = rgba(0, 130, 229, 255);

    public static final NVGColor COLOR_BUTTON = rgba(64, 64, 64, 255);

    public static final NVGColor COLOR_GREEN_LIGHT = rgba(0, 255, 0, 64);
    public static final NVGColor COLOR_RED_LIGHT = rgba(255, 0, 0, 64);

    public static NVGColor brighten(NVGColor color, double fraction) {
        int r = Utils.clamp((int)(color.r() * 255 + 255 * fraction), 0, 255);
        int g = Utils.clamp((int)(color.g() * 255 + 255 * fraction), 0, 255);
        int b = Utils.clamp((int)(color.b() * 255 + 255 * fraction), 0, 255);
        int a = (int)(color.a() * 255);
        return rgba(r, g, b, a);
    }

    public static NVGColor alpha(NVGColor color, int alpha) {
        int r = (int)(color.r() * 255);
        int g = (int)(color.g() * 255);
        int b = (int)(color.b() * 255);
        return rgba(r, g, b, alpha);
    }

    // --- utils ---

    public static NVGColor rgba(int r, int g, int b, int a) {
        NVGColor color = NVGColor.create();
        nvgRGBA((byte) r, (byte) g, (byte) b, (byte) a, color);
        return color;
    }
}
