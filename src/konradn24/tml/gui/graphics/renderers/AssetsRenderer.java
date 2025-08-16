package konradn24.tml.gui.graphics.renderers;

import static org.lwjgl.nanovg.NanoVG.*;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;

import konradn24.tml.graphics.renderer.Texture;

public class AssetsRenderer {

	public static void renderTexture(long vg, Texture texture, float x, float y, float width, float height) {
		float srcX = texture.u0 * texture.sheet.getWidth();
		float srcY = texture.v0 * texture.sheet.getHeight();
		float srcW = (texture.u1 - texture.u0) * texture.sheet.getWidth();
		float srcH = (texture.v1 - texture.v0) * texture.sheet.getHeight();
		
		NVGPaint paint = NVGPaint.calloc();
		nvgBeginPath(vg);
		nvgRect(vg, x, y, width, height);
		
		nvgScissor(vg, x, y, width, height);
		
		nvgImagePattern(
			vg,
			x - srcX * (width / srcW), 						// move pattern left
			y - srcY * (height / srcH), 					// move pattern up
			texture.sheet.getWidth() * (width / srcW),    	// scaled width
			texture.sheet.getHeight() * (height / srcH),  	// scaled height
			0.0f,
			texture.sheet.getNvgID(),
			1.0f,
			paint
		);
		
		nvgFillPaint(vg, paint);
		nvgFill(vg);
		
		nvgResetScissor(vg);
		paint.free();
	}
	
	public static void renderDropdownIcon(long vg, float x, float y, float size, NVGColor color) {
	    nvgBeginPath(vg);

	    nvgMoveTo(vg, x, y);                  	// left
	    nvgLineTo(vg, x + size, y);           	// right
	    nvgLineTo(vg, x + size / 2, y + size);	// bottom-middle
	    nvgClosePath(vg);

	    nvgFillColor(vg, color);
	    nvgFill(vg);
	}
}
