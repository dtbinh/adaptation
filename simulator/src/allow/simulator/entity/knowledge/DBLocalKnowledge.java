package allow.simulator.entity.knowledge;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import allow.simulator.entity.Entity;
import allow.simulator.entity.knowledge.DBConnector.DBType;
import allow.simulator.mobility.data.TType;

public class DBLocalKnowledge implements DBKnowledgeModel {
	
	private static final String MY_SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %1$s "
			+ "(nodeId INT, "
			// + "prevNodeId INT, "
			//+ "weather TINYINT UNSIGNED, weekday TINYINT UNSIGNED, "
			// + "timeOfDay TINYINT UNSIGNED, 
			+ " modality TINYINT UNSIGNED, ttime FLOAT,"
			//+ "prevttime FLOAT, "
			+ "fillLevel FLOAT, "
			+ "weight DOUBLE, "
			// + "PRIMARY KEY(nodeId, prevNodeId, weather, weekday, timeOfDay, modality));%2$s";
			+ "PRIMARY KEY(nodeId, "
			//+ "prevNodeId, "
			//+ "timeOfDay,"
			+ "modality));%2$s";
	
	private static final String POSTGRE_SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %1$s "
			+ " (entryNo SERIAL PRIMARY KEY, nodeId INTEGER, prevNodeId INTEGER, "
			+ "weather SMALLINT, weekday SMALLINT, "
			+ "timeOfDay SMALLINT, modality SMALLINT, ttime REAL, prevttime REAL, fillLevel REAL, "
			+ "number REAL, "
			+ "UNIQUE(nodeId, prevNodeId, weather, weekday, timeOfDay, modality)); "
			// + "startTime INTEGER, endTime INTEGER); "
			+ "CREATE INDEX on %2$s "
			+ "(nodeId, modality, timeOfDay, weekday, prevNodeId, prevttime)";
	
	private static final String MY_SQL_SHOW_TABLES = "SHOW TABLES LIKE '%1$s'";
	
	private static final String POSTGRE_SQL_SHOW_TABLES = "SELECT * FROM pg_catalog.pg_tables where "
			+ "tablename like '%1s'";
	
	private static final String SQL_INSERT_VALUES = "INSERT INTO %1$s "
			 + " (nodeId, "
			 //+ "prevNodeId, "
			 //+ "timeOfDay, "
			 + "modality, ttime, "
			 // + "prevttime, "
			 + "fillLevel, weight)"
			// + " (nodeId, prevNodeId, weather, weekday, timeOfDay, modality, ttime, prevttime, fillLevel, weight)"
			//+ ", startTime, endTime)"
			+ " VALUES ";
	
	private static final String MY_SQL_UPDATE_ON_INSERT = "ON DUPLICATE KEY UPDATE "
			+ "ttime=(%1$s.ttime * %1$s.weight + VALUES(ttime)) / (%1$s.weight + 1), "
			// + "prevttime=(%1$s.prevttime * %1$s.weight + VALUES(prevttime)) / (%1$s.weight + 1), "
			+ "filllevel=(%1$s.filllevel * %1$s.weight + VALUES(filllevel)) / (%1$s.weight + 1), "
			+ "weight=(%1$s.weight + 1)";
	
	private static final String MY_SQL_MERGE_SIMPLE = "CREATE TABLE IF NOT EXISTS %1$s AS SELECT * FROM %2$s; "
			+ "ALTER TABLE %1$s ADD PRIMARY KEY(nodeId, "
			//+ "prevNodeId, "
			//+ "timeOfDay, "
			+ "modality); ";
			//+ "ALTER TABLE %1$s ADD PRIMARY KEY(nodeId, prevNodeId, weather, weekday, timeOfDay, modality); ";
			// + ", startTime INT UNSIGNED, endTime INT UNSIGNED, "
			// + "ALTER TABLE %1$s ADD INDEX(nodeId, prevNodeId, weather, weekday, timeOfDay, modality);";
	
	private static final String MY_SQL_MERGE_MUTUAL = 
			//"CREATE TEMPORARY TABLE ex AS (SELECT nodeId, prevNodeId, weather, "
			//+ "weekday, timeOfDay, modality, ttime, prevttime, filllevel, weight FROM %1$s); "
			"CREATE TEMPORARY TABLE ex AS (SELECT nodeId,"
			//+ "prevNodeId, "
			//+ "timeOfDay, "
			+ "modality, ttime, "
			//+ "prevttime, "
			+ "filllevel, weight FROM %1$s); "
			
			+ "INSERT INTO %1$s (SELECT * FROM %2$s) "
			+ "ON DUPLICATE KEY UPDATE "
			+ "ttime=(%1$s.ttime*%1$s.weight+VALUES(ttime))/(%1$s.weight+1), "
			//+ "prevttime=(%1$s.prevttime*%1$s.weight+VALUES(prevttime))/(%1$s.weight+1), "
			+ "filllevel=(%1$s.filllevel*%1$s.weight+VALUES(filllevel))/(%1$s.weight+1), "
			+ "weight=(%1$s.weight+1); "
			
			+ "INSERT INTO %2$s (SELECT * FROM ex) "
			+ "ON DUPLICATE KEY UPDATE "
			+ "ttime=(%2$s.ttime*%2$s.weight+VALUES(ttime))/(%2$s.weight+1), "
			//+ "prevttime=(%2$s.prevttime*%2$s.weight+VALUES(prevttime))/(%2$s.weight+1), "
			+ "filllevel=(%2$s.filllevel*%2$s.weight+VALUES(filllevel))/(%2$s.weight+1), "
			+ "weight=(%2$s.weight+1); ";
	
	private DBType type;
	private String tablePrefix;
	private String modelName;
	
	private String sqlCreateTables;
	private String sqlShowTables;
	private String sqlInsertValues;
	private String sqlUpdateOnInsert;
	private String sqlMergeSimple;
	private String sqlMergeMutual;
	
	// Dictionary holding tables which have been 
	private ConcurrentHashMap<String, Boolean> aIdTableExists = new ConcurrentHashMap<String, Boolean>();
	
	public DBLocalKnowledge(DBType type, String tablePrefix, String modelName) {
		this.type = type;
		this.tablePrefix = tablePrefix;
		this.modelName = modelName;
		
		switch (type) {
		case MYSQL:
			sqlCreateTables = MY_SQL_CREATE_TABLE;
			sqlShowTables = MY_SQL_SHOW_TABLES;
			sqlInsertValues = SQL_INSERT_VALUES;
			sqlUpdateOnInsert = MY_SQL_UPDATE_ON_INSERT;
			sqlMergeSimple = MY_SQL_MERGE_SIMPLE;
			sqlMergeMutual = MY_SQL_MERGE_MUTUAL;
			break;
			
		case POSTGRE:
			throw new UnsupportedOperationException("Error: Postresql is currently not supported.");
			/*sqlCreateTables = POSTGRE_SQL_CREATE_TABLE;
			sqlShowTables = POSTGRE_SQL_SHOW_TABLES;
			sqlInsertValues = SQL_INSERT_VALUES;
			break;*/
			
		default:
			throw new IllegalArgumentException("Error: Unknown DB type " + type);
		}
		initaIdTableExists();
	}
	
