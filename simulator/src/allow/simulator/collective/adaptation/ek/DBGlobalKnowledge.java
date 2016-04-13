package allow.simulator.collective.adaptation.ek;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import allow.simulator.collective.adaptation.ek.DBConnector.DBType;

public class DBGlobalKnowledge implements DBKnowledgeModel {
    // Dictionary holding tables which have been
    private static ConcurrentHashMap<String, Boolean> aIdTableExists = new ConcurrentHashMap<String, Boolean>();
    private static final String GLOBAL_TABLE_NAME = "global";

    private static final String MY_SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %1$s "
	    + "(entryNo INTEGER UNSIGNED AUTO_INCREMENT PRIMARY KEY, issueType INT UNSIGNED, passengersWaiting INT UNSIGNED, "
	    + "passengers INT UNSIGNED, "
	    // + "weekday TINYINT UNSIGNED, timeOfDay TINYINT UNSIGNED, modality
	    // TINYINT UNSIGNED,"
	    + "solver INT UNSIGNED, utility FLOAT, "
	    + "INDEX(issueType, " /* modality, timeOfDay, weekday, */ + "passengersWaiting, passengers));%2$s";

    private static final String POSTGRE_SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS %1$s "
	    + " (entryNo SERIAL PRIMARY KEY, issueType INTEGER, passengersWaiting INTEGER, passengers INTEGER, "
	    // + "weekday SMALLINT, timeOfDay SMALLINT, modality SMALLINT, "
	    + "solver INTEGER, utility REAL); CREATE INDEX on %2$s "
	    + "(issueType, " /* modality, timeOfDay, weekday, */ + "passengersWaiting, passengers)";

    private static final String MY_SQL_SHOW_TABLES = "SHOW TABLES LIKE '%1$s'";

    private static final String POSTGRE_SQL_SHOW_TABLES = "SELECT * FROM pg_catalog.pg_tables where "
	    + "tablename like '%1s'";

    private static final String SQL_INSERT_VALUES = "INSERT INTO %1$s " + " (issueType, passengersWaiting, passengers, "
    // + "weekday, timeOfDay, modality, "
	    + "solver, utility) VALUES ";

    private DBType type;
    private String sqlCreateTables;
    private String sqlShowTables;
    private String sqlInsertValues;

