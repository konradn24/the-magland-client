package konradn24.tml.gfx.style;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import konradn24.tml.display.Display;

public class Style {

	// TODO: Dialogs font: AMERICAN TYPEWRITER
	
	public enum AlignX { LEFT, CENTER, RIGHT }
    public enum AlignY { TOP, CENTER, BOTTOM }

    public static int centerX(int width) {
    	return Display.LOGICAL_WIDTH / 2 - width / 2;
    }
    
    public static int centerY(int height) {
    	return Display.LOGICAL_HEIGHT / 2 - height / 2;
    }
    
    /**
     * Calculates position X for element according to container position and alignment.
     */
    public static int alignX(int containerX, int containerWidth, int elementWidth, AlignX alignment) {
        return switch (alignment) {
            case LEFT   -> containerX;
            case CENTER -> containerX + (containerWidth - elementWidth) / 2;
            case RIGHT  -> containerX + containerWidth - elementWidth;
        };
    }

    /**
     * Calculates position Y for element according to container position and alignment.
     */
    public static int alignY(int containerY, int containerHeight, int elementHeight, AlignY alignment) {
        return switch (alignment) {
            case TOP    -> containerY;
            case CENTER -> containerY + (containerHeight - elementHeight) / 2;
            case BOTTOM -> containerY + containerHeight - elementHeight;
        };
    }

    /**
     * Returns point (x, y) for centered element relative to container.
     */
    public static Point center(int containerX, int containerY, int containerWidth, int containerHeight,
                               int elementWidth, int elementHeight) {
        int x = alignX(containerX, containerWidth, elementWidth, AlignX.CENTER);
        int y = alignY(containerY, containerHeight, elementHeight, AlignY.CENTER);
        return new Point(x, y);
    }

    /**
     * Allows for full element positioning relative to container (all combinations).
     */
    public static Point align(int containerX, int containerY, int containerWidth, int containerHeight,
                              int elementWidth, int elementHeight,
                              AlignX alignX, AlignY alignY) {
        int x = alignX(containerX, containerWidth, elementWidth, alignX);
        int y = alignY(containerY, containerHeight, elementHeight, alignY);
        return new Point(x, y);
    }
    
    // ======================
    // Layout helper classes
    // ======================

    public static class Layout {

        /**
         * Automatically positions elements in a vertical column layout.
         */
        public static List<Point> column(int containerX, int containerY, int containerWidth, int containerHeight,
                                	     int elementWidth, int elementHeight, int count, int spacing,
                                         AlignX alignX, AlignY alignY) {

            int totalHeight = count * elementHeight + (count - 1) * spacing;
            int startY = Style.alignY(containerY, containerHeight, totalHeight, alignY);

            List<Point> result = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                int x = Style.alignX(containerX, containerWidth, elementWidth, alignX);
                int y = startY + i * (elementHeight + spacing);
                result.add(new Point(x, y));
            }
            return result;
        }
        
        /**
         * Automatically positions and sizes elements in a vertical column layout.
         * First result array element is element width and height.
         */
        public static List<Point> column(int containerX, int containerY, int containerWidth, int containerHeight,
                                	     int count, int spacing, AlignX alignX) {

        	int totalSpacing = spacing * (count + 1);
        	int elementWidth = containerWidth - spacing * 2;
        	int elementHeight = (containerHeight - totalSpacing) / count;
        	
            List<Point> result = new ArrayList<>();
            result.add(new Point(elementWidth, elementHeight));
            
            for (int i = 0; i < count; i++) {
                int x = Style.alignX(containerX, containerWidth, elementWidth, alignX);
                int y = (containerY + spacing) + (i - 1) * (elementHeight + spacing);
                result.add(new Point(x, y));
            }
            
            return result;
        }
        
        /**
         * Automatically positions elements in a grid layout.
         */
        public static List<Point> grid(int containerX, int containerY, int containerWidth, int containerHeight,
        	    					   int elementWidth, int elementHeight,
        	    					   int columns, int count, int hSpacing, int vSpacing,
        	    					   AlignX alignX, AlignY alignY) {
        	
    	    int rows = (int) Math.ceil(count / (double) columns);

    	    int totalWidth = columns * elementWidth + (columns - 1) * hSpacing;
    	    int totalHeight = rows * elementHeight + (rows - 1) * vSpacing;

    	    int startX = Style.alignX(containerX, containerWidth, totalWidth, alignX);
    	    int startY = Style.alignY(containerY, containerHeight, totalHeight, alignY);

    	    List<Point> result = new ArrayList<>();

    	    for (int i = 0; i < count; i++) {
    	        int row = i / columns;
    	        int col = i % columns;

    	        int x = startX + col * (elementWidth + hSpacing);
    	        int y = startY + row * (elementHeight + vSpacing);

    	        result.add(new Point(x, y));
    	    }

    	    return result;
    	}
    }
}
