package konradn24.tml.utils;

import org.joml.Vector2f;

import konradn24.tml.display.Display;

public class Transform {

	public Vector2f position;
	public Vector2f size;
	public float zIndex;
	
	public Transform() {
		this.position = new Vector2f();
		this.size = new Vector2f();
	}
	
	public Transform(Vector2f position) {
		this.position = position;
		this.size = new Vector2f();
		this.zIndex = 1;
	}

	public Transform(Vector2f position, Vector2f size) {
		this.position = position;
		this.size = size;
		this.zIndex = 1;
	}
	
	public Transform(Vector2f position, float zIndex) {
		this.position = position;
		this.size = new Vector2f();
		this.zIndex = zIndex;
	}

	public Transform(Vector2f position, Vector2f size, float zIndex) {
		this.position = position;
		this.size = size;
		this.zIndex = zIndex;
	}
	
	public Transform autoZIndex() {
		calculateZIndex();
		return this;
	}
	
	public Transform copy() {
		return new Transform(new Vector2f(this.position), new Vector2f(this.size), this.zIndex);
	}
	
	public void copy(Transform to) {
		to.position.set(this.position);
		to.size.set(this.size);
		to.zIndex = zIndex;
	}
	
	public void calculateZIndex() {
		this.zIndex = position.y / Display.LOGICAL_HEIGHT / 2 - 1.0f;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof Transform)) return false;
		
		Transform t = (Transform) o;
		return t.position.equals(this.position) && t.size.equals(this.size) && t.zIndex == this.zIndex;
	}
}
