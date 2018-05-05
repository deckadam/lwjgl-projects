package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	private Vector3f position = new Vector3f(0, 10, 0);
	private float pitch = 30;
	private float yaw;
	private float roll;

	private Player player;
	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 180;

	private float horizontalDistance = 0;
	private float verticalDistance = 0;

	public void move() {
		if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
			angleAroundPlayer = 180;
			distanceFromPlayer = 50;
			pitch = 30;
		} else {
			calculateAngle();
			calculateZoom();
			calculatePitch();
		}
		horizontalDistance = calculateHorizontalDistanceFromPlayer();
		verticalDistance = calculateVerticalDistanceFromPlayer();
		calculateCameraPosition();
		this.yaw = 180 - (player.getRy() + angleAroundPlayer);
	}

	private float theta;
	private float offsetx;
	private float offsetz;

	private void calculateCameraPosition() {

		theta = player.getRy() + angleAroundPlayer;
		offsetx = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		offsetz = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetx;
		position.z = player.getPosition().z - offsetz;
		position.y = player.getPosition().y + verticalDistance;

	}

	public void invertPitch() {
		pitch = -pitch;
	}

	public void invertRoll() {
		roll = -roll;
	}

	public void invertYaw() {
		yaw = -yaw;
	}

	private float calculateHorizontalDistanceFromPlayer() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistanceFromPlayer() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private float zoomLevel = 0;

	private void calculateZoom() {
		zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
	}

	private float pitchChange = 0;

	private void calculatePitch() {
		if (Mouse.isButtonDown(1)) {
			pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
		}
	}

	private float angleChange = 0;

	private void calculateAngle() {
		if (Mouse.isButtonDown(0)) {
			angleChange = Mouse.getDX() * 0.1f;
			angleAroundPlayer -= angleChange;
		}
	}

	public Camera(Player player) {
		this.player = player;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getRoll() {
		return roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

}
