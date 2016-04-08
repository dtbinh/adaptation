package allow.simulator.mobility.data.gtfs;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GTFSServiceException {
	
	private static final DateTimeFormatter gtfsDate = DateTimeFormatter.ofPattern("uuuuMMdd");

	private String serviceId;
	private LocalDate date;
	private int type;
	
	private static int SERVICE_ID = 0;
	private static int SERVICE_EXCEPTION_DATE = 1;
	private static int SERVICE_EXCEPTION_TYPE = 2;
	
	public String getServiceId() {
		return serviceId;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public int getType() {
		return type;
	}
	
	public static GTFSServiceException fromGTFS(String line) throws ParseException {
		GTFSServiceException i = new GTFSServiceException();
		String tokens[] = line.split(",");
		i.serviceId = tokens[SERVICE_ID];
		i.date = LocalDate.parse(tokens[SERVICE_EXCEPTION_DATE], gtfsDate);
		i.type = Integer.parseInt(tokens[SERVICE_EXCEPTION_TYPE]);
		return i;
	}
}
