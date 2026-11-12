package controller;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Feedback;
import model.ModelException;
import model.Service;
import model.User;
import model.dao.DAOFactory;
import model.dao.FeedbackDAO;
import model.dao.ServiceDAO;

/**
 * Feedbacks dos clientes (antigos "Posts").
 *
 *   GET  /feedbacks        -> lista: o CLIENTE vê só os seus; a equipe vê todos (consulta)
 *   GET  /feedback/form    -> CLIENTE: formulário de novo feedback (só serviços dele, concluídos e sem feedback)
 *   GET  /feedback/update  -> CLIENTE: edita um feedback dele
 *   POST /feedback/insert  -> CLIENTE
 *   POST /feedback/update  -> CLIENTE
 *   POST /feedback/delete  -> CLIENTE (o dele) ou CHEFE (moderação)
 *
 * O filtro decide QUEM pode usar cada rota; aqui o controller confere o DADO:
 * o autor do feedback é sempre o usuário logado (nunca vem do formulário)
 * e o serviço precisa ser de um veículo dele.
 */
@WebServlet(urlPatterns = { "/feedbacks", "/feedback/form", "/feedback/insert", "/feedback/update", "/feedback/delete" })
public class FeedbacksController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = ControllerUtil.getAction(req);
		User logged = ControllerUtil.getLoggedUser(req);

		switch (action) {
		case "/feedback/form": {
			List<Service> available = listAvailableServices(logged);

			if (available == null || available.isEmpty()) {
				ControllerUtil.errorMessage(req, "Você não tem serviços concluídos aguardando avaliação.");
				ControllerUtil.redirect(resp, req.getContextPath() + "/feedbacks");
				return;
			}

			req.setAttribute("services", available);
			// vem do botão "Avaliar" da tela de serviços; se não for número, ignora
			try {
				req.setAttribute("selectedServiceId", Integer.valueOf(req.getParameter("serviceId")));
			} catch (NumberFormatException e) {
				// sem serviço pré-selecionado
			}
			req.setAttribute("action", "insert");
			ControllerUtil.forward(req, resp, "/form-feedback.jsp");
			break;
		}
		case "/feedback/update": {
			Feedback feedback = loadFeedback(req);

			if (feedback == null || !isOwner(feedback, logged)) {
				ControllerUtil.errorMessage(req, "Feedback não encontrado para alteração.");
				ControllerUtil.redirect(resp, req.getContextPath() + "/feedbacks");
				return;
			}

			req.setAttribute("action", "update");
			req.setAttribute("feedback", feedback);
			ControllerUtil.forward(req, resp, "/form-feedback.jsp");
			break;
		}
		default:
			listFeedbacks(req, logged);

			ControllerUtil.transferSessionMessagesToRequest(req);

			ControllerUtil.forward(req, resp, "/feedbacks.jsp");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = ControllerUtil.getAction(req);
		User logged = ControllerUtil.getLoggedUser(req);

		switch (action) {
		case "/feedback/insert":
			insertFeedback(req, logged);
			break;
		case "/feedback/update":
			updateFeedback(req, logged);
			break;
		case "/feedback/delete":
			deleteFeedback(req, logged);
			break;
		default:
			System.out.println("URL inválida " + action);
		}

		ControllerUtil.redirect(resp, req.getContextPath() + "/feedbacks");
	}

	// Lista paginada. O cliente só enxerga os feedbacks dele.
	private void listFeedbacks(HttpServletRequest req, User logged) {
		FeedbackDAO dao = DAOFactory.createDAO(FeedbackDAO.class);

		try {
			List<Feedback> feedbacks;

			if (logged.isClient()) {
				int offset = ControllerUtil.preparePagination(req, dao.countByUser(logged.getId()));
				feedbacks = dao.listPageByUser(logged.getId(), ControllerUtil.PAGE_SIZE, offset);
			} else {
				int offset = ControllerUtil.preparePagination(req, dao.count());
				feedbacks = dao.listPage(ControllerUtil.PAGE_SIZE, offset);
			}

			req.setAttribute("feedbacks", feedbacks);
		} catch (ModelException e) {
			e.printStackTrace();
		}
	}

	private List<Service> listAvailableServices(User logged) {
		try {
			ServiceDAO dao = DAOFactory.createDAO(ServiceDAO.class);
			return dao.listAvailableForFeedback(logged.getId());
		} catch (ModelException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Feedback loadFeedback(HttpServletRequest req) {
		try {
			int id = Integer.parseInt(req.getParameter("feedbackId"));

			FeedbackDAO dao = DAOFactory.createDAO(FeedbackDAO.class);
			return dao.findById(id);
		} catch (NumberFormatException | ModelException e) {
			e.printStackTrace();
			return null;
		}
	}

	// O feedback é do usuário logado?
	private boolean isOwner(Feedback feedback, User logged) {
		return feedback.getUser() != null && feedback.getUser().getId() == logged.getId();
	}

	private void insertFeedback(HttpServletRequest req, User logged) {
		FeedbackDAO dao = DAOFactory.createDAO(FeedbackDAO.class);

		try {
			String rating = req.getParameter("rating");
			int serviceId = Integer.parseInt(req.getParameter("serviceId"));

			if (!Feedback.isValidRating(rating)) {
				ControllerUtil.errorMessage(req, "Escolha uma avaliação.");
				return;
			}

			ServiceDAO serviceDAO = DAOFactory.createDAO(ServiceDAO.class);
			Service service = serviceDAO.findById(serviceId);

			// Só pode avaliar um serviço que foi feito no carro DELE e que já foi concluído
			if (service == null || service.getVehicle().getUser().getId() != logged.getId()) {
				ControllerUtil.errorMessage(req, "Você só pode avaliar serviços feitos no seu veículo.");
				return;
			}
			if (!service.isFinished()) {
				ControllerUtil.errorMessage(req, "Você só pode avaliar um serviço depois que ele for concluído.");
				return;
			}

			Feedback feedback = new Feedback();
			feedback.setRating(rating);
			feedback.setComment(req.getParameter("comment"));
			feedback.setService(service);
			feedback.setUser(logged); // o autor é quem está logado, não um campo do formulário

			if (dao.save(feedback)) {
				ControllerUtil.sucessMessage(req, "Obrigado! Seu feedback foi enviado.");
			} else {
				ControllerUtil.errorMessage(req, "Feedback não pode ser salvo.");
			}
		} catch (NumberFormatException e) {
			ControllerUtil.errorMessage(req, "Dados inválidos no formulário.");
		} catch (ModelException e) {
			e.printStackTrace();
			if (e.getCause() instanceof SQLIntegrityConstraintViolationException)
				ControllerUtil.errorMessage(req, "Este serviço já foi avaliado.");
			else
				ControllerUtil.errorMessage(req, e.getMessage());
		}
	}

	private void updateFeedback(HttpServletRequest req, User logged) {
		FeedbackDAO dao = DAOFactory.createDAO(FeedbackDAO.class);

		try {
			Feedback feedback = loadFeedback(req);

			if (feedback == null || !isOwner(feedback, logged)) {
				ControllerUtil.errorMessage(req, "Feedback não encontrado para alteração.");
				return;
			}

			String rating = req.getParameter("rating");
			if (!Feedback.isValidRating(rating)) {
				ControllerUtil.errorMessage(req, "Escolha uma avaliação.");
				return;
			}

			feedback.setRating(rating);
			feedback.setComment(req.getParameter("comment"));

			if (dao.update(feedback)) {
				ControllerUtil.sucessMessage(req, "Feedback atualizado com sucesso.");
			} else {
				ControllerUtil.errorMessage(req, "Feedback não pode ser atualizado.");
			}
		} catch (ModelException e) {
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	}

	private void deleteFeedback(HttpServletRequest req, User logged) {
		FeedbackDAO dao = DAOFactory.createDAO(FeedbackDAO.class);

		try {
			int id = Integer.parseInt(req.getParameter("id")); // vem do modal de exclusão

			Feedback feedback = dao.findById(id);
			if (feedback == null)
				throw new ModelException("Feedback não encontrado para deleção.");

			// Cliente só apaga o próprio; o chefe pode apagar qualquer um (moderação)
			if (!isOwner(feedback, logged) && !logged.isBoss()) {
				ControllerUtil.errorMessage(req, "Você não tem permissão para excluir este feedback.");
				return;
			}

			if (dao.delete(feedback)) {
				ControllerUtil.sucessMessage(req, "Feedback deletado com sucesso.");
			} else {
				ControllerUtil.errorMessage(req, "Feedback não pode ser deletado.");
			}
		} catch (NumberFormatException e) {
			ControllerUtil.errorMessage(req, "Identificador inválido.");
		} catch (ModelException e) {
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	}
}
