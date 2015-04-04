package de.lurch.funcannon.util.attributes;

public class ParticleAttributes {

	private float offsetX;
	private float offsetY;
	private float offsetZ;
	private float speed;
	private int amount;

	public ParticleAttributes(float offsetX, float offsetY, float offsetZ, float speed, int amount) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.amount = amount;
		this.speed = speed;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}

	public float getOffsetZ() {
		return offsetZ;
	}

	public void setOffsetZ(float offsetZ) {
		this.offsetZ = offsetZ;
	}
}
