/*
 * Copyright (C) 2013 Steffen Pfiffner
 * 
 * Licence: GPL v3
 */

package sp.drinkmixer;

import java.util.ArrayList;

import android.os.AsyncTask;

public class Drink {

	String name;

	/*
	 * How often was this drink mixed db creation
	 */
	private int totalUsageCount;

	/*
	 * How often was this drink mixed since session started
	 */
	private int sessionUsageCount;

	private int glassSize = 0;

	private ArrayList<IngredientInDrink> ingredients;

	public Drink(String name) {

		this.name = name;

		this.ingredients = new ArrayList<IngredientInDrink>();

		this.totalUsageCount = 0;
		this.sessionUsageCount = 0;
		this.glassSize = 0;

	}

	/*
	 * @param amount in ml
	 */
	public void addIngredient(Ingredient ingredient, int amount) {

		ingredients.add(new IngredientInDrink(ingredient, amount));

	}

	public IngredientInDrink addIngredient() {

		IngredientInDrink ingredientInDrink = new IngredientInDrink();

		ingredients.add(ingredientInDrink);

		return ingredientInDrink;

	}

	public void deleteIngredient(int position) {

		ingredients.remove(position);

	}

	public void setIngredientAtPosition(int position, Ingredient ingredient) {

		ingredients.get(position).ingredient = ingredient;

	}

	public String getIngredientNameAtPosition(int position) {

		return ingredients.get(position).ingredient.getName();
	}

	public void setIngredientNameAtPosition(int position, String name) {

		ingredients.get(position).ingredient.setName(name);
	}

	public int getIngredientAmountAtPosition(int position) {

		return ingredients.get(position).getAmount();
	}

	public void setIngredientAmountAtPosition(int position, int amount) {

		ingredients.get(position).setAmount(amount);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<IngredientInDrink> getIngredients() {
		return ingredients;
	}

	public void setIngredients(ArrayList<IngredientInDrink> ingredients) {
		this.ingredients = ingredients;
	}

	public void incrementUsageCount() {

		this.totalUsageCount++;
		this.sessionUsageCount++;
	}

	public int getSessionUsageCount() {
		return sessionUsageCount;
	}

	public int getTotalUsageCount() {
		return totalUsageCount;
	}

	public void mix(DrinkMixerActivity activity) {

		// check if all ingredients of drink are in the current
		// configuration

		for (IngredientInDrink ingredientInDrink : this.ingredients) {

			if (activity.drinkMixer.getConfiguration().contains(
					ingredientInDrink.ingredient)) {
				// OK ingredient is in configuration

				// Is ingredient empty?

				if (ingredientInDrink.ingredient.getCurrentAmount() < ingredientInDrink.amount) {

					// Ingredient is empty
					System.out.println("Ingredient "
							+ ingredientInDrink.ingredient.getName()
							+ " is empty");

					activity.showIngredientEmptyRefillDecitionDialog(ingredientInDrink.ingredient);

					activity.openDrinksFragment();

					return;

				}

			} else {
				// Ingredient is not in configuration
				System.out.println("Ingredient "
						+ ingredientInDrink.ingredient.getName()
						+ " is not in the current configuration");

				activity.showErrorDialog("Zutat "
						+ ingredientInDrink.ingredient.getName()
						+ " ist nicht in der aktuellen Belegung");

				activity.openDrinksFragment();

				return;
			}

		}

		// Open valve of each ingredient in drink for length calculated by
		// amount and flowrate
		
		System.out.println("Mixing " + getName());

		for (IngredientInDrink ingredientInDrink : this.ingredients) {

			activity.drinkMixer.getRunningTasks().add(
					(MixDrinkAsyncTask) new MixDrinkAsyncTask(activity, this,
							ingredientInDrink).executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, ingredientInDrink));

		}

		return;

	}

	public void newSession() {

		sessionUsageCount = 0;

	}

	public int getTotalAmountOfAllIngredients() {
		int totalAmount = 0;

		for (IngredientInDrink ingredient : ingredients) {

			totalAmount += ingredient.amount;
		}

		return totalAmount;
	}

	public int getGlassSize() {
		return glassSize;
	}

	public void setGlassSize(int glassSize) {
		this.glassSize = glassSize;
	}

	public double getAmountOfAlcohol() {
		double alcoholAmount = 0;
		
		for (IngredientInDrink ingredientInDrink: ingredients){
			
			alcoholAmount += ingredientInDrink.amount * (ingredientInDrink.ingredient.getAlcoholConcentration() / 100.0d);
			
		}

		return alcoholAmount;
	}

	public double getPrice() {
		double price = 0;
		
		for (IngredientInDrink ingredientInDrink: ingredients){
			
			price += ingredientInDrink.amount * (ingredientInDrink.ingredient.getPricePerL()/1000.0d);
			
		}

		return price;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		Drink drink = new Drink(this.name + " 2");

		drink.glassSize = this.glassSize;

		for (IngredientInDrink ingredientInDrink : ingredients) {

			drink.ingredients
					.add((IngredientInDrink) ingredientInDrink.clone());
		}

		return drink;
	}
}
