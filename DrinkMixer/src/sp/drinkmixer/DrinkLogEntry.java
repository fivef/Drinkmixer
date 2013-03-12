/*
 * Copyright (C) 2013 Steffen Pfiffner
 * 
 * Licence: GPL v3
 */

package sp.drinkmixer;

import java.util.Date;

public class DrinkLogEntry {
	
	/*
	 * Drink Object
	 */
	private Drink drink;
	/*
	 * Date Object with the time the drink has been made
	 */
	private Date timestamp;

	
	private User userSnapshot;

	
	public DrinkLogEntry(Drink drink, User user){
		
		this.timestamp = new Date();
		

		try {
			this.drink = (Drink) drink.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		this.userSnapshot = user.makeSnapshot();
		

		
	}
	
	
	
	public String getDrinkName() {
		return drink.getName();
	}
	public String getTimestamp() {
		return timestamp.toString();
	}



	public User getUserSnapshot() {
		return userSnapshot;
	}



}
