package model.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Employee;
import model.ModelException;

public class MySQLEmployeeDAO implements EmployeeDAO {

	private static final String SELECT_BASE =
			"SELECT e.* FROM employees e INNER JOIN users u ON u.id = e.user_id ";

	@Override
	public boolean save(Employee e) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("INSERT INTO employees (user_id, job_title, specialty, phone, hire_date, shift, active) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?);");

		db.setInt(1, e.getUser().getId());
		db.setString(2, e.getJobTitle());
		db.setString(3, e.getSpecialty());
		db.setString(4, e.getPhone());
		db.setDate(5, e.getHireDate() == null ? new Date() : e.getHireDate());
		db.setString(6, e.getShift());
		db.setBoolean(7, e.isActive());

		return db.executeUpdate() > 0;
	}

	@Override
	public boolean update(Employee e) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("UPDATE employees SET user_id = ?, job_title = ?, specialty = ?, phone = ?, "
				+ "hire_date = ?, shift = ?, active = ? WHERE id = ?;");

		db.setInt(1, e.getUser().getId());
		db.setString(2, e.getJobTitle());
		db.setString(3, e.getSpecialty());
		db.setString(4, e.getPhone());
		db.setDate(5, e.getHireDate() == null ? new Date() : e.getHireDate());
		db.setString(6, e.getShift());
		db.setBoolean(7, e.isActive());
		db.setInt(8, e.getId());

		return db.executeUpdate() > 0;
	}

	@Override
	public boolean delete(Employee e) throws ModelException {
		DBHandler db = new DBHandler();

		// services.employee_id tem ON DELETE SET NULL: os serviços ficam, sem responsável
		db.prepareStatement("DELETE FROM employees WHERE id = ?;");
		db.setInt(1, e.getId());

		return db.executeUpdate() > 0;
	}

	@Override
	public List<Employee> listAll() throws ModelException {
		DBHandler db = new DBHandler();

		db.createStatement();
		db.executeQuery(SELECT_BASE + "ORDER BY u.nome, e.id;");

		List<Employee> employees = new ArrayList<Employee>();
		while (db.next()) {
			employees.add(createEmployee(db));
		}
		db.close();

		return employees;
	}

	// Paginação + serviço atual de cada funcionário da página
	@Override
	public List<Employee> listPage(int limit, int offset) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement(SELECT_BASE + "ORDER BY u.nome, e.id LIMIT ? OFFSET ?;");
		db.setInt(1, limit);
		db.setInt(2, offset);
		db.executeQuery();

		List<Employee> employees = new ArrayList<Employee>();
		while (db.next()) {
			employees.add(createEmployee(db));
		}
		db.close();

		// Depois de fechar a consulta principal, busca o serviço em andamento de cada um.
		// (findById NÃO busca o serviço atual: assim não existe ciclo Employee <-> Service.)
		ServiceDAO serviceDAO = DAOFactory.createDAO(ServiceDAO.class);
		for (Employee e : employees) {
			e.setCurrentService(serviceDAO.findCurrentByEmployee(e.getId()));
		}

		return employees;
	}

	@Override
	public int count() throws ModelException {
		DBHandler db = new DBHandler();

		db.createStatement();
		db.executeQuery("SELECT COUNT(*) AS total FROM employees;");

		int total = db.next() ? db.getInt("total") : 0;
		db.close();

		return total;
	}

	@Override
	public Employee findById(int id) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("SELECT * FROM employees WHERE id = ?;");
		db.setInt(1, id);
		db.executeQuery();

		Employee e = db.next() ? createEmployee(db) : null;
		db.close();

		return e;
	}

	private Employee createEmployee(DBHandler db) throws ModelException {
		Employee e = new Employee(db.getInt("id"));
		e.setJobTitle(db.getString("job_title"));
		e.setSpecialty(db.getString("specialty"));
		e.setPhone(db.getString("phone"));
		e.setHireDate(db.getDate("hire_date"));
		e.setShift(db.getString("shift"));
		e.setActive(db.getBoolean("active"));

		UserDAO userDAO = DAOFactory.createDAO(UserDAO.class);
		e.setUser(userDAO.findById(db.getInt("user_id")));

		return e;
	}
}
