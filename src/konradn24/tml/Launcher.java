package konradn24.tml;

import konradn24.tml.debug.Logging;

public class Launcher {
	
	public static final String VERSION = "ALPHA.2";
	
	public static long time;

	public static void main(String[] args) {
		time = System.currentTimeMillis();
		
		Logging.init();
		Logging.info("Start time: " + time);
		
		Game game = new Game("The Magland", 960, 720); //640 480
		game.start();
		
		Logging.info("Main thread started. Elapsed time: " + (System.currentTimeMillis() - time) + "ms");
	}
}
