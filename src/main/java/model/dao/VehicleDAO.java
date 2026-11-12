package model.dao;

import java.util.List;

import model.ModelException;
import model.Vehicle;

public interface VehicleDAO {
	boolean save(Vehicle vehicle) throws ModelException;
	boolean update(Vehicle vehicle) throws ModelException;
	boolean delete(Vehicle vehicle) throws ModelException;
	List<Vehicle> listAll() throws ModelException;                        // usado nos selects
	List<Vehicle> listPage(int limit, int offset) throws ModelException;  // usado na paginação
	int count() throws ModelException;                                    // total de registros
	Vehicle findById(int id) throws ModelException;
}
