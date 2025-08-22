package konradn24.tml.graphics.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;

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
    
    public SpriteSheet(long vg, String path) {
    	this.path = path;
    	
        var widthBuf = BufferUtils.createIntBuffer(1);
        var heightBuf = BufferUtils.createIntBuffer(1);
        var compBuf = BufferUtils.createIntBuffer(1);

        STBImage.stbi_set_flip_vertically_on_load(false);
        ByteBuffer image = STBImage.stbi_load(path, widthBuf, heightBuf, compBuf, 4);

        if (image == null) {
            throw new RuntimeException("Failed to load texture at first stage (flipped Y): " + STBImage.stbi_failure_reason());
        }
        
        width = widthBuf.get(0);
        height = heightBuf.get(0);

        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                     GL_RGBA, GL_UNSIGNED_BYTE, image);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glGenerateMipmap(GL_TEXTURE_2D);
        
        if(vg != Assets.NO_NVG) {
        	nvgID = NanoVG.nvgCreateImageRGBA(vg, width, height, NanoVG.NVG_IMAGE_NEAREST, image);
        }
        
        STBImage.stbi_image_free(image);
    }

    public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
    }
    
    public void unbind() {
    	glBindTexture(GL_TEXTURE_2D, 0);
    }

    public Texture crop(float x, float y, float width, float height, boolean generateCursor) {
    	return new Texture(this, x, y, width, height, generateCursor);
    }
    
    public Texture getTexture(boolean generateCursor) {
    	return new Texture(this, generateCursor);
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
	
	@Override
	public String toString() {
		return "SpriteSheet<ID(" + id + "), path('" + path + "')>";
	}
}
