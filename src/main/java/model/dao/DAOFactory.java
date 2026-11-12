package model.dao;

import java.util.HashMap;
import java.util.Map;

public class DAOFactory {
	
	private static Map<Class<?>, Object> listDAOsInterfaces = new HashMap<Class<?>, Object>();
	
	// Para o DAOFactory funcionar para suas classes de domínio, adicione na 
	// lista suas interfaces e classes DAO na listDAOsInterfaces
	// Se tiver curiosidade, pergunte ao professor sobre o funcionamento de blocos estáticos
	static {
		listDAOsInterfaces.put(UserDAO.class, new MySQLUserDAO());
		listDAOsInterfaces.put(VehicleDAO.class, new MySQLVehicleDAO());
		listDAOsInterfaces.put(ServiceDAO.class, new MySQLServiceDAO());
		// Feedbacks (antigos Posts) e Equipe (antiga Empresas)
		listDAOsInterfaces.put(FeedbackDAO.class, new MySQLFeedbackDAO());
		listDAOsInterfaces.put(EmployeeDAO.class, new MySQLEmployeeDAO());
	}

	@SuppressWarnings("unchecked")
	public static <DAOInterface> DAOInterface createDAO(Class<?> entity){
		return (DAOInterface) listDAOsInterfaces.get(entity);
	} 
}
