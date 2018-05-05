package textures;

public class TerrainTexturePack {
	private TerrainTexture backgroundTexture;
	private TerrainTexture texture2;
	private TerrainTexture texture3;
	private TerrainTexture texture4;
	public TerrainTexturePack(TerrainTexture texture1, TerrainTexture texture2, TerrainTexture texture3,TerrainTexture texture4) {
		this.backgroundTexture = texture1;
		this.texture2 = texture2;
		this.texture3 = texture3;
		this.texture4 = texture4;
	}
	public TerrainTexture getBackgroundTexture() {
		return backgroundTexture;
	}
	public TerrainTexture getTexture2() {
		return texture2;
	}
	public TerrainTexture getTexture3() {
		return texture3;
	}
	public TerrainTexture getTexture4() {
		return texture4;
	}
	
}
