package konradn24.tml.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import konradn24.tml.Handler;
import konradn24.tml.debug.Logging;
import konradn24.tml.gfx.components.Button;
import konradn24.tml.gfx.components.Checkbox;
import konradn24.tml.gfx.components.Label;
import konradn24.tml.gfx.images.Assets;
import konradn24.tml.gfx.images.ImageLoader;

public class SettingsState extends State {

//	private static final int CHECKBOX_SIZE = 32;
	
	public static State lastState;
	
	private BufferedImage background;
	private Button back;
	
	private Checkbox showFPS;
	private Label showFPSLabel;
	
	public static boolean showFPSCounter;
	
	public SettingsState(Handler handler) {
		super(handler);
		
		background = ImageLoader.loadImage("/textures/background.png");
		
		back = new Button(Assets.getAnimation("iconBack"), 5, 5, 32, 32, handler);
		
//		showFPS = new Checkbox(handler.getStyle().positionCenterX("settings", 0, CHECKBOX_SIZE), 
//				handler.getStyle().positionCenterY("settings", 0, CHECKBOX_SIZE), 
//				CHECKBOX_SIZE, CHECKBOX_SIZE, handler); // 50, 70, 32, 32
		
		showFPSLabel = new Label("Show FPS counter", handler);
		showFPSLabel.setPositionCenterX(true, "settings", 0);
		showFPSLabel.setPositionCenterY(true, "settings", 0);
		showFPSLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
		showFPSLabel.setColor(Color.BLACK);
		showFPSLabel.setMarginX(90);
		showFPSLabel.setMarginY(-5);
		
		Logging.info("Settings State initialized");
	}

	@Override
	public void tick() {
		back.tick();
		
		//When mouse button released on btn
		if(back.isLeftReleased()) {
			State.setState(lastState);
		}
		
//		showFPS.tick();
		showFPSCounter = showFPS.getState();
	}

	@Override
	public void render(Graphics2D g) {
		g.drawImage(background, 0, 0, handler.getDisplayWidth(), handler.getDisplayHeight(), null);
		
		//Back button
		back.render(g);
		
		//FPS counter
//		showFPS.render(g);
		showFPSLabel.render(g);
	}
}
