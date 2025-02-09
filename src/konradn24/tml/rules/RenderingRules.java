package konradn24.tml.rules;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import konradn24.tml.Handler;
import konradn24.tml.gfx.Style.GridLayout;

public class RenderingRules extends RuleSet {
	
	@Rule public boolean layoutBorders;
	
	public void render(Graphics g, Handler handler) {
		if(layoutBorders) renderBorders(g, handler);
	}
	
	private void renderBorders(Graphics g, Handler handler) {
		int visibleLayouts = 0;
		
		for(GridLayout layout : handler.getStyle().getLayouts()) {
			if(layout.state == null)
				continue;
			
			if(!handler.getGame().getGameState().getClass().equals(layout.state))
				continue;
			
			int cellSizeX = layout.width / layout.columns;
			int cellSizeY = layout.height / layout.rows;
			
			for(int i = 0; i < layout.columns; i++) {
				for(int j = 0; j < layout.rows; j++) {
					g.setColor(Color.magenta);
					g.drawRect(layout.x + i * cellSizeX, layout.y + j * cellSizeY, cellSizeX, cellSizeY);
					
					if(handler.getMouseManager().isOn(layout.x + i * cellSizeX, layout.y + j * cellSizeY, cellSizeX, cellSizeY)) {
						g.setFont(new Font("Arial", Font.BOLD, 18));
						g.setColor(Color.red);
						g.drawString(layout.id + "(" + i + ", " + j + ")", handler.getMouseManager().getMouseX() + 10, handler.getMouseManager().getMouseY() + visibleLayouts * 25);
					}
				}
			}
			
			visibleLayouts++;
		}
	}
}
