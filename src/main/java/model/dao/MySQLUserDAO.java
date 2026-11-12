package model.dao;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import model.ModelException;
import model.User;

public class MySQLUserDAO implements UserDAO {

	@Override
	public boolean save(User user) throws ModelException {
		
		DBHandler db = new DBHandler();
		
		String sqlInsert = "INSERT INTO users (nome, sexo, email, senha, role) VALUES "
				+ " (?, ?, ?, ?, ?);";
		
		db.prepareStatement(sqlInsert);
		db.setString(1, user.getName());
		db.setString(2, user.getGender());
		db.setString(3, user.getEmail());
		db.setString(4, user.getPassword()); // já vem como hash SHA-256 do controller
		db.setString(5, user.getRole());
		  
		return db.executeUpdate() > 0;
	}

	@Override
	public boolean update(User user) throws ModelException {
		
		DBHandler db = new DBHandler();
		
		String sqlUpdate = "UPDATE users "
				         	+ "SET nome = ?, "
				         	+ "sexo = ?, "
				         	+ "email = ?, "
				         + "role = ? "
				         + "WHERE id = ?";
		
		
		db.prepareStatement(sqlUpdate);
		
		db.setString(1, user.getName());
		db.setString(2, user.getGender());
		db.setString(3, user.getEmail());
		db.setString(4, user.getRole());
		db.setInt(5, user.getId());
		
		return db.executeUpdate() > 0;
	}

	@Override
	public boolean delete(User user) throws ModelException {
		
		DBHandler db = new DBHandler();
		
		String sqlDelete = " DELETE FROM users "
		                 + " WHERE id = ?;";

		db.prepareStatement(sqlDelete);		
		db.setInt(1, user.getId());
		
		try {
			
			return db.executeUpdate() > 0;
			
		} catch (ModelException e) {
			if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
				return false;
			}
			
			throw e; 
		}
	}

	@Override
	public List<User> listAll() throws ModelException {
	
		DBHandler db = new DBHandler();
		
		List<User> users = new ArrayList<User>();
			
		// Declara um instrução SQL
		String sqlQuery = "SELECT * FROM users";
		
		db.createStatement();
	
		db.executeQuery(sqlQuery);

		while (db.next()) {
			User u = createUser(db);
			users.add(u);
		}
		db.close();
		
		return users;
	}

	@Override
	public User findById(int id) throws ModelException {
		
		DBHandler db = new DBHandler();
				
		String sql = "SELECT * FROM users WHERE id = ?";
		
		db.prepareStatement(sql);
		db.setInt(1, id);
		db.executeQuery();
		
		User u = null;
		while (db.next()) {
			u = createUser(db);
			break;
		}
		db.close();
		
		return u;
	}
	
	// Paginação: LIMIT = quantos registros, OFFSET = quantos pular
	@Override
	public List<User> listPage(int limit, int offset) throws ModelException {
		DBHandler db = new DBHandler();
		
		db.prepareStatement("SELECT * FROM users ORDER BY nome, id LIMIT ? OFFSET ?;");
		db.setInt(1, limit);
		db.setInt(2, offset);
		db.executeQuery();
		
		List<User> users = new ArrayList<User>();
		while (db.next()) {
			users.add(createUser(db));
		}
		db.close();
		
		return users;
	}

	@Override
	public int count() throws ModelException {
		DBHandler db = new DBHandler();
		
		db.createStatement();
		db.executeQuery("SELECT COUNT(*) AS total FROM users;");
		
		int total = db.next() ? db.getInt("total") : 0;
		db.close();
		
		return total;
	}

	@Override
	public List<User> listByRole(String role) throws ModelException {
		DBHandler db = new DBHandler();
		
		db.prepareStatement("SELECT * FROM users WHERE role = ? ORDER BY nome, id;");
		db.setString(1, role);
		db.executeQuery();
		
		List<User> users = new ArrayList<User>();
		while (db.next()) {
			users.add(createUser(db));
		}
		db.close();
		
		return users;
	}

	@Override
	public List<User> listStaff() throws ModelException {
		DBHandler db = new DBHandler();
		
		db.createStatement();
		db.executeQuery("SELECT * FROM users WHERE role IN ('CHEFE', 'FUNCIONARIO') ORDER BY nome, id;");
		
		List<User> users = new ArrayList<User>();
		while (db.next()) {
			users.add(createUser(db));
		}
		db.close();
		
		return users;
	}

	// Login: procura um usuário com este e-mail E este hash de senha
	@Override
	public User authenticate(String email, String passwordHash) throws ModelException {
		DBHandler db = new DBHandler();
		
		db.prepareStatement("SELECT * FROM users WHERE email = ? AND senha = ?;");
		db.setString(1, email);
		db.setString(2, passwordHash);
		db.executeQuery();
		
		User u = db.next() ? createUser(db) : null;
		db.close();
		
		return u;
	}
	
	private User createUser(DBHandler db) throws ModelException {
		User u = new User(db.getInt("id"));
		u.setName(db.getString("nome"));
		u.setGender(db.getString("sexo"));
		u.setEmail(db.getString("email"));
		u.setPassword(db.getString("senha"));
		u.setRole(db.getString("role"));
		
		return u;
	}
}
