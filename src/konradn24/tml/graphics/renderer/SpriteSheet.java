package konradn24.tml.graphics.renderer;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.stb.STBImage;

import konradn24.tml.graphics.Assets;

public class SpriteSheet {
	private String path;
	private int id, nvgID;
    private int width;
    private int height;
    
    private boolean beingUsed;

    public SpriteSheet(long vg, String path) {
    	this.path = path;
    	
        var widthBuf = BufferUtils.createIntBuffer(1);
        var heightBuf = BufferUtils.createIntBuffer(1);
        var compBuf = BufferUtils.createIntBuffer(1);

        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer glImage = STBImage.stbi_load(path, widthBuf, heightBuf, compBuf, 4);

        if (glImage == null) {
            throw new RuntimeException("Failed to load texture at first stage (flipped Y): " + STBImage.stbi_failure_reason());
        }
        
        width = widthBuf.get(0);
        height = heightBuf.get(0);

        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                     GL_RGBA, GL_UNSIGNED_BYTE, glImage);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        STBImage.stbi_image_free(glImage);
        
        STBImage.stbi_set_flip_vertically_on_load(false);
        ByteBuffer nvgImage = STBImage.stbi_load(path, widthBuf, heightBuf, compBuf, 4);

        if (nvgImage == null) {
            throw new RuntimeException("Failed to load texture at second stage (default Y): " + STBImage.stbi_failure_reason());
        }
        
        if(vg != Assets.NO_NVG) {
        	nvgID = NanoVG.nvgCreateImageRGBA(vg, width, height, NanoVG.NVG_IMAGE_NEAREST, nvgImage);
        }
        
        STBImage.stbi_image_free(nvgImage);
    }

    public void bind() {
    	if(beingUsed) {
    		return;
    	}
    	
		glBindTexture(GL_TEXTURE_2D, id);
		beingUsed = true;
    }
    
    public void unbind() {
    	if(!beingUsed) {
    		return;
    	}
    	
    	glBindTexture(GL_TEXTURE_2D, 0);
    	beingUsed = false;
    }

    public Texture crop(float x, float y, float width, float height, boolean generateCursor) {
    	return new Texture(this, x, y, width, height, generateCursor);
    }
    
    public String getPath() {
		return path;
	}

	public int getID() {
        return id;
    }

	public int getNvgID() {
		return nvgID;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
