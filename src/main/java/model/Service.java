package model;

import java.util.Date;

/**
 * Serviço de estética automotiva realizado em um veículo.
 * Tabela "services". Só funcionários/chefe alteram; o cliente apenas acompanha o status.
 */
public class Service {
	public static final String SCHEDULED = "AGENDADO";
	public static final String IN_PROGRESS = "EM_ANDAMENTO";
	public static final String DONE = "CONCLUIDO";

	private int id;
	private String type;       // input radio: LAVAGEM, POLIMENTO, VITRIFICACAO ou HIGIENIZACAO
	private Date serviceDate;  // input date
	private double price;      // input number (step 0.01)
	private String details;    // textarea
	private String status;     // select: AGENDADO, EM_ANDAMENTO ou CONCLUIDO
	private boolean paid;      // input checkbox
	private Vehicle vehicle;   // select (FK -> vehicles)
	private Employee employee; // select (FK -> employees): quem executa; pode ser null
	private boolean rated;     // já tem feedback? (calculado na consulta, não é coluna)

	public Service() {
		this(0);
	}

	public Service(int id) {
		this.id = id;
		setType("");
		setDetails("");
		setStatus(SCHEDULED);
	}

	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getServiceDate() {
		return serviceDate;
	}

	public void setServiceDate(Date serviceDate) {
		this.serviceDate = serviceDate;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public boolean isRated() {
		return rated;
	}

	public void setRated(boolean rated) {
		this.rated = rated;
	}

	public boolean isFinished() {
		return DONE.equals(status);
	}

	public boolean isInProgress() {
		return IN_PROGRESS.equals(status);
	}

	public static boolean isValidStatus(String status) {
		return SCHEDULED.equals(status) || IN_PROGRESS.equals(status) || DONE.equals(status);
	}

	public static boolean isValidType(String type) {
		return "LAVAGEM".equals(type) || "POLIMENTO".equals(type)
				|| "VITRIFICACAO".equals(type) || "HIGIENIZACAO".equals(type);
	}
}
