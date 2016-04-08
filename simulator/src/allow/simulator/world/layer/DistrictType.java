package allow.simulator.world.layer;

public enum DistrictType {
	
	RESIDENTIAL,
	
	INDUSTRIAL,
	
	SHOPPING,
	
	UNIVERSITY,
	
	SCHOOL,
	
	UNKNOWN;
	
	public static DistrictType fromString(String typeString) {
		String temp = typeString.toUpperCase().trim();
		
		if (temp.equals("RESIDENTIAL")) {
			return RESIDENTIAL;
		} else if (temp.equals("INDUSTRIAL")) {
			return INDUSTRIAL;
		} else if (temp.equals("SHOPPING")) {
			return SHOPPING;
		} else if (temp.equals("UNIVERSITY")) {
			return UNIVERSITY;
		} else if (temp.equals("SCHOOL")) {
			return SCHOOL;
		} else {
			return UNKNOWN;
		}
	}
}
