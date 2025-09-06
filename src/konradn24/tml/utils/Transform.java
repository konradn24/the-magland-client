package konradn24.tml.utils;

import org.joml.Vector2d;

public class Transform {

	public Vector2d position;
	public Vector2d size;
	
	public Transform() {
		this.position = new Vector2d();
		this.size = new Vector2d();
	}
	
	public Transform(Vector2d position) {
		this.position = position;
		this.size = new Vector2d();
	}

	public Transform(Vector2d position, Vector2d size) {
		this.position = position;
		this.size = size;
	}
	
	public Transform(double x, double y, double width, double height) {
		this.position = new Vector2d(x, y);
		this.size = new Vector2d(width, height);
	}
	
	public Transform copy() {
		return new Transform(new Vector2d(this.position), new Vector2d(this.size));
	}
	
	public void copy(Transform to) {
		to.position.set(this.position);
		to.size.set(this.size);
	}
	
	public void set(Transform from) {
		this.position.set(from.position);
		this.size.set(from.size);
	}
	
	public Transform addPosition(double dx, double dy) {
		this.position.add(dx, dy);
		return this;
	}
	
	public Transform addPosition(Vector2d v) {
		this.position.add(v);
		return this;
	}
	
	public boolean isZero() {
		return Math.abs(size.x) < 1e-6f && Math.abs(size.y) < 1e-6f;
	}
	
	public double left()   	{ return position.x; }
    public double right()  	{ return position.x + size.x; }
    public double top()    	{ return position.y; }
    public double bottom() 	{ return position.y + size.y; }
    public double centerX() 	{ return position.x + size.x / 2; }
    public double centerY() 	{ return position.y + size.y / 2; }

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
