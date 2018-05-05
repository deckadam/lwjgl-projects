package renderEngine;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;

public class EntityRenderer {

	private StaticShader shdr;
	
	public EntityRenderer (StaticShader shdr,Matrix4f projectionMatrix){
		this.shdr=shdr;
		shdr.start();
		shdr.connectShadowMapTexture();
		shdr.loadProjectionMatrix(projectionMatrix);
		shdr.stop();		
	}
	
	public void render(Map<TexturedModel,List<Entity>> entities,Matrix4f toShadowSpace	) {
		shdr.loadToShadowMapSpace(toShadowSpace);
		for(TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity : batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES,model.getRawmodel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);				
			}
			unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawmodel=model.getRawmodel();
		GL30.glBindVertexArray(rawmodel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture =model.getMdlTexture();
		shdr.loadNumberOfRows(texture.getNumberOfRows());
		if(texture.isHasTransparency()) {MasterRenderer.disableCulling();}
		shdr.loadShineVariable(texture.getShineDamper(), texture.getReflectivity());
		shdr.loadFakeLighting(texture.isUseFakeLighting());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getMdlTexture().getID());
		shdr.loadUseSpecularMap(texture.isHasSpecular());
		if(texture.isHasSpecular()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D,texture.getSpecularMap());
		}
	}
	
	private void unbindTexturedModel() {
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix=Maths.createTransfromationMatrix(entity.getPosition(),
				entity.getRx(), entity.getRy(), entity.getRz(), entity.getScale());
		shdr.loadTransformationMatrix(transformationMatrix);
		shdr.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
	}
	
}