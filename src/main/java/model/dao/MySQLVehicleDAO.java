package model.dao;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import model.ModelException;
import model.Vehicle;

public class MySQLVehicleDAO implements VehicleDAO {

	@Override
	public boolean save(Vehicle v) throws ModelException {
		DBHandler db = new DBHandler();

		// A ordem dos "?" segue a ordem das colunas da tabela vehicles
		db.prepareStatement("INSERT INTO vehicles VALUES (DEFAULT, ?, ?, ?, ?, ?, ?);");

		db.setString(1, v.getModel());
		db.setInt(2, v.getModelYear());
		db.setString(3, v.getColor());
		db.setString(4, v.getCategory());
		db.setBoolean(5, v.isCeramicCoating());
		db.setInt(6, v.getUser().getId()); // FK: só o id do proprietário vai para o banco

		return db.executeUpdate() > 0;
	}

	@Override
	public boolean update(Vehicle v) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("UPDATE vehicles SET model = ?, model_year = ?, color = ?, "
				+ "category = ?, ceramic_coating = ?, user_id = ? WHERE id = ?;");

		db.setString(1, v.getModel());
		db.setInt(2, v.getModelYear());
		db.setString(3, v.getColor());
		db.setString(4, v.getCategory());
		db.setBoolean(5, v.isCeramicCoating());
		db.setInt(6, v.getUser().getId());
		db.setInt(7, v.getId());

		return db.executeUpdate() > 0;
	}

	@Override
	public boolean delete(Vehicle v) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("DELETE FROM vehicles WHERE id = ?;");
		db.setInt(1, v.getId());

		try {
			return db.executeUpdate() > 0;
		} catch (ModelException e) {
			// Veículo que já tem serviços não pode ser excluído (violaria a FK):
			// devolvemos false e o controller mostra uma mensagem amigável.
			if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
				return false;
			}

			throw e;
		}
	}

	/**
	 * Lista uma "página" de veículos.
	 * LIMIT = quantos registros trazer; OFFSET = quantos pular.
	 * Sempre usar ORDER BY junto, senão a ordem entre as páginas não é garantida.
	 */
	@Override
	public List<Vehicle> listPage(int limit, int offset) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("SELECT * FROM vehicles ORDER BY model, id LIMIT ? OFFSET ?;");
		db.setInt(1, limit);
		db.setInt(2, offset);
		db.executeQuery();

		List<Vehicle> vehicles = new ArrayList<Vehicle>();
		while (db.next()) {
			vehicles.add(createVehicle(db));
		}
		db.close();

		return vehicles;
	}

	// Sem paginação: usado para preencher o select do formulário de Serviços
	@Override
	public List<Vehicle> listAll() throws ModelException {
		return listPage(Integer.MAX_VALUE, 0);
	}

	@Override
	public int count() throws ModelException {
		DBHandler db = new DBHandler();

		db.createStatement();
		db.executeQuery("SELECT COUNT(*) AS total FROM vehicles;");

		int total = db.next() ? db.getInt("total") : 0;
		db.close();

		return total;
	}

	@Override
	public Vehicle findById(int id) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("SELECT * FROM vehicles WHERE id = ?;");
		db.setInt(1, id);
		db.executeQuery();

		Vehicle v = db.next() ? createVehicle(db) : null;
		db.close();

		return v;
	}

	// Converte a linha atual do ResultSet em um objeto Vehicle
	private Vehicle createVehicle(DBHandler db) throws ModelException {
		Vehicle v = new Vehicle(db.getInt("id"));
		v.setModel(db.getString("model"));
		v.setModelYear(db.getInt("model_year"));
		v.setColor(db.getString("color"));
		v.setCategory(db.getString("category"));
		v.setCeramicCoating(db.getBoolean("ceramic_coating"));

		// A FK vira um objeto User completo (buscado pelo UserDAO)
		UserDAO userDAO = DAOFactory.createDAO(UserDAO.class);
		v.setUser(userDAO.findById(db.getInt("user_id")));

		return v;
	}
}
