package allow.simulator.mobility.planner;

public final class RequestId {

	private static long reqId = 0;

	private final long requestId;
	private int requestNumber;
	
	public RequestId() {
		requestId = getReqId();
	}
	
	public long getRequestId() {
		return requestId;
	}
	
	public int getNextRequestNumber() {
		int ret = requestNumber;
		requestNumber++;
		return ret;
	}
	
	private static synchronized long getReqId() {
		long id = reqId;
		reqId++;
		return id;
	}
	
}
