package controller;

import java.io.IOException;

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
 * Login e logout.
 *
 *   GET  /login   -> mostra a tela de login
 *   POST /login   -> valida e-mail + senha e guarda o usuário na sessão
 *   GET  /logout  -> encerra a sessão
 *
 * O AuthenticationFilter bloqueia as outras telas enquanto não houver
 * o atributo "loggedUser" na sessão.
 */
@WebServlet(urlPatterns = { "/login", "/logout" })
public class LoginController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (ControllerUtil.getAction(req).equals("/logout")) {
			req.getSession().invalidate(); // apaga a sessão (e o usuário logado)
			ControllerUtil.redirect(resp, req.getContextPath() + "/login");
			return;
		}

		// Quem já está logado não precisa ver a tela de login
		if (req.getSession().getAttribute("loggedUser") != null) {
			ControllerUtil.redirect(resp, req.getContextPath() + "/services");
			return;
		}

		ControllerUtil.forward(req, resp, "/login.jsp");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String email = req.getParameter("email");
		String password = req.getParameter("password");

		User user = null;
		if (email != null && password != null) {
			UserDAO dao = DAOFactory.createDAO(UserDAO.class);

			try {
				// O banco guarda o hash; comparamos hash com hash.
				user = dao.authenticate(email.trim(), ControllerUtil.sha256(password));
			} catch (ModelException e) {
				e.printStackTrace();
			}
		}

		if (user == null) {
			req.setAttribute("error", "E-mail ou senha inválidos.");
			req.setAttribute("email", email); // mantém o e-mail digitado no campo
			ControllerUtil.forward(req, resp, "/login.jsp");
			return;
		}

		user.setPassword(""); // não deixe o hash da senha na sessão

		req.getSession();       // garante que existe uma sessão
		req.changeSessionId();  // gera novo id de sessão após o login (evita session fixation)
		req.getSession().setAttribute("loggedUser", user);

		ControllerUtil.redirect(resp, req.getContextPath() + "/services");
	}
}
