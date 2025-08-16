package konradn24.tml.display;

import static org.lwjgl.glfw.GLFW.*;

public class Cursor {

	public static final long NORMAL		= glfwCreateStandardCursor(GLFW_CURSOR_NORMAL);
	public static final long ARROW   	= glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
	public static final long IBEAM   	= glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);
	public static final long CROSSHAIR  = glfwCreateStandardCursor(GLFW_CROSSHAIR_CURSOR);
	public static final long HAND    	= glfwCreateStandardCursor(GLFW_HAND_CURSOR);
	public static final long HRESIZE 	= glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
	public static final long VRESIZE 	= glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR);
}
