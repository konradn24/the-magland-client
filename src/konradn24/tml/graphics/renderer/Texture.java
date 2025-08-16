package konradn24.tml.graphics.renderer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import konradn24.tml.utils.Logging;

public class Texture {
	
	public final SpriteSheet sheet;
	public final float u0, v0, u1, v1;
	public final float width, height;
	
	public long cursor;
	
	public Texture(SpriteSheet sheet, float x, float y, float width, float height, boolean generateCursor) {
		this.sheet = sheet;
		this.width = width;
		this.height = height;
		
		float sheetWidth = sheet.getWidth();
		float sheetHeight = sheet.getHeight();
		
		this.u0 = x / sheetWidth;
		this.v0 = y / sheetHeight;
		this.u1 = (x + width) / sheetWidth;
		this.v1 = (y + height) / sheetHeight;
		
		if(generateCursor) generateCursor((int) x, (int) y, (int) width, (int) height, 0, 0);
	}
	
	public Texture(long vg, String path, boolean generateCursor) {
		this.sheet = new SpriteSheet(vg, path);
		this.width = sheet.getWidth();
		this.height = sheet.getHeight();
		this.u0 = 0;
		this.v0 = 0;
		this.u1 = 1;
		this.v1 = 1;
		
		if(generateCursor) generateCursor(0, 0, (int) width, (int) height, 0, 0);
	}
	
	private void generateCursor(int x, int y, int width, int height, int hotspotX, int hotspotY) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer sheetW = stack.mallocInt(1);
			IntBuffer sheetH = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);
			
			ByteBuffer sheetData = STBImage.stbi_load(sheet.getPath(), sheetW, sheetH, channels, 4);
			if(sheetData == null) {
				Logging.error("Texture " + this.getClass().hashCode() + ": failed to load STBImage of sprite sheet for generating cursor");
				return;
			}
			
			if (x < 0 || y < 0 || x + width > sheetW.get() || y + height > sheetH.get()) {
			    throw new IllegalArgumentException("Requested region is out of bounds of the sprite sheet (" + x + ", " + y + ", " + width + ", " + height + ")");
			}
			
			ByteBuffer cursorData = BufferUtils.createByteBuffer(width * height * 4);
			
			int sheetWidth = sheetW.get(0);
			for(int row = 0; row < height; row++) {
				int sheetRowStart = ((y + row) * sheetWidth + x) * 4;
				int cursorRowStart = row * width * 4;
				for(int col = 0; col < width * 4; col++) {
					cursorData.put(cursorRowStart + col, sheetData.get(sheetRowStart + col));
				}
			}
			
			GLFWImage cursorImage = GLFWImage.malloc(stack);
			cursorImage.set(width, height, cursorData);
			
			cursor = glfwCreateCursor(cursorImage, hotspotX, hotspotY);
			if(cursor == 0) {
				Logging.error("Texture " + this.getClass().hashCode() + ": failed to create GLFW cursor");
			}
			
			STBImage.stbi_image_free(sheetData);
		}
	}
	
	public void bind() {
		sheet.bind();
	}
	
	public void unbind() {
		sheet.unbind();
	}
}