package model;

import java.util.Calendar;
import java.util.Date;

/**
 * Feedback do cliente sobre um serviço concluído (substitui a antiga classe Post).
 * O carro vem do serviço: feedback.service.vehicle.
 */
public class Feedback {
	// Avaliações pré-definidas, da pior para a melhor (a posição + 1 = número de estrelas)
	public static final String[] RATINGS = { "RUIM", "REGULAR", "BOM", "OTIMO", "EXCELENTE" };

	private int id;
	private String rating;
	private String comment;
	private Date feedbackDate;
	private Service service; // FK -> services (e daí o veículo)
	private User user;      // autor (dono do veículo)

	public Feedback() {
		this(0);
	}

	public Feedback(int id) {
		this.id = id;
		setRating("");
		setComment("");
		setFeedbackDate(Calendar.getInstance().getTime());
	}

	public int getId() {
		return id;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getFeedbackDate() {
		return feedbackDate;
	}

	public void setFeedbackDate(Date feedbackDate) {
		this.feedbackDate = feedbackDate;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	// 1 a 5 (0 se a avaliação for inválida); usado para desenhar as estrelas
	public int getStars() {
		for (int i = 0; i < RATINGS.length; i++) {
			if (RATINGS[i].equals(rating)) return i + 1;
		}
		return 0;
	}

	public static boolean isValidRating(String rating) {
		for (String r : RATINGS) {
			if (r.equals(rating)) return true;
		}
		return false;
	}
}
