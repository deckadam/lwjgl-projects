package water;

public class WaterTile {

	public static final float TILE_SIZE = 40;

	private float height;
	private float x, z;

	public WaterTile(float centerX, float height, float centerZ) {
		this.x = centerX;
		this.height = height;
		this.z = centerZ;
	}

	public float getHeight() {
		return height;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

}