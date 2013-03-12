/*
 * Copyright (C) 2013 Steffen Pfiffner
 * 
 * Licence: GPL v3
 */

package sp.drinkmixer;

import java.util.Comparator;

public class UserComparator implements Comparator<User> {

	/*
	 * 0 = pure alcohol 1 = promille 2 = drinks count 3 = price 4 =
	 * alphabetically
	 */
	private int sortCriterium;

	public UserComparator(int sortCriterium) {

		this.sortCriterium = sortCriterium;

	}

	@Override
	public int compare(User lower, User higher) {

		switch (sortCriterium) {

		// 0 = pure alcohol
		case 0:
			
			if(higher.getPureAlcohol() > lower.getPureAlcohol()){
				return 1;
			}
			
			if(higher.getPureAlcohol() < lower.getPureAlcohol()){
				return -1;
			}
			
			if(higher.getPureAlcohol() == lower.getPureAlcohol()){
				return 0;
			}


		// 1 = promille
		case 1:
			
			if(higher.getPromille() > lower.getPromille()){
				return 1;
			}
			
			if(higher.getPromille() < lower.getPromille()){
				return -1;
			}
			
			if(higher.getPromille() == lower.getPromille()){
				return 0;
			}



		// 2 = drinks count
		case 2:
			
			if(higher.getDrinkCount() > lower.getDrinkCount()){
				return 1;
			}
			
			if(higher.getDrinkCount() < lower.getDrinkCount()){
				return -1;
			}
			
			if(higher.getDrinkCount() == lower.getDrinkCount()){
				return 0;
			}

			
			

			return 0;
			
			
	    // 3 = costs
		case 3:
			
			if(higher.getCosts() > lower.getCosts()){
				return 1;
			}
			
			if(higher.getCosts() < lower.getCosts()){
				return -1;
			}
			
			if(higher.getCosts() == lower.getCosts()){
				return 0;
			}
			
		// 4 = alphabetically
		case 4:
			
			return lower.getName().compareTo(higher.getName());

		

		default:
			
			
			return 0;

		}
		
		

	}

}
