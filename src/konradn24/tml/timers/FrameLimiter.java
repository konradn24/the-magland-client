package konradn24.tml.timers;

public class FrameLimiter {
	
	private double timePerFrame;
	private long lastTime;
	
	private int fps;
	private int syncCounter;
	private long lastSecond;
	
	public FrameLimiter(int targetFPS) {
		this.timePerFrame = 1.0 / targetFPS;
		this.lastTime = System.nanoTime();
		this.lastSecond = System.nanoTime();
	}
	
	public void sync() {
		long now = System.nanoTime();
		double elapsed = (now - lastTime) / 1_000_000_000.0;
		
		syncCounter++;
		
		if(now - lastSecond >= 1_000_000_000) {
			fps = syncCounter;
			syncCounter = 0;
			lastSecond = now;
		}
		
		while(elapsed < timePerFrame) {
			try {
				Thread.sleep(0);
			} catch(InterruptedException ignored) {}
			
			now = System.nanoTime();
			elapsed = (now - lastTime) / 1_000_000_000.0;
		}
		
		lastTime = System.nanoTime();
	}
	
	public int getFPS() {
		return fps;
	}
	
	public void setTargetFPS(int targetFPS) {
		this.timePerFrame = 1.0 / targetFPS;
	}
}
