package model;

import java.util.Date;

/**
 * Funcionário da empresa (aba "Equipe"). Substitui a antiga classe Company.
 * Tabela "employees". Está ligado a um User (FUNCIONARIO ou CHEFE).
 */
public class Employee {
	private int id;
	private User user;              // select (FK -> users)
	private String jobTitle;        // input text
	private String specialty;       // select: LAVAGEM, POLIMENTO, VITRIFICACAO, HIGIENIZACAO, GERAL
	private String phone;           // input tel
	private Date hireDate;          // input date
	private String shift;           // input radio: MANHA, TARDE, INTEGRAL
	private boolean active;         // input checkbox
	// Preenchido só nas listagens: serviço EM_ANDAMENTO deste funcionário (null se não houver)
	private Service currentService;

	public Employee() {
		this(0);
	}

	public Employee(int id) {
		this.id = id;
		setJobTitle("");
		setSpecialty("");
		setPhone("");
		setShift("");
		setActive(true);
	}

	public int getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Date getHireDate() {
		return hireDate;
	}

	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}

	public String getShift() {
		return shift;
	}

	public void setShift(String shift) {
		this.shift = shift;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Service getCurrentService() {
		return currentService;
	}

	public void setCurrentService(Service currentService) {
		this.currentService = currentService;
	}

	// Situação calculada: INATIVO, EM_SERVICO ou DISPONIVEL
	public String getAvailability() {
		if (!active) return "INATIVO";
		return currentService != null ? "EM_SERVICO" : "DISPONIVEL";
	}

	public static boolean isValidSpecialty(String s) {
		return "LAVAGEM".equals(s) || "POLIMENTO".equals(s) || "VITRIFICACAO".equals(s)
				|| "HIGIENIZACAO".equals(s) || "GERAL".equals(s);
	}

	public static boolean isValidShift(String s) {
		return "MANHA".equals(s) || "TARDE".equals(s) || "INTEGRAL".equals(s);
	}
}
