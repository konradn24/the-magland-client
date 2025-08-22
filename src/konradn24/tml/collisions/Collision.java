package konradn24.tml.collisions;

import java.util.List;

import konradn24.tml.entities.Entity;
import konradn24.tml.utils.Transform;

public class Collision {

	/**
	 * Swept AABB (continuous collision detection)
	 * @param moving 	moving object
	 * @param dx		move X in this frame
	 * @param dy		move Y in this frame
	 * @param target	static object
	 * @return result with t(0..1) and collision normal 
	 */

	public static SweepResult sweep(Transform moving, Transform target, float dx, float dy) {
		SweepResult res = new SweepResult();
        final float EPS = 1e-6f;

        if (moving.intersects(target)) {
            res.overlap = true;
            res.collided = true;
            res.t = 0f;
            res.nx = 0f; res.ny = 0f;
            return res;
        }

        float broadLeft  = Math.min(moving.left(), moving.left() + dx);
        float broadTop   = Math.min(moving.top(),  moving.top()  + dy);
        float broadRight = Math.max(moving.right(), moving.right() + dx);
        float broadBottom= Math.max(moving.bottom(), moving.bottom() + dy);

        Transform broad = new Transform(broadLeft, broadTop, broadRight - broadLeft, broadBottom - broadTop);
        if (!broad.intersects(target)) {
            res.collided = false;
            return res;
        }

        float xEntry, yEntry, xExit, yExit;

        if (dx > 0f) {
            xEntry = target.left() - moving.right();
            xExit  = target.right() - moving.left();
        } else {
            xEntry = target.right() - moving.left();
            xExit  = target.left() - moving.right();
        }

        if (dy > 0f) {
            yEntry = target.top() - moving.bottom();
            yExit  = target.bottom() - moving.top();
        } else {
            yEntry = target.bottom() - moving.top();
            yExit  = target.top() - moving.bottom();
        }

        float txEntry, txExit, tyEntry, tyExit;
        if (Math.abs(dx) < EPS) {
            txEntry = Float.NEGATIVE_INFINITY;
            txExit  = Float.POSITIVE_INFINITY;
        } else {
            txEntry = xEntry / dx;
            txExit  = xExit  / dx;
        }

        if (Math.abs(dy) < EPS) {
            tyEntry = Float.NEGATIVE_INFINITY;
            tyExit  = Float.POSITIVE_INFINITY;
        } else {
            tyEntry = yEntry / dy;
            tyExit  = yExit  / dy;
        }

        float tEntry = Math.max(txEntry, tyEntry);
        float tExit  = Math.min(txExit, tyExit);

        if (tEntry > tExit || tEntry < 0f || tEntry > 1f) {
            res.collided = false;
            return res;
        }

        res.collided = true;
        res.t = tEntry;

        if (txEntry > tyEntry) {
            res.ny = 0f;
            res.nx = (dx > 0f) ? -1f : 1f;
        } else {
            res.nx = 0f;
            res.ny = (dy > 0f) ? -1f : 1f;
        }

        return res;
    }
	
	public static SweepResult sweep(Entity moving, Entity target, float dx, float dy) {
		if(moving.getBounds().isZero() || target.getBounds().isZero() || moving == target) {
			return new SweepResult();
		}
		
		return sweep(
			moving.getBounds().copy().addPosition(moving.getTransform().position),
			target.getBounds().copy().addPosition(target.getTransform().position),
			dx, dy
		);
	}
	
	public static float sweep(Transform moving, List<Transform> targets, float dx, float dy) {
		SweepResult earliest = new SweepResult();
		
		if(moving.isZero()) {
			return earliest.t;
		}
		
		for(Transform target : targets) {
			SweepResult result = sweep(moving, target, dx, dy);
			
			if(result.t < earliest.t) {
				earliest = result;
			}
		}
		
		return earliest.t;
	}
	
	public static SweepResult sweep(Entity moving, List<Entity> targets, float dx, float dy) {
		SweepResult earliest = new SweepResult();
		
		if(moving.getBounds().isZero()) {
			return earliest;
		}
		
		for(Entity target : targets) {
			SweepResult result = sweep(moving, target, dx, dy);
			
			if(result.t < earliest.t) {
				earliest = result;
			}
		}
		
		return earliest;
	}
}
