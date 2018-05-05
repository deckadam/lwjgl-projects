package textures;

public class ModelTexture {

	private int textureID;
	private int normalMap;
	private int specularMap;
	
	private float shineDamper = 1;
	private float reflectivity = 0;

	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	private boolean hasSpecular=false;
	
	private int numberOfRows = 1;

	public void setSpecularMap(int specMap) {
		this.specularMap=specMap;
		this.hasSpecular=true;
	}
	
	
	
	public boolean isHasSpecular() {
		return hasSpecular;
	}

	public int getSpecularMap() {
		return specularMap;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public int getNormalMap() {
		return normalMap;
	}

	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	public ModelTexture(int id) {
		this.textureID = id;
	}

	public int getID() {
		return this.textureID;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public boolean isHasTransparency() {
		return hasTransparency;
	}

	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}

	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}

}