    public DBGlobalKnowledge(DBType type) {
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

    @Override
    public boolean addEntry(String agentId, List<Experience> experiences, String tablePrefix) {
	// Check if table for agent already exists (hopefully saves database
	// overhead)
	boolean tableExists = aIdTableExists.get(GLOBAL_TABLE_NAME) == null ? false : true;

	// track error state to avoid having to nest too many try catch
	// statements
	boolean error = false;

	// connection and statement for database query
	Statement stmt = null;
	Connection con = null;
	String stmtString = "";
	String tableName = tablePrefix + "_tbl_" + GLOBAL_TABLE_NAME;

	try {

	    // get connection
	    con = DSFactory.getConnection();
	    stmt = con.createStatement();

	    // create a new table for an agent representing his EvoKnowledge if
	    // it doesnt exist already
	    if (!tableExists) {
		try {
		    System.out.println(
			    String.format(sqlCreateTables, tableName, ((type == DBType.MYSQL) ? "" : tableName)));
		    stmt.execute(String.format(sqlCreateTables, tableName, ((type == DBType.MYSQL) ? "" : tableName)));

		} catch (SQLException e) {
		    e.printStackTrace();
		    stmt.close();
		    con.close();
		    return false;
		}
		aIdTableExists.put(GLOBAL_TABLE_NAME, true);
	    }

	    // parse the itinerary and add a line for each entry
	    stmtString = String.format(sqlInsertValues, tableName);
	    boolean first = true;

	    for (Experience ex : experiences) {
		stmtString = stmtString.concat(first ? "" : ",");
		stmtString = stmtString.concat("('" + ex.getIssueType() + "',");
		stmtString = stmtString.concat("'" + ex.getNumberOfPassengersWaiting() + "',");
		stmtString = stmtString.concat(ex.getNumberOfPassenger() + ",");
		// stmtString = stmtString.concat(ex.getWeekday() + ",");
		// stmtString = stmtString.concat(ex.getTimeOfDay() + ",");
		// stmtString = stmtString.concat(ex.getModality() + ",");
		stmtString = stmtString.concat(ex.getSolverType() + ",");
		stmtString = stmtString.concat(ex.getActualUtility() + ")");
		first = false;
	    }
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
    public List<Prediction> getPredictedParameters(String agentId, Query query, String tablePrefix) {
	// connection and statement for database query
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String tableName = tablePrefix + "_tbl_" + GLOBAL_TABLE_NAME;

	// Collects all possible solvers
	Map<Integer, Prediction> possibleSolvers = new HashMap<Integer, Prediction>();

	try {

	    // get connection
	    con = DSFactory.getConnection();
	    stmt = con.createStatement();

	    // check if user even has evoknowledge
	    rs = stmt.executeQuery(String.format(sqlShowTables, tableName));

	    if (!rs.next()) {
		rs.close();
		return new ArrayList<Prediction>(0);
	    }
	    String stmt1 = "SELECT %1$s FROM " + tablePrefix + "_tbl_" + GLOBAL_TABLE_NAME + " WHERE issueType = %2$d";
	    String stmt2 = stmt1.concat(" AND passengers=%3$d");
	    String stmt3 = stmt2.concat(" AND passengersWaiting=%4$d");
	    // String stmt2 = stmt1.concat(" AND passengers BETWEEN %3$d AND
	    // %4$d");
	    // String stmt3 = stmt2.concat(" AND passengersWaiting BETWEEN %5$d
	    // AND %6$d");
	    // String stmt4 = stmt3.concat(" AND weekday = %5$d");
	    // String stmt5 = stmt4.concat(" AND timeofday = %6$d");
	    // String stmt6 = stmt5.concat(" AND modality = %7$d)");

	    // int minPassengers = Math.max(0, query.getNumberOfPassenger() -
	    // 5);
	    // int maxPassengers = query.getNumberOfPassenger() + 5;
	    // int minPassengersWaiting = Math.max(0,
	    // query.getNumberOfPassengersWaiting() - 5);
	    // int maxPassengersWaiting = query.getNumberOfPassengersWaiting() +
	    // 5;

	    // Most detailed query.
	    /*
	     * String stmtString = String.format(stmt5 + " GROUP BY solver;",
	     * "solver, AVG(utility)", query.getIssueType(), minPassengers,
	     * maxPassengers, minPassengersWaiting, maxPassengersWaiting,
	     * query.getWeekday(), query.getTimeOfDay()); rs =
	     * stmt.executeQuery(stmtString);
	     * 
	     * while (rs.next()) { int solver = rs.getInt(1); double utility =
	     * rs.getDouble(2); possibleSolvers.put(solver, new
	     * Prediction(solver, utility, 5)); } rs.close();
	     */

	    // Second query without time of day.
	    /*
	     * stmtString = String.format(stmt4 + " GROUP BY solver;",
	     * "solver, AVG(utility)", query.getIssueType(), minPassengers,
	     * maxPassengers, minPassengersWaiting, maxPassengersWaiting,
	     * query.getWeekday()); rs = stmt.executeQuery(stmtString);
	     * 
	     * while (rs.next()) { int solver = rs.getInt(1); double utility =
	     * rs.getDouble(2);
	     * 
	     * if (!possibleSolvers.containsKey(solver))
	     * possibleSolvers.put(solver, new Prediction(solver, utility, 4));
	     * } rs.close();
	     */

	    // Third query without time of day and weekday
	    String stmtString = String.format(stmt3 + " GROUP BY solver;", "solver, AVG(utility)", query.getIssueType(),
		    query.getNumberOfPassengers(), query.getNumberOfPassengersWaiting());
	    rs = stmt.executeQuery(stmtString);

	    while (rs.next()) {
		int solver = rs.getInt(1);
		double utility = rs.getDouble(2);

		if (!possibleSolvers.containsKey(solver))
		    possibleSolvers.put(solver, new Prediction(solver, utility, 3));
	    }
	    rs.close();

	    // Forth query without time of day, weekday, and number of waiting
	    // passengers
	    stmtString = String.format(stmt2 + " GROUP BY solver;", "solver, AVG(utility)", query.getIssueType(),
		    query.getNumberOfPassengers());
	    rs = stmt.executeQuery(stmtString);

	    while (rs.next()) {
		int solver = rs.getInt(1);
		double utility = rs.getDouble(2);

		if (!possibleSolvers.containsKey(solver))
		    possibleSolvers.put(solver, new Prediction(solver, utility, 2));
	    }
	    rs.close();

	    // Forth query without time of day, weekday, and number of waiting
	    // passengers
	    stmtString = String.format(stmt1 + " GROUP BY solver;", "solver, AVG(utility)", query.getIssueType());
	    rs = stmt.executeQuery(stmtString);

	    while (rs.next()) {
		int solver = rs.getInt(1);
		double utility = rs.getDouble(2);

		if (!possibleSolvers.containsKey(solver))
		    possibleSolvers.put(solver, new Prediction(solver, utility, 1));
	    }
	    rs.close();

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
	return new ArrayList<Prediction>(possibleSolvers.values());
    }

    @Override
    public void clean(String agentId, String tablePrefix) {
	if (aIdTableExists.get(GLOBAL_TABLE_NAME) == null) {
	    return;
	}

	// connection and statement for database query
	Statement stmt = null;
	Connection con = null;
	String tableName = tablePrefix + "_tbl_" + GLOBAL_TABLE_NAME;

	try {
	    // get connection
	    con = DSFactory.getConnection();
	    stmt = con.createStatement();

	    String stmtString = "DELETE FROM " + tableName + ";";
	    stmt.executeUpdate(stmtString);

	} catch (SQLException e) {
	    e.printStackTrace();

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
    }
}
