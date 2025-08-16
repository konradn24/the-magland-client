package konradn24.tml.graphics.renderer;

public class Animation {

	private int interval, index;
	private long lastTime, timer;
	private Texture[] frames;
	
	private boolean loop, play;
	
	public Animation(int interval, Texture[] frames, boolean loop) {
		this.interval = interval;
		this.frames = frames;
		this.loop = loop;
		play = true;
		index = 0;
		timer = 0;
		lastTime = System.currentTimeMillis();
	}
	
	public void update() {
		if(!play) {
			return;
		}
		
		timer += System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		
		if(timer >= interval) {
			index++;
			timer = 0;
			
			if(index >= frames.length) {
				if(loop) {
					index = 0;
				} else {
					index = frames.length - 1;
					play = false;
				}
			}
		}
	}
	
	public Texture getCurrentFrame() {
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
