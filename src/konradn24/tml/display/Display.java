// LWJGL Display system (replacement for AWT/Swing)
package konradn24.tml.display;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import konradn24.tml.graphics.renderer.Texture;
import konradn24.tml.gui.graphics.Fonts;

public class Display {

	public static final int LOGICAL_WIDTH = 1920;
	public static final int LOGICAL_HEIGHT = 1080;
	public static final Matrix4f PROJECTION = new Matrix4f().ortho2D(0, LOGICAL_WIDTH, LOGICAL_HEIGHT, 0);
	
	private long window;
	private long vg;

	private int windowWidth, windowHeight;
	private int viewportX, viewportY, viewportWidth, viewportHeight;

	private boolean fullscreen;
	private String title;

	public Display(String title) {
		this.title = title;
		createDisplay(false);
	}

	public void createDisplay(boolean fullscreen) {
		this.fullscreen = fullscreen;

		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		long monitor = fullscreen ? glfwGetPrimaryMonitor() : MemoryUtil.NULL;

		GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		windowWidth = videoMode.width();
		windowHeight = videoMode.height();

		window = glfwCreateWindow(windowWidth, windowHeight, title, monitor, MemoryUtil.NULL);
		if (window == MemoryUtil.NULL) {
			throw new RuntimeException("Failed to create GLFW window");
		}

		glfwMakeContextCurrent(window);
		createCapabilities();
		glfwSwapInterval(0);
		glfwShowWindow(window);
		
		glfwSetFramebufferSizeCallback(window, (win, w, h) -> onResize(w, h));
		
		var widthBuffer = BufferUtils.createIntBuffer(1);
		var heightBuffer = BufferUtils.createIntBuffer(1);
		
		glfwGetWindowSize(window, widthBuffer, heightBuffer);
		
		onResize(widthBuffer.get(), heightBuffer.get());
		
		vg = NanoVGGL3.nnvgCreate(NanoVGGL3.NVG_ANTIALIAS);
		
		if(vg == 0) {
			throw new RuntimeException("Could not init NanoVG");
		}
		
		int font = NanoVG.nvgCreateFont(vg, Fonts.GLOBAL_FONT, Fonts.GLOBAL_FONT_PATH);
		
		if(font == -1) {
			throw new RuntimeException("Could not load global font");
		}
		
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	private void onResize(int width, int height) {
		float logicalAspect = (float) LOGICAL_WIDTH / LOGICAL_HEIGHT;
		float realAspect = (float) width / height;
		
		if (realAspect > logicalAspect) {
			viewportHeight = height;
			viewportWidth = (int) (height * logicalAspect);
		} else {
			viewportWidth = width;
			viewportHeight = (int) (width / logicalAspect);
		}
		
		viewportX = (width - viewportWidth) / 2;
		viewportY = (height - viewportHeight) / 2;
		
		glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
	}

	public void showLoadingScreen(String info) {
		glfwPollEvents();
		glClearColor(0, 0, 0, 1);
		glClear(GL_COLOR_BUFFER_BIT);
		
		NanoVG.nvgBeginFrame(vg, LOGICAL_WIDTH, LOGICAL_HEIGHT, 1);
		NanoVG.nvgBeginPath(vg);
		
		NanoVG.nvgFontSize(vg, 64.0f);
		NanoVG.nvgFontFace(vg, Fonts.GLOBAL_FONT);
		
		float textWidth = NanoVG.nvgTextBounds(vg, 0, 0, info, new float[4]);
		float x = LOGICAL_WIDTH / 2f - textWidth / 2f;
		float y = LOGICAL_HEIGHT / 2f;
		
		NanoVG.nvgFillColor(vg, NVGColor.create().r(1.0f).g(1.0f).b(1.0f).a(1.0f));
		NanoVG.nvgText(vg, x, y, info);
		
		NanoVG.nvgEndFrame(vg);
		
		glfwSwapBuffers(window);
	}
	
	public void setCursor(long cursor) {
		glfwSetCursor(window, cursor);
	}
	
	public void setCursor(Texture cursorTexture) {
		if(cursorTexture.cursor == 0) {
			return;
		}
		
		glfwSetCursor(window, cursorTexture.cursor);
	}
	
	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}

	public void destroy() {
		glfwDestroyWindow(window);
	}

	public static float x(float x) {
    	return x * Display.LOGICAL_WIDTH;
    }
    
    public static float y(float y) {
    	return y * Display.LOGICAL_HEIGHT;
    }
	
	public long getWindow() {
		return window;
	}
	
	public long getVG() {
		return vg;
	}

	public int getWidth() {
		return windowWidth;
	}

	public int getHeight() {
		return windowHeight;
	}

	public int getWindowWidth() {
		return windowWidth;
	}

	public int getWindowHeight() {
		return windowHeight;
	}

	public int getViewportX() {
		return viewportX;
	}

	public int getViewportY() {
		return viewportY;
	}

	public int getViewportWidth() {
		return viewportWidth;
	}

	public int getViewportHeight() {
		return viewportHeight;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}
}
