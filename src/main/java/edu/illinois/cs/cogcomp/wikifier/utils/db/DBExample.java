package edu.illinois.cs.cogcomp.wikifier.utils.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.illinois.cs.cogcomp.wikifier.utils.db.DBHelper.Column;

/**
 * Running this will create a db for you at the location specified by dbFile
 * Once you are done populating, you can view the db by connecting to it by
 * running java -cp target/dependency/h2-1.4.182.jar org.h2.tools.Server -web
 * -webPort 9090 and connecting to the dbURL
 * 
 * @author upadhya3
 *
 */
public class DBExample {
	private String dbFile;
	private String tableName;
	private static PreparedStatement insertSt;
	private static final Logger logger = LoggerFactory
			.getLogger(DBExample.class);

	private static DBExample instance;

	public static DBExample getInstance(String dbFile, String tableName) {
		if (instance == null) {
			instance = new DBExample(dbFile, tableName);

		}
		return instance;
	}

	private DBExample(String dbFile, String tableName) {
		this.dbFile = dbFile;
		this.tableName = tableName;
		logger.info("Checking for database at " + dbFile);
		boolean create = DBHelper.dbFileExists(dbFile);
		logger.info("cache {} found", create ? "not " : "");

		DBHelper.initializeConnection(dbFile);

		if (create)
			createDatabase();

		try {
			setupPreparedStatements();
		} catch (Exception ex) {
			logger.error("Unable to prepare SQL statements", ex);
		}

	}

	private void setupPreparedStatements() throws SQLException {
		Connection connection = DBHelper.getConnection(dbFile);
		prepareInsert(connection);

	}

	private void prepareInsert(Connection connection) throws SQLException {
		String insert = "insert into "
				+ tableName
				+ " (task, dataset, input, numVars, constraintsId, solutionId) "
				+ "values (?, ?, ?, ?, ?, ?)";

		insertSt = connection.prepareStatement(insert);
	}

	private void createDatabase() {
		try {
			logger.info("Creating ILP cache database at " + dbFile);

			List<Column> columns = new ArrayList<DBHelper.Column>();
			columns.add(new Column("task int not null", true));
			columns.add(new Column("dataset int not null", true));
			columns.add(new Column("input int not null", true));
			columns.add(new Column("numVars int not null", false));
			columns.add(new Column("constraintsId int not null", true));
			columns.add(new Column("solutionId int not null", false));

			DBHelper.createTable(dbFile, tableName, columns);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void addItem(SomeItem item) throws SQLException {
		insertSt.clearParameters();
		insertSt.setInt(1, item.task);
		insertSt.setInt(2, item.dataset);
		insertSt.setInt(3, item.input);
		insertSt.setInt(4, item.numVars);
		insertSt.setInt(5, item.constraintsId);
		insertSt.setInt(6, item.solutionId);
		insertSt.executeUpdate();

	}

	public static void main(String[] args) throws SQLException {
		DBExample db = DBExample.getInstance("/scratch/upadhya3/database",
				"myTable");
		Random random = new Random(0);
		for (int i = 0; i < 10; i++)
			db.addItem(new SomeItem(random.nextInt(), random.nextInt(), random
					.nextInt(), random.nextInt(), random.nextInt(), random
					.nextInt()));
	}
}
