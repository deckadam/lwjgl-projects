package fontRendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontMeshCreator.TextMeshData;
import renderEngine.Loader;

public class TextMaster {
	private static Loader loader;
	private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
	private static FontRenderer renderer;

	public static void init(Loader loader2) {
		loader = loader2;
		renderer = new FontRenderer();
	}

	public static void render () {
		renderer.render(texts);
	}
	
	public static void loadText(GUIText text) {
		FontType type = text.getFont();
		TextMeshData data = type.loadText(text);
		int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		text.setMeshInfo(vao, data.getVertexCount());
		List<GUIText> batch = texts.get(type);
		if (batch == null) {
			batch = new ArrayList<GUIText>();
			texts.put(type, batch);
		}
		batch.add(text);
	}

	public static void loadTextSpecs(float edge,float width,float borderEdge,float borderWidth,Vector2f offset, Vector3f colour) {
		renderer.setSpecs(new FontSpecs(edge,width,borderEdge,borderWidth,offset,colour));
	}
	
	
	public static void removeText(GUIText text) {
		List<GUIText> batch = texts.get(text.getFont());
		batch.remove(text);
		if (batch.isEmpty()) {
			texts.remove(text.getFont());
		}
	}

	public static void cleanup() {
		renderer.cleanUp();
	}
}
