// LWJGL Display system (replacement for AWT/Swing)
package konradn24.tml.display;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLDebugMessageCallback;

import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import konradn24.tml.graphics.renderer.Texture;
import konradn24.tml.gui.graphics.Fonts;
import konradn24.tml.utils.Logging;

public class Display {

	public static final int LOGICAL_WIDTH = 1920;
	public static final int LOGICAL_HEIGHT = 1080;
	public static final Matrix4f PROJECTION = new Matrix4f().ortho2D(0, LOGICAL_WIDTH, LOGICAL_HEIGHT, 0);
	
	private long window;
	private long vg;
	private int windowedX, windowedY, windowedWidth, windowedHeight;

	private int windowWidth, windowHeight;
	private int viewportX, viewportY, viewportWidth, viewportHeight;

	private boolean fullscreen;
	private String title;

	public Display(String title) {
		this.title = title;
		this.fullscreen = false;
		
		createDisplay();
	}

	public void createDisplay() {
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		windowWidth = videoMode.width();
		windowHeight = videoMode.height();

		window = glfwCreateWindow(windowWidth, windowHeight, title, MemoryUtil.NULL, MemoryUtil.NULL);
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
		
		int width = widthBuffer.get(0), height = heightBuffer.get(0);
		
		onResize(width, height);
		
		int[] xpos = new int[1];
		int[] ypos = new int[1];
		glfwGetWindowPos(window, xpos, ypos);
		windowedX = xpos[0];
		windowedY = ypos[0];
		windowedWidth = width;
		windowedHeight = height;
		
		vg = NanoVGGL3.nnvgCreate(NanoVGGL3.NVG_ANTIALIAS);
		
		if(vg == 0) {
			throw new RuntimeException("Could not init NanoVG");
		}
		
		int font = NanoVG.nvgCreateFont(vg, Fonts.GLOBAL_FONT, Fonts.GLOBAL_FONT_PATH);
		
		if(font == -1) {
			throw new RuntimeException("Could not load global font");
		}
		
		GL43.glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
		    Logging.error("OpenGL: " + GLDebugMessageCallback.getMessage(length, message));
		}, 0);
	}
	
	public void enableFullscreen() {
        fullscreen = true;
        
        int[] xpos = new int[1];
        int[] ypos = new int[1];
        int[] w = new int[1];
        int[] h = new int[1];
        glfwGetWindowPos(window, xpos, ypos);
        glfwGetWindowSize(window, w, h);

        windowedX = xpos[0];
        windowedY = ypos[0];
        windowedWidth = w[0];
        windowedHeight = h[0];
        
        long monitor = glfwGetPrimaryMonitor();
        var mode = glfwGetVideoMode(monitor);

        glfwSetWindowMonitor(window, monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate());
    }
	
	public void disableFullscreen() {
		fullscreen = false;
		
		glfwSetWindowMonitor(window, MemoryUtil.NULL, windowedX, windowedY, windowedWidth, windowedHeight, 0);
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
