package model;

/**
 * Novo cadastro 1: veículo de um cliente.
 * Cada atributo corresponde a um input HTML diferente no formulário
 * (veja form-vehicle.jsp) e a uma coluna da tabela "vehicles".
 */
public class Vehicle {
	private int id;
	private String model;           // input text
	private int modelYear;          // input number
	private String color;           // input color: guarda "#RRGGBB"
	private String category;        // input radio: HATCH, SEDAN, SUV ou PICKUP
	private boolean ceramicCoating; // input checkbox
	private User user;              // select (FK -> users): o proprietário

	public Vehicle() {
		this(0);
	}

	public Vehicle(int id) {
		this.id = id;
		setModel("");
		setColor("#000000");
		setCategory("");
	}

	public int getId() {
		return id;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getModelYear() {
		return modelYear;
	}

	public void setModelYear(int modelYear) {
		this.modelYear = modelYear;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	// Para atributos boolean o getter começa com "is" (JavaBeans);
	// na JSP, ${vehicle.ceramicCoating} chama este método.
	public boolean isCeramicCoating() {
		return ceramicCoating;
	}

	public void setCeramicCoating(boolean ceramicCoating) {
		this.ceramicCoating = ceramicCoating;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
