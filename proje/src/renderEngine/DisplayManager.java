package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {

	private static  int FPS_CAP ;
	
	private static long lastFrameTime;
	private static float delta;
	private static long currentFrameTime;
	private static final String TITLE = "Esnaf tycoon";
	private static int thisWidth,thisHeight;
	
	public static int getWidth() {
		return thisWidth;
	}


	public static int getHeight() {
		return thisHeight;
	}


	private static float elapsedTime=0;
	
	public DisplayManager() {
	}
	
	public static void createDisplay(int width,int height,int fpsCap) {
		FPS_CAP=fpsCap;
		thisHeight=height;
		thisWidth=width;
		ContextAttribs attribs = new ContextAttribs(3, 3).withForwardCompatible(true).withProfileCore(true);
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create(new PixelFormat().withDepthBits(24), attribs);
			Display.setTitle(TITLE);
			System.out.println(GL11.glGetInteger(GL11.GL_DEPTH_BITS));
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		GL11.glViewport(0, 0, width, height);
		lastFrameTime=getCurrentTime();
	}

	public static void updateDisplay() {
		Display.sync(FPS_CAP);
		Display.update();
		currentFrameTime=getCurrentTime();
		delta=(currentFrameTime-lastFrameTime)/1000f;
		lastFrameTime=currentFrameTime;
		elapsedTime+=delta;
	}
	
	public static float getFrameTimeSeconds() {
		return delta;
	}
	
	public static void closeDisplay() {
		Display.destroy();
	}	
	
	public static float getElapsedTime() {
		return elapsedTime;
	}

	private static long getCurrentTime() {
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}
}