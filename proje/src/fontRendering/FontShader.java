package fontRendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import shaders.ShaderProgram;

public class FontShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/fontRendering/fontVertex.txt";
	private static final String FRAGMENT_FILE = "src/fontRendering/fontFragment.txt";

	private static int location_colour;
	private static int location_translation;
	private static int location_width;
	private static int location_edge;
	private static int location_borderWidth;
	private static int location_borderEdge;
	private static int location_offset;
	private static int location_outlineColour;

	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_colour = super.getUniformLocation("colour");
		location_translation = super.getUniformLocation("translation");
		
		location_width = super.getUniformLocation("width");
		
		location_edge = super.getUniformLocation("edge");
		
		location_borderWidth = super.getUniformLocation("borderWidth");
		
		location_borderEdge = super.getUniformLocation("borderEdge");
		
		location_offset = super.getUniformLocation("offset");
		
		location_outlineColour = super.getUniformLocation("outlineColour");
	}

	public void loadSpecs(FontSpecs specr) {
		super.loadFloat(location_edge, specr.getEdge());
		super.loadFloat(location_width, specr.getWidth());
		super.loadFloat(location_borderWidth,specr.getBorderWidth());
		super.loadFloat(location_borderEdge, specr.getBorderEdge());
		super.load2DVector(location_offset, specr.getOffset());
		super.loadVector(location_outlineColour, specr.getColour());
	}

	protected void loadColour(Vector3f colour) {
		super.loadVector(location_colour, colour);
	}

	protected void loadTranslation(Vector2f translation) {
		super.load2DVector(location_translation, translation);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

}
