package konradn24.tml.utils;

import org.joml.Vector2f;

public class Transform {

	public Vector2f position;
	public Vector2f size;
	
	public Transform() {
		this.position = new Vector2f();
		this.size = new Vector2f();
	}
	
	public Transform(Vector2f position) {
		this.position = position;
		this.size = new Vector2f();
	}

	public Transform(Vector2f position, Vector2f size) {
		this.position = position;
		this.size = size;
	}
	
	public Transform(float x, float y, float width, float height) {
		this.position = new Vector2f(x, y);
		this.size = new Vector2f(width, height);
	}
	
	public Transform copy() {
		return new Transform(new Vector2f(this.position), new Vector2f(this.size));
	}
	
	public void copy(Transform to) {
		to.position.set(this.position);
		to.size.set(this.size);
	}
	
	public void set(Transform from) {
		this.position.set(from.position);
		this.size.set(from.size);
	}
	
	public Transform addPosition(float dx, float dy) {
		this.position.add(dx, dy);
		return this;
	}
	
	public Transform addPosition(Vector2f v) {
		this.position.add(v);
		return this;
	}
	
	public boolean isZero() {
		return Math.abs(size.x) < 1e-6f && Math.abs(size.y) < 1e-6f;
	}
	
	public float left()   	{ return position.x; }
    public float right()  	{ return position.x + size.x; }
    public float top()    	{ return position.y; }
    public float bottom() 	{ return position.y + size.y; }
    public float centerX() 	{ return position.x + size.x / 2; }
    public float centerY() 	{ return position.y + size.y / 2; }

    public boolean intersects(Transform o) {
        return !(right() <= o.left() || left() >= o.right()
              || bottom() <= o.top() || top() >= o.bottom());
    }
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof Transform)) return false;
		
		Transform t = (Transform) o;
		return t.position.equals(this.position) && t.size.equals(this.size);
	}
	
	@Override
	public String toString() {
		return "Transform<position(" + position.x + ", " + position.y + "), size(" + size.x + ", " + size.y + ")>";
	}
}
