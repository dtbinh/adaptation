package allow.simulator.collective.adaptation.ek;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DSFactory {

    private static EKConfiguration config = null;

    public static void init(EKConfiguration config) {
	DSFactory.config = config;
    }

    public static Connection getConnection() throws SQLException {
	Connection conn = DriverManager.getConnection(config.getModelPath() + config.getModelName(), config.getUser(),
		config.getPassword());
	return conn;
    }
}
