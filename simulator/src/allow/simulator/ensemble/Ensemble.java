package allow.simulator.ensemble;

import java.util.ArrayList;
import java.util.List;

import allow.simulator.entity.Entity;

public class Ensemble {

	private String id;
	private Entity creator;
	private List<Entity> participants;
	
	public Ensemble(String id, Entity creator) {
		this.id = id;
		this.creator = creator;
		participants = new ArrayList<Entity>();
	}
	
	public void join(Entity newEntity) {
		// System.out.println(newEntity + " joined ensemble + " + id);
	}
	
	public void leave(Entity entity) {
		// System.out.println(entity + " left ensemble + " + id);
	}
	
	public int getNumberOfParticipants() {
		return participants.size();
	}
	
	public Entity getCreator() {
		return creator;
	}
	
	public String getID() {
		return id;
	}
}
