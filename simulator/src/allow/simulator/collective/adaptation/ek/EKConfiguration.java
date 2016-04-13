package allow.simulator.collective.adaptation.ek;

public class EKConfiguration {
    // Database URL e.g. jdbc:mysql://localhost:3306/
    private String modelPath;

    // Name of the database, e.g. allowek
    private String modelName;

    // User name for database login if required
    private String user;

    // Password for database login if required
    private String password;

    /**
     * Creates a new instance of an EKConfiguration used to set up the
     * connections to EvoKnowledge database models.
     * 
     * @param modelPath
     *            Database URL e.g. jdbc:mysql://localhost:3306/
     * @param modelName
     *            Name of the database, e.g. allowek
     * @param user
     *            User name for database login if required
     * @param password
     *            Password for database login if required
     */
    public EKConfiguration(String modelPath, String modelName, String user, String password) {
	if ((modelPath == null) || modelPath.equals(""))
	    throw new IllegalArgumentException("Error: modelPath must not be null or empty.");

	if ((modelName == null) || modelName.equals(""))
	    throw new IllegalArgumentException("Error: modelName must not be null or empty.");

	this.modelPath = modelPath;
	this.modelName = modelName;
	this.user = user;
	this.password = password;
    }

    /**
     * Returns the URL to the database to use.
     * 
     * @return URL to the database to use
     */
    public String getModelPath() {
	return modelPath;
    }

    /**
     * Returns the name of the database to use.
     * 
     * @return Name of the database to use
     */
    public String getModelName() {
	return modelName;
    }

    /**
     * Returns the user name for database login if required
     * 
     * @return User name for database login if required
     */
    public String getUser() {
	return user;
    }

    /**
     * Returns the password for database login if required
     * 
     * @return Password for database login if required
     */
    public String getPassword() {
	return password;
    }
}
