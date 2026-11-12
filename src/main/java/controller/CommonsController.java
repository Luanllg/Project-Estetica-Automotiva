package controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import model.Employee;
import model.ModelException;
import model.User;
import model.Vehicle;
import model.dao.DAOFactory;
import model.dao.EmployeeDAO;
import model.dao.UserDAO;
import model.dao.VehicleDAO;

public class CommonsController {
	
	// Select de proprietário do veículo: só usuários CLIENTE (atributo "users")
	public static void listClients(HttpServletRequest req) {
		UserDAO dao = DAOFactory.createDAO(UserDAO.class);
		
		try {
			req.setAttribute("users", dao.listByRole(User.CLIENT));
		} catch (ModelException e) {
			// Log no servidor
			e.printStackTrace();
		}
	}
	
	// Select do cadastro da Equipe: chefe e funcionários (atributo "users")
	public static void listStaffUsers(HttpServletRequest req) {
		UserDAO dao = DAOFactory.createDAO(UserDAO.class);
		
		try {
			req.setAttribute("users", dao.listStaff());
		} catch (ModelException e) {
			e.printStackTrace();
		}
	}
	
	// Preenche o <select> de veículos do formulário de Serviços (lista completa, sem paginação)
	public static void listVehicles(HttpServletRequest req) {
		VehicleDAO dao = DAOFactory.createDAO(VehicleDAO.class);
		
		List<Vehicle> listVehicles = null;
		try {
			listVehicles = dao.listAll();
		} catch (ModelException e) {
			e.printStackTrace();
		}
		
		if (listVehicles != null)
			req.setAttribute("vehicles", listVehicles);
	}
	
	// Preenche o <select> de responsável do formulário de Serviços
	public static void listEmployees(HttpServletRequest req) {
		EmployeeDAO dao = DAOFactory.createDAO(EmployeeDAO.class);
		
		List<Employee> listEmployees = null;
		try {
			listEmployees = dao.listAll();
		} catch (ModelException e) {
			e.printStackTrace();
		}
		
		if (listEmployees != null)
			req.setAttribute("employees", listEmployees);
	}
}
