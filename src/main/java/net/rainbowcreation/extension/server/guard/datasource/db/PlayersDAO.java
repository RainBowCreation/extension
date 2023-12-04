package net.rainbowcreation.extension.server.guard.datasource.db;

import net.rainbowcreation.extension.server.Main;
import net.rainbowcreation.extension.server.model.IPlayer;
import net.rainbowcreation.extension.server.model.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

public class PlayersDAO implements IPlayersDAO<IPlayer> {
  private static final Logger LOGGER = Main.LOGGER;
  
  private final String table;
  
  private final IConnectionFactory connectionFactory;
  
  private final Map<String, String> columns;
  
  private static final String EMAIL_COLUMN = "email";
  
  private static final String USERNAME_COLUMN = "username";
  
  private static final String UUID_COLUMN = "uuid";
  
  private static final String PASSWORD_COLUMN = "password";
  
  private static final String BANNED_COLUMN = "banned";
  
  public PlayersDAO(IConnectionFactory connectionFactory) throws SQLException {
    this("players", connectionFactory);
  }
  
  public PlayersDAO(IConnectionFactory connectionFactory, Map<String, String> columns) throws SQLException {
    this.table = "players";
    this.columns = columns;
    this.connectionFactory = connectionFactory;
    checkTable();
  }
  
  public PlayersDAO(String table, IConnectionFactory connectionFactory) throws SQLException {
    this.table = table;
    this.connectionFactory = connectionFactory;
    this.columns = new HashMap<>();
    this.columns.put("email", "email");
    this.columns.put("banned", "banned");
    this.columns.put("password", "password");
    this.columns.put("username", "realname");
    this.columns.put("uuid", "uuid");
    checkTable();
  }
  
  private void checkTable() throws SQLException {
    try(Connection connection = this.connectionFactory.getConnection(); 
        
        PreparedStatement stmt = connection.prepareStatement(
          String.format("SELECT %s,%s,%s,%s,%s FROM %s", new Object[] { this.columns.getOrDefault("email", "email"), this.columns
              .getOrDefault("banned", "banned"), this.columns
              .getOrDefault("password", "password"), this.columns
              .getOrDefault("username", "username"), this.columns
              .getOrDefault("uuid", "uuid"), this.table }))) {
      stmt.executeQuery();
    } catch (SQLException e) {
      LOGGER.error(e.getMessage());
      throw e;
    } 
  }
  
  public void create(IPlayer player) throws SQLException {
    String query = String.format("INSERT INTO %s(%s, %s, %s, %s) VALUES(?, ?, ?, ?)", new Object[] { this.table, this.columns.get("email"), this.columns
          .get("password"), this.columns
          .get("username"), this.columns
          .get("uuid") });
    try(Connection conn = this.connectionFactory.getConnection(); 
        PreparedStatement stmt = conn.prepareStatement(query)) {
      stmt.setString(1, player.getEmail());
      stmt.setString(2, player.getPassword());
      stmt.setString(3, player.getUsername());
      if (player.getUuid() == null || player.getUuid().length() == 0) {
        stmt.setNull(4, 12);
      } else {
        stmt.setString(4, player.getUuid());
      } 
      stmt.executeUpdate();
    } 
  }
  
  public IPlayer find(IPlayer player) throws SQLException {
    try(Connection conn = this.connectionFactory.getConnection(); 
        
        PreparedStatement stmt = conn.prepareStatement(
          String.format("SELECT * FROM %s WHERE %s = ? OR %s = ?", new Object[] { this.table, this.columns.get("email"), this.columns
              .get("username") }))) {
      stmt.setString(1, player.getEmail());
      stmt.setString(2, player.getUsername());
      ResultSet rs = stmt.executeQuery();
      return (IPlayer)createPlayer(rs);
    } 
  }
  
  private Player createPlayer(ResultSet rs) throws SQLException {
    Player player = null;
    if (rs != null && rs.next()) {
      player = new Player();
      player.setBanned((rs.getInt(this.columns.get("banned")) != 0));
      player.setEmail(rs.getString(this.columns.get("email")));
      player.setPassword(rs.getString(this.columns.get("password")));
      player.setUsername(rs.getString(this.columns.get("username")));
      player.setUuid(rs.getString(this.columns.get("uuid")));
    } 
    if (rs != null)
      rs.close(); 
    return player;
  }
}