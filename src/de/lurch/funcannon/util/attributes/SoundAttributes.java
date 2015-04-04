package de.lurch.funcannon.util.attributes;

public class SoundAttributes {

	private float volume;
	private float pitch;

	public SoundAttributes(float pitch, float volume) {
		this.volume = volume;
		this.pitch = pitch;
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

}
