package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity {

	private static final float RUN_SPEED = 50f;
	private static final float WALK_SPEED = 25f;
	private static final float TURN_SPEED = 100;

	private float velocityWalk = 0;
	private float currentRotation = 0;

	private Vector3f position = new Vector3f(0, 10, 0);

	public Player(TexturedModel model, Vector3f position, float rx, float ry, float rz, float scale) {
		super(model, position, rx, ry, rz, scale);
		this.position.y = 0;
	}


	public void move(Terrain terrain) {
		checkInputs();
		walk();
		rotate();
		this.setPosition(new Vector3f(super.getPosition().x,terrain.getHeightOfTerrain(super.getPosition().x,super.getPosition().z),super.getPosition().z));
	}

	private void rotate() {
		super.increaseRotation(0, currentRotation * DisplayManager.getFrameTimeSeconds(), 0);
	}

	private void walk() {
		float distance = velocityWalk * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRy())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRy())));
		super.increasePosition(dx, 0, dz);
	}


	private float velocityUp(float value, float limitSpeed, float change) {
		if (value < limitSpeed) {
			value += change;
		} else if (value > limitSpeed) {
			value -= change;
		}
		return value;
	}

	private void checkInputs() {
		// WALK CHECK
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				velocityWalk = velocityUp(velocityWalk, -RUN_SPEED, 1f);
			else
				velocityWalk = velocityUp(velocityWalk, -WALK_SPEED, 1f);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				velocityWalk = velocityUp(velocityWalk, RUN_SPEED, 1f);
			else
				velocityWalk = velocityUp(velocityWalk, WALK_SPEED, 1f);
		} else {
			velocityWalk = velocityUp(velocityWalk, 0, 1f);
		}

		// ROTATE CHECK
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			currentRotation = -TURN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			currentRotation = TURN_SPEED;
		} else {
			currentRotation = 0;
		}

	}
}