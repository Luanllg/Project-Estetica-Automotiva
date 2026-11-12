package model.dao;

import java.util.List;

import model.ModelException;
import model.Service;

public interface ServiceDAO {
	boolean save(Service service) throws ModelException;
	boolean update(Service service) throws ModelException;
	boolean delete(Service service) throws ModelException;
	List<Service> listAll() throws ModelException;
	List<Service> listPage(int limit, int offset) throws ModelException;
	int count() throws ModelException;
	Service findById(int id) throws ModelException;
	// Serviços dos veículos de um cliente (tela "meus serviços")
	List<Service> listPageByUser(int userId, int limit, int offset) throws ModelException;
	int countByUser(int userId) throws ModelException;
	// Serviço EM_ANDAMENTO de um funcionário (null se não houver)
	Service findCurrentByEmployee(int employeeId) throws ModelException;
	// Serviços CONCLUIDOS de um cliente que ainda não têm feedback
	List<Service> listAvailableForFeedback(int userId) throws ModelException;
}
