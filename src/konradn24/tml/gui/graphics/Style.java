package konradn24.tml.gui.graphics;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

import konradn24.tml.display.Display;

public class Style {

	// TODO: Dialogs font: AMERICAN TYPEWRITER
	
	public enum AlignX { LEFT, CENTER, RIGHT }
    public enum AlignY { TOP, CENTER, BOTTOM }

    public static float centerX(float width) {
    	return Display.x(.5f) - width / 2;
    }
    
    public static float centerY(float height) {
    	return Display.y(.5f) - height / 2;
    }
    
    /**
     * Calculates position X for element according to container position and alignment.
     */
    public static float alignX(float containerX, float containerWidth, float elementWidth, AlignX alignment) {
        return switch (alignment) {
            case LEFT   -> containerX;
            case CENTER -> containerX + (containerWidth - elementWidth) / 2;
            case RIGHT  -> containerX + containerWidth - elementWidth;
        };
    }

    /**
     * Calculates position Y for element according to container position and alignment.
     */
    public static float alignY(float containerY, float containerHeight, float elementHeight, AlignY alignment) {
        return switch (alignment) {
            case TOP    -> containerY;
            case CENTER -> containerY + (containerHeight - elementHeight) / 2;
            case BOTTOM -> containerY + containerHeight - elementHeight;
        };
    }

    /**
     * Returns point (x, y) for centered element relative to container.
     */
    public static Vector2f center(float containerX, float containerY, float containerWidth, float containerHeight,
                               float elementWidth, float elementHeight) {
        float x = alignX(containerX, containerWidth, elementWidth, AlignX.CENTER);
        float y = alignY(containerY, containerHeight, elementHeight, AlignY.CENTER);
        return new Vector2f(x, y);
    }

    /**
     * Allows for full element positioning relative to container (all combinations).
     */
    public static Vector2f align(float containerX, float containerY, float containerWidth, float containerHeight,
                              float elementWidth, float elementHeight,
                              AlignX alignX, AlignY alignY) {
        float x = alignX(containerX, containerWidth, elementWidth, alignX);
        float y = alignY(containerY, containerHeight, elementHeight, alignY);
        return new Vector2f(x, y);
    }
    
    // ======================
    // Layout helper classes
    // ======================

    public static class Layout {

        /**
         * Automatically positions elements in a vertical column layout.
         */
        public static List<Vector2f> column(float containerX, float containerY, float containerWidth, float containerHeight,
                                	     float elementWidth, float elementHeight, int count, float spacing,
                                         AlignX alignX, AlignY alignY) {

            float totalHeight = count * elementHeight + (count - 1) * spacing;
            float startY = Style.alignY(containerY, containerHeight, totalHeight, alignY);

            List<Vector2f> result = new ArrayList<>();
            
            for (int i = 0; i < count; i++) {
                float x = Style.alignX(containerX, containerWidth, elementWidth, alignX);
                float y = startY + i * (elementHeight + spacing);
                result.add(new Vector2f(x, y));
            }
            
            return result;
        }
        
        /**
         * Automatically positions and sizes elements in a vertical column layout.
         * First result array element is element width and height.
         */
        public static List<Vector2f> column(float containerX, float containerY, float containerWidth, float containerHeight,
                                	     int count, float spacing, AlignX alignX) {

        	float totalSpacing = spacing * (count + 1);
        	float elementWidth = containerWidth - spacing * 2;
        	float elementHeight = (containerHeight - totalSpacing) / count;
        	
            List<Vector2f> result = new ArrayList<>();
            result.add(new Vector2f(elementWidth, elementHeight));
            
            for (int i = 0; i < count; i++) {
                float x = Style.alignX(containerX, containerWidth, elementWidth, alignX);
                float y = (containerY + spacing) + (i - 1) * (elementHeight + spacing);
                result.add(new Vector2f(x, y));
            }
            
            return result;
        }
        
        /**
         * Automatically positions elements in a grid layout.
         */
        public static List<Vector2f> grid(float containerX, float containerY, float containerWidth, float containerHeight,
        	    					   float elementWidth, float elementHeight,
        	    					   int columns, int count, float hSpacing, float vSpacing,
        	    					   AlignX alignX, AlignY alignY) {
        	
    	    int rows = (int) Math.ceil(count / (double) columns);

    	    float totalWidth = columns * elementWidth + (columns - 1) * hSpacing;
    	    float totalHeight = rows * elementHeight + (rows - 1) * vSpacing;

    	    float startX = Style.alignX(containerX, containerWidth, totalWidth, alignX);
    	    float startY = Style.alignY(containerY, containerHeight, totalHeight, alignY);

    	    List<Vector2f> result = new ArrayList<>();

    	    for (int i = 0; i < count; i++) {
    	        int row = i / columns;
    	        int col = i % columns;

    	        float x = startX + col * (elementWidth + hSpacing);
    	        float y = startY + row * (elementHeight + vSpacing);

    	        result.add(new Vector2f(x, y));
    	    }

    	    return result;
    	}
    }
}
