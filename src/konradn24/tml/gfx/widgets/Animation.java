package konradn24.tml.gfx.widgets;

import java.awt.image.BufferedImage;

public class Animation {

	private int interval, index;
	private long lastTime, timer;
	private BufferedImage[] frames;
	
	private boolean loop, play;
	
	public Animation(int interval, BufferedImage[] frames, boolean loop) {
		this.interval = interval;
		this.frames = frames;
		this.loop = loop;
		play = true;
		index = 0;
		timer = 0;
		lastTime = System.currentTimeMillis();
	}
	
	public void tick() {
		timer += System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		
		if(timer >= interval && play) {
			index++;
			timer = 0;
			
			if(index >= frames.length && loop)
				index = 0;
		}
	}
	
	public BufferedImage getCurrentFrame() {
		return frames[index];
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public boolean isPlay() {
		return play;
	}

	public void setPlay(boolean play) {
		this.play = play;
	}
}