	private void initaIdTableExists() {
		aIdTableExists.clear();
		Statement stmt = null;
		Connection con = null;
		ResultSet rs = null;
		
		String stmtString = "";
		String tableName = tablePrefix + "_tbl_%";
		
		try {
			// get connection
			con = DSFactory.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery("SHOW TABLES FROM " + modelName + " LIKE '" + tableName + "'");
			
			while (rs.next()) {
				String table = rs.getString(1);
				String[] tokens = table.split("_");
				aIdTableExists.put(tokens[tokens.length - 1], true);
			}

		} catch (SQLException e) {
			System.out.println(stmtString);
			// e.printStackTrace();

		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
				if (rs != null)
					rs.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean addEntry(Entity agent, List<TravelExperience> prior, List<TravelExperience> it, String tablePrefix) {
		String agentId = String.valueOf(agent.getId());
		String tableName = tablePrefix + "_tbl_" + agentId;

		if (it.size() == 0) {
			return false;
		}
		// check if table for agent already exists (hopefully saves database
		// overhead)
		boolean tableExists = aIdTableExists.get(tableName) == null ? false : true;

		// track error state to avoid having to nest too many try catch
		// statements
		boolean error = false;

		// connection and statement for database query
		Statement stmt = null;
		Connection con = null;
		String stmtString = "";
		
		try {

			// get connection
			con = DSFactory.getConnection();
			stmt = con.createStatement();

			// create a new table for an agent representing his EvoKnowledge if it doesnt exist already
			if (!tableExists) {
				try {
					stmt.execute(String.format(sqlCreateTables, tableName, ((type == DBType.MYSQL) ? "" : tableName)));
				
				} catch (SQLException e) {
					e.printStackTrace();
					stmt.close();
					con.close();
					return false;
				}
				aIdTableExists.put(tableName, true);
			}

			// parse the itinerary and add a line for each entry
			stmtString = String.format(sqlInsertValues, tableName);

			boolean firstSeg = true;
			long prevNodeId = -1;
			double prevDuration = -1;

			for (TravelExperience ex : it) {
				long nodeId = ex.getSegmentId();
				//long start = ex.getStartingTime();
				//long end = ex.getEndTime();
				double duration = ex.getTravelTime();

				stmtString = stmtString.concat(firstSeg ? "" : ",");
				stmtString = stmtString.concat("('" + nodeId + "',");
				// stmtString = stmtString.concat("'" + prevNodeId + "',");
				// stmtString = stmtString.concat(ex.getWeather().getEncoding() + ",");
				// stmtString = stmtString.concat(ex.getWeekday() + ",");
				//stmtString = stmtString.concat(EvoEncoding.getTimeOfDay(ex.getTStart().getHour()) + ",");
				stmtString = stmtString.concat(TType.getEncoding(ex.getTransportationMean()) + ",");
				stmtString = stmtString.concat(duration + ",");
				//stmtString = stmtString.concat(prevDuration + ",");
				stmtString = stmtString.concat(ex.getPublicTransportationFillingLevel() + ",");
				stmtString = stmtString.concat("1) ");
				//		+ ","); // Density = Number of other entities on
								// segment.
				// stmtString = stmtString.concat(String.valueOf(start / 1000) + ",");
				// stmtString = stmtString.concat(String.valueOf(end / 1000) + ")");

				firstSeg = false;
				prevNodeId = nodeId;
				prevDuration = duration;
			}
			stmtString = stmtString.concat(String.format(sqlUpdateOnInsert, tableName));
			stmtString = stmtString.concat(";");

			// System.out.println(stmtString);
			stmt.execute(stmtString);

		} catch (SQLException e) {
			System.out.println(stmtString);
			// e.printStackTrace();
			error = true;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();

			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return !error;
	}

	@Override
	public List<TravelExperience> getPredictedItinerary(Entity agent, List<TravelExperience> it, String tablePrefix) {
		String agentId = String.valueOf(agent.getId());
		String tableName = tablePrefix + "_tbl_" + agentId;

		// connection and statement for database query
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		boolean tableExists = aIdTableExists.get(tableName) == null ? false : true;
		
		if (!tableExists) {
			return it;
		}
		try {

			// get connection
			con = DSFactory.getConnection();
			stmt = con.createStatement();

			// check if user even has evoknowledge
			//rs = stmt.executeQuery(String.format(sqlShowTables, tableName));
			
			//if (!rs.next()) {
			//	rs.close();
			//	return it;
			//}
			String stmt1 = "SELECT %1$s FROM " + tableName + " WHERE nodeId = %2$d";
			String stmt2 = stmt1.concat(" AND modality = %3$d");
			//String stmt3 = stmt2.concat(" AND timeOfDay = %4$d");
			//String stmt4 = stmt3.concat(" AND prevNodeId = %6$d AND (prevttime BETWEEN %7$d AND %8$d)");
			//String stmt4 = stmt3.concat(" AND weekday = %5$d");
			//String stmt5 = stmt4.concat(" AND prevNodeId = %6$d AND (prevttime BETWEEN %7$d AND %8$d)");

			String stmtString = null;
			boolean firstSeg = true;

			long prevNodeId = -1;
			double prevTTime = -1;
			long segmentTStart = 0;

			for (TravelExperience ex : it) {
				
				if (firstSeg) {
					segmentTStart = ex.getStartingTime() / 1000;
				}

				if (ex.isTransient()) {
					continue;
				}
				double predictedTravelTime = ex.getTravelTime();
				double predictedFillLevel = 0.0;
				
				boolean foundMatch = false;

				long nodeId = ex.getSegmentId();
				byte modality = TType.getEncoding(ex.getTransportationMean());
				byte timeOfDay = EvoEncoding.getTimeOfDay(ex.getTStart().getHour());
				// byte weekDay = (byte) ex.getWeekday();
				
				// try the most detailed query first
				if (!foundMatch) {
				//if (!firstSeg && prevTTime != -1) {
					// stmtString = String.format(stmt5, "COUNT(*)", nodeId,
					// modality, timeOfDay, weekDay, prevNodeId,
					// Math.round((double)prevTTime * 0.7),
					// Math.round((double)prevTTime * 1.3));
					// stmtString = String.format(stmt2, "AVG(ttime), AVG(fillLevel)", nodeId,
					//		modality, timeOfDay, prevNodeId,
					//		Math.round((double) prevTTime * 0.7),
					//		Math.round((double) prevTTime * 1.3));
					stmtString = String.format(stmt2, "AVG(ttime), AVG(fillLevel)", nodeId, modality);
					rs = stmt.executeQuery(stmtString);

					if (rs.next() && (rs.getDouble(1) > 0)) {
						// stmtPredStr = String.format(stmt5, "AVG(ttime)",
						// nodeId, modality, timeOfDay, weekDay, prevNodeId,
						// Math.round((double)prevTTime * 0.7),
						// Math.round((double)prevTTime * 1.3));
						predictedTravelTime = rs.getDouble(1);
						predictedFillLevel = rs.getDouble(2);
						foundMatch = true;
					}
					rs.close();
				}

				// Try without previous node
				if (!foundMatch) {
					// stmtString = String.format(stmt4, "COUNT(*)", nodeId,
					// modality, timeOfDay, weekDay);
					//stmtString = String.format(stmt4, "AVG(ttime), AVG(fillLevel)", nodeId,
					//		modality, timeOfDay);
					stmtString = String.format(stmt1, "AVG(ttime), AVG(fillLevel)", nodeId);
					rs = stmt.executeQuery(stmtString);

					if (rs.next() && (rs.getDouble(1) > 0)) {
						// stmtPredStr = String.format(stmt4, "AVG(ttime)",
						// nodeId, modality, timeOfDay, weekDay);
						predictedTravelTime = rs.getDouble(1);
						predictedFillLevel = rs.getDouble(2);
						foundMatch = true;
					}
					rs.close();
				}

				// Try without weekday
				/*if (!foundMatch) {
					// stmtString = String.format(stmt3, "COUNT(*)", nodeId,
					// modality, timeOfDay);
					stmtString = String.format(stmt3, "AVG(ttime), AVG(fillLevel)", nodeId,
							modality, timeOfDay);
					rs = stmt.executeQuery(stmtString);

					if (rs.next() && (rs.getDouble(1) > 0)) { // case matched,
																// estimate
																// ttime
						// stmtPredStr = String.format(stmt3, "AVG(ttime)",
						// nodeId, modality, timeOfDay);
						predictedTravelTime = rs.getDouble(1);
						predictedFillLevel = rs.getDouble(2);
						foundMatch = true;
					}
					rs.close();
				}*/

				// Try without time of day
				/*if (!foundMatch) {
					// stmtString = String.format(stmt2, "COUNT(*)", nodeId,
					// modality);
					stmtString = String.format(stmt2, "AVG(ttime), AVG(fillLevel)", nodeId,
							modality);
					rs = stmt.executeQuery(stmtString);

					if (rs.next() && (rs.getDouble(1) > 0)) { // case matched,
																// estimate
																// ttime
						// stmtPredStr = String.format(stmt2, "AVG(ttime)",
						// nodeId, modality);
						predictedTravelTime = rs.getDouble(1);
						predictedFillLevel = rs.getDouble(2);
						foundMatch = true;
					}
					rs.close();
				}*/

				// try without modality
				/*if (!foundMatch) {
					// stmtString = String.format(stmt1, "COUNT(*)", nodeId);
					stmtString = String.format(stmt1, "AVG(ttime), AVG(fillLevel)", nodeId);
					rs = stmt.executeQuery(stmtString);

					if (rs.next() && (rs.getDouble(1) > 0)) { // case matched,
																// estimate
																// ttime
						// stmtPredStr = String.format(stmt1, "AVG(ttime)",
						// nodeId);
						predictedTravelTime = rs.getDouble(1);
						predictedFillLevel = rs.getDouble(2);
						foundMatch = true;
					}
					rs.close();
				}*/
				// Estimate actual ttime
				if (!foundMatch) {
					// No estimate is possible for this segment
					// maybe do something

				} /*
				 * else { rs = stmt.executeQuery(stmtPredStr);
				 * 
				 * if (rs.next()) { predictedTravelTime = rs.getInt(1); } else {
				 * System.err.println(
				 * "Couldnt estimate despite db entries. Should not happen!"); }
				 * rs.close(); }
				 */
				firstSeg = false;
				prevNodeId = nodeId;
				prevTTime = predictedTravelTime;

				ex.setStartingTime(segmentTStart * 1000);
				segmentTStart = segmentTStart
						+ ((int) predictedTravelTime * 1000);
				ex.setEndTime(segmentTStart * 1000);
				ex.setTravelTime(predictedTravelTime);
				ex.setPublicTransportationFillingLevel(predictedFillLevel);
			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {

			try {
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
				if (rs != null)
					rs.close();

			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
		return it;
	}

	@Override
	public void clean(Entity agend, String tablePrefix) {
		
	}

	@Override
	public boolean exchangeKnowledge(Entity agent1, Entity agent2, String tablePrefix) {
		String agentId1 = String.valueOf(agent1.getId());
		String tableName1 = tablePrefix + "_tbl_" + agentId1;
		boolean tableExists1 = aIdTableExists.get(tableName1) == null ? false : true;
		
		String agentId2 = String.valueOf(agent2.getId());
		String tableName2 = tablePrefix + "_tbl_" + agentId2;
		boolean tableExists2 = aIdTableExists.get(tableName2) == null ? false : true;

		if (!tableExists1 && !tableExists2)
			return false;
		
		Statement stmt = null;
		Connection con = null;
		String stmtString = null;
		String type = null;
		
		try {
			con = DSFactory.getConnection();
			stmt = con.createStatement();
			
			if (tableExists1 && !tableExists2) {
				type = "simple";
				stmtString = String.format(sqlMergeSimple, tableName2, tableName1);
				aIdTableExists.put(tableName2, true);
				stmt.execute(stmtString);
			}

			if (!tableExists1 && tableExists2) {
				type = "simple";
				stmtString = String.format(sqlMergeSimple, tableName1, tableName2);
				aIdTableExists.put(tableName1, true);
				stmt.execute(stmtString);
			}

			if (tableExists1 && tableExists2) {
				type = "mutual";
				stmtString = String.format(sqlMergeMutual, tableName1, tableName2);
				stmt.execute(stmtString);
			}
			
		} catch (SQLException e) {
			//System.out.println(agentId1 + "->" + agentId2 + ": " + type);
			System.out.println(agentId1 + "->" + agentId2 + ": " + e.getMessage());
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
