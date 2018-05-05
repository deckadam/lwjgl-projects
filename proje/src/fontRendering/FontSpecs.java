package fontRendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class FontSpecs {
	private  float edge;
	private  float width;
	private  float borderEdge;
	private  float borderWidth;
	private  Vector2f offset;
	private  Vector3f colour;
	public float getEdge() {
		return edge;
	}
	public void setEdge(float edge) {
		this.edge = edge;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getBorderEdge() {
		return borderEdge;
	}
	public void setBorderEdge(float borderEdge) {
		this.borderEdge = borderEdge;
	}
	public float getBorderWidth() {
		return borderWidth;
	}
	public void setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
	}
	public Vector2f getOffset() {
		return offset;
	}
	public void setOffset(Vector2f offset) {
		this.offset = offset;
	}
	public Vector3f getColour() {
		return colour;
	}
	public void setColour(Vector3f colour) {
		this.colour = colour;
	}
	public FontSpecs(float edge, float width, float borderEdge, float borderWidth, Vector2f offset, Vector3f colour) {
		super();
		this.edge = edge;
		this.width = width;
		this.borderEdge = borderEdge;
		this.borderWidth = borderWidth;
		this.offset = offset;
		this.colour = colour;
	}
	
}
