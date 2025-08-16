package konradn24.tml.gui.graphics.layouts;

import java.lang.reflect.Array;
import java.util.function.BiConsumer;

import konradn24.tml.Handler;
import konradn24.tml.gui.graphics.components.Component;
import konradn24.tml.utils.Logging;

public class ColumnLayout<T extends Component> {
	private T[] components;
	
	public int count;
	public float x, y, componentWidth, componentHeight, spacing;
	
	@SuppressWarnings("unchecked")
	public ColumnLayout(Class<T> componentClass, int count, float x, float y, float width, float height, float spacing, boolean useComponentSize, Handler handler) {
		this.components = (T[]) Array.newInstance(componentClass, count);
		
		this.count = count;
		this.x = x;
		this.y = y;
		
		this.componentWidth = width;
		
		if(useComponentSize) {
			this.componentHeight = height;
		} else {
			this.componentHeight = (height - count * spacing * 2) / count;
		}
		
		if(this.componentWidth <= 0 || this.componentHeight <= 0) {
			Logging.error("ColumnLayout: components have illegal size (" + this.componentWidth + "x" + this.componentHeight  + ")");
		}
		
		for(int i = 0; i < count; i++) {
			try {
				T component = componentClass.getDeclaredConstructor(Handler.class).newInstance(handler);
				
				component.setX(x);
				component.setY(y + i * (componentHeight + spacing));
				component.setWidth(componentWidth);
				component.setHeight(componentHeight);
				
				components[i] = component;
			} catch (Exception e) {
				throw new RuntimeException("ColumnLayout: could not instantiate component", e);
			}
		}
	}
	
	public ColumnLayout<T> customize(BiConsumer<T, Integer> customizer) {
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
		
		this.componentWidth = width;
		
		if(useComponentSize) {
			this.componentHeight = height;
		} else {
			this.componentHeight = (height - count * spacing * 2) / count;
		}
		
		for(int i = 0; i < components.length; i++) {
			T component = components[i];
			
			if(component == null) {
				continue;
			}
			
			component.setX(x);
			component.setY(y + i * (componentHeight + spacing));
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
	
	public float getHeight() {
		return components[components.length - 1].getY() + components[components.length - 1].getHeight();
	}
}
