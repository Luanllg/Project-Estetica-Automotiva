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
import model.dao.DAOFactory;
import model.dao.UserDAO;

/**
 * Cadastro de usuários. SÓ O CHEFE chega aqui (regra no AuthenticationFilter):
 * é ele quem cria as contas e escolhe o perfil (CHEFE, FUNCIONARIO ou CLIENTE) de cada uma.
 */
@WebServlet(urlPatterns = {"/users", "/user/form", "/user/delete", "/user/insert", "/user/update"})
public class UsersController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = ControllerUtil.getAction(req);
		
		switch (action) {
		case "/user/form": {
			req.setAttribute("action", "insert");
			ControllerUtil.forward(req, resp, "/form-user.jsp");
			break;
		}
		case "/user/update": {
			User user = loadUser(req);
			
			if (user == null) {
				ControllerUtil.errorMessage(req, "Usuário não encontrado para alteração.");
				ControllerUtil.redirect(resp, req.getContextPath() + "/users");
				return;
			}
			
			req.setAttribute("user", user);
			req.setAttribute("action", "update");
			ControllerUtil.forward(req, resp, "/form-user.jsp");
			break;
		}
		default:
			listUsers(req);
			
			ControllerUtil.transferSessionMessagesToRequest(req);
		
			ControllerUtil.forward(req, resp, "/users.jsp");
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = ControllerUtil.getAction(req);
		
		switch (action) {
		case "/user/delete":
			deleteUser(req);
			break;	
		case "/user/insert":
			insertUser(req);
			break;
		case "/user/update":
			updateUser(req);
			break;
		default:
			System.out.println("URL inválida " + action);
			break;
		}
			
		ControllerUtil.redirect(resp, req.getContextPath() + "/users");
	}

	// Devolve o usuário do parâmetro userId (ou null se não achar)
	private User loadUser(HttpServletRequest req) {
		try {
			int userId = Integer.parseInt(req.getParameter("userId"));
			
			UserDAO dao = DAOFactory.createDAO(UserDAO.class);
			return dao.findById(userId);
		} catch (NumberFormatException | ModelException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// Lista paginada: conta o total, calcula o offset e busca só a página atual
	private void listUsers(HttpServletRequest req) {
		UserDAO dao = DAOFactory.createDAO(UserDAO.class);
		
		try {
			int offset = ControllerUtil.preparePagination(req, dao.count());
			
			List<User> users = dao.listPage(ControllerUtil.PAGE_SIZE, offset);
			req.setAttribute("users", users);
		} catch (ModelException e) {
			// Log no servidor
			e.printStackTrace();
		}
	}
	
	private void insertUser(HttpServletRequest req) {
		String userRole = req.getParameter("role");
		
		if (!User.isValidRole(userRole)) {
			ControllerUtil.errorMessage(req, "Escolha um perfil válido para o usuário.");
			return;
		}
		
		String userPassword = req.getParameter("password");
		
		User user = new User();
		user.setName(req.getParameter("name"));
		user.setGender(req.getParameter("gender"));
		user.setEmail(req.getParameter("mail"));
		user.setRole(userRole);
		// Guardamos só o hash SHA-256 da senha, nunca a senha em texto puro
		user.setPassword(ControllerUtil.sha256(userPassword == null ? "" : userPassword));
		
		UserDAO dao = DAOFactory.createDAO(UserDAO.class);
		
		try {
			if (dao.save(user)) {
				ControllerUtil.sucessMessage(req, "Usuário '" + user.getName() + "' (" 
						+ user.getRoleLabel() + ") salvo com sucesso.");
			}
			else {
				ControllerUtil.errorMessage(req, "Usuário '" + user.getName() + "' não pode ser salvo.");
			}
				
		} catch (ModelException e) {
			// log no servidor
			e.printStackTrace();
			ControllerUtil.errorMessage(req, "Não foi possível salvar. O e-mail já pode estar em uso.");
		}
	}
	
	private void updateUser(HttpServletRequest req) {
		User user = loadUser(req);
		
		if (user == null) {
			ControllerUtil.errorMessage(req, "Usuário não encontrado para alteração.");
			return;
		}
		
		String userRole = req.getParameter("role");
		User logged = ControllerUtil.getLoggedUser(req);
		
		// O chefe não pode rebaixar a si mesmo (a empresa ficaria sem ninguém para cadastrar usuários)
		if (user.getId() == logged.getId()) {
			userRole = user.getRole();
		}
		
		if (!User.isValidRole(userRole)) {
			ControllerUtil.errorMessage(req, "Escolha um perfil válido para o usuário.");
			return;
		}
		
		user.setName(req.getParameter("name"));
		user.setGender(req.getParameter("gender"));
		user.setEmail(req.getParameter("mail"));
		user.setRole(userRole);
		
		UserDAO dao = DAOFactory.createDAO(UserDAO.class);
		
		try {
			if (dao.update(user)) {
				ControllerUtil.sucessMessage(req, "Usuário '" + user.getName() + "' atualizado com sucesso.");
			}
			else {
				ControllerUtil.errorMessage(req, "Usuário '" + user.getName() + "' não pode ser atualizado.");
			}
				
		} catch (ModelException e) {
			// log no servidor
			e.printStackTrace();
			ControllerUtil.errorMessage(req, "Não foi possível atualizar. O e-mail já pode estar em uso.");
		}
	}
	
	private void deleteUser(HttpServletRequest req) {
		UserDAO dao = DAOFactory.createDAO(UserDAO.class);
		
		try {
			int userId = Integer.parseInt(req.getParameter("id"));
			
			if (userId == ControllerUtil.getLoggedUser(req).getId()) {
				ControllerUtil.errorMessage(req, "Você não pode excluir a sua própria conta.");
				return;
			}
			
			User user = dao.findById(userId);
			
			if (user == null)
				throw new ModelException("Usuário não encontrado para deleção.");
			
			// O DAO devolve false se houver veículos, equipe ou feedbacks ligados ao usuário (FK)
			if (dao.delete(user)) {
				ControllerUtil.sucessMessage(req, "Usuário '" + user.getName() + "' deletado com sucesso.");
			}
			else {
				ControllerUtil.errorMessage(req, "Usuário '" + user.getName() + "' não pode ser deletado. Há dados relacionados ao usuário.");
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
