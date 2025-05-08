package konradn24.tml.gfx.components;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import konradn24.tml.Handler;
import konradn24.tml.gfx.images.Assets;

public class AdvancedLabel extends Label {
	
	private static final char ICON_START = '{';
	private static final char ICON_END = '}';
	private static final String ICON_WHITESPACE = "     ";
	private static final int ICON_Y_OFFSET = 2;
	
	private List<Icon> icons = new ArrayList<>();
	
	private String originalContent;
	private float iconSizeScale;

	public AdvancedLabel(String content) {
		super(content);
		
		this.originalContent = content;
		this.content = prepareIcons(content);
		
		iconSizeScale = 1f;
	}

	public AdvancedLabel(String content, int x, int y) {
		super(content, x, y);
		
		this.originalContent = content;
		this.content = prepareIcons(content);
		
		iconSizeScale = 1f;
	}
	
	public void render(Graphics2D g) {
		if(invisible)
			return;
		
		super.render(g);
		
		renderIcons(g);
	}
	
	public void render(Graphics2D g, Handler handler) {
		if(invisible)
			return;
		
		super.render(g, handler);
		
		renderIcons(g);
	}
	
	private String prepareIcons(String str) {
		int searchFrom = 0;
		int bracketStart = str.indexOf(ICON_START);
		int bracketEnd = str.indexOf(ICON_END, bracketStart);
		
		while(bracketStart != -1 && bracketEnd != -1) {
			String assetName = str.substring(bracketStart + 1, bracketEnd);
			
			icons.add(new Icon((BufferedImage) Assets.getTexture(assetName), bracketStart));
			
			StringBuilder builder = new StringBuilder(str);
			builder = builder.delete(bracketStart, bracketEnd + 1).insert(bracketStart, ICON_WHITESPACE);
			str = builder.toString();
			
			searchFrom = bracketStart + ICON_WHITESPACE.length();
			bracketStart = str.indexOf(ICON_START, searchFrom);
			bracketEnd = str.indexOf(ICON_END, bracketStart);
		}
		
		return str;
	}
	
	public void calculateSize(Graphics2D g, Font font) {
		super.calculateSize(g, font);
		
		for(Icon icon : icons) {
			int size = (int) (height * iconSizeScale);
			int x = g.getFontMetrics(font).stringWidth(content.substring(0, icon.startIndex));
			
			icon.size = size;
			icon.x = (int) (x + (height - height * iconSizeScale) / 2);
			icon.y = (int) ((height - height * iconSizeScale) / 2);
		}
	}
	
	public void calculateSize(Handler handler, Font font) {
		super.calculateSize(handler, font);
		
		for(Icon icon : icons) {
			int size = (int) (height * iconSizeScale);
			int x = handler.getGame().getDisplay().getCanvas().getFontMetrics(font).stringWidth(content.substring(0, icon.startIndex));
			
			icon.size = size;
			icon.x = (int) (x + (height - height * iconSizeScale) / 2);
			icon.y = (int) ((height - height * iconSizeScale) / 2);
		}
	}
	
	private void renderIcons(Graphics2D g) {
		for(Icon icon : icons) {
			g.drawImage(icon.img, x + icon.x + marginX, y + icon.y + marginY + ICON_Y_OFFSET, icon.size, icon.size, null);
//			g.drawRect(x + icon.x + marginX, y + ICON_Y_OFFSET + marginY, icon.size, icon.size);
		}
	}
	
	@SuppressWarnings("unused")
	private class Icon {
		private BufferedImage img;
		private int startIndex;
		private int x, y, size; // Relative X, Y
		
		public Icon(BufferedImage img, int startIndex) {
			this.img = img;
			this.startIndex = startIndex;
		}
		
		public Icon(BufferedImage img, int x, int y, int size) {
			this.img = img;
			this.x = x;
			this.y = y;
			this.size = size;
		}
	}
	
	public String getContent() {
		return originalContent;
	}

	public void setContent(String content) {
		this.originalContent = content;
		this.content = prepareIcons(content);
	}

	public List<Icon> getIcons() {
		return icons;
	}

	public void setIcons(List<Icon> icons) {
		this.icons = icons;
	}

	public float getIconSizeScale() {
		return iconSizeScale;
	}

	public void setIconSizeScale(float iconSizeScale) {
		this.iconSizeScale = iconSizeScale;
	}
}
