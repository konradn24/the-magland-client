package konradn24.tml.gui.graphics.layouts;

import java.lang.reflect.Array;
import java.util.function.BiConsumer;

import konradn24.tml.Handler;
import konradn24.tml.gui.graphics.components.Component;
import konradn24.tml.utils.Logging;

public class RowLayout<T extends Component> {
	private T[] components;
	
	public int count;
	public float x, y, componentWidth, componentHeight;
	
	@SuppressWarnings("unchecked")
	public RowLayout(Class<T> componentClass, int count, float x, float y, float width, float height, float spacing, boolean useComponentSize, Handler handler) {
		this.components = (T[]) Array.newInstance(componentClass, count);
		
		this.count = count;
		this.x = x;
		this.y = y;
		
		this.componentHeight = height;
		
		if(useComponentSize) {
			this.componentWidth = width;
		} else {
			this.componentWidth = (width - count * spacing * 2) / count;
		}
		
		if(this.componentWidth <= 0 || this.componentHeight <= 0) {
			Logging.error("RowLayout: components have illegal size (" + this.componentWidth + "x" + this.componentHeight  + ")");
		}
		
		for(int i = 0; i < count; i++) {
			try {
				T component = componentClass.getDeclaredConstructor(Handler.class).newInstance(handler);
				
				component.setX(x + i * (componentWidth + spacing));
				component.setY(y);
				component.setWidth(componentWidth);
				component.setHeight(componentHeight);
				
				components[i] = component;
			} catch (Exception e) {
				throw new RuntimeException("RowLayout: could not instantiate component", e);
			}
		}
	}
	
	public RowLayout<T> customize(BiConsumer<T, Integer> customizer) {
        for (int i = 0; i < components.length; i++) {
            customizer.accept(components[i], i);
        }
        
        return this;
    }
	
	public void forEach(BiConsumer<T, Integer> customizer) {
        for (int i = 0; i < components.length; i++) {
            customizer.accept(components[i], i);
        }
    }
	
	public void update(float x, float y, float width, float height, float spacing, boolean useComponentSize) {
		this.x = x;
		this.y = y;
		
		this.componentHeight = height;
		
		if(useComponentSize) {
			this.componentWidth = width;
		} else {
			this.componentWidth = (width - count * spacing * 2) / count;
		}
		
		for(int i = 0; i < components.length; i++) {
			T component = components[i];
			
			if(component == null) {
				continue;
			}
			
			component.setX(x + i * (componentWidth + spacing));
			component.setY(y);
			component.setWidth(componentWidth);
			component.setHeight(componentHeight);
			
			components[i] = component;
		}
	}
	
	public void update(float dt) {
		for(T component : components) {
			component.update(dt);
		}
	}
	
	public void renderGUI(long vg) {
		for(T component : components) {
			component.renderGUI(vg);
		}
	}
	
	public T[] getComponents() {
		return components;
	}
	
	public float getWidth() {
		return components[components.length - 1].getX() + components[components.length - 1].getWidth();
	}
}

