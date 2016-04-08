package allow.simulator.entity.knowledge;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import allow.simulator.entity.Entity;
import allow.simulator.entity.knowledge.DBConnector.DBType;
import allow.simulator.mobility.data.TType;

public class DBExpertKnowledge implements DBKnowledgeModel {
	// Dictionary holding tables which have been
	//private static ConcurrentHashMap<String, Boolean> aIdTableExists = new ConcurrentHashMap<String, Boolean>();
	public static List<Long> tableList = new ArrayList<Long>();	
	//Not tested
	private static final String MY_SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %1$s "
			+ "(entryNo INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY, entityId BIGINT, nodeId INT, prevNodeId INT, "
			+ "ttime DOUBLE, prevttime DOUBLE, weather TINYINT UNSIGNED, weekday TINYINT UNSIGNED, "
			+ "timeOfDay TINYINT UNSIGNED, modality TINYINT UNSIGNED, density FLOAT, startTime INT UNSIGNED, "
			+ "endTime INT UNSIGNED, err FLOAT, INDEX(entityId, nodeId, modality, timeOfDay, weekday, prevNodeId, prevttime));%2$s";
	
	private static final String POSTGRE_SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %1$s "
			+ " (entryNo SERIAL PRIMARY KEY, entityId BIGINT, nodeId INTEGER, prevNodeId INTEGER, "
			+ "ttime REAL, prevttime REAL, weather SMALLINT, weekday SMALLINT, "
			+ "timeOfDay SMALLINT, modality SMALLINT, density REAL, startTime INTEGER, "
			+ "endTime INTEGER, err REAL); CREATE INDEX on %2$s "
			+ "(entityId, nodeId, modality, timeOfDay, weekday, prevNodeId, prevttime)";

	private static final String MY_SQL_SHOW_TABLES = "SHOW TABLES LIKE '%1$s'";

	private static final String POSTGRE_SQL_SHOW_TABLES = "SELECT * FROM pg_catalog.pg_tables where "
			+ "tablename like '%1s'";

	private static final String SQL_INSERT_VALUES = "INSERT INTO %1$s "
			+ " (entityId, nodeId, prevNodeId, ttime, prevttime, weather, weekday, timeOfDay, "
			+ "modality, density, startTime, endTime, err) VALUES ";
	
	//private static final String SQL_GET_Confidence = "Select (sqrt(abs((1/median(ttime))))*(1/stddev(ttime))) as confidence, weather from ek_0_tbl_10317 group by weather order by confidence desc;";

	private DBType type;
	private String sqlCreateTables;
	private String sqlShowTables;
	private String sqlInsertValues;
	private static final int MAXENTITYPERTABLE = 10000000;

	public DBExpertKnowledge(DBType type) {
		this.type = type;

		switch (type) {

		case MYSQL:
			sqlCreateTables = MY_SQL_CREATE_TABLE;
			sqlShowTables = MY_SQL_SHOW_TABLES;
			sqlInsertValues = SQL_INSERT_VALUES;
			break;

		case POSTGRE:
			sqlCreateTables = POSTGRE_SQL_CREATE_TABLE;
			sqlShowTables = POSTGRE_SQL_SHOW_TABLES;
			sqlInsertValues = SQL_INSERT_VALUES;
			break;

		default:
			throw new IllegalArgumentException("Error: Unknown DB type " + type);
		}
	}

	/**
	 * Inserts journey information to the node making the trip and also sends
	 * feedback to the predictor nodes (seperately per roadsegment)
	 */
	@Override
	public boolean addEntry(Entity agent, List<TravelExperience> prior,
			List<TravelExperience> it, String tablePrefix) {
		
		boolean success = true;
		String agentId = String.valueOf(agent.getId());

		if (it.size() == 0) {
			return false;
		}
		
		//Make sure the agent has evoKnowledge
		success = createTableIfNotExists(agentId, tablePrefix);

		//Generate and exeute queries
		List<String> statemnens = compileSQLInsertStatements(it, prior, tablePrefix, agentId);
		success = executeInsertQuery(statemnens);
		return success;
	}

	/**
	 * Estimates travel time for a journey by estimating the road segments individually.
	 * Generates an detailled sql statement to estimate the travel time for each road segment.
	 * If not all nodes can be predicted a less detailed query is send to estimate the remaining nodes.
	 * For road segments that can't be predicted the default value from the input is used.
	 * Finally the resulting itinerary is cleand up so that also the previous travel time fields match.
	 * 
	 * 
	 * NOTE: We requesting all road segments at once. Therefore, we can't take the predicted travel time
	 * for previous road segments into account. If something like this is needed we could use the input time
	 * or predict travel time in an itterative process by estimating the traveltime once for each segqment,
	 * and then run one more query where we use the allready predictedt times to refine our results.
	 */
	@Override
	public List<TravelExperience> getPredictedItinerary(Entity agent,
			List<TravelExperience> it, String tablePrefix) {

		// prepare a hashmap for fast access to the predictions per segment
		ConcurrentHashMap<Long, TravelExperience> itMap = new ConcurrentHashMap<Long, TravelExperience>();
		for (TravelExperience ex : it) {
			// Skip transient activities
			if (ex.isTransient()) {
				continue;
			}
			itMap.put(ex.getSegmentId(), ex);
		}

		//check if we have to do something
		if (itMap.size()<1){
			return it;
		}
		
		// connection and statement for database query
		Connection con = null;
		Statement stmt = null;
		ResultSet result = null;

		// get connection
		try {
			con = DSFactory.getConnection();
			stmt = con.createStatement();

			// check if user even has evoknowledge
			// TODO: Check all tables not only 0
			String tableName = tablePrefix + "_tbl_" + 0;
			result = stmt.executeQuery(String.format(sqlShowTables, tableName));
			if (!result.next()) {
				return it;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (!result.isClosed()) {
					result.close();
				}
				if (!stmt.isClosed()) {
					stmt.close();
				}
				if (!con.isClosed()) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		// Continuously decrease query precision and predict segments
		for (int precission = 0; precission < 5; precission++) {
			String sql = compileSQLSelectStatement(precission, tablePrefix, it);
			executeSelectQuery(sql, itMap);
		}

		// Update the times per segment
		boolean firstSeg = true;
		long segmentTStart = 0;

		for (TravelExperience ex : it) {

			// ignore transient segments
			if (ex.isTransient()) {
				continue;
			}

			if (firstSeg) {
				segmentTStart = ex.getStartingTime() / 1000;
			}

			ex.setStartingTime(segmentTStart * 1000);
			segmentTStart = segmentTStart + ((int) ex.getTravelTime() * 1000);
			ex.setEndTime(segmentTStart * 1000);

			firstSeg = false;
		}

		return it;
	}
	
	/**
	 * Currently nothing to do here.
	 */
	@Override
	public void clean(Entity entity, String tablePrefix) {
		// TODO Auto-generated method stub

	}	
	
	/**
	 * Check if table for agent already exists, creates if not.
	 * @param agentId
	 * @param tabelePrefix
	 */
	private boolean createTableIfNotExists(String agentId, String tablePrefix) {
		boolean tableExists = contains(getTableId(agentId),tableList);
		boolean success = true;
		
		String tableName = tablePrefix + "_tbl_" + getTableId(agentId);

		if (!tableExists) {
			success = executeInsertQuery(String.format(sqlCreateTables, tableName, ((type == DBType.MYSQL) ? "" : tableName)));
			tableList.add((Long)getTableId(agentId));
		}
		return success;
	}

	/**
	 * Generates a list of SQL statement strings to add the knowledge to the database.
	 * The lists starts with the inserts for the individual feedback of the predictors of the road segments
	 * The last SQL statement adds the combined feedback that will go to the "traveling" entity
	 * 
	 * @param it
	 * @param tablePrefix
	 * @param agentId
	 * @return
	 */
	public List<String> compileSQLInsertStatements(List<TravelExperience> it, List<TravelExperience> prior, String tablePrefix, String agentId){
		List<String> statemantList = new ArrayList<String>();
		
		String tableName = tablePrefix + "_tbl_" + getTableId(agentId);
		
		// parse the itinerary and add a line for each entry
		String stmtCombined= String.format(sqlInsertValues, tableName);

		boolean firstSeg = true;
		long prevNodeId = 0;
		double prevDuration = 0;

		// pushes every road-segment to the database of the predicting node
		for (TravelExperience ex : it) {
			//calculate the prediction error
			int index = it.indexOf(ex);

			if (prior.size()<= index) continue;
			
			double error = prior.get(index).getTravelTime()-ex.getTravelTime();
			
			String individualTableName = tablePrefix + "_tbl_" + getTableId(((Long)ex.getPredictorId()).toString());
			String individualFeedback = String.format(sqlInsertValues, individualTableName);
			
			long nodeId = ex.getSegmentId();
			long start = ex.getStartingTime();
			long end = ex.getEndTime();
			double duration = ex.getTravelTime();
			String stmtStringRaw = "";

			stmtCombined = stmtCombined.concat(firstSeg ? "" : ",");
			stmtCombined = stmtCombined.concat("('" + agentId + "',");
			
			//entity id for individual feedback (combined is set above)
			
			individualFeedback = individualFeedback.concat("('" + ((Long)ex.getPredictorId()).toString()+ "',");
			
			//compile the data
			stmtStringRaw = stmtStringRaw.concat("'" + nodeId + "',");
			stmtStringRaw = stmtStringRaw.concat("'" + prevNodeId + "',");
			stmtStringRaw = stmtStringRaw.concat(duration + ",");
			stmtStringRaw = stmtStringRaw.concat(prevDuration + ",");
			stmtStringRaw = stmtStringRaw.concat(ex.getWeather().getEncoding() + ",");
			stmtStringRaw = stmtStringRaw.concat(ex.getWeekday() + ",");
			stmtStringRaw = stmtStringRaw.concat(EvoEncoding.getTimeOfDay(ex
					.getTStart().getHour()) + ",");
			stmtStringRaw = stmtStringRaw.concat(TType.getEncoding(ex
					.getTransportationMean()) + ",");
			// Density = Number of other entities on segment.
			stmtStringRaw = stmtStringRaw.concat(ex.getPublicTransportationFillingLevel() + ","); 
			stmtStringRaw = stmtStringRaw.concat(String.valueOf(start / 1000) + ",");
			stmtStringRaw = stmtStringRaw.concat(String.valueOf(end / 1000) + ",");
			stmtStringRaw = stmtStringRaw.concat(String.valueOf(error) + ")");
			
			
			//combined feedback running string
			stmtCombined = stmtCombined.concat(stmtStringRaw);
			
			//individual feedback string
			individualFeedback = individualFeedback.concat(stmtStringRaw);
			individualFeedback = individualFeedback.concat(";");
			statemantList.add(individualFeedback);
			
			firstSeg = false;
			prevNodeId = nodeId;
			prevDuration = duration;

		}

		// this finishes and adds the statement for the entity which will add
		// the whole journy to its evkoknowledge
		stmtCombined = stmtCombined.concat(";");
		statemantList.add(stmtCombined);

		return statemantList;
		
	}
	
	/**
	 * This funtion compiles a detail level specific query for all 
	 * @param detailLevel
	 * @param tablePrefix
	 * @param it
	 * @return
	 */
	public String compileSQLSelectStatement(int detailLevel, String tablePrefix, List<TravelExperience> it){

		// fit input
		if (detailLevel > 4 || detailLevel < 0) {
			detailLevel = 0;
		}
		
		//generate the where statement dependent on the detail level
		List<String> where = new ArrayList<String>();
		//Predefine the query statements
		where.add(" nodeId = %1$d ");
		where.add( where.get(0).concat("AND modality = %2$d "));
		where.add( where.get(1).concat("AND timeOfDay = %3$d "));
		where.add( where.get(2).concat("AND weekday = %4$d "));
		where.add( where.get(3).concat("AND prevNodeId = %5$d "));

		
		//prepare query
		String query = "Select entityId, nodeId, avtt from (Select rank() over (partition by nodeId order by confidence desc,entityId) as rank, entityId, nodeId, avtt, confidence from (Select entityId, nodeId, AVG(ttime) as avtt, EXP(-0.1*abs(avg(err)))*EXP(-0.8*stddev_pop(err)) as confidence from ";
		int id = 0;
		String tableName = tablePrefix + "_tbl_" + id;
		query = query.concat(" "+tableName+" ");
		
		boolean firstSeg = true;
		long prevNodeId = 0;
		
		query = query.concat(" WHERE ");
		
		//Loop the where statement
		for (TravelExperience ex : it){
		
			//ignore transient segments
			if (ex.isTransient()){
				continue;
			}
			
			//ignore already predicted segments
			if (ex.isPredicted()){
				continue;
			}
			
			//prepare content
			long nodeId = ex.getSegmentId();
			byte modality = TType.getEncoding(ex.getTransportationMean());  
			byte timeOfDay = EvoEncoding.getTimeOfDay(ex.getTStart().getHour());
			byte weekDay = (byte) ex.getWeekday(); 
			
			//Add a part of the where statement with the required level of detail
			//for each part of the journey 
			if (!firstSeg){
				query = query.concat(" OR ");
			}
			
			//Build query
			query = query.concat("("+where.get(detailLevel)+")");
			query = String.format(query, nodeId, modality, timeOfDay, weekDay, prevNodeId);
		
			
			//maintain
			firstSeg = false;
			prevNodeId = ex.getSegmentId();
		}
		//finish query
		query = query.concat(" group by entityId, nodeId order by confidence desc) as relWithConf) as rankedData where rank = 1;");
		
		
		
		return query;
	}

	
	/**
	 * Executes an SQL-Select-Query and handles results
	 * @param The sql query
	 * @return
	 */
	private void executeSelectQuery(String sql,	ConcurrentHashMap<Long, TravelExperience> itMap) {

		// show me the query
		//System.out.println(sql);

		// connection and statement for database query
		ResultSet result = null;
		Statement stmt = null;
		Connection con = null;

		// execute query and handle result
		try {
			con = DSFactory.getConnection();
			stmt = con.createStatement();
			stmt.execute(sql);
			result = stmt.getResultSet();

			while (result.next()) {
				itMap.get(result.getLong(2)).setPredictorId(result.getInt(1));
				itMap.get(result.getLong(2)).setTravelTime(result.getDouble(3));
				itMap.get(result.getLong(2)).setPredicted(true);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(sql);
		} finally {
			try {
				if (!result.isClosed()) {
					result.close();
				}
				if (!stmt.isClosed()) {
					stmt.close();
				}
				if (!con.isClosed()) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				
			}
		}
	}
	
	/**
	 * Writes stuff to the db
	 * @param stuff
	 */
	private boolean executeInsertQuery(String stuff) {

		boolean success = true;
		Statement stmt = null;
		Connection con = null;

		// Manage the connection and query
		try {
			con = DSFactory.getConnection();
			stmt = con.createStatement();
			stmt.execute(stuff);
		} catch (SQLException e) {
			e.printStackTrace();
			success = false;
		} finally {
			try {
				if (!stmt.isClosed()) {
					stmt.close();
				}
				if (!con.isClosed()) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return success;
	}
	
	/**
	 * Executes several SQL statements using one single statement.
	 * Hopefully saves some resources as the connection overhead is saved.
	 * @param stuff
	 * @return
	 */
	private boolean executeInsertQuery(List<String> stuff){
	
		String query = "";
		for (String s : stuff){
				query+=s+" ";		
		}
		
		return executeInsertQuery(query);
	}


	/**
	 * Returns the Id of the tabel where an entity is stored.
	 * This is dependend on the maxEntityPerTable global variable.
	 * @param entityId
	 * @return
	 */
	public long getTableId(String entityId){
	
		return (long) Math.floor(Long.valueOf(entityId)/MAXENTITYPERTABLE);
	}
	
	/**
	 * Tests if a given integer value is contained in an integer List.
	 * @param testVal
	 * @param list
	 * @return
	 */
	private boolean contains(long testVal, List<Long> list){
		boolean test = false;
		for (long x : list){
			if (x == testVal) test = true;
		}
		return test;
	}

	@Override
	public boolean exchangeKnowledge(Entity agent1, Entity agent2, String tablePrefix) {
		return false;
	}
}
