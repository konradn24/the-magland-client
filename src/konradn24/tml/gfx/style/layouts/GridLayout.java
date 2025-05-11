package konradn24.tml.gfx.style.layouts;

import java.awt.Graphics2D;
import java.lang.reflect.Array;
import java.util.function.BiConsumer;

import konradn24.tml.Handler;
import konradn24.tml.gfx.components.Component;

public class GridLayout<T extends Component> {
	private T[] components;
	
	@SuppressWarnings("unchecked")
	public GridLayout(Class<T> componentClass, int count, int columns, int x, int y, 
						int componentWidth, int componentHeight, int spacing, Handler handler) {
		
		this.components = (T[]) Array.newInstance(componentClass, count);
		
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
	
	public void tick() {
		for(T component : components) {
			component.tick();
		}
	}
	
	public void render(Graphics2D g) {
		for(T component : components) {
			component.render(g);
		}
	}
	
	public T[] getComponents() {
		return components;
	}
}
