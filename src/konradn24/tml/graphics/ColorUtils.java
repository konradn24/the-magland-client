package konradn24.tml.graphics;

public class ColorUtils {

    public static float[] rgbToHsb(float r, float g, float b) {
        float cMax = Math.max(r, Math.max(g, b));
        float cMin = Math.min(r, Math.min(g, b));
        float delta = cMax - cMin;

        float h = 0f;
        if (delta != 0) {
            if (cMax == r) {
                h = ((g - b) / delta) % 6f;
            } else if (cMax == g) {
                h = ((b - r) / delta) + 2f;
            } else {
                h = ((r - g) / delta) + 4f;
            }
            h /= 6f;
            if (h < 0) h += 1f;
        }

        float s = cMax == 0 ? 0 : delta / cMax;
        float v = cMax;

        return new float[] { h, s, v };
    }

    public static float[] hsbToRgb(float h, float s, float v) {
        float r = 0, g = 0, b = 0;

        int i = (int)(h * 6f);
        float f = (h * 6f) - i;
        float p = v * (1f - s);
        float q = v * (1f - f * s);
        float t = v * (1f - (1f - f) * s);

        switch (i % 6) {
            case 0: r = v; g = t; b = p; break;
            case 1: r = q; g = v; b = p; break;
            case 2: r = p; g = v; b = t; break;
            case 3: r = p; g = q; b = v; break;
            case 4: r = t; g = p; b = v; break;
            case 5: r = v; g = p; b = q; break;
        }

        return new float[] { r, g, b };
    }

    public static float[] adjustBrightness(float r, float g, float b, float delta) {
        float[] hsb = rgbToHsb(r, g, b);
        hsb[2] = clamp01(hsb[2] + delta);
        return hsbToRgb(hsb[0], hsb[1], hsb[2]);
    }

    public static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }
}

