package konradn24.tml.collisions;

public class SweepResult {

	public boolean collided;   // czy doszło do kontaktu w [0,1]
    public boolean overlap;    // czy było nakładanie na starcie (t=0)
    public double t;            // czas wejścia (0..1)
    public double nx, ny;       // normal kolizji

    public SweepResult() {
        collided = false;
        overlap = false;
        t = 1.0;
        nx = 0.0;
        ny = 0.0;
    }

    @Override
    public String toString() {
        return String.format("SweepResult<collided(%s), overlap(%s), t(%.6f), nx(%.1f), ny(%.1f)>",
                collided, overlap, t, nx, ny);
    }
}
