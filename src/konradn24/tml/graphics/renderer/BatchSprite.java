package konradn24.tml.graphics.renderer;

import org.joml.Vector4f;

import konradn24.tml.utils.Transform;

public class BatchSprite {

	private Vector4f color;
	private Texture texture;
	
	private Transform transform;
	private boolean dirty;
	
	public BatchSprite(Vector4f color, Transform transform) {
		this.color = color;
		this.texture = null;
		this.transform = transform.autoZIndex();
	}
	
	public BatchSprite(Texture texture, Transform transform) {
		this.color = new Vector4f(1, 1, 1, 1);
		this.texture = texture;
		this.transform = transform.autoZIndex();
	}
	
	public void update(Transform transform) {
		if(!this.transform.equals(transform)) {
			transform.copy(this.transform);
			this.transform.calculateZIndex();
			dirty = true;
		}
	}

	public Vector4f getColor() {
		return color;
	}

	public Texture getTexture() {
		return texture;
	}

	public Transform getTransform() {
		return transform;
	}

	public boolean isDirty() {
		return dirty;
	}
	
	public void setClean() {
		this.dirty = false;
	}
}
