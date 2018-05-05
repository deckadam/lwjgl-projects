package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	public static void main(String[] args) {
		float waterLevel = 10;
		float pps = 100;
		DisplayManager.createDisplay(1920, 1080, 1200);
		Loader loader = new Loader();
		TextMaster.init(loader);
		FontType font = new FontType(loader.loadTexture("arial"), new File("res/arial.fnt"));
		GUIText text = new GUIText("TEST", 2f, font, new Vector2f(0f, 0f), 20, false);
		Terrain terrain = createTerrain(loader);
		List<Entity> entity = createEntityList(loader, terrain, terrain.getHeightOfTerrain(0, 0));
		
		List<Entity> normalMappedEntitys = createNormalMappedEntity(loader);		
		Player player = createPlayer(loader);
		entity.add(player);
		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		ParticleMaster.init(loader, renderer.getProjectionMatrix(), pps);
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		List<Light> lights = createLightList(terrain);
		MousePicker mousepicker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
		List<WaterTile> waterTiles = createWaterTileList(waterLevel);
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		WaterShader wtrshdr = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, wtrshdr, renderer.getProjectionMatrix(), fbos);

		float edge = 0.1f;
		float width = 0.5f;
		float borderEdge = 0.4f;
		float borderWidth = 0.5f;
		ParticleTexture pTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4);
		ParticleSystem particleSystem = new ParticleSystem(pps, 25, 0.5f, 1f, 1, pTexture);
		int frames = 0;
		double lastTime = 0;
		double currentTime = 0;

		
		Fbo multisampledFbo=new Fbo(Display.getWidth(), Display.getHeight());
		Fbo outputFbo=new Fbo(Display.getWidth(), Display.getHeight(),Fbo.DEPTH_TEXTURE);
		PostProcessing.init(loader);
		
		TextMaster.loadTextSpecs(edge, width, borderEdge, borderWidth, new Vector2f(0.005f, 0.005f),
				new Vector3f(0.2f, 0.2f, 0.2f));
		
		while (!Display.isCloseRequested()) {
			camera.move();
			mousepicker.update();
			player.move(terrain);
			
			
			Vector3f mousePos = mousepicker.getTerrainPosition();
			entity.get(0)
					.setPosition(new Vector3f(lights.get(3).getPosition().x,
							terrain.getHeightOfTerrain(lights.get(3).getPosition().x, lights.get(3).getPosition().z),
							lights.get(3).getPosition().z));
			
			

			renderer.renderShadowMap(entity, lights.get(0));
			
			

			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - waterTiles.get(0).getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderUpperScene(entity, camera, player, terrain, lights,
					new Vector4f(0, 1, 0, -waterTiles.get(0).getHeight()), waterLevel);
			camera.invertPitch();
			camera.getPosition().y += distance;
			fbos.bindRefractionFrameBuffer();
			renderer.renderLowerScene(entity, camera, player, terrain, lights,
					new Vector4f(0, -1, 0, waterTiles.get(0).getHeight() + 0.1f), waterLevel);
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			fbos.unbindCurrentFrameBuffer();
			
			
			multisampledFbo.bindFrameBuffer();
			renderer.renderScene(entity, normalMappedEntitys, camera, player, terrain, lights,new Vector4f(0,0, 0, 100000));
			waterRenderer.render(waterTiles, camera, lights);
			if (mousePos != null) {
				lights.get(3).setPosition(new Vector3f(mousePos.x, mousePos.y + 10, mousePos.z));
				if (DisplayManager.getElapsedTime() > 1.0f) {
					particleSystem.generateParticles(mousePos);
					ParticleMaster.update(camera);
					ParticleMaster.renderParticles(camera);					
				}
			}
			multisampledFbo.unbindFrameBuffer();
			//multisampledFbo.resolveToFbo(outputFbo);
			multisampledFbo.resolveToScreen();
			//PostProcessing.doPostProcessing(outputFbo.getColourTexture());
			
			
			guiRenderer.render(guis);
			currentTime += DisplayManager.getFrameTimeSeconds();
			frames++;
			if (currentTime - lastTime >= 1.0) {
				text.setTextString(Integer.toString(frames));
				TextMaster.loadText(text);
				frames = 0;
				lastTime += 1.0;
			}			
			TextMaster.render();

			DisplayManager.updateDisplay();
		}
		PostProcessing.cleanUp();
		guiRenderer.cleanUp();
		multisampledFbo.cleanUp();
		outputFbo.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		wtrshdr.cleanup();
		fbos.cleanUp();
		TextMaster.cleanup();
		ParticleMaster.cleanup();
		DisplayManager.closeDisplay();
	}

	private static List<Entity> createEntityList(Loader loader, Terrain terrain, float waterLevel) {
		List<Entity> entities = new ArrayList<Entity>();
		RawModel treeModel = OBJLoader.loadObjModel("tree", loader);
		TexturedModel treeTextureModel = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("tree")));
		ModelTexture treeTexture = treeTextureModel.getMdlTexture();
		treeTexture.setShineDamper(10);
		treeTexture.setReflectivity(0.1f);

		RawModel lampModel = OBJLoader.loadObjModel("lamp", loader);
		ModelTexture lampModelTexture = new ModelTexture(loader.loadTexture("lamp"));
		lampModelTexture.setUseFakeLighting(true);
		lampModelTexture.setHasTransparency(true);
		TexturedModel lampTexturedModel = new TexturedModel(lampModel, lampModelTexture);
		entities.add(new Entity(lampTexturedModel, new Vector3f(0, 0, 0), 0, 0, 0, 1.0f));

		ModelTexture textureAtlas = new ModelTexture(loader.loadTexture("fernAtlas"));
		textureAtlas.setNumberOfRows(2);
		RawModel fernModel = OBJLoader.loadObjModel("fern", loader);
		TexturedModel fernTextureModel = new TexturedModel(fernModel, textureAtlas);
		ModelTexture fernTexture = fernTextureModel.getMdlTexture();
		fernTexture.setShineDamper(10);
		fernTexture.setReflectivity(0.05f);
		fernTexture.setHasTransparency(true);
		fernTexture.setUseFakeLighting(true);

		float x;
		float z;
		Random random = new Random();
		for (int i = 0; i < 200; i++) {
			x = random.nextFloat() * terrain.getSýze();
			z = -random.nextFloat() * terrain.getSýze();
			if (terrain.getHeightOfTerrain(x, z) > waterLevel)
				entities.add(new Entity(treeTextureModel, new Vector3f(x, terrain.getHeightOfTerrain(x, z), z), 0f, 0f,
						0f, 3.0f));
			x = random.nextFloat() * terrain.getSýze();
			z = -random.nextFloat() * terrain.getSýze();
			entities.add(new Entity(fernTextureModel, random.nextInt(4),
					new Vector3f(x, terrain.getHeightOfTerrain(x, z), z), 0f, 0f, 0f, 0.4f));
		}
		
		
		
		return entities;
	}

	private static List<Entity> createNormalMappedEntity(Loader loader) {
		List<Entity> returnList = new ArrayList<Entity>();
		/*TexturedModel barrel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),new ModelTexture(loader.loadTexture("barrel")));
		barrel.getMdlTexture().setShineDamper(10);
		barrel.getMdlTexture().setReflectivity(0.5f);
		barrel.getMdlTexture().setNormalMap(loader.loadTexture("barrelNormal"));
		barrel.getMdlTexture().setSpecularMap(loader.loadTexture("barrelS"));
		returnList.add(new Entity(barrel, new Vector3f(0, 0, 0), 0, 0, 0, 1));


		RawModel barrelModel = OBJLoader.loadObjModel("barrel", loader);
		ModelTexture barrelModelTexture = new ModelTexture(loader.loadTexture("barrel"));
		barrelModelTexture.setUseFakeLighting(true);
		barrelModelTexture.setHasTransparency(true);
		TexturedModel barrelTexturedModel = new TexturedModel(barrelModel, barrelModelTexture);
		returnList.add(new Entity(barrelTexturedModel, new Vector3f(50, 0, 0), 0, 0, 0, 1.0f));
		
		RawModel cherryModel=OBJLoader.loadObjModel("cherry", loader);
		ModelTexture cherryModelTexture=new ModelTexture(loader.loadTexture("cherry"));
		cherryModelTexture.setUseFakeLighting(true);
		cherryModelTexture.setHasTransparency(true);
		cherryModelTexture.setSpecularMap(loader.loadTexture("cherryS"));
		TexturedModel cherryTexturedModel=new TexturedModel(cherryModel,cherryModelTexture);
		returnList.add(new Entity(cherryTexturedModel,new Vector3f(0,0,-20),0,0,0,1));*/
		return returnList;
	}

	private static List<GuiTexture> createGuiList(Loader loader) {
		return null;
	}

	private static List<Light> createLightList(Terrain terrain) {
		List<Light> lights = new ArrayList<Light>();

		Light light1 = new Light(new Vector3f(20000, 20000, 20000), new Vector3f(2f, 2f, 2f));
		lights.add(light1);

		Light light2 = new Light(new Vector3f(200, terrain.getHeightOfTerrain(200, -200) + 12, -200),
				new Vector3f(0, 0, 5), new Vector3f(1, 0.01f, 0.002f));
		lights.add(light2);

		Light light3 = new Light(new Vector3f(100, 12, 0), new Vector3f(0, 5, 0), new Vector3f(1, 0.01f, 0.002f));
		lights.add(light3);

		Light light4 = new Light(new Vector3f(100, 12, 100), new Vector3f(1f, 1f, 1f), new Vector3f(1, 0.005f, 0.001f));
		lights.add(light4);

		return lights;
	}

	private static Terrain createTerrain(Loader loader) {
		TerrainTexture texture1 = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture texture2 = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture texture3 = new TerrainTexture(loader.loadTexture("flowers"));
		TerrainTexture texture4 = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		TerrainTexturePack txtpack = new TerrainTexturePack(texture1, texture2, texture3, texture4);
		return new Terrain(0, -1, loader, txtpack, blendMap, "heightmap");
	}

	private static List<WaterTile> createWaterTileList(float waterLevel) {
		List<WaterTile> waterTileList = new ArrayList<WaterTile>();
		waterTileList.add(new WaterTile(40, waterLevel - 12, -50));
		waterTileList.add(new WaterTile(120, waterLevel - 12, -50));
		waterTileList.add(new WaterTile(200, waterLevel - 12, -50));

		waterTileList.add(new WaterTile(40, waterLevel - 12, -130));
		waterTileList.add(new WaterTile(120, waterLevel - 12, -130));
		waterTileList.add(new WaterTile(200, waterLevel - 12, -130));

		waterTileList.add(new WaterTile(40, waterLevel - 12, -210));
		waterTileList.add(new WaterTile(120, waterLevel - 12, -210));
		waterTileList.add(new WaterTile(200, waterLevel - 12, -210));
		return waterTileList;
	}

	private static Player createPlayer(Loader loader) {
		RawModel playerRawModel = OBJLoader.loadObjModel("dragon", loader);
		TexturedModel playerTexturedModel = new TexturedModel(playerRawModel,
				new ModelTexture(loader.loadTexture("path")));
		Player player = new Player(playerTexturedModel, new Vector3f(0, 0, 0), 0, 0, 0, 0.2f);
		player.getModel().getMdlTexture().setShineDamper(10);
		player.getModel().getMdlTexture().setReflectivity(0.5f);
		return player;
	}
}