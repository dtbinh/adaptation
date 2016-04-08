package allow.simulator.entity.knowledge;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import allow.simulator.core.EvoKnowledgeConfiguration;

public class DSFactory {

	private static EvoKnowledgeConfiguration config = null;
	
	public static void init(EvoKnowledgeConfiguration config) {
		DSFactory.config = config;
	}
	
	public static Connection getConnection() throws SQLException {
		Connection conn = DriverManager.getConnection(config.getModelPath() + config.getModelName() + "?allowMultiQueries=true", config.getUser(), config.getPassword());
        return conn;
    }
	
}
