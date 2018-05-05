package terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;

public class Terrain {
	private static final float SIZE=200;
	private static final float MAX_HEIGHT=20;
	private static final float MAX_PIXEL_COLOUR=256*256*256;
	
	private float x;
	private float z;
	private RawModel model;
	private TerrainTexturePack txtpack;
	private TerrainTexture blendMap;
	
	private float [][] heights;
	
	
	public Terrain(int gridX,int gridZ,Loader loader,TerrainTexturePack txtpack,TerrainTexture blendMap,String heightMap) {
		this.x=gridX*SIZE;
		this.z=gridZ*SIZE;		
		this.txtpack=txtpack;
		this.blendMap=blendMap;
		this.model=generateTerrain(loader,heightMap);
	}	
	
	private RawModel generateTerrain(Loader loader,String heightMap){
		BufferedImage bfrImg=null;
		try {
			bfrImg=ImageIO.read(new File("res/"+heightMap+".png"));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		int VERTEX_COUNT = bfrImg.getHeight();
		heights= new float [VERTEX_COUNT][VERTEX_COUNT];
		float height;
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
				height=getHeight(j,i,bfrImg);
				vertices[vertexPointer*3+1] = height;
				heights[j][i]=height;
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				Vector3f normal = calculateNormal(j,i,bfrImg);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	
	float terrainX;
	float terrainZ;
	float gridSquareSýze;
	int gridX;
	int gridZ;
	float xCoord;
	float zCoord;
	float answer;
	public float getHeightOfTerrain(float worldX,float worldZ) {
		terrainX=worldX-this.x;
		terrainZ=worldZ-this.z;
		gridSquareSýze=SIZE/((float) heights.length-1);
		gridX=(int) Math.floor(terrainX / gridSquareSýze);
		gridZ=(int) Math.floor(terrainZ / gridSquareSýze);
		if(gridX>=heights.length-1||gridZ>=heights.length-1||gridZ<0||gridX<0)
			return 0;
		xCoord=(terrainX%gridSquareSýze)/gridSquareSýze;
		zCoord=(terrainZ%gridSquareSýze)/gridSquareSýze;
		if (xCoord <= (1-zCoord)) {
			answer = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ], 0), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} else {
			answer = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
							heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
							heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}
		return answer;
	}
	
	private Vector3f calculateNormal(int x , int z , BufferedImage bfrImg) {
		float heightL=getHeight(x-1,z,bfrImg);
		float heightR=getHeight(x+1,z,bfrImg);
		float heightD=getHeight(x,z-1,bfrImg);
		float heightU=getHeight(x,z+1,bfrImg);
		Vector3f normal=new Vector3f(heightL-heightR,5f,heightD-heightU);
		normal.normalise();
		return normal;
	}
	
	private float getHeight(int x ,int z , BufferedImage bfrImg) {
		if(x<0||x>=bfrImg.getHeight()||z<0||z>=bfrImg.getHeight())return 0;
		float height=bfrImg.getRGB(x , z);
		height+=MAX_PIXEL_COLOUR/2f;
		height/=MAX_PIXEL_COLOUR/2f;
		height*=MAX_HEIGHT;
		return height;
	}
	
	public float getX() {
		return x;
	}
	
	public float getZ() {
		return z;
	}
	
	public RawModel getModel() {
		return model;
	}

	public float getSýze() {
		return SIZE;
	}
	
	public TerrainTexturePack getTxtpack() {
		return txtpack;
	}

	public TerrainTexture getBlendMap() {
		return blendMap;
	}
}
