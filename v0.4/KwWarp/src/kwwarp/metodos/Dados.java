package kwwarp.metodos;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Dados {

	private Connection conn;
	private Statement stmt;
	private boolean conectado = false;

	private void iniciarConexao(String link) {
		try {
			this.conn = DriverManager.getConnection(link);
			this.stmt = this.conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startConexao(File f) {
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
			}
		}
		try {
			conectado = true;
			Class.forName("org.sqlite.JDBC");
			this.conn = DriverManager.getConnection("jdbc:sqlite:" + f.toString());
			this.stmt = this.conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load(String ip, String database, String usuario, String senha) {
		if (((ip != null) && (!ip.equalsIgnoreCase(""))) && ((database != null) && (!database.equalsIgnoreCase("")))
				&& ((usuario != null) && (!usuario.equalsIgnoreCase("")))
				&& ((senha != null) && (!senha.equalsIgnoreCase("")))) {
			conectado = true;
			iniciarConexao("jdbc:mysql://" + ip + "/" + database + "?" + "user=" + usuario + "&password=" + senha);
			return;
		}
	}

	public void update(String cmd) {
		if (conectado) {
			try {
				this.stmt.executeUpdate(cmd);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		try {
			this.stmt.close();
			this.conn.close();
		} catch (Exception localException) {
		}
	}

	public boolean isConnected() {
		if (conectado) {
			try {
				return (this.stmt != null) && (this.conn != null) && (!this.stmt.isClosed()) && (!this.conn.isClosed());
			} catch (Exception localException) {
			}
		}
		return false;
	}

	public PreparedStatement ps(String linha) throws SQLException {
		return conn.prepareStatement(linha);
	}

	public Connection getConexao() {
		return this.conn;
	}

}
