/*
 * Copyright (C) 2013 Steffen Pfiffner
 * 
 * Licence: GPL v3
 */

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

	// the ssid of the drinkmixers wlan router
	public static final String WLAN_SSID = "WLAN";

	// the avr net io's ip address
	public static final String AVR_NET_IO_IP_ADDRESS = "192.168.178.90";

	/*
	 * digital IO ports of AVR Net IO
	 */
	private EthersexDigitalIO digitalIO;

	private EthersexAnalogIO analogIO;

	private boolean connectedToNETIO = false;

	// saves network id to reconnect to the wlan the device was connected before
	int oldNetworkId;

	private DrinkMixerActivity activity;

	// List of currently running mixing tasks
	private ArrayList<MixDrinkAsyncTask> activeMixingTasks = new ArrayList<MixDrinkAsyncTask>();

	// indicates if cleaning mode is enabled
	private boolean isCleaning;

	/*
	 * pressure control:
	 */
	AsyncTask<Void, Double, Double> pressureSensor;

	// indicates if pressure control is enabled
	private boolean pressureControlEnabled;

	// indicates the desired pressure in the system in bar
	private double pressureSetPoint = 0.2;

	// sample period in ms
	public static final int PRESSURE_SENSOR_SAMPLING_PRERIOD = 1000;

	// which shift regeister pin is the compressor connected to
	public static final int COMPRESSOR_PIN = 12;

	// switching hysteresis
	//public static final double PRESSURE_SENSOR_HYSTERESIS = 0.002;
	public static final double PRESSURE_SENSOR_HYSTERESIS = 0.01;

	// voltage offset to get a 0 from sensor on ambient pressure
	public static final double PRESSURE_SENSOR_OFFSET = 0.17;

	/*
	 * motor control
	 */

	// encoder pulses per cm. Needed for centimeters to encoder pulses
	// conversion.
	public static final int MOTOR_CONTROL_ENCODER_PULSES_PER_CM = 120;

	// used for positioning by seek bar
	public static final int WORKING_SPACE_WIDTH_IN_ENCODER_PULSES = 3900;

	/*
	 * The drink which was last selected in drinksFragment.
	 */
	Drink selectedDrink;

	/*
	 * the currently active user
	 */
	private User currentUser;

	private AsyncTask<String, Void, Boolean> connectTask;
	private AsyncTask<Void, Double, Double> fillShotsThread;

	public User getCurrentUser() {

		if (currentUser != null) {

			return currentUser;
		} else {
			return getUsers().get(0);
		}
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

			if (drink.getName().compareToIgnoreCase(name) == 0) {
				return drink;
			}

		}

		System.out.println("Drink with name " + name + " not found");

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

	public void sendECMDCommand(String command) {

		if (isConnectedToNETIO()) {
			// Set pin PortNumber on port c to true
			new SendECMDCommandToNetIOAsyncTask(activity).execute(command);

		} else {
			System.out.println("Could not execute " + command + ". Reason: Not connected to NET IO");
		}

	}

	public void openValve(int portNumber) {

		if (isConnectedToNETIO()) {
			// Set pin PortNumber on port c to true
			new SendValveCommandToNetIOAsyncTask(activity).execute(portNumber,
					1);

		} else {
			System.out.println("Open Valve " + portNumber + ": Not connected to NET IO");
		}

	}
	
	public void closeAllValves() {
		for(int i = 0; i<16;i++){
			
			closeValve(i);
		}
		
	}

	public void closeValve(int portNumber) {

		if (isConnectedToNETIO()) {
			// Set pin PortNumber on port c to false
			new SendValveCommandToNetIOAsyncTask(activity).execute(portNumber,
					0);

		} else {
			System.out.println("Close Valve " + portNumber + ": Not connected to NET IO");
		}

	}

	public void connectToNetIO() {

		if (connectTask != null) {
			connectTask.cancel(true);
		}

		// default port of ethernetcontrol library and ethersex is 2701
		connectTask = new ConnectToNetIO(activity)
				.execute(DrinkMixer.AVR_NET_IO_IP_ADDRESS);

	}

	public void disconnectFromNetIO() {

		stopPressureSensorAsyncTask();

		WifiManager wifimanager = (WifiManager) activity
				.getSystemService(Context.WIFI_SERVICE);

		if(!activity.DEBUG_MODE){
			wifimanager.enableNetwork(oldNetworkId, true);
		}

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

		boolean state;

		try {
			state = data.valveStates.get(number);
		} catch (IndexOutOfBoundsException e) {
			state = false;
		}
		return state;
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
		return activeMixingTasks;
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
		for (MixDrinkAsyncTask task : activeMixingTasks) {

			task.cancel(true);

		}

	}

	public void clean() {

		isCleaning = true;

		for (Ingredient ingredient : data.configuration) {

			IngredientInDrink ingredientInDrink = new IngredientInDrink(
					ingredient, 20);

			activeMixingTasks.add((MixDrinkAsyncTask) new MixDrinkAsyncTask(
					activity, new Drink("clean"), ingredientInDrink)
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

	public void startPressureSensorAsyncTask() {

		pressureSensor = new PressureControlAsyncTask(activity)
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public void stopPressureSensorAsyncTask() {

		if (pressureSensor != null) {
			pressureSensor.cancel(true);
		}
	}

	public void startFillShotsThread() {

		// Cancel thread if alread running
		if (fillShotsThread != null) {
			fillShotsThread.cancel(true);
		}

		fillShotsThread = new MotorControlAsyncTask(activity)
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	/*
	 * not needed? public void stopFillShotsThread() { if (fillShotsThread !=
	 * null) { fillShotsThread.cancel(true); }
	 * 
	 * }
	 */

	public void duplicateDrink(Drink selectedDrink) {

		try {
			data.drinks.add((Drink) selectedDrink.clone());
		} catch (CloneNotSupportedException e) {

			activity.showErrorDialog("Error: " + e.getMessage());

			e.printStackTrace();
		}

	}

	public User newUser() {

		User newUser = new User();
		data.users.add(newUser);
		return newUser;
	}

	public void deleteUser(User number) {
		data.users.remove(number);

	}

	public void selectUser(int number) {
		currentUser = data.users.get(number);
	}

	public ArrayList<User> getUsers() {

		return data.users;

	}

	public void setPressureControlEnabled(boolean enabled) {
		this.pressureControlEnabled = enabled;

	}

	public boolean isPressureControlEnabled() {

		return this.pressureControlEnabled;
	}

	public double getPressureSetPoint() {
		return pressureSetPoint;
	}

	public void setPressureSetPoint(double pressureSetPoint) {
		this.pressureSetPoint = pressureSetPoint;
	}

	public EthersexAnalogIO getAnalogIO() {
		return analogIO;
	}

	public void setAnalogIO(EthersexAnalogIO analogIO) {
		this.analogIO = analogIO;
	}



}
