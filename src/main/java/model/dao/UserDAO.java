package model.dao;

import java.util.List;

import model.ModelException;
import model.User;

public interface UserDAO {
	boolean save(User user) throws ModelException ;
	boolean update(User user) throws ModelException;
	boolean delete(User user) throws ModelException;
	List<User> listAll() throws ModelException;
	User findById(int id) throws ModelException;
	List<User> listPage(int limit, int offset) throws ModelException;
	int count() throws ModelException;
	// Login: devolve o usuário se e-mail e hash da senha conferem; senão, null
	// Usuários de um perfil (usado nos selects: donos de veículos = CLIENTE)
	List<User> listByRole(String role) throws ModelException;
	// Chefe + funcionários (usado no select do cadastro da Equipe)
	List<User> listStaff() throws ModelException;
	User authenticate(String email, String passwordHash) throws ModelException;
}
