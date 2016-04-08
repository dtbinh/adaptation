package allow.simulator.entity.knowledge;

import java.util.LinkedList;
import java.util.Queue;

public class WorkerPool {
	
	private Queue<Worker> workerPool;
	
	public WorkerPool(int initialCapacity) {
		workerPool = new LinkedList<Worker>();
		
		for (int i = 0; i < initialCapacity; i++) {
			workerPool.add(new Worker());
		}
	}
	
	public Worker pop() {
		return !workerPool.isEmpty() ? workerPool.poll() : new Worker();
	}
	
	public void put(Worker worker) {
		workerPool.add(worker);
	}
}
