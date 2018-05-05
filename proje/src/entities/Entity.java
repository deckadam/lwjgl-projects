package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Entity {
	private TexturedModel model;
	private	Vector3f position;
	private float rx,ry,rz;
	private float scale;
	
	private int textureIndex=0;
	
	public Entity(TexturedModel model, Vector3f position, float rx, float ry, float rz, float scale) {
		this.model = model;
		this.position = position;
		this.rx = rx;
		this.ry = ry;
		this.rz = rz;
		this.scale = scale;
	}

	
	public Entity(TexturedModel model,int textureIndex, Vector3f position, float rx, float ry, float rz, float scale) {
		this.model = model;
		this.position = position;
		this.rx = rx;
		this.ry = ry;
		this.rz = rz;
		this.scale = scale;
		this.textureIndex=textureIndex;
	}
	
	public float getTextureXOffset() {
		int column = textureIndex%model.getMdlTexture().getNumberOfRows();
		return (float) column/(float)model.getMdlTexture().getNumberOfRows();
	}
	
	public float getTextureYOffset() {
		int row = textureIndex/model.getMdlTexture().getNumberOfRows();
		return (float) row/(float)model.getMdlTexture().getNumberOfRows();
	}
	
	
	public void increasePosition(float velx,float vely,float velz) {
		this.position.x+=velx;
		this.position.y+=vely;
		this.position.z+=velz;
	}
	
	public void increaseRotation(float velrx,float velry,float velrz) {
		this.rx+=velrx;
		this.ry+=velry;
		this.rz+=velrz;
	}
	
	public TexturedModel getModel() {
		return model;
	}
	
	public void setModel(TexturedModel model) {
		this.model = model;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public float getRx() {
		return rx;
	}
	
	public void setRx(float rx) {
		this.rx = rx;
	}
	
	public float getRy() {
		return ry;
	}
	
	public void setRy(float ry) {
		this.ry = ry;
	}
	
	public float getRz() {
		return rz;
	}
	
	public void setRz(float rz) {
		this.rz = rz;
	}
	
	public float getScale() {
		return scale;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
}
