package sp.drinkmixer;

public class Ingredient {
	
	private String name;
	
	/*
	 * bootle price
	 */
	private double price;
	
	/*
	 * total amount in bottle in ml
	 */
	private int totalAmount;
	
	/*
	 * the amount left in the bottle
	 */
	private int currentAmount;
	
	
	/*
	 * flow rate in ml/s
	 */
	private double flowRate;
	
	/*
	 * Amount of this ingredient used since db creation in ml
	 */
	private int totalUsageAmount;
	
	/*
	 * Amount of this ingredient used since session started in ml
	 */
	private int sessionUsageAmount;
	
	/*
	 * Percentage of alcohol in drink
	 */
	private double alcoholConcentration;
	
	
	public Ingredient(String name, int totalAmount){
		
		this.name = name;
		
		this.currentAmount = totalAmount;
		this.totalAmount = totalAmount;
		
		this.flowRate = 5.8;
		this.totalUsageAmount = 0;
		this.sessionUsageAmount = 0;
		this.price = 0;
		this.alcoholConcentration = 0;
			
	}
	
	public Ingredient(){
		
		this.name = "";
		this.totalAmount = 1500;
		this.currentAmount = 1500;
		this.flowRate = 5.8;
		this.totalUsageAmount = 0;
		this.sessionUsageAmount = 0;
		this.price = 0;
	}


	
	
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public double getPricePerL() {
		return price;
	}


	public void setPricePerMl(double price) {
		this.price = price;
	}


	public int getTotalAmount() {
		return totalAmount;
	}


	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
		
	}


	public int getCurrentAmount() {
		return currentAmount;
	}


	public void setCurrentAmount(int currentAmount) {
		this.currentAmount = currentAmount;
	}
	
	public double getFlowRate() {
		
		
		
		return flowRate;
	}


	public void setFlowRate(double flowRate) {
		this.flowRate = flowRate;
	}
	
	public int getTotalUsageCountInMl() {
		return totalUsageAmount;
	}

	public void setTotalUsageAmount(int totalUsageAmount) {
		this.totalUsageAmount = totalUsageAmount;
	}
	
	public int getSessionUsageAmount() {
		return sessionUsageAmount;
	}

	/**
	 * Use the ingredient.
	 * Decrements current amount and increments usage statistics.
	 * @param The amount used.
	 */
	public void use(int amount){
		
				
		this.totalUsageAmount += amount;
		this.currentAmount -= amount;
		this.sessionUsageAmount += amount;
		
		
	}

		
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}
	

	public void newSession(){
		
		sessionUsageAmount = 0;
		
	}
	
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		
		Ingredient ingredient = new Ingredient();
		
		ingredient.name = this.name;
		ingredient.totalAmount = this.totalAmount;
		ingredient.currentAmount = this.currentAmount;
		ingredient.flowRate = this.flowRate;
		ingredient.totalUsageAmount = this.totalUsageAmount;
		ingredient.sessionUsageAmount = this.sessionUsageAmount;
		ingredient.price = this.price;
		
	

		return ingredient;
	}

	public void refill() {
		currentAmount = totalAmount;		
	}

	public double getAlcoholConcentration() {
		return alcoholConcentration;
	}

	public void setAlcoholConcentration(double alcoholConcentration) {
		this.alcoholConcentration = alcoholConcentration;
	}


}
