package sp.drinkmixer;

import java.util.ArrayList;

public class DataModel {

	/**
	 * Array which contains drinks
	 * 
	 */
	public ArrayList<Drink> drinks = new ArrayList<Drink>();
	
	
	
	/*
	 * Array which contains the current configuration of the drink mixer. 
	 * Which ingredient is on which port/valve
	 */
	public ArrayList<Ingredient> configuration = new ArrayList<Ingredient>();
	
	
	/*
	 * array contains the state of the valves
	 */
	public ArrayList<Boolean> valveStates = new ArrayList<Boolean>();
	/*
	 * Array which contains the supported ingredients
	 */
	public ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
	
	/*
	 * Array which contains the users
	 */
	public ArrayList<User> users = new ArrayList<User>();
	
	public DataModel(){
		
		
	}


	
}
