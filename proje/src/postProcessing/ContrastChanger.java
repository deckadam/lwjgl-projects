package postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class ContrastChanger {

	private ImageRenderer imgrenderer ;
	private ContrastShader shader;
	
	public ContrastChanger() {
		imgrenderer=new ImageRenderer();
		shader=new ContrastShader();
		
	}
	
	public void render(int texture) {
		shader.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		imgrenderer.renderQuad();
		shader.stop();
	}
	
	public void cleanup() {
		imgrenderer.cleanUp();
		shader.cleanup();
	}
	
	
}
