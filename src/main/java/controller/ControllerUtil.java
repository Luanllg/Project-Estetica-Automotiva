package controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

public class ControllerUtil {

	private static String MESSAGE_KEY = "message";
	private static int ERROR = 0;
	private static int SUCESS = 1;

	static void forward(HttpServletRequest req, HttpServletResponse resp, String target)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = req.getRequestDispatcher(target);

		dispatcher.forward(req, resp);
	}

	static void redirect(HttpServletResponse resp, String target) throws IOException {
		resp.sendRedirect(target);
	}

	static void sucessMessage(HttpServletRequest req, String message) {
		req.getSession().setAttribute(MESSAGE_KEY, message);
		req.getSession().setAttribute("alertType", SUCESS);
	}

	static void errorMessage(HttpServletRequest req, String message) {
		req.getSession().setAttribute(MESSAGE_KEY, message);
		req.getSession().setAttribute("alertType", ERROR);
	}

	static void transferSessionMessagesToRequest(HttpServletRequest req) {
		if (req.getSession().getAttribute(MESSAGE_KEY) != null) {
			String message = (String) req.getSession().getAttribute(MESSAGE_KEY);
			req.setAttribute(MESSAGE_KEY, message);
			req.getSession().removeAttribute(MESSAGE_KEY);
		}

		if (req.getSession().getAttribute("alertType") != null) {
			Integer message = (Integer) req.getSession().getAttribute("alertType");
			req.setAttribute("alertType", message);
			req.getSession().removeAttribute("alertType");
		}
	}

	public static Date formatDate(String stringDate) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		try {
			return dateFormat.parse(stringDate);
		} catch (ParseException e) {
			return null;
		}
	}

	// ------------------------------------------------------------------
	// Rotas
	// ------------------------------------------------------------------

	// Devolve a rota sem o context path. Ex.: "/crud-manager/vehicle/form" -> "/vehicle/form".
	// Assim os controllers novos funcionam mesmo se o context root não for "crud-manager".
	static String getAction(HttpServletRequest req) {
		return req.getRequestURI().substring(req.getContextPath().length());
	}

	// Usuário logado (o AuthenticationFilter garante que ele existe e está atualizado)
	static User getLoggedUser(HttpServletRequest req) {
		return (User) req.getSession().getAttribute("loggedUser");
	}

	// ------------------------------------------------------------------
	// Paginação (usada por todos os cadastros)
	// ------------------------------------------------------------------

	// Quantos registros aparecem por página
	public static final int PAGE_SIZE = 5;

	// Lê o parâmetro ?page= da URL. Se faltar ou for inválido, usa a página 1.
	static int getPage(HttpServletRequest req) {
		try {
			return Math.max(1, Integer.parseInt(req.getParameter("page")));
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	// Total de páginas para a quantidade de registros (no mínimo 1)
	static int totalPages(int totalItems) {
		return Math.max(1, (int) Math.ceil(totalItems / (double) PAGE_SIZE));
	}

	// Calcula página atual e total e já os coloca na requisição para a pagination.jsp.
	// Devolve o OFFSET que deve ser passado ao DAO.listPage(PAGE_SIZE, offset).
	static int preparePagination(HttpServletRequest req, int totalItems) {
		int totalPages = totalPages(totalItems);
		int currentPage = Math.min(getPage(req), totalPages); // ?page=999 cai na última página

		req.setAttribute("currentPage", currentPage);
		req.setAttribute("totalPages", totalPages);

		return (currentPage - 1) * PAGE_SIZE;
	}

	// ------------------------------------------------------------------
	// Senha
	// ------------------------------------------------------------------

	// Hash SHA-256 em hexadecimal minúsculo: igual ao SHA2('texto', 256) do MySQL.
	public static String sha256(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));

			StringBuilder sb = new StringBuilder();
			for (byte b : hash) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
