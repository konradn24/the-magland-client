package konradn24.tml.gui.graphics.layouts;

import java.lang.reflect.Array;
import java.util.function.BiConsumer;

import konradn24.tml.Handler;
import konradn24.tml.gui.graphics.components.Component;

public class GridLayout<T extends Component> {
	private T[] components;
	
	public int count, columns;
	public float x, y, componentWidth, componentHeight, spacing;
	
	@SuppressWarnings("unchecked")
	public GridLayout(Class<T> componentClass, int count) {
		this.components = (T[]) Array.newInstance(componentClass, count);
		this.count = count;
	}
	
	@SuppressWarnings("unchecked")
	public GridLayout(Class<T> componentClass, int count, int columns, float x, float y, 
						float componentWidth, float componentHeight, float spacing, Handler handler) {
		
		this.components = (T[]) Array.newInstance(componentClass, count);
		
		this.count = count;
		this.columns = columns;
		this.x = x;
		this.y = y;
		this.componentWidth = componentWidth;
		this.componentHeight = componentHeight;
		
		for(int i = 0; i < count; i++) {
			int column = i % columns;
			int row = (int) Math.floor(i / columns);
			
			try {
				T component = componentClass.getDeclaredConstructor(Handler.class).newInstance(handler);
				
				component.setX(x + column * (componentWidth + spacing));
				component.setY(y + row * (componentHeight + spacing));
				component.setWidth(componentWidth);
				component.setHeight(componentHeight);
				
				components[i] = component;
			} catch (Exception e) {
				throw new RuntimeException("GridLayout: could not instantiate component", e);
			}
		}
	}
	
	public GridLayout<T> customize(BiConsumer<T, Integer> customizer) {
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
	
	public void update(int columns, float x, float y, float componentWidth, float componentHeight, float spacing) {
		for(int i = 0; i < components.length; i++) {
			T component = components[i];
			
			if(component == null) {
				continue;
			}
			
			int column = i % columns;
			int row = (int) Math.floor(i / columns);
			
			component.setX(x + column * (componentWidth + spacing));
			component.setY(y + row * (componentHeight + spacing));
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
}
