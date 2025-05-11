package konradn24.tml.states;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import konradn24.tml.Handler;
import konradn24.tml.gfx.Presets;
import konradn24.tml.gfx.components.Label;
import konradn24.tml.gfx.images.ImageLoader;

public class CreditsState extends State {

	private BufferedImage background;
	private Label title, content;
	
	public CreditsState(Handler handler) {
		super(handler);
		
		background = ImageLoader.loadImage("/textures/background.png");
		
		title = new Label("Credits", 720, 100, 480, 200, 0, handler);
		title.setColor(Presets.COLOR_SECONDARY);
		title.setFont(Presets.FONT_GLOBAL.deriveFont(68f));
		
		content = new Label("{heart} by Kiranshastry\n"
				+ "{apple} by Freepik\n"
				+ "{drop} by Vectors Market", 100, 600, 1720, 800, 0, handler);
		content.setColor(Presets.COLOR_SECONDARY);
		content.setFont(Presets.FONT_GLOBAL.deriveFont(34f));
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render(Graphics2D g) {
		g.drawImage(background, 0, 0, handler.getDisplayWidth(), handler.getDisplayHeight(), null);
		
		title.render(g);
		content.render(g);
	}
}
