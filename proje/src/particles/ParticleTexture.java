package particles;

public class ParticleTexture {
	private int textureID;
	private int rows;

	private boolean additive=false;
	public ParticleTexture(int textureID, int rows) {
		super();
		this.textureID = textureID;
		this.rows = rows;
	}
	public int getTextureID() {
		return textureID;
	}
	public int getRows() {
		return rows;
	}

	public boolean isAdditive() {
		return additive;
	}

	public void setAdditive(boolean additive) {
		this.additive = additive;
	}

	
}
