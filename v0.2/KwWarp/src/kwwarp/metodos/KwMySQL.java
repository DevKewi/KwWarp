package kwwarp.metodos;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class KwMySQL {

	private Connection conn;
	private File file;
	private Statement stmt;

	private KwMySQL(File f) {
		file = f;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + file);
			stmt = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private KwMySQL(String urlconn) {
		try {
			conn = DriverManager.getConnection(urlconn);
			stmt = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static KwMySQL load(File f) {
		return new KwMySQL(f);
	}

	public static KwMySQL load(String f) {
		return new KwMySQL(new File(f));
	}

	public static KwMySQL load(String host, String database, String user, String pass) {
		return new KwMySQL("jdbc:mysql://" + host + "/" + database + "?" + "user=" + user + "&password=" + pass);
	}

	public void update(String q) {
		try {
			stmt.executeUpdate(q);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ResultSet query(String q) {
		try {
			return stmt.executeQuery(q);
		} catch (Exception e) {
		}
		return null;
	}

	public void close() {
		try {
			stmt.close();
			conn.close();
		} catch (Exception e) {
		}
	}

	public boolean isConnected() {
		try {
			return stmt != null && conn != null && !stmt.isClosed() && !conn.isClosed();
		} catch (Exception e) {
			KwMetodos.getErro(false, false, false, null);
		}
		return false;
	}

	public Connection getConnection() {
		return conn;
	}

}