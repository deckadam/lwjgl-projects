package renderEngine;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import shaders.TerrainShader;
import terrains.Terrain;
import textures.TerrainTexturePack;
import toolbox.Maths;
public class TerrainRenderer {
	private TerrainShader shdr;
	
	public TerrainRenderer (TerrainShader shdr , Matrix4f projectionMatrix) {
		this.shdr=shdr;
		shdr.start();
		shdr.connectTextureUnits();
		shdr.loadProjectionMatrix(projectionMatrix);
		shdr.stop();
	}
	
	public void render(List<Terrain> terrains,Matrix4f toShadowMapSpace) {
		shdr.loadShadowMapSpace(toShadowMapSpace);
		for(Terrain terrain:terrains) {
			prepareTerrain(terrain);
			loadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES,terrain.getModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);				
			unbindTexturedModel();
		}
	}
	
	private void prepareTerrain(Terrain terrain) {
		RawModel rawmodel=terrain.getModel();
		GL30.glBindVertexArray(rawmodel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		shdr.loadShineVariable(1, 0);
		bindTextures(terrain);
	}
	
	private void bindTextures(Terrain terrain) {
		TerrainTexturePack txtpack=terrain.getTxtpack();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, txtpack.getBackgroundTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, txtpack.getTexture2().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, txtpack.getTexture3().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, txtpack.getTexture4().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
		
	}
	
	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix=Maths.createTransfromationMatrix(new Vector3f(terrain.getX(),0,terrain.getZ()),
				0, 0, 0, 1);
		shdr.loadTransformationMatrix(transformationMatrix);
	}
}
