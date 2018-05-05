package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import SkyBox.SkyBoxRenderer;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.TexturedModel;
import normalMappingRenderer.NormalMappingRenderer;
import postProcessing.Fbo;
import shaders.StaticShader;
import shaders.TerrainShader;
import shadows.ShadowMapMasterRenderer;
import terrains.Terrain;

public class MasterRenderer {
	public static final float FOV = 70;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000;
	public static final float RED = 0.5f;
	public static final float GREEN = 0.5f;
	public static final float BLUE = 0.5f;

	private Matrix4f projectionMatrix;

	private StaticShader shdr = new StaticShader();
	private EntityRenderer renderer;

	private TerrainShader trrnshdr = new TerrainShader();
	private TerrainRenderer terrainrenderer;
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	private SkyBoxRenderer skyboxRenderer;

	private NormalMappingRenderer nrmlrndr;
	
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<TexturedModel, List<Entity>>();
	
	private ShadowMapMasterRenderer shadowMaster;

	public MasterRenderer(Loader loader,Camera camera) {
		enableCulling();
		createProjectionMatrix();
		renderer = new EntityRenderer(shdr, projectionMatrix);
		terrainrenderer = new TerrainRenderer(trrnshdr, projectionMatrix);
		skyboxRenderer = new SkyBoxRenderer(loader, projectionMatrix);
		nrmlrndr=new NormalMappingRenderer(projectionMatrix);
		shadowMaster=new ShadowMapMasterRenderer(camera);
		
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public void renderScene(List<Entity> entity,List<Entity> normalMapEntityList, Camera camera, Player player, Terrain terrain, List<Light> lights,
			Vector4f clipPlane) {
		for (Entity object : entity) {
			processEntity(object);
		}
		for(Entity object : normalMapEntityList) {
			processNormalMapEntity(object);
		}
		processEntity(player);
		processTerrain(terrain);
		render(lights, camera, clipPlane);
	}
	public void renderUpperScene(List<Entity> entity, Camera camera, Player player, Terrain terrain, List<Light> lights,
			Vector4f clipPlane,float level) {
		for (Entity object : entity) {
			if(object.getPosition().y>level)processEntity(object);
		}
		processEntity(player);
		processTerrain(terrain);
		render(lights, camera, clipPlane);
	}
	
	public void renderLowerScene( List<Entity> entities,Camera camera, Player player, Terrain terrain, List<Light> lights,
			Vector4f clipPlane,float level) {
		for(Entity entity:entities) {
			if(entity.getPosition().y<level)
			processEntity(entity);
		}
		processEntity(player);
		processTerrain(terrain);
		render(lights, camera, clipPlane);
	}
	private void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
		prepare();
		shdr.start();
		shdr.loadPlaneCulling(clipPlane);
		shdr.loadSkyColour(RED, GREEN, BLUE);
		shdr.loadLights(lights);
		shdr.loadViewMatrix(camera);
		renderer.render(entities,shadowMaster.getToShadowMapSpaceMatrix());
		shdr.stop();
		nrmlrndr.render(normalMapEntities, clipPlane, lights, camera);
		trrnshdr.start();
		trrnshdr.loadPlaneCulling(clipPlane);
		trrnshdr.loadSkyColour(RED, GREEN, BLUE);
		trrnshdr.loadLights(lights);
		trrnshdr.loadViewMatrix(camera);
		terrainrenderer.render(terrains,shadowMaster.getToShadowMapSpaceMatrix());
		trrnshdr.stop();
		skyboxRenderer.render(camera, RED, GREEN, BLUE);
		terrains.clear();
		entities.clear();
		normalMapEntities.clear();
		
	}
	

	public void processTerrain(Terrain terrain) {
		terrains.add(terrain);
	}

	public void processEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if (batch != null)
			batch.add(entity);
		else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	public void processNormalMapEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = normalMapEntities.get(entityModel);
		if (batch != null)
			batch.add(entity);
		else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			normalMapEntities.put(entityModel, newBatch);
		}
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void prepare(Fbo fbo) {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	    GL30.glClearBuffer(GL11.GL_COLOR, 0, fbo.getColourBuffer()); 
	    GL30.glClearBuffer(GL11.GL_DEPTH, 0, fbo.getDepthBuffer());
		GL11.glClearColor(RED, GREEN, BLUE, 1);
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
	}
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
	}

	public void cleanUp() {
		shdr.cleanup();
		skyboxRenderer.cleanUp();
		trrnshdr.cleanup();
		nrmlrndr.cleanUp();
		shadowMaster.cleanUp();
	}

	public void renderShadowMap(List<Entity> entityList,Light sun) {
		for(Entity entity:entityList) {
			processEntity(entity);
		}
		shadowMaster.render(entities, sun);
		entities.clear();
	}
	
	public int getShadowMapTexture() {
		return shadowMaster.getShadowMap();
	}
	
	private void createProjectionMatrix() {
		projectionMatrix=new Matrix4f();
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float yscale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) );
		float xscale = yscale / aspectRatio;
		float flength = FAR_PLANE - NEAR_PLANE;
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = xscale;
		projectionMatrix.m11 = yscale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / flength);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / flength);
		projectionMatrix.m33 = 0;
	}
}