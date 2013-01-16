package sp.drinkmixer;

import java.util.ArrayList;

public class User {
	
	/*
	 * Name of the user
	 */
	private String name;
	
	/*
	 * Body weight of the user in kg
	 */
	private int weight;
	/*
	 * Age of person
	 */
	private int age;
	
	/*
	 * size of person in cm
	 */
	private int size;
	
	/*
	 * sex of person (true = male)
	 */
	
	private boolean sex;
	
	/*
	 * ArrayList containing all the drinks the user has drunk since db creation (with timestamp)
	 */
	private ArrayList<DrinkLogEntry> allTimeDrinkLog;
	/*
	 * ArrayList containing all the drinks the user has drunk in the current session (with timestamp)
	 */
	private ArrayList<DrinkLogEntry> drinkLog;
	
	/*
	 * Drinks status list. Contains all the drinks drunk by the user with the respecting count.
	 */
	
	private ArrayList<DrinkCount> drinksStatus;
	public ArrayList<DrinkCount> getDrinksStatus() {
		return drinksStatus;
	}


	private ArrayList<DrinkCount> allTimeDrinksStatus;
	
	/*
	 * Number of drinks drunk by the user
	 */
	private int drinkCount;
	private int allTimeDrinkCount;
	
	/*
	 * pure alcohol in ml
	 */
	
	private double pureAlcohol;
	private double allTimePureAlcohol;
	
	/*
	 * equivalent alcohol in 0,33 beers
	 */
	private double beerEquivalent;
	private double allTimebeerEquivalent;

	/*
	 * blood alcohol concentration in 1/1000
	 * based on body weight and how much the user has eaten
	 */
	
	private double promille;
	private double allTimepromille;
	
	/*
	 * cost of the drinks drunk
	 */
	
	private double costs;
	


	private double allTimeCosts;
	
	
	/**
	 * Creates a new user
	 * @param name of the user
	 */
	public User(){
		this.name = "";
		this.weight = 75;
		this.age = 25;
		this.size = 180;
		this.sex = true;
		
		this.drinkCount = 0;
		this.pureAlcohol = 0;
		this.beerEquivalent = 0;
		this.promille = 0;
		this.costs = 0;
		this.allTimebeerEquivalent = 0;
		this.allTimeCosts = 0;
		this.allTimeDrinkCount = 0;
		this.allTimepromille = 0;
		this.allTimePureAlcohol = 0;
		this.drinkLog = new ArrayList<DrinkLogEntry>();
		this.allTimeDrinkLog = new ArrayList<DrinkLogEntry>();
		this.drinksStatus = new ArrayList<DrinkCount>();
		this.allTimeDrinksStatus = new ArrayList<DrinkCount>();
		
	}
	


	
	public void newSession(){
		
		allTimeDrinkLog.addAll(drinkLog);
		drinkLog.clear();
		
		
		for(DrinkCount drinkCurrent:drinksStatus){
		
			boolean found = false;
			
			for(DrinkCount drinkAllTime:allTimeDrinksStatus){
			
				//if the names are the same
				if(drinkCurrent.getName().compareTo(drinkAllTime.getName()) == 0){
					drinkAllTime.setCount(drinkAllTime.getCount() + drinkCurrent.getCount());
					found = true;
					break;
				}
				
				
			}
			
			if(!found){
				
				allTimeDrinksStatus.add(drinkCurrent);
			}
		}
		
		drinksStatus = new ArrayList<DrinkCount>();
		
		allTimeDrinkCount += drinkCount;
		drinkCount = 0;
		
		allTimebeerEquivalent += beerEquivalent;
		beerEquivalent = 0;
		
		allTimepromille += promille;
		promille = 0;
		
		allTimePureAlcohol += pureAlcohol;
		pureAlcohol = 0;
		
	}
	
