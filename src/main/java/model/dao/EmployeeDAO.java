package model.dao;

import java.util.List;

import model.Employee;
import model.ModelException;

public interface EmployeeDAO {
	boolean save(Employee employee) throws ModelException;
	boolean update(Employee employee) throws ModelException;
	boolean delete(Employee employee) throws ModelException;
	List<Employee> listAll() throws ModelException;                        // usado no select de Serviços
	List<Employee> listPage(int limit, int offset) throws ModelException; // traz também o serviço atual
	int count() throws ModelException;
	Employee findById(int id) throws ModelException;                      // dados básicos (sem serviço atual)
}
