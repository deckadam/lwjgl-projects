package particles;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import renderEngine.DisplayManager;

public class Particle {
	private float lifeTime;
	private float rotation;
	private float scale;
	private Vector3f position;
	private Vector3f velocity;
	private ParticleTexture texture;
	private float distance;

	private Vector2f texOffset1 = new Vector2f();
	private Vector2f texOffset2 = new Vector2f();
	private float blend;

	public ParticleTexture getTexture() {
		return texture;
	}

	private float elapsedTime = 0;
	private float gravity;

	public Particle(float gravityComplient, float lifeLength, float rotation, float scale, Vector3f position,
			Vector3f velocity, ParticleTexture texture) {
		this.texture = texture;
		this.lifeTime = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		this.position = position;
		this.velocity = velocity;
		this.gravity=gravityComplient;
		ParticleMaster.addParticle(this);
	}
	
	protected void positionReset(Vector3f position,Vector3f velocity) {
		this.elapsedTime=0;
		this.position=new Vector3f(position);
		this.velocity=velocity;
	}
	
	protected float getRotation() {
		return rotation;
	}

	protected float getDistance() {
		return distance;
	}

	protected float getScale() {
		return scale;
	}

	protected Vector3f getPosition() {
		return position;
	}

	protected Vector2f getTexOffset1() {
		return texOffset1;
	}

	protected Vector2f getTexOffset2() {
		return texOffset2;
	}

	protected float getBlend() {
		return blend;
	}

	protected boolean update(Camera camera) {
		velocity.y += gravity * DisplayManager.getFrameTimeSeconds();
		Vector3f change = new Vector3f(velocity);
		change.scale(DisplayManager.getFrameTimeSeconds());
		Vector3f.add(change, position, position);
		updateTextureCoordInfo();
		elapsedTime += DisplayManager.getFrameTimeSeconds();
		distance = Vector3f.sub(camera.getPosition(), position, null).lengthSquared();
		return elapsedTime < lifeTime;
	}

	private void updateTextureCoordInfo() {
		float lifeFactor = elapsedTime / lifeTime;
		int stageCount = texture.getRows() * texture.getRows();
		float atlasProgression = lifeFactor * stageCount;
		int index1 = (int) Math.floor(atlasProgression);
		int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
		this.blend = atlasProgression % 1;
		setTextureOffset(texOffset1, index1);
		setTextureOffset(texOffset2, index2);
	}

	private void setTextureOffset(Vector2f vector, int index) {
		int column = index % texture.getRows();
		int row = index / texture.getRows();
		vector.x = (float) column / texture.getRows();
		vector.y = (float) row / texture.getRows();
	}
}