	public void resetAllStatistics() {
		
		
		drinkCount = 0;
		beerEquivalent = 0;
		promille = 0;
		pureAlcohol = 0;
		
		costs = 0;
		
		drinkLog = new ArrayList<DrinkLogEntry>();
		drinksStatus = new ArrayList<DrinkCount>();
		
		allTimebeerEquivalent = 0;
		allTimeCosts = 0;
		allTimeDrinkCount = 0;
		allTimeDrinkLog = new ArrayList<DrinkLogEntry>();
		allTimeDrinksStatus = new ArrayList<DrinkCount>();
		allTimepromille = 0;
		allTimePureAlcohol = 0;
		
	}
	
	
	public void drink(Drink drink){
		
		
		
		drinkCount++;
		
		
		/*
		 * Check if drink has already been drunk by the user.
		 * If yes just increment, if not create a new DrinkCount Object in the drinksStatus ArrayList
		 */
		boolean found = false;
		
		for(DrinkCount entry:drinksStatus){
			
			//if the names are the same
			if(entry.getName().compareTo(drink.getName()) == 0){
				entry.incrementCount();
				found = true;
				break;
			}
			
		}
		
		//drink not jet drunk by the user
		if(!found){
			
			DrinkCount newDrink = new DrinkCount(drink);
			newDrink.incrementCount();
			
			drinksStatus.add(newDrink);
		}
		
		
		
		pureAlcohol += drink.getAmountOfAlcohol();
		costs += drink.getPrice();
		
		
		beerEquivalent = calcBeerEquivalent();
		promille = calcPromille();
		
		
		drinkLog.add(new DrinkLogEntry(drink, this));
		
	}
	
	private double calcPromille() {
		
		//Calc GKW (Total amount of water in body in l) see. Watson-Formula Wikipedia
		//http://de.wikipedia.org/wiki/Blutalkoholkonzentration#Berechnung_der_BAK
		double GKW = 0;
		
		if(isMale()){
			
			GKW = 2.447 - 0.09516*getAge()+0.1074*getSize()+0.3362*getWeight();
			
		}else{
			
			GKW = 0.203 - 0.07*getAge()+0.1069*getSize()+0.2466*getWeight();
			
		}
		
		//Calc distribution factor
		
		final double bloodDensity = 1.005;
		final double fractionOfWaterInBlood = 0.8;
		
		double distributionFactor = (bloodDensity * GKW) / (fractionOfWaterInBlood * getWeight());
				
		final double alcoholDensity = 0.8;
				
		double MassOfAlcohol = getPureAlcohol() * alcoholDensity;		
		
		
		//Widmark-Formular for theoretical maximum BAC in 1/1000
		
		double BAC = MassOfAlcohol / (getWeight() * distributionFactor);
		
		return BAC;
	}




	private double calcBeerEquivalent() {
		//in 0,33 bottle, 5.1% alcohol
		
		//calculate alcohol in one beer
		double alcoholInBeerInMl = 330*0.051;

		return pureAlcohol/alcoholInBeerInMl;
	}




	public void payForDrinks(){
		
		//TODO generate Paypal payment
		
		/*Only if Paypal Payment is successfull reset costs
		allTimeCosts = costs;
		costs = 0;
		*/
	}


	
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}

	public ArrayList<DrinkLogEntry> getDrinkLog() {
		return drinkLog;
	}
	public int getAge() {
		return age;
	}




	public void setAge(int age) {
		this.age = age;
	}




	public int getSize() {
		return size;
	}




	public void setSize(int size) {
		this.size = size;
	}




	public boolean isMale() {
		return sex;
	}




	public void setSex(boolean sex) {
		this.sex = sex;
	}




	public void setDrinkLog(ArrayList<DrinkLogEntry> drinkLog) {
		this.drinkLog = drinkLog;
	}
	
	public int getDrinkCount() {
		return drinkCount;
	}

	public double getPureAlcohol() {
		return pureAlcohol;
	}

	public double getBeerEquivalent() {
		return beerEquivalent;
	}

	public double getPromille() {
		return promille;
	}
	
	public double getCosts() {
		return costs;
	}
	
	
	public int getAllTimeDrinkCount() {
		return allTimeDrinkCount;
	}




	public double getAllTimePureAlcohol() {
		return allTimePureAlcohol;
	}




	public double getAllTimebeerEquivalent() {
		return allTimebeerEquivalent;
	}




	public double getAllTimepromille() {
		return allTimepromille;
	}




	public double getAllTimeCosts() {
		return allTimeCosts;
	}




	public User makeSnapshot() {

		User user = new User();
		
		user.costs = this.costs;
		
		user.drinkCount = this.drinkCount;
		
		user.promille = this.promille;
		
		user.beerEquivalent = this.beerEquivalent;

		return user;
	}

	
	class DrinkCount{
		
		private String name;
		

		private int count;
		
		public DrinkCount(Drink drink){
			this.name = drink.getName();
			this.count = 0;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getCount() {
			return count;
		}

		public void incrementCount(){
			this.count++;
		}
		
		public void setCount(int count) {
			this.count = count;
		}
	}


	
}
