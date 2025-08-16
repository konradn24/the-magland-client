package konradn24.tml.gui.graphics.widgets;

import static org.lwjgl.nanovg.NanoVG.*;

import konradn24.tml.Handler;
import konradn24.tml.display.Display;
import konradn24.tml.gui.graphics.Colors;
import konradn24.tml.gui.graphics.components.Component;
import konradn24.tml.utils.Logging;

public class ScrollPanel extends Component {

	public static interface RenderFunction {
		public void renderGUI();
	}
	
	public static final float SCROLL_SPEED = Display.y(.018f);
	public static final float SCROLLBAR_WIDTH = Display.x(.00625f);
	public static final float MIN_SCROLLBAR_HEIGHT = Display.y(.028f);
	public static final float SCROLLBAR_MARGIN_X = Display.x(.004f);
	public static final float SCROLLBAR_MARGIN_Y = Display.y(.009f);
	
	private float scrollOffsetY, scrollContentHeight;
	
	private float visibleContentRatio;
	private float thumbHeight, thumbY;
	
	private boolean emptyAreaWarningPrompted;
	
	public ScrollPanel(float x, float y, float width, float height, Handler handler) {
		super(x, y, width, height, handler);
		
		scrollContentHeight = height;
	}
	
	@Override
	public void update(float dt) {
		if(scrollContentHeight <= height) {
			scrollOffsetY = 0;
			return;
		}
		
		scrollOffsetY -= handler.getMouseManager().getScrollY() * SCROLL_SPEED;
		clampScroll();
		
		float height = this.height - SCROLLBAR_MARGIN_Y * 2;
        float scrollContentHeight = this.scrollContentHeight - SCROLLBAR_MARGIN_Y * 2;
        
        visibleContentRatio = Math.min((float) height / scrollContentHeight, 1);
        thumbHeight = Math.max((int) (height * visibleContentRatio), MIN_SCROLLBAR_HEIGHT);
        thumbY = (int) ((float) scrollOffsetY / (scrollContentHeight - height) * (height - thumbHeight));
        
        if(handler.getMouseManager().isDraggingOn(x, y, width, height)) {
			float deltaY = (float) handler.getMouseManager().getDragY();
		    float scrollRange = scrollContentHeight - height;
		    float thumbRange = height - thumbHeight;
		    
		    scrollOffsetY += deltaY / thumbRange * scrollRange;
		    clampScroll();
		}
        
//        if(handler.getMouseManager().isLeftPressedOn(x + width - SCROLLBAR_WIDTH - SCROLLBAR_MARGIN_X, y + SCROLLBAR_MARGIN_Y + thumbY, SCROLLBAR_WIDTH, thumbHeight) && !dragging) {
//        	dragging = true;
//        	dragStartY = (float) handler.getMouseManager().getMouseY();
//        	dragStartOffset = scrollOffsetY;
//        }
	}

	@Override
	public void renderGUI(long vg) {
		renderGUIArea(vg, () -> {
			if(!emptyAreaWarningPrompted) {
				Logging.warning("Scroll Panel " + this.getClass().hashCode() + ": rendering empty GUI area, try ScrollPanel.renderGUIArea(...) function");
				emptyAreaWarningPrompted = true;
			}
		});
	}
	
	public void renderGUIArea(long vg, RenderFunction f) {
		nvgSave(vg);
		nvgScissor(vg, x, y, width, height);
		
        if(scrollContentHeight > height) {
        	nvgTranslate(vg, x, y - scrollOffsetY);
        } else {
        	nvgTranslate(vg, x, y);
        }
        
        f.renderGUI();

        nvgRestore(vg);
        
        if(scrollContentHeight > height) {
        	float height = this.height - SCROLLBAR_MARGIN_Y * 2;
        	
        	nvgBeginPath(vg);
        	nvgRect(vg, x + width - SCROLLBAR_WIDTH - SCROLLBAR_MARGIN_X, y + SCROLLBAR_MARGIN_Y + thumbY, SCROLLBAR_WIDTH, thumbHeight);
        	nvgFillColor(vg, Colors.COLOR_BACKGROUND_LIGHT);
        	nvgFill(vg);
        	
        	nvgBeginPath(vg);
        	nvgRect(vg, x + width - SCROLLBAR_WIDTH - SCROLLBAR_MARGIN_X, y + SCROLLBAR_MARGIN_Y, SCROLLBAR_WIDTH, height);
        	nvgStrokeWidth(vg, 3f);
        	nvgStrokeColor(vg, Colors.COLOR_OUTLINE);
        	nvgStroke(vg);
        }
	}
	
	public void clampScroll() {
		scrollOffsetY = Math.max(0, Math.min(scrollOffsetY, scrollContentHeight - height));
	}
	
//	@Override
//	public void mouseWheelMoved(MouseWheelEvent e) {
//		if(!isOn()) {
//			return;
//		}
//		
//		int notches = e.getWheelRotation();
//        scrollOffsetY += notches * SCROLL_SPEED;
//        clampScroll();
//	}
//	
//	@Override
//	public void mouseDragged(MouseEvent e) {
//		if(!dragging) {
//			return;
//		}
//		
//		int deltaY = handler.getMouseManager().getMouseY() - dragStartY;
//		float visibleContentRatio = Math.min((float) height / scrollContentHeight, 1);
//        int thumbHeight = Math.max((int) (height * visibleContentRatio), MIN_SCROLLBAR_HEIGHT);
//        int scrollRange = scrollContentHeight - height;
//        int thumbRange = height - thumbHeight;
//        
//        scrollOffsetY = dragStartOffset + (int) ((float) deltaY / thumbRange * scrollRange);
//        clampScroll();
//	}

	public float getScrollOffsetY() {
		return scrollOffsetY;
	}

	public void setScrollOffsetY(float scrollOffsetY) {
		this.scrollOffsetY = scrollOffsetY;
	}

	public float getScrollContentHeight() {
		return scrollContentHeight;
	}

	public void setScrollContentHeight(float scrollContentHeight) {
		this.scrollContentHeight = scrollContentHeight;
	}
}
