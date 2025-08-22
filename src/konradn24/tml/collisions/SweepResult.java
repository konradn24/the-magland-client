package konradn24.tml.collisions;

public class SweepResult {

	public boolean collided;   // czy doszło do kontaktu w [0,1]
    public boolean overlap;    // czy było nakładanie na starcie (t=0)
    public float t;            // czas wejścia (0..1)
    public float nx, ny;       // normal kolizji

    public SweepResult() {
        collided = false;
        overlap = false;
        t = 1f;
        nx = 0f;
        ny = 0f;
    }

    @Override
    public String toString() {
        return String.format("SweepResult<collided(%s), overlap(%s), t(%.6f), nx(%.1f), ny(%.1f)>",
                collided, overlap, t, nx, ny);
    }
}
