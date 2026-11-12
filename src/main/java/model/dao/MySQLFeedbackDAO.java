package model.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Feedback;
import model.ModelException;

public class MySQLFeedbackDAO implements FeedbackDAO {

	@Override
	public boolean save(Feedback f) throws ModelException {
		DBHandler db = new DBHandler();

		// service_id é UNIQUE: avaliar o mesmo serviço duas vezes lança
		// ModelException com SQLIntegrityConstraintViolationException (o controller trata).
		db.prepareStatement("INSERT INTO feedbacks (rating, comment, feedback_date, service_id, user_id) "
				+ "VALUES (?, ?, CURDATE(), ?, ?);");

		db.setString(1, f.getRating());
		db.setString(2, f.getComment());
		db.setInt(3, f.getService().getId());
		db.setInt(4, f.getUser().getId());

		return db.executeUpdate() > 0;
	}

	// Na edição só a nota e o comentário mudam (serviço e autor continuam os mesmos)
	@Override
	public boolean update(Feedback f) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("UPDATE feedbacks SET rating = ?, comment = ? WHERE id = ?;");
		db.setString(1, f.getRating());
		db.setString(2, f.getComment());
		db.setInt(3, f.getId());

		return db.executeUpdate() > 0;
	}

	@Override
	public boolean delete(Feedback f) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("DELETE FROM feedbacks WHERE id = ?;");
		db.setInt(1, f.getId());

		return db.executeUpdate() > 0;
	}

	@Override
	public Feedback findById(int id) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("SELECT * FROM feedbacks WHERE id = ?;");
		db.setInt(1, id);
		db.executeQuery();

		Feedback f = db.next() ? createFeedback(db) : null;
		db.close();

		return f;
	}

	@Override
	public List<Feedback> listPage(int limit, int offset) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("SELECT * FROM feedbacks ORDER BY feedback_date DESC, id DESC LIMIT ? OFFSET ?;");
		db.setInt(1, limit);
		db.setInt(2, offset);
		db.executeQuery();

		return readList(db);
	}

	@Override
	public int count() throws ModelException {
		DBHandler db = new DBHandler();

		db.createStatement();
		db.executeQuery("SELECT COUNT(*) AS total FROM feedbacks;");

		int total = db.next() ? db.getInt("total") : 0;
		db.close();

		return total;
	}

	@Override
	public List<Feedback> listPageByUser(int userId, int limit, int offset) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("SELECT * FROM feedbacks WHERE user_id = ? "
				+ "ORDER BY feedback_date DESC, id DESC LIMIT ? OFFSET ?;");
		db.setInt(1, userId);
		db.setInt(2, limit);
		db.setInt(3, offset);
		db.executeQuery();

		return readList(db);
	}

	@Override
	public int countByUser(int userId) throws ModelException {
		DBHandler db = new DBHandler();

		db.prepareStatement("SELECT COUNT(*) AS total FROM feedbacks WHERE user_id = ?;");
		db.setInt(1, userId);
		db.executeQuery();

		int total = db.next() ? db.getInt("total") : 0;
		db.close();

		return total;
	}

	private List<Feedback> readList(DBHandler db) throws ModelException {
		List<Feedback> feedbacks = new ArrayList<Feedback>();
		while (db.next()) {
			feedbacks.add(createFeedback(db));
		}
		db.close();

		return feedbacks;
	}

	private Feedback createFeedback(DBHandler db) throws ModelException {
		Feedback f = new Feedback(db.getInt("id"));
		f.setRating(db.getString("rating"));
		f.setComment(db.getString("comment"));
		f.setFeedbackDate(db.getDate("feedback_date"));

		ServiceDAO serviceDAO = DAOFactory.createDAO(ServiceDAO.class);
		f.setService(serviceDAO.findById(db.getInt("service_id")));

		UserDAO userDAO = DAOFactory.createDAO(UserDAO.class);
		f.setUser(userDAO.findById(db.getInt("user_id")));

		return f;
	}
}
