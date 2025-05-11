package konradn24.tml.gfx.style;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import konradn24.tml.gfx.images.Assets;

public class StyleText {
	public enum AlignX { LEFT, CENTER, RIGHT }
    public enum AlignY { TOP, CENTER, BOTTOM }

    public static Point getStringSize(Graphics2D g, String text) {
    	FontMetrics fm = g.getFontMetrics();

        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent(); // baseline to top
        
        return new Point(textWidth, textHeight);
    }
    
    /**
     * Draws a string aligned within a given rectangle.
     */
    public static void drawString(Graphics2D g, String text, int x, int y, int width, int height,
                                  AlignX alignX, AlignY alignY) {
        FontMetrics fm = g.getFontMetrics();

        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent(); // baseline to top

        int drawX = switch (alignX) {
            case LEFT -> x;
            case CENTER -> x + (width - textWidth) / 2;
            case RIGHT -> x + width - textWidth;
        };

        int drawY = switch (alignY) {
            case TOP -> y + textHeight;
            case CENTER -> y + (height + textHeight) / 2 - 2; // tweak for visual center
            case BOTTOM -> y + height;
        };
        
    	g.drawString(text, drawX, drawY);
    }
    
    /**
     * Draws a string on the given (x, y) coordinates.
     */
    public static void drawString(Graphics2D g, String text, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        
        int textHeight = fm.getAscent(); // ascent is the distance from baseline to the top of the text
        
        g.drawString(text, x, y - textHeight - 2);
    }
    
    /**
     * Draws a string centered on the given (x, y) coordinates.
     * The coordinates are considered to be the center of the text.
     */
    public static void drawCenteredString(Graphics2D g, String text, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent(); // ascent is the distance from baseline to the top of the text
        
        int drawX = x - textWidth / 2;
        int drawY = y + textHeight / 2; // draw from baseline
        
        g.drawString(text, drawX, drawY);
    }
    
    /**
     * Draws a string centered within a container (with padding).
     * Automatically adjusts font size to fit the text inside the container.
     */
    public static void drawStringWithAutoSizing(Graphics2D g, String text, int x, int y, int width, int height, int padding) {
        // Calculate the available space inside the container, after considering padding
        int availableWidth = width - 2 * padding;
        int availableHeight = height - 4 * padding;

        // Set the initial font size (you can start with any reasonable default size)
        int fontSize = 80;
        Font font = g.getFont().deriveFont((float) fontSize);

        // Set the font to the graphics context
        g.setFont(font);

        // Calculate the size of the text with the current font size
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent(); // ascent is the distance from baseline to the top of the text

        // Scale down the font size to fit the available width and height
        while (textWidth > availableWidth || textHeight > availableHeight) {
            fontSize--;
            font = g.getFont().deriveFont((float) fontSize);
            g.setFont(font);
            fm = g.getFontMetrics();
            textWidth = fm.stringWidth(text);
            textHeight = fm.getAscent();
        }

        // Now, calculate the starting point to center the text in the available space
        int drawX = x + padding + (availableWidth - textWidth) / 2;
        int drawY = y + padding + (availableHeight + textHeight) / 2;

        // Draw the text
        g.drawString(text, drawX, drawY);
    }
    
    /**
     * Draws text with icons, where icon names are surrounded by curly braces: {ICON_NAME}.
     * Icons will be drawn in place of their respective icon names.
     * Icons are resized to match the height of the text.
     */
    public static void drawStringWithIcons(Graphics2D g, String text, int x, int y) {
    	FontMetrics fm = g.getFontMetrics();
        int currentX = x;
        int currentY = y;

        // Get the height of the text
        int textHeight = fm.getAscent(); // Ascent is the distance from baseline to the top of the text

        // Regex pattern to match icon names in curly braces: {ICON_NAME}
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(text);

        int lastEnd = 0;  // To keep track of the last match's end position

        // Process all matches of icon names
        while (matcher.find()) {
            // Draw the text before the icon
            String beforeIcon = text.substring(lastEnd, matcher.start());
            g.drawString(beforeIcon, currentX, currentY);
            currentX += fm.stringWidth(beforeIcon); // Update the X position

            // Get the icon name and load the image
            String iconName = matcher.group(1);
            BufferedImage icon = (BufferedImage) Assets.getTexture(iconName);

            // Calculate the scaling factor based on the text height
            int iconHeight = textHeight;  // Scale icon to match text height
            int iconWidth = icon.getWidth(null);
            
            // Calculate the scaling factor (preserve the aspect ratio)
            double scaleFactor = (double) iconHeight / icon.getHeight(null);
            iconWidth = (int) (iconWidth * scaleFactor);
            iconHeight = (int) (iconHeight * scaleFactor);

            // Draw the icon, scaled to match the height of the text
            g.drawImage(icon, currentX, currentY - iconHeight, iconWidth, iconHeight, null);
            currentX += iconWidth; // Update the X position after drawing the icon

            // Update the position of the last end of the match
            lastEnd = matcher.end();
        }

        // Draw the remaining text after the last icon
        String remainingText = text.substring(lastEnd);
        g.drawString(remainingText, currentX, currentY);
    }
    
