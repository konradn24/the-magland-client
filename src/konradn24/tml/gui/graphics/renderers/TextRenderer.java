package konradn24.tml.gui.graphics.renderers;

import static org.lwjgl.nanovg.NanoVG.*;
import java.util.ArrayList;
import java.util.List;

import konradn24.tml.gui.graphics.Style.AlignX;
import konradn24.tml.gui.graphics.Style.AlignY;

public class TextRenderer {
    public enum Overflow { WRAP, IGNORE, HIDE, ELLIPSIS }

    /**
     * Draws a string aligned within a given rectangle.
     */
    public static void renderString(long vg, String text, float x, float y, float width, float height,
            AlignX alignX, AlignY alignY, Overflow overflow) {

		float lineHeight = nvgTextMetricsHeight(vg);

		if (overflow == Overflow.IGNORE) {
			float drawX = switch (alignX) {
				case LEFT -> x;
				case CENTER -> x + width / 2f;
				case RIGHT -> x + width;
			};

			float drawY = switch (alignY) {
				case TOP -> y;
				case CENTER -> y + height / 2f - lineHeight / 2f;
				case BOTTOM -> y + height - lineHeight;
			};

			nvgTextAlign(vg, (alignX == AlignX.LEFT ? NVG_ALIGN_LEFT
					: alignX == AlignX.CENTER ? NVG_ALIGN_CENTER : NVG_ALIGN_RIGHT) | NVG_ALIGN_TOP);
			nvgText(vg, drawX, drawY, text);
			return;
		}

		// For ELLIPSIS/HIDE we need to clip text manually
		String clippedText = text;
		if (overflow == Overflow.HIDE || overflow == Overflow.ELLIPSIS) {
			float[] bounds = new float[4];
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < text.length(); i++) {
				String t = sb.toString() + text.charAt(i);
				nvgTextBounds(vg, 0, 0, t, bounds);
				if (bounds[2] - bounds[0] > width)
					break;
				sb.append(text.charAt(i));
			}

			if (overflow == Overflow.ELLIPSIS && sb.length() < text.length()) {
				sb.append("...");
			}
			clippedText = sb.toString();
		}

		// Split into lines with wrapping
		List<String> lines = new ArrayList<>();
		for (String part : clippedText.split("\\\\n|\\n")) {
			lines.addAll(wrapLine(vg, part, width));
		}

		float totalTextHeight = lines.size() * lineHeight;
		float startY = switch (alignY) {
			case TOP -> y;
			case CENTER -> y + (height - totalTextHeight) / 2f;
			case BOTTOM -> y + height - totalTextHeight;
		};

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			float drawX = switch (alignX) {
				case LEFT -> x;
				case CENTER -> x + width / 2f;
				case RIGHT -> x + width;
			};

			float drawY = startY + i * lineHeight;

			nvgTextAlign(vg, (alignX == AlignX.LEFT ? NVG_ALIGN_LEFT
					: alignX == AlignX.CENTER ? NVG_ALIGN_CENTER : NVG_ALIGN_RIGHT) | NVG_ALIGN_TOP);
			nvgText(vg, drawX, drawY, line);
		}
	}
    
    public static float[] measureText(long vg, String text, float width, Overflow overflow) {
		float lineHeight = nvgTextMetricsHeight(vg);

		if (overflow == Overflow.IGNORE) {
			return measureText(vg, text);
		}

		// For ELLIPSIS/HIDE we need to clip text manually
		if (overflow == Overflow.HIDE || overflow == Overflow.ELLIPSIS) {
			float[] bounds = new float[4];
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < text.length(); i++) {
				String t = sb.toString() + text.charAt(i);
				nvgTextBounds(vg, 0, 0, t, bounds);
				if (bounds[2] - bounds[0] > width)
					break;
				sb.append(text.charAt(i));
			}

			if (overflow == Overflow.ELLIPSIS && sb.length() < text.length()) {
				sb.append("...");
			}
			
			return measureText(vg, sb.toString());
		}

		// Split into lines with wrapping
		List<String> lines = new ArrayList<>();
		for (String part : text.split("\\\\n|\\n")) {
			lines.addAll(wrapLine(vg, part, width));
		}

		float textHeight = lines.size() * lineHeight;
		
		return new float[] { width, textHeight };
	}

	private static List<String> wrapLine(long vg, String text, float maxWidth) {
		List<String> lines = new ArrayList<>();
		String[] words = text.split(" ");
		StringBuilder line = new StringBuilder();

		for (String word : words) {
			String testLine = line.length() == 0 ? word : line + " " + word;

			float[] bounds = new float[4];
			nvgTextBounds(vg, 0, 0, testLine, bounds);
			float width = bounds[2] - bounds[0];

			if (width <= maxWidth) {
				line = new StringBuilder(testLine);
			} else {
				if (line.length() > 0)
					lines.add(line.toString());
				line = new StringBuilder(word);
			}
		}

		if (!line.isEmpty()) {
			lines.add(line.toString());
		}

		return lines;
	}

    public static float[] measureText(long vg, String text) {
        float[] bounds = new float[4];
        float[] asc = new float[1];
        float[] desc = new float[1];
        float[] lh = new float[1];

        nvgTextBounds(vg, 0, 0, text, bounds);

        nvgTextMetrics(vg, asc, desc, lh);

        float width = bounds[2] - bounds[0];
        float height = lh[0];

        return new float[] { width, height };
    }
	
	private static float nvgTextMetricsHeight(long vg) {
		float[] asc = new float[1];
		float[] desc = new float[1];
		float[] lh = new float[1];
		nvgTextMetrics(vg, asc, desc, lh);
		return lh[0];
	}
}
