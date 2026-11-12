package model;

public class User {
	private int id;
	private String name;
	private String gender;
	private String email;
	// Hash SHA-256 da senha (nunca a senha em texto puro). Usado no login.
	private String password;
	// Perfil de acesso: CHEFE, FUNCIONARIO ou CLIENTE
	private String role;
	
	public static final String BOSS = "CHEFE";
	public static final String EMPLOYEE = "FUNCIONARIO";
	public static final String CLIENT = "CLIENTE";
	
	public User() {
		this(0);
	}
	
	public User(int id) {
		this.id = id;
		setName("");
		setGender("");
		setEmail("");
		setPassword("");
		setRole(CLIENT); // menor privilégio por padrão
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	// Os métodos isXxx() viram propriedades na JSP: ${loggedUser.boss}, ${loggedUser.staff}...
	public boolean isBoss() {
		return BOSS.equals(role);
	}

	// "staff" = quem trabalha na empresa (chefe ou funcionário)
	public boolean isStaff() {
		return BOSS.equals(role) || EMPLOYEE.equals(role);
	}

	public boolean isClient() {
		return CLIENT.equals(role);
	}

	public String getRoleLabel() {
		if (isBoss()) return "Chefe";
		if (isStaff()) return "Funcionário";
		return "Cliente";
	}

	public static boolean isValidRole(String role) {
		return BOSS.equals(role) || EMPLOYEE.equals(role) || CLIENT.equals(role);
	}
}
