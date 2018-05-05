package particles;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/particles/particleVShader.txt";
	private static final String FRAGMENT_FILE = "src/particles/particleFShader.txt";

	private int location_nor;//number of rows
	private int location_projectionMatrix;
	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_nor = super.getUniformLocation("nor");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
	}
	
	protected void loadNOR(float value) {
		super.loadFloat(location_nor, value);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelViewMatrix");
		super.bindAttribute(5, "offset");
		super.bindAttribute(6, "blendFac");
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}

}
