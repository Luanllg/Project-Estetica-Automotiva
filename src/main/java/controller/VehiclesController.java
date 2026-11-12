package controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ModelException;
import model.User;
import model.Vehicle;
import model.dao.DAOFactory;
import model.dao.VehicleDAO;

/**
 * Controlador do CRUD de Veículos.
 * Mesmo molde do EmployeesController: um único Servlet atende todas as URLs
 * do cadastro e decide o que fazer olhando a URL ("action").
 *
 *   GET  /vehicles          -> lista (paginada)
 *   GET  /vehicle/form      -> formulário vazio (cadastrar)
 *   GET  /vehicle/update    -> formulário preenchido (editar)
 *   POST /vehicle/insert    -> grava novo
 *   POST /vehicle/update    -> grava alterações
 *   POST /vehicle/delete    -> exclui
 */
@WebServlet(urlPatterns = { "/vehicles", "/vehicle/form", "/vehicle/insert", "/vehicle/update", "/vehicle/delete" })
public class VehiclesController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Rota sem o context path (ex.: "/vehicle/form")
		String action = ControllerUtil.getAction(req);

		switch (action) {
		case "/vehicle/form": {
			CommonsController.listClients(req); // alimenta o select de proprietários (só CLIENTE)
			req.setAttribute("action", "insert");
			ControllerUtil.forward(req, resp, "/form-vehicle.jsp");
			break;
		}
		case "/vehicle/update": {
			Vehicle vehicle = loadVehicle(req);

			if (vehicle == null) {
				ControllerUtil.errorMessage(req, "Veículo não encontrado para alteração.");
				ControllerUtil.redirect(resp, req.getContextPath() + "/vehicles");
				return;
			}

			CommonsController.listClients(req);
			req.setAttribute("action", "update");
			req.setAttribute("vehicle", vehicle);
			ControllerUtil.forward(req, resp, "/form-vehicle.jsp");
			break;
		}
		default:
			listVehicles(req);

			ControllerUtil.transferSessionMessagesToRequest(req);

			ControllerUtil.forward(req, resp, "/vehicles.jsp");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = ControllerUtil.getAction(req);

		switch (action) {
		case "/vehicle/insert":
			insertVehicle(req);
			break;
		case "/vehicle/update":
			updateVehicle(req);
			break;
		case "/vehicle/delete":
			deleteVehicle(req);
			break;
		default:
			System.out.println("URL inválida " + action);
		}

		// Post/Redirect/Get: evita reenviar o formulário ao atualizar a página
		ControllerUtil.redirect(resp, req.getContextPath() + "/vehicles");
	}

	// Lista paginada: conta o total, calcula o offset e busca só a página atual
	private void listVehicles(HttpServletRequest req) {
		VehicleDAO dao = DAOFactory.createDAO(VehicleDAO.class);

		try {
			int offset = ControllerUtil.preparePagination(req, dao.count());

			List<Vehicle> vehicles = dao.listPage(ControllerUtil.PAGE_SIZE, offset);
			req.setAttribute("vehicles", vehicles);
		} catch (ModelException e) {
			// Log no servidor
			e.printStackTrace();
		}
	}

	// Busca o veículo pelo parâmetro vehicleId. Devolve null se não achar.
	private Vehicle loadVehicle(HttpServletRequest req) {
		try {
			int id = Integer.parseInt(req.getParameter("vehicleId"));

			VehicleDAO dao = DAOFactory.createDAO(VehicleDAO.class);
			return dao.findById(id);
		} catch (NumberFormatException | ModelException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Lê os campos do formulário e monta o objeto (usado no insert e no update)
	private Vehicle readForm(HttpServletRequest req, int id) {
		Vehicle v = new Vehicle(id);
		v.setModel(req.getParameter("model"));                          // input text
		v.setModelYear(Integer.parseInt(req.getParameter("modelYear"))); // input number
		v.setColor(req.getParameter("color"));                          // input color
		v.setCategory(req.getParameter("category"));                    // input radio

		// input checkbox: se estiver desmarcado o navegador NÃO envia o parâmetro,
		// então o valor chega como null.
		v.setCeramicCoating(req.getParameter("ceramicCoating") != null);

		// select (FK): só chega o id; montamos um User apenas com o id
		v.setUser(new User(Integer.parseInt(req.getParameter("user"))));

		return v;
	}

	private void insertVehicle(HttpServletRequest req) {
		VehicleDAO dao = DAOFactory.createDAO(VehicleDAO.class);

		try {
			Vehicle v = readForm(req, 0);

			if (dao.save(v)) {
				ControllerUtil.sucessMessage(req, "Veículo '" + v.getModel() + "' salvo com sucesso.");
			} else {
				ControllerUtil.errorMessage(req, "Veículo '" + v.getModel() + "' não pode ser salvo.");
			}
		} catch (NumberFormatException e) {
			ControllerUtil.errorMessage(req, "Dados inválidos no formulário.");
		} catch (ModelException e) {
			// log no servidor
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	}

	private void updateVehicle(HttpServletRequest req) {
		VehicleDAO dao = DAOFactory.createDAO(VehicleDAO.class);

		try {
			int id = Integer.parseInt(req.getParameter("vehicleId")); // campo hidden do form
			Vehicle v = readForm(req, id);

			if (dao.update(v)) {
				ControllerUtil.sucessMessage(req, "Veículo '" + v.getModel() + "' atualizado com sucesso.");
			} else {
				ControllerUtil.errorMessage(req, "Veículo '" + v.getModel() + "' não pode ser atualizado.");
			}
		} catch (NumberFormatException e) {
			ControllerUtil.errorMessage(req, "Dados inválidos no formulário.");
		} catch (ModelException e) {
			// log no servidor
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	}

	private void deleteVehicle(HttpServletRequest req) {
		VehicleDAO dao = DAOFactory.createDAO(VehicleDAO.class);

		try {
			int id = Integer.parseInt(req.getParameter("id")); // vem do modal de exclusão

			Vehicle v = dao.findById(id);
			if (v == null)
				throw new ModelException("Veículo não encontrado para deleção.");

			if (dao.delete(v)) {
				ControllerUtil.sucessMessage(req, "Veículo '" + v.getModel() + "' deletado com sucesso.");
			} else {
				ControllerUtil.errorMessage(req, "Veículo '" + v.getModel()
						+ "' não pode ser deletado. Há serviços relacionados ao veículo.");
			}
		} catch (NumberFormatException e) {
			ControllerUtil.errorMessage(req, "Identificador inválido.");
		} catch (ModelException e) {
			// log no servidor
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	}
}
