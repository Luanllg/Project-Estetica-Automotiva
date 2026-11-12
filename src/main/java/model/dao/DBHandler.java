package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import model.ModelException;

public class DBHandler {
	private Connection connection = null;
	private PreparedStatement preparedStatement = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	
	public DBHandler() throws ModelException {
		connection = MySQLConnectionFactory.getConnection();
	}

	public void prepareStatement(String sqlInsert) throws ModelException {
		try {
			preparedStatement = connection.prepareStatement(sqlInsert);
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao preparar a SQL", e);
		}
	}

	public void setString(int i, String value) throws ModelException {
		validatePreparedStatement();
		
		try {
			preparedStatement.setString(i, value);
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao atribuir string", e);
		}
	}
	
	public void setNullDate(int i) throws ModelException {
		validatePreparedStatement();
		
		try {
			preparedStatement.setNull(i, java.sql.Types.DATE);
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao atribuir null", e);
		}
	}
	
	public void setDate(int i, Date value) throws ModelException {
		validatePreparedStatement();
		
		try {
			java.sql.Date date = new java.sql.Date(value.getTime());
			preparedStatement.setDate(i, date);
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao atribuir data", e);
		}
	}

	public void setInt(int i, int value) throws ModelException {
		validatePreparedStatement();
		
		try {
			preparedStatement.setInt(i, value);
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao atribuir inteiro", e);
		}
	}

	// ---- Novos métodos para os cadastros de Veículos e Serviços ----

	public void setDouble(int i, double value) throws ModelException {
		validatePreparedStatement();
		
		try {
			preparedStatement.setDouble(i, value);
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao atribuir decimal", e);
		}
	}

	public void setBoolean(int i, boolean value) throws ModelException {
		validatePreparedStatement();
		
		try {
			preparedStatement.setBoolean(i, value);
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao atribuir booleano", e);
		}
	}

	// Para colunas que aceitam NULL (ex.: services.employee_id)
	public void setNullInt(int i) throws ModelException {
		validatePreparedStatement();
		
		try {
			preparedStatement.setNull(i, java.sql.Types.INTEGER);
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao atribuir null", e);
		}
	}

	public int executeUpdate() throws ModelException  {
		validatePreparedStatement();
		
		try {
			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new ModelException("Erro ao executar SQL", e);
		} finally{
			close();
		}
	}
	
	public void createStatement() throws ModelException {
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			throw new ModelException("Erro ao criar o statement", e);
		}
	}
	
	public void executeQuery(String sql) throws ModelException {
		validateStatement();
		
		try {
			resultSet = statement.executeQuery(sql);
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao executar SQL", e);
		}
	}
	
	public void executeQuery() throws ModelException {
		validatePreparedStatement();
		
		try {
			resultSet = preparedStatement.executeQuery();
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao executar SQL", e);
		}
	}
	
	public boolean next() throws ModelException {
		validateResultSet();
		
		try {
			return resultSet.next();
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao chamar próximo registro", e);
		}
	}
	
	public int getInt(String column) throws ModelException {
		try {
			return resultSet.getInt(column);
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao chamar getInt", e);
		}
	}
	
	// Devolve null se a coluna estiver NULL (getInt devolveria 0)
	public Integer getNullableInt(String column) throws ModelException {
		try {
			int value = resultSet.getInt(column);
			return resultSet.wasNull() ? null : value;
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao chamar getNullableInt", e);
		}
	}
	
	public double getDouble(String column) throws ModelException {
		try {
			return resultSet.getDouble(column);
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao chamar getDouble", e);
		}
	}
	
	public boolean getBoolean(String column) throws ModelException {
		try {
			return resultSet.getBoolean(column);
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao chamar getBoolean", e);
		}
	}
	
	public String getString(String column) throws ModelException {
		try {
			return resultSet.getString(column);
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao chamar getString", e);
		}
	}
	
	public Date getDate(String column) throws ModelException {
		try {
			return resultSet.getDate(column);
		} catch (SQLException e) {
			close();
			throw new ModelException("Erro ao chamar getDate", e);
		}
	}
	
	// Público para que os DAOs possam fechar a conexão ao terminar uma consulta
	// (antes, só executeUpdate() fechava a conexão e as consultas deixavam abertas).
	public void close() throws ModelException {
		try {
			if (preparedStatement != null)
				preparedStatement.close();
			
			if (statement != null)
				statement.close();
			
			if (resultSet != null)
				resultSet.close();
			
			connection.close();
		} catch (SQLException e) {
			throw new ModelException("Erro ao fechar recursos", e);
		}
	}
	
	private void validatePreparedStatement() throws ModelException {
		if (preparedStatement == null)
			throw new ModelException("Preparede Statement não foi criado");
	}

	private void validateStatement() throws ModelException {
		if (statement == null)
			throw new ModelException("Statement não foi criado");
	}

	private void validateResultSet() throws ModelException {
		if (resultSet == null)
			throw new ModelException("Result set não foi criado");
	}
}
