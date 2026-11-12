package model.dao;

import java.util.List;

import model.Feedback;
import model.ModelException;

public interface FeedbackDAO {
	boolean save(Feedback feedback) throws ModelException;
	boolean update(Feedback feedback) throws ModelException;
	boolean delete(Feedback feedback) throws ModelException;
	Feedback findById(int id) throws ModelException;
	List<Feedback> listPage(int limit, int offset) throws ModelException;
	int count() throws ModelException;
	// Só os feedbacks de um cliente (o cliente nunca vê os dos outros)
	List<Feedback> listPageByUser(int userId, int limit, int offset) throws ModelException;
	int countByUser(int userId) throws ModelException;
}
