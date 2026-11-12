package controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Employee;
import model.ModelException;
import model.Service;
import model.User;
import model.Vehicle;
import model.dao.DAOFactory;
import model.dao.EmployeeDAO;
import model.dao.ServiceDAO;

/**
 * Controlador do CRUD de Serviços de estética automotiva.
 *
 *   GET /services          -> lista. TODOS acessam: o CLIENTE vê só os serviços dos
 *                             seus carros (somente leitura); a equipe vê todos.
 *   /service/form|insert|update|delete -> SÓ equipe (regra no AuthenticationFilter).
 */
@WebServlet(urlPatterns = { "/services", "/service/form", "/service/insert", "/service/update", "/service/delete" })
public class ServicesController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = ControllerUtil.getAction(req);

		switch (action) {
		case "/service/form": {
			CommonsController.listVehicles(req);  // alimenta o select de veículos
			CommonsController.listEmployees(req); // alimenta o select de responsável
			req.setAttribute("action", "insert");
			ControllerUtil.forward(req, resp, "/form-service.jsp");
			break;
		}
		case "/service/update": {
			Service service = loadService(req);

			if (service == null) {
				ControllerUtil.errorMessage(req, "Serviço não encontrado para alteração.");
				ControllerUtil.redirect(resp, req.getContextPath() + "/services");
				return;
			}

			CommonsController.listVehicles(req);
			CommonsController.listEmployees(req);
			req.setAttribute("action", "update");
			req.setAttribute("service", service);
			ControllerUtil.forward(req, resp, "/form-service.jsp");
			break;
		}
		default:
			listServices(req);

			ControllerUtil.transferSessionMessagesToRequest(req);

			ControllerUtil.forward(req, resp, "/services.jsp");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = ControllerUtil.getAction(req);

		switch (action) {
		case "/service/insert":
			insertService(req);
			break;
		case "/service/update":
			updateService(req);
			break;
		case "/service/delete":
			deleteService(req);
			break;
		default:
			System.out.println("URL inválida " + action);
		}

		ControllerUtil.redirect(resp, req.getContextPath() + "/services");
	}

	// Lista paginada. O cliente só enxerga os serviços dos veículos dele.
	private void listServices(HttpServletRequest req) {
		ServiceDAO dao = DAOFactory.createDAO(ServiceDAO.class);
		User logged = ControllerUtil.getLoggedUser(req);

		try {
			List<Service> services;

			if (logged.isClient()) {
				int offset = ControllerUtil.preparePagination(req, dao.countByUser(logged.getId()));
				services = dao.listPageByUser(logged.getId(), ControllerUtil.PAGE_SIZE, offset);
			} else {
				int offset = ControllerUtil.preparePagination(req, dao.count());
				services = dao.listPage(ControllerUtil.PAGE_SIZE, offset);
			}

			req.setAttribute("services", services);
		} catch (ModelException e) {
			// Log no servidor
			e.printStackTrace();
		}
	}

	private Service loadService(HttpServletRequest req) {
		try {
			int id = Integer.parseInt(req.getParameter("serviceId"));

			ServiceDAO dao = DAOFactory.createDAO(ServiceDAO.class);
			return dao.findById(id);
		} catch (NumberFormatException | ModelException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Service readForm(HttpServletRequest req, int id) {
		Service s = new Service(id);
		s.setType(req.getParameter("type"));                                         // input radio
		s.setServiceDate(ControllerUtil.formatDate(req.getParameter("serviceDate"))); // input date
		s.setPrice(Double.parseDouble(req.getParameter("price")));                   // input number
		s.setDetails(req.getParameter("details"));                                   // textarea
		s.setStatus(req.getParameter("status"));                                     // select
		s.setPaid(req.getParameter("paid") != null);                                 // input checkbox
		s.setVehicle(new Vehicle(Integer.parseInt(req.getParameter("vehicle"))));    // select (FK)

		// select (FK) opcional: vazio = serviço ainda sem responsável
		String employeeParam = req.getParameter("employee");
		if (employeeParam != null && !employeeParam.isEmpty())
			s.setEmployee(new Employee(Integer.parseInt(employeeParam)));

		return s;
	}

	/**
	 * Regras de negócio. Devolve a mensagem de erro, ou null se estiver tudo certo.
	 * Aproveita para trocar o Employee "só com id" pelo objeto completo do banco.
	 */
	private String validate(Service s) throws ModelException {
		if (!Service.isValidType(s.getType()) || !Service.isValidStatus(s.getStatus())
				|| s.getServiceDate() == null || s.getPrice() < 0)
			return "Dados inválidos no formulário.";

		if (s.isInProgress() && s.getEmployee() == null)
			return "Para colocar o serviço em andamento, escolha o funcionário responsável.";

		if (s.getEmployee() != null) {
			EmployeeDAO employeeDAO = DAOFactory.createDAO(EmployeeDAO.class);
			Employee e = employeeDAO.findById(s.getEmployee().getId());

			if (e == null)
				return "Funcionário não encontrado.";

			if (!e.isActive() && !s.isFinished())
				return e.getUser().getName() + " está inativo e não pode receber serviços.";

			// Um funcionário executa um serviço por vez
			if (s.isInProgress()) {
				ServiceDAO serviceDAO = DAOFactory.createDAO(ServiceDAO.class);
				Service current = serviceDAO.findCurrentByEmployee(e.getId());

				if (current != null && current.getId() != s.getId())
					return e.getUser().getName() + " já está executando outro serviço ("
							+ current.getVehicle().getModel() + "). Conclua-o antes de iniciar este.";
			}

			s.setEmployee(e);
		}

		return null;
	}

	private void insertService(HttpServletRequest req) {
		ServiceDAO dao = DAOFactory.createDAO(ServiceDAO.class);

		try {
			Service s = readForm(req, 0);

			String error = validate(s);
			if (error != null) {
				ControllerUtil.errorMessage(req, error);
				return;
			}

			if (dao.save(s)) {
				ControllerUtil.sucessMessage(req, "Serviço de " + s.getType() + " salvo com sucesso.");
			} else {
				ControllerUtil.errorMessage(req, "Serviço de " + s.getType() + " não pode ser salvo.");
			}
		} catch (NumberFormatException e) {
			ControllerUtil.errorMessage(req, "Dados inválidos no formulário.");
		} catch (ModelException e) {
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	}

	private void updateService(HttpServletRequest req) {
		ServiceDAO dao = DAOFactory.createDAO(ServiceDAO.class);

		try {
			int id = Integer.parseInt(req.getParameter("serviceId"));

			Service old = dao.findById(id);
			if (old == null)
				throw new ModelException("Serviço não encontrado para alteração.");

			Service s = readForm(req, id);

			String error = validate(s);

			// Serviço que já recebeu feedback precisa continuar concluído
			if (error == null && old.isRated() && !s.isFinished())
				error = "Este serviço já foi avaliado pelo cliente e deve continuar como concluído.";

			if (error != null) {
				ControllerUtil.errorMessage(req, error);
				return;
			}

			if (dao.update(s)) {
				ControllerUtil.sucessMessage(req, "Serviço de " + s.getType() + " atualizado com sucesso.");
			} else {
				ControllerUtil.errorMessage(req, "Serviço de " + s.getType() + " não pode ser atualizado.");
			}
		} catch (NumberFormatException e) {
			ControllerUtil.errorMessage(req, "Dados inválidos no formulário.");
		} catch (ModelException e) {
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	}

	private void deleteService(HttpServletRequest req) {
		ServiceDAO dao = DAOFactory.createDAO(ServiceDAO.class);

		try {
			int id = Integer.parseInt(req.getParameter("id"));

			Service s = dao.findById(id);
			if (s == null)
				throw new ModelException("Serviço não encontrado para deleção.");

			if (dao.delete(s)) {
				ControllerUtil.sucessMessage(req, "Serviço de " + s.getType() + " deletado com sucesso.");
			} else {
				ControllerUtil.errorMessage(req, "Serviço de " + s.getType()
						+ " não pode ser deletado. Há feedback do cliente relacionado a ele.");
			}
		} catch (NumberFormatException e) {
			ControllerUtil.errorMessage(req, "Identificador inválido.");
		} catch (ModelException e) {
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	}
}
