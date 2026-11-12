package controller;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Employee;
import model.ModelException;
import model.User;
import model.dao.DAOFactory;
import model.dao.EmployeeDAO;
import model.dao.UserDAO;

/**
 * Aba "Equipe" (antiga "Empresas"): funcionários e o serviço que cada um executa.
 *
 *   GET  /employees        -> lista (CHEFE e FUNCIONARIO, só consulta)
 *   GET  /employee/form    -> formulário vazio        (só CHEFE)
 *   GET  /employee/update  -> formulário preenchido   (só CHEFE)
 *   POST /employee/insert | update | delete           (só CHEFE)
 * As permissões ficam no AuthenticationFilter.
 */
@WebServlet(urlPatterns = { "/employees", "/employee/form", "/employee/insert", "/employee/update", "/employee/delete" })
public class EmployeesController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = ControllerUtil.getAction(req);

		switch (action) {
		case "/employee/form": {
			CommonsController.listStaffUsers(req); // alimenta o select de usuários da equipe
			req.setAttribute("action", "insert");
			ControllerUtil.forward(req, resp, "/form-employee.jsp");
			break;
		}
		case "/employee/update": {
			Employee employee = loadEmployee(req);

			if (employee == null) {
				ControllerUtil.errorMessage(req, "Funcionário não encontrado para alteração.");
				ControllerUtil.redirect(resp, req.getContextPath() + "/employees");
				return;
			}

			CommonsController.listStaffUsers(req);
			req.setAttribute("action", "update");
			req.setAttribute("employee", employee);
			ControllerUtil.forward(req, resp, "/form-employee.jsp");
			break;
		}
		default:
			listEmployees(req);

			ControllerUtil.transferSessionMessagesToRequest(req);

			ControllerUtil.forward(req, resp, "/employees.jsp");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = ControllerUtil.getAction(req);

		switch (action) {
		case "/employee/insert":
			insertEmployee(req);
			break;
		case "/employee/update":
			updateEmployee(req);
			break;
		case "/employee/delete":
			deleteEmployee(req);
			break;
		default:
			System.out.println("URL inválida " + action);
		}

		ControllerUtil.redirect(resp, req.getContextPath() + "/employees");
	}

	// Lista paginada: cada funcionário vem com o serviço em andamento (se houver)
	private void listEmployees(HttpServletRequest req) {
		EmployeeDAO dao = DAOFactory.createDAO(EmployeeDAO.class);

		try {
			int offset = ControllerUtil.preparePagination(req, dao.count());

			List<Employee> employees = dao.listPage(ControllerUtil.PAGE_SIZE, offset);
			req.setAttribute("employees", employees);
		} catch (ModelException e) {
			e.printStackTrace();
		}
	}

	private Employee loadEmployee(HttpServletRequest req) {
		try {
			int id = Integer.parseInt(req.getParameter("employeeId"));

			EmployeeDAO dao = DAOFactory.createDAO(EmployeeDAO.class);
			return dao.findById(id);
		} catch (NumberFormatException | ModelException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Lê os campos do formulário. Devolve null (e já registra a mensagem) se algo for inválido.
	private Employee readForm(HttpServletRequest req, int id) throws ModelException {
		Employee e = new Employee(id);
		e.setJobTitle(req.getParameter("jobTitle"));                                // input text
		e.setSpecialty(req.getParameter("specialty"));                              // select
		e.setPhone(req.getParameter("phone"));                                      // input tel
		e.setHireDate(ControllerUtil.formatDate(req.getParameter("hireDate")));     // input date
		e.setShift(req.getParameter("shift"));                                      // input radio
		e.setActive(req.getParameter("active") != null);                            // input checkbox

		if (!Employee.isValidSpecialty(e.getSpecialty()) || !Employee.isValidShift(e.getShift())
				|| e.getHireDate() == null) {
			ControllerUtil.errorMessage(req, "Dados inválidos no formulário.");
			return null;
		}

		// select (FK): o usuário precisa existir e ser da equipe (CHEFE ou FUNCIONARIO)
		UserDAO userDAO = DAOFactory.createDAO(UserDAO.class);
		User user = userDAO.findById(Integer.parseInt(req.getParameter("user")));

		if (user == null || !user.isStaff()) {
			ControllerUtil.errorMessage(req, "Escolha um usuário com perfil de Chefe ou Funcionário.");
			return null;
		}

		e.setUser(user);
		return e;
	}

	private void insertEmployee(HttpServletRequest req) {
		EmployeeDAO dao = DAOFactory.createDAO(EmployeeDAO.class);

		try {
			Employee e = readForm(req, 0);
			if (e == null)
				return;

			if (dao.save(e)) {
				ControllerUtil.sucessMessage(req, "'" + e.getUser().getName() + "' adicionado à equipe.");
			} else {
				ControllerUtil.errorMessage(req, "Funcionário não pode ser salvo.");
			}
		} catch (NumberFormatException ex) {
			ControllerUtil.errorMessage(req, "Dados inválidos no formulário.");
		} catch (ModelException ex) {
			ex.printStackTrace();
			if (ex.getCause() instanceof SQLIntegrityConstraintViolationException)
				ControllerUtil.errorMessage(req, "Este usuário já faz parte da equipe.");
			else
				ControllerUtil.errorMessage(req, ex.getMessage());
		}
	}

	private void updateEmployee(HttpServletRequest req) {
		EmployeeDAO dao = DAOFactory.createDAO(EmployeeDAO.class);

		try {
			int id = Integer.parseInt(req.getParameter("employeeId")); // campo hidden do form
			Employee e = readForm(req, id);
			if (e == null)
				return;

			if (dao.update(e)) {
				ControllerUtil.sucessMessage(req, "Dados de '" + e.getUser().getName() + "' atualizados com sucesso.");
			} else {
				ControllerUtil.errorMessage(req, "Funcionário não pode ser atualizado.");
			}
		} catch (NumberFormatException ex) {
			ControllerUtil.errorMessage(req, "Dados inválidos no formulário.");
		} catch (ModelException ex) {
			ex.printStackTrace();
			if (ex.getCause() instanceof SQLIntegrityConstraintViolationException)
				ControllerUtil.errorMessage(req, "Este usuário já faz parte da equipe.");
			else
				ControllerUtil.errorMessage(req, ex.getMessage());
		}
	}

	private void deleteEmployee(HttpServletRequest req) {
		EmployeeDAO dao = DAOFactory.createDAO(EmployeeDAO.class);

		try {
			int id = Integer.parseInt(req.getParameter("id")); // vem do modal de exclusão

			Employee e = dao.findById(id);
			if (e == null)
				throw new ModelException("Funcionário não encontrado para deleção.");

			if (dao.delete(e)) {
				ControllerUtil.sucessMessage(req, "'" + e.getUser().getName()
						+ "' removido da equipe. Os serviços dele continuam salvos, sem responsável.");
			} else {
				ControllerUtil.errorMessage(req, "Funcionário não pode ser removido.");
			}
		} catch (NumberFormatException ex) {
			ControllerUtil.errorMessage(req, "Identificador inválido.");
		} catch (ModelException ex) {
			ex.printStackTrace();
			ControllerUtil.errorMessage(req, ex.getMessage());
		}
	}
}
