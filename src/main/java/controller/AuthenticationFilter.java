package controller;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ModelException;
import model.User;
import model.dao.DAOFactory;
import model.dao.UserDAO;

/**
 * Filtro que protege a aplicação inteira ("/*"). Toda requisição passa por aqui
 * antes de chegar ao Servlet e ele faz DUAS coisas:
 *
 *  1) AUTENTICAÇÃO: sem usuário logado, redireciona para /login
 *     (ficam livres: a tela de login e os arquivos estáticos css, js, fontes e imagens).
 *
 *  2) AUTORIZAÇÃO por perfil (CHEFE, FUNCIONARIO, CLIENTE). Todas as regras de
 *     "quem pode abrir o quê" ficam concentradas no método isAllowed() abaixo.
 *     (Regras que dependem do DADO, como "o feedback é meu?", ficam no controller.)
 *
 * A cada requisição o usuário é relido do banco: se o chefe mudar o perfil de alguém
 * (ou apagar a conta), vale já na próxima página, sem precisar sair e entrar de novo.
 */
@WebFilter(urlPatterns = "/*")
public class AuthenticationFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		// Caminho da requisição sem o context path. Ex.: "/vehicles"
		String path = req.getRequestURI().substring(req.getContextPath().length());

		boolean publicResource = path.equals("/login") 
				|| path.startsWith("/css/") 
				|| path.startsWith("/js/")
				|| path.startsWith("/fonts/") 
				|| path.startsWith("/images/");

		if (publicResource) {
			chain.doFilter(request, response);
			return;
		}

		// getSession(false): NÃO cria sessão nova só para checar
		HttpSession session = req.getSession(false);
		User sessionUser = session == null ? null : (User) session.getAttribute("loggedUser");

		if (sessionUser == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		// Relê o usuário do banco (perfil sempre atualizado)
		User user;
		try {
			UserDAO dao = DAOFactory.createDAO(UserDAO.class);
			user = dao.findById(sessionUser.getId());
		} catch (ModelException e) {
			throw new ServletException(e);
		}

		if (user == null) { // a conta foi excluída
			session.invalidate();
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		user.setPassword(""); // não deixe o hash da senha na sessão
		session.setAttribute("loggedUser", user);

		if (isAllowed(path, user)) {
			chain.doFilter(request, response);
		} else {
			ControllerUtil.errorMessage(req, "Você não tem permissão para acessar esta área.");
			resp.sendRedirect(req.getContextPath() + "/services");
		}
	}

	/**
	 * Tabela de permissões:
	 *
	 *   /users, /user/...            CHEFE
	 *   /employees                   CHEFE, FUNCIONARIO (só consulta)
	 *   /employee/... (alterações)   CHEFE
	 *   /vehicles, /vehicle/...      CHEFE, FUNCIONARIO
	 *   /services                    todos (o cliente só vê os seus)
	 *   /service/... (alterações)    CHEFE, FUNCIONARIO
	 *   /feedbacks                   todos (o cliente só vê os seus)
	 *   /feedback/form|insert|update CLIENTE
	 *   /feedback/delete             CLIENTE (o seu) e CHEFE (moderação); o controller confere o dono
	 */
	private boolean isAllowed(String path, User user) {
		if (path.equals("/users") || path.startsWith("/user/"))
			return user.isBoss();

		if (path.equals("/employees"))
			return user.isStaff();
		if (path.startsWith("/employee/"))
			return user.isBoss();

		if (path.equals("/vehicles") || path.startsWith("/vehicle/"))
			return user.isStaff();

		if (path.startsWith("/service/"))
			return user.isStaff();

		if (path.equals("/feedback/delete"))
			return user.isClient() || user.isBoss();
		if (path.startsWith("/feedback/"))
			return user.isClient();

		// "/", "/services", "/feedbacks", "/logout": qualquer usuário logado
		return true;
	}
}
