package konradn24.tml.states;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import konradn24.tml.Handler;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.AdvancedLabel;
import konradn24.tml.gfx.components.Label;
import konradn24.tml.gfx.images.ImageLoader;

public class CreditsState extends State {

	private BufferedImage background;
	private Label title;
	private AdvancedLabel content;
	
	public CreditsState(Handler handler) {
		super(handler);
		
		handler.getStyle().addLayout(this.getClass(), "credits", 12, 1);
		
		background = ImageLoader.loadImage("/textures/background.png");
		
		title = new Label("Credits");
		title.setCenterX(true);
		title.setPositionCenterY(true, "credits", 2);
		title.setColor(Presets.COLOR_SECONDARY);
		title.setFont(Presets.FONT_GLOBAL.deriveFont(68f));
		
		content = new AdvancedLabel("{heart} by Kiranshastry\n"
				+ "{apple} by Freepik\n"
				+ "{drop} by Vectors Market");
		content.setCenterX(true);
		content.setPositionCenterY(true, "credits", 4);
		content.setColor(Presets.COLOR_SECONDARY);
		content.setFont(Presets.FONT_GLOBAL.deriveFont(34f));
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(background, 0, 0, handler.getWidth(), handler.getHeight(), null);
		
		title.render(g, handler);
		content.render(g, handler);
	}
}
