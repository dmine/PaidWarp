package com.dmine.PaidWarp;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Location;

public class SerializableLocation implements Serializable {

	private static final long serialVersionUID = 1L;
	private double x;
	private double y;
	private double z;
	private float pitch;
	private float yaw;
	private UUID uuid;
	
	public SerializableLocation(double x, double y, double z, float pitch, float yaw, UUID uuid) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.uuid = uuid;
	}
	
	public SerializableLocation(Location location) {
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.pitch = location.getPitch();
		this.yaw = location.getYaw();
		this.uuid = location.getWorld().getUID();
	}
	
	public void updateLocation(Location location) {
		location.setX(this.x);
		location.setY(this.y);
		location.setZ(this.z);
		location.setPitch(this.pitch);
		location.setYaw(this.yaw);
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public double getZ() {
		return this.z;
	}
	
	public float getPitch() {
		return this.pitch;
	}
	
	public float getYaw() {
		return this.yaw;
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
}