    /**
     * Function to scale the text (and icons) to fit within the container height
     */
    public static void drawStringWithIconsScaledToHeight(Graphics2D g, String text, int containerX, int containerY, int containerWidth, int containerHeight) {
        // Calculate the scaling factor for the text based on the container height
        double scaleFactor = (double) containerHeight / getTextHeightWithIcons(g, text);

        // Set the font size to the scaled value
        Font originalFont = g.getFont();
        Font scaledFont = originalFont.deriveFont((float) (originalFont.getSize() * scaleFactor));
        g.setFont(scaledFont);

        // Now calculate the text width after scaling
        FontMetrics fm = g.getFontMetrics();
        int textWidth = getTextWidthWithIcons(g, text);

        // Calculate the position to center the text horizontally and vertically
        int x = containerX + (containerWidth - textWidth) / 2;
        int y = containerY + (containerHeight + fm.getAscent()) / 2;

        // Draw the scaled text with icons
        drawStringWithIcons(g, text, x, y);
    }

    // Function to calculate the height of the text with icons (scalable to fit the container)
    private static int getTextHeightWithIcons(Graphics2D g, String text) {
        FontMetrics fm = g.getFontMetrics();
        int textHeight = fm.getAscent(); // Ascent defines the height of the text

        // Regex pattern to match icon names in curly braces: {ICON_NAME}
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            // Get the icon name and load the image
            String iconName = matcher.group(1);
            BufferedImage icon = (BufferedImage) Assets.getTexture(iconName);

            // Calculate the scaling factor for the icon based on the text height
            int iconHeight = fm.getAscent(); // Scale the icon to match the height of the text
            int iconWidth = icon.getWidth(null);

            // Scale the icon to match the text height
            double scaleFactor = (double) iconHeight / icon.getHeight(null);
            iconWidth = (int) (iconWidth * scaleFactor);
            iconHeight = (int) (iconHeight * scaleFactor);

            // Update the textHeight to include the icon's height
            textHeight = Math.max(textHeight, iconHeight);
        }

        return textHeight;
    }

    // Function to calculate the width of the text with icons
    private static int getTextWidthWithIcons(Graphics2D g, String text) {
        FontMetrics fm = g.getFontMetrics();
        int width = 0;

        // Regex pattern to match icon names in curly braces: {ICON_NAME}
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(text);

        int lastEnd = 0;  // To keep track of the last match's end position

        while (matcher.find()) {
            // Calculate width of text before the icon
            String beforeIcon = text.substring(lastEnd, matcher.start());
            width += fm.stringWidth(beforeIcon); // Add width of text before icon

            // Get the icon name and load the image
            String iconName = matcher.group(1);
            BufferedImage icon = (BufferedImage) Assets.getTexture(iconName);

            // Calculate the scaling factor based on the text height
            int iconHeight = fm.getAscent();  // Scale icon to match text height
            int iconWidth = icon.getWidth(null);

            // Calculate the scaling factor (preserve the aspect ratio)
            double scaleFactor = (double) iconHeight / icon.getHeight(null);
            iconWidth = (int) (iconWidth * scaleFactor);

            width += iconWidth;  // Add width of icon
            lastEnd = matcher.end();
        }

        // Add width of remaining text
        String remainingText = text.substring(lastEnd);
        width += fm.stringWidth(remainingText);

        return width;
    }
}
