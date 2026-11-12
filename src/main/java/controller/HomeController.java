package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Página inicial ("/"): leva todo mundo para a lista de serviços
 * (o cliente vê só os seus; a equipe vê todos).
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "" })
public class HomeController extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ControllerUtil.redirect(resp, req.getContextPath() + "/services");
	}
}
