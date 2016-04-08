package allow.simulator.world.layer;

import java.util.List;

import allow.simulator.util.Coordinate;

public class SafetyArea extends Area {

	private int safetyLevel;
	
	public SafetyArea(String name, List<Coordinate> point, int safetyLevel) {
		super(name, point);
		this.safetyLevel = safetyLevel;
	}
	
	public int getSafetyLevel() {
		return safetyLevel;
	}

}
