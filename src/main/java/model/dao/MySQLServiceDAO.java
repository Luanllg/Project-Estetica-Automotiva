package model.dao;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Employee;
import model.ModelException;
import model.Service;

public class MySQLServiceDAO implements ServiceDAO {

	// "rated" = quantos feedbacks o serviço tem (0 ou 1): vira o boolean Service.rated
	private static final String SELECT_BASE =
			"SELECT s.*, (SELECT COUNT(*) FROM feedbacks f WHERE f.service_id = s.id) AS rated "
			+ "FROM services s ";

	private static final String BY_USER =
			"INNER JOIN vehicles v ON v.id = s.vehicle_id WHERE v.user_id = ? ";

	@Override
	public boolean save(Service s) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("INSERT INTO services "
				+ "(service_type, service_date, price, details, status, paid, vehicle_id, employee_id) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?);");

		fillParameters(db, s);

		return db.executeUpdate() > 0;
	}

	@Override
	public boolean update(Service s) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("UPDATE services SET service_type = ?, service_date = ?, price = ?, "
				+ "details = ?, status = ?, paid = ?, vehicle_id = ?, employee_id = ? WHERE id = ?;");

		fillParameters(db, s);
		db.setInt(9, s.getId());

		return db.executeUpdate() > 0;
	}

	// Parâmetros 1 a 8, na mesma ordem no INSERT e no UPDATE
	private void fillParameters(DBHandler db, Service s) throws ModelException {
		db.setString(1, s.getType());
		db.setDate(2, s.getServiceDate() == null ? new Date() : s.getServiceDate());
		db.setDouble(3, s.getPrice());
		db.setString(4, s.getDetails());
		db.setString(5, s.getStatus());
		db.setBoolean(6, s.isPaid());
		db.setInt(7, s.getVehicle().getId());

		if (s.getEmployee() == null)
			db.setNullInt(8);
		else
			db.setInt(8, s.getEmployee().getId());
	}

	@Override
	public boolean delete(Service s) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("DELETE FROM services WHERE id = ?;");
		db.setInt(1, s.getId());

		try {
			return db.executeUpdate() > 0;
		} catch (ModelException e) {
			// Serviço que já recebeu feedback não pode ser excluído (violaria a FK)
			if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
				return false;
			}

			throw e;
		}
	}

	@Override
	public List<Service> listPage(int limit, int offset) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement(SELECT_BASE + "ORDER BY s.service_date DESC, s.id DESC LIMIT ? OFFSET ?;");
		db.setInt(1, limit);
		db.setInt(2, offset);
		db.executeQuery();

		return readList(db);
	}

	@Override
	public List<Service> listAll() throws ModelException {
		return listPage(Integer.MAX_VALUE, 0);
	}

	@Override
	public int count() throws ModelException {
		DBHandler db = new DBHandler();

		db.createStatement();
		db.executeQuery("SELECT COUNT(*) AS total FROM services;");

		int total = db.next() ? db.getInt("total") : 0;
		db.close();

		return total;
	}

	@Override
	public Service findById(int id) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement(SELECT_BASE + "WHERE s.id = ?;");
		db.setInt(1, id);
		db.executeQuery();

		Service s = db.next() ? createService(db) : null;
		db.close();

		return s;
	}

	@Override
	public List<Service> listPageByUser(int userId, int limit, int offset) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement(SELECT_BASE + BY_USER + "ORDER BY s.service_date DESC, s.id DESC LIMIT ? OFFSET ?;");
		db.setInt(1, userId);
		db.setInt(2, limit);
		db.setInt(3, offset);
		db.executeQuery();

		return readList(db);
	}

	@Override
	public int countByUser(int userId) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("SELECT COUNT(*) AS total FROM services s " + BY_USER + ";");
		db.setInt(1, userId);
		db.executeQuery();

		int total = db.next() ? db.getInt("total") : 0;
		db.close();

		return total;
	}

	@Override
	public Service findCurrentByEmployee(int employeeId) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement(SELECT_BASE
				+ "WHERE s.employee_id = ? AND s.status = 'EM_ANDAMENTO' ORDER BY s.id DESC LIMIT 1;");
		db.setInt(1, employeeId);
		db.executeQuery();

		Service s = db.next() ? createService(db) : null;
		db.close();

		return s;
	}

	@Override
	public List<Service> listAvailableForFeedback(int userId) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement(SELECT_BASE + BY_USER
				+ "AND s.status = 'CONCLUIDO' "
				+ "AND NOT EXISTS (SELECT 1 FROM feedbacks f WHERE f.service_id = s.id) "
				+ "ORDER BY s.service_date DESC, s.id DESC;");
		db.setInt(1, userId);
		db.executeQuery();

		return readList(db);
	}

	// Lê todas as linhas, fecha a conexão e devolve a lista
	private List<Service> readList(DBHandler db) throws ModelException {
		List<Service> services = new ArrayList<Service>();
		while (db.next()) {
			services.add(createService(db));
		}
		db.close();

		return services;
	}

	private Service createService(DBHandler db) throws ModelException {
		Service s = new Service(db.getInt("id"));
		s.setType(db.getString("service_type"));
		s.setServiceDate(db.getDate("service_date"));
		s.setPrice(db.getDouble("price"));
		s.setDetails(db.getString("details"));
		s.setStatus(db.getString("status"));
		s.setPaid(db.getBoolean("paid"));
		s.setRated(db.getInt("rated") > 0);

		// A FK vira um objeto Vehicle completo (que por sua vez traz o User)
		VehicleDAO vehicleDAO = DAOFactory.createDAO(VehicleDAO.class);
		s.setVehicle(vehicleDAO.findById(db.getInt("vehicle_id")));

		// employee_id pode ser NULL (serviço ainda sem responsável)
		Integer employeeId = db.getNullableInt("employee_id");
		if (employeeId != null) {
			EmployeeDAO employeeDAO = DAOFactory.createDAO(EmployeeDAO.class);
			Employee e = employeeDAO.findById(employeeId);
			s.setEmployee(e);
		}

		return s;
	}
}
