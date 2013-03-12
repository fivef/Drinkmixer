/*
 * Copyright (C) 2013 Steffen Pfiffner
 * 
 * Licence: GPL v3
 */

package sp.drinkmixer;

public class IngredientInDrink {

	Ingredient ingredient;
	
	/*
	 * amount in ml
	 */
	int amount;
	
	
	public int getAmount() {
		return amount;
	}


	public void setAmount(int amount) {
		this.amount = amount;
	}


	public IngredientInDrink(Ingredient ingredient, int amount){
		
		this.ingredient = ingredient;
		this.amount = amount;
		
		
	}
	
	public IngredientInDrink(){
		
		this.ingredient = new Ingredient();
	}
	
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		IngredientInDrink ingredientInDrink = new IngredientInDrink(this.ingredient, this.amount);
		return ingredientInDrink;
	}
}
