package sp.drinkmixer;

import java.util.ArrayList;

import li.rudin.ethernetcontrol.ethersex.device.EthersexTCPDevice;
import li.rudin.ethernetcontrol.ethersex.ecmd.EthersexAnalogIO;
import li.rudin.ethernetcontrol.ethersex.ecmd.EthersexDigitalIO;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

public class DrinkMixer {

	private DataModel data;

	/*
	 * AVR Net IO
	 */
	private EthersexTCPDevice dev;

	int oldNetworkId;

	AsyncTask<String, Void, Boolean> connectTask;

	/*
	 * digital IO ports of AVR Net IO
	 */
	private EthersexDigitalIO digitalIO;

	private EthersexAnalogIO analogIO;

	public EthersexAnalogIO getAnalogIO() {
		return analogIO;
	}

	public void setAnalogIO(EthersexAnalogIO analogIO) {
		this.analogIO = analogIO;
	}

	private boolean connectedToNETIO = false;

	private DrinkMixerActivity activity;

	private ArrayList<MixDrinkAsyncTask> runningIngredientTasks = new ArrayList<MixDrinkAsyncTask>();

	AsyncTask<Void, Double, Double> inputSensorThread;

	public AsyncTask<Void, Double, Double> getPressureSensorThread() {
		return inputSensorThread;
	}

	public void setPressureSensorThread(
			AsyncTask<Void, Double, Double> pressureSensorThread) {
		this.inputSensorThread = pressureSensorThread;
	}

	public Ingredient lastAddedIngredient;

	private boolean isCleaning;

	public String wlanSSID = "WLAN";
	
	
	//acutator and sensor names
	
	public static final int horizontalMotorOnOffPin = 15;
	
	/*
	 * false = left, true = right
	 */
	public static final int horizontalMotorDirectionPin = 14;
	public static final int horizontalMotorLeftContactSensor = 0;

	/*
	 * The drink which was last selected in drinksFragment.
	 */

	Drink selectedDrink;
	
	
	/*
	 * the currently acitve user
	 */
	private User currentUser;

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	DrinkMixer(DrinkMixerActivity activity) {
		this.activity = activity;

		data = new DataModel();

	}

	public Drink newDrink(String name) {

		Drink drink = new Drink(name);

		data.drinks.add(drink);

		return drink;
	}

	public Drink getDrinkByName(String name) {

		for (Drink drink : data.drinks) {

			if (drink.getName() == name) {
				return drink;
			}

		}

		System.out.println("Drink with name " + name + "not found");

		return null;

	}

	public Drink getDrinkByPosition(int position) {

		return data.drinks.get(position);

	}

	public Ingredient newIngredient(String name, int amount) {

		Ingredient ingredient = new Ingredient(name, amount);

		data.ingredients.add(ingredient);

		return ingredient;

	}

	public Ingredient newIngredient() {

		Ingredient ingredient = new Ingredient();

		data.ingredients.add(ingredient);

		return ingredient;
	}

	public void setConfiguration(int portNumber, Ingredient ingredient) {

		data.configuration.set(portNumber, ingredient);

	}

	/*
	 * initialize the configuration for 11 valves
	 * 
	 * no more needed if configuration is loaded from db
	 */
	public void initConfiguration() {

		for (int i = 0; i < 11; i++) {

			addValve();

		}

	}

	public void addValve() {

		Ingredient emptyIng = new Ingredient("leer", 0);

		data.configuration.add(emptyIng);

		data.valveStates.add(false);
	}

	public void openValveForIngredient(Ingredient ingredient) {

		openValve(data.configuration.indexOf(ingredient));

	}

	public void closeValveOfIngredient(Ingredient ingredient) {

		closeValve(data.configuration.indexOf(ingredient));

	}
	
	public void sendECMDCommand(String command){
		
		if (isConnectedToNETIO()) {
			// Set pin PortNumber on port c to true
			new SendECMDCommandToNetIOAsyncTask(activity).execute(command);

			
		} else {
			System.out.println("Open Valve: Not connected to NET IO");
		}
		
	}

	public void openValve(int portNumber) {

		

		if (isConnectedToNETIO()) {
			// Set pin PortNumber on port c to true
			new SendValveCommandToNetIOAsyncTask(activity).execute(portNumber, 1);

			
		} else {
			System.out.println("Open Valve: Not connected to NET IO");
		}

	}

	public void closeValve(int portNumber) {

		

		if (isConnectedToNETIO()) {
			// Set pin PortNumber on port c to false
			new SendValveCommandToNetIOAsyncTask(activity).execute(portNumber, 0);

			
		} else {
			System.out.println("Close Valve: Not connected to NET IO");
		}

	}

	public void connectToNetIO() {

		if (connectTask != null) {
			connectTask.cancel(true);
		}

		// default port of ethernetcontrol library and ethersex is 2701
		connectTask = new ConnectToNetIO(activity).execute("192.168.178.90");

	}

	public void disconnectFromNetIO() {

		stopReadInputValuesThread();

		WifiManager wifimanager = (WifiManager) activity
				.getSystemService(Context.WIFI_SERVICE);

		wifimanager.enableNetwork(oldNetworkId, true);

		System.out.println("Connected to "
				+ wifimanager.getConnectionInfo().getSSID());

		// Close device properly
		if (dev != null) {
			dev.close();
		}

		stopAllRunningTasks();
		
		

		setConnectedToNETIO(false);

	}

	public ArrayList<Drink> getDrinks() {
		return data.drinks;
	}

	public void setDrinks(ArrayList<Drink> drinks) {
		this.data.drinks = drinks;
	}

	public ArrayList<Ingredient> getConfiguration() {
		return data.configuration;
	}

	public void setConfiguration(ArrayList<Ingredient> configuration) {
		this.data.configuration = configuration;
	}

	public boolean getValveState(int number) {
		return data.valveStates.get(number);
	}

	public void setValveState(int number, boolean state) {
		data.valveStates.set(number, state);
	}

	public ArrayList<Ingredient> getIngredients() {
		return data.ingredients;
	}

	/**
	 * @param ingredient
	 *            to delete
	 * @return true if ingredient was deleted successfully, false if the
	 *         ingredient is sill in a drink and could not be deleted
	 */
	public boolean deleteIngredient(Ingredient ingredient) {

		// check if ingredient is in a drink

		for (Drink drink : data.drinks) {

			for (IngredientInDrink ingredientInDrink : drink.getIngredients()) {
				if (ingredientInDrink.ingredient == ingredient) {

					// ingredient is in at least one drink
					return false;
				}
			}
		}

		// ingredient is not in a drink, can be deleted
		data.ingredients.remove(ingredient);

		return true;
	}

	public String[] getIngredientsAsStringArray() {

		String[] ingredients = new String[getIngredients().size()];

		int i = 0;

		for (Ingredient ingredient : getIngredients()) {

			ingredients[i] = ingredient.getName();
			i++;
		}

		return ingredients;

	}

	public void setIngredients(ArrayList<Ingredient> ingredients) {
		this.data.ingredients = ingredients;
	}

	public EthersexTCPDevice getDev() {
		return dev;
	}

	public void setDev(EthersexTCPDevice dev) {
		this.dev = dev;
	}

	public EthersexDigitalIO getDigitalIO() {
		return digitalIO;
	}

	public void setDigitalIO(EthersexDigitalIO digitalIO) {
		this.digitalIO = digitalIO;
	}

	public ArrayList<MixDrinkAsyncTask> getRunningTasks() {
		return runningIngredientTasks;
	}

	public boolean isCleaning() {
		return isCleaning;
	}

	public void setCleaning(boolean isCleaning) {
		this.isCleaning = isCleaning;
	}

	public void setConnectedToNETIO(boolean connectedToNETIO) {
		this.connectedToNETIO = connectedToNETIO;

	}

	public boolean isConnectedToNETIO() {
		return connectedToNETIO;
	}

	public Drink getSelectedDrink() {
		return selectedDrink;
	}

	public void setSelectedDrink(Drink selectedDrink) {
		this.selectedDrink = selectedDrink;
	}

	public DataModel getDataModel() {
		return data;
	}

	public void setData(DataModel data) {
		this.data = data;
	}

	public void stopAllRunningTasks() {

		// stop all tasks (handled in oncanceled eventhandler in
		// MixDrinkAsyncTask)
		for (MixDrinkAsyncTask task : runningIngredientTasks) {

			task.cancel(true);

		}

	}

	public void clean() {

		isCleaning = true;

		for (Ingredient ingredient : data.configuration) {

			IngredientInDrink ingredientInDrink = new IngredientInDrink(
					ingredient, 20);

			runningIngredientTasks
					.add((MixDrinkAsyncTask) new MixDrinkAsyncTask(activity,
							new Drink("clean"), ingredientInDrink)
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
									ingredientInDrink));

		}

	}

	/*
	 * create new session (execute newSession() for all drinks and ingredients)
	 * 
	 * resets sessionCounts/Amounts to 0
	 */
	public void newSession() {

		for (Drink drink : getDrinks()) {

			drink.newSession();

		}

		for (Ingredient ingredient : getIngredients()) {

			ingredient.newSession();

		}
	}

	public void deleteSelectedDrink() {

		data.drinks.remove(selectedDrink);

	}

	public void startReadInputValuesThread() {

		inputSensorThread = new ReadInputValuesAsyncTask(activity)
		.executeOnExecutor(
				AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public void stopReadInputValuesThread() {

		if (inputSensorThread != null) {
			inputSensorThread.cancel(true);
		}
	}

	public void duplicateDrink(Drink selectedDrink) {

		try {
			data.drinks.add((Drink) selectedDrink.clone());
		} catch (CloneNotSupportedException e) {

			activity.showErrorDialog("Error: " + e.getMessage());

			e.printStackTrace();
		}

	}
	
	public User newUser(){
		
		User newUser = new User(); 
		data.users.add(newUser);
		return newUser;
	}
	
	public void deleteUser(User number){
		data.users.remove(number);
		
	}
	public void selectUser(int number){
		currentUser = data.users.get(number);
	}
	
	public ArrayList<User> getUsers(){
		
		return data.users;
		
	}

}
