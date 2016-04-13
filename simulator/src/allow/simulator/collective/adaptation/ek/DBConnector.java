package allow.simulator.collective.adaptation.ek;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DBConnector {

    public enum DBType {

	MYSQL,

	POSTGRE;
    }

    private static boolean initialized = false;
    private static String prefix = null;
    private static EKConfiguration config;
    private static DBKnowledgeModel model;
    private static DBType dbType;

    private static void initMySQL(boolean deletePrevious) {
	Connection con = null, con2 = null;
	Statement stmt = null, stmt2 = null;
	ResultSet queries = null;

	try {
	    // Init driver
	    Class.forName("com.mysql.jdbc.Driver");

	    // Creating database
	    con = DriverManager.getConnection(config.getModelPath(), config.getUser(), config.getPassword());
	    stmt = con.createStatement();
	    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + config.getModelName());
	    queries = stmt.executeQuery("SELECT CONCAT(\"DROP TABLE \", table_name, \";\") "
		    + "FROM information_schema.tables WHERE table_schema = \"" + config.getModelName() + "\" "
		    + "AND table_name LIKE \"" + prefix + "%\";");

	    // Deleting previous tables
	    if (deletePrevious) {
		con2 = DriverManager.getConnection(config.getModelPath() + config.getModelName(), config.getUser(),
			config.getPassword());
		stmt2 = con2.createStatement();

		while (queries.next()) {
		    stmt2.executeUpdate(queries.getString(1));
		}
		stmt2.executeUpdate("DROP TABLE IF EXISTS " + prefix + "_tbl_global;");
	    }

	} catch (SQLException e) {
	    e.printStackTrace();

	} catch (ClassNotFoundException e) {
	    e.printStackTrace();

	} finally {

	    try {
		if (stmt != null)
		    stmt.close();
		if (con != null)
		    con.close();
		if (stmt2 != null)
		    stmt2.close();
		if (con2 != null)
		    con2.close();
		if (queries != null)
		    queries.close();

	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
    }

    private static void initPostgre(boolean deletePrevious) {
	Connection con = null;
	Statement stmt = null;

	try {
	    Class.forName("org.postgresql.Driver");

	    // Reset tables if they exist.
	    con = DriverManager.getConnection(config.getModelPath() + config.getModelName(), config.getUser(),
		    config.getPassword());
	    stmt = con.createStatement();

	    if (deletePrevious)
		stmt.executeUpdate("DROP TABLE IF EXISTS " + prefix + "_tbl_global;");

	} catch (SQLException e) {
	    e.printStackTrace();

	} catch (ClassNotFoundException e) {
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

    private static void initDatabase(boolean deletePrevious) {

	if (config.getModelPath().contains("mysql")) {
	    dbType = DBType.MYSQL;
	    initMySQL(deletePrevious);

	} else if (config.getModelPath().contains("postgres")) {
	    dbType = DBType.POSTGRE;
	    initPostgre(deletePrevious);

	} else {
	    throw new IllegalArgumentException("Error: Unknown database driver.");
	}
	System.out.println("EvoKnowledge database connector initialized.");
    }

    public static void init(EKConfiguration config, String prefix, boolean deletePrevious) {
	DBConnector.prefix = prefix;
	DBConnector.config = config;
	DSFactory.init(config);
	initDatabase(deletePrevious);
	model = new DBGlobalKnowledge(dbType);
	initialized = true;
    }

    public static boolean isInitialized() {
	return initialized;
    }

    public static boolean addEntry(String agentId, List<Experience> parameter) {
	return model.addEntry(agentId, parameter, prefix);
    }

    public static List<Prediction> getPredictedParameters(String agentId, Query query) {
	return model.getPredictedParameters(agentId, query, prefix);
    }

    public static void cleanModel(String agentId) {
	model.clean(agentId, prefix);
    }
}