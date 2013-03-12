/*
 * Copyright (C) 2013 Steffen Pfiffner
 * 
 * Licence: GPL v3
 */

package sp.drinkmixer;

import java.text.DecimalFormat;

import android.os.AsyncTask;

public class MotorControlAsyncTask extends AsyncTask<Void, Double, Double> {

	DrinkMixerActivity activity;
	DrinkMixer drinkMixer;

	boolean abort = false;
	
	private int currentSetPoint = 0;

	//Distance between the center of the shot glasses in mm
	public final static int DISTANCE_BETWEEN_SHOT_GLASSES = 43;
	
	//Distance between the center of the first shot glass and init position in mm
	public final static int DISTANCE_BETWEEN_FIRST_GLASS_AND_INIT_POSITION = 27;
	
	//Time to fill a shot in ms TODO: use the calculated time the valve is open instead
	public final static int TIME_TO_FILL_SHOT = 4000;

	public MotorControlAsyncTask(DrinkMixerActivity activity) {

		this.drinkMixer = activity.drinkMixer;
		this.activity = activity;

	}
	
	@Override
	protected Double doInBackground(Void... params) {
	
		
	
		try {
			
			moveRight(DISTANCE_BETWEEN_FIRST_GLASS_AND_INIT_POSITION);
			
			//TODO: use a callback when movement is finished instead
			Thread.sleep(5000);
			
			
			Drink drink = drinkMixer.getDrinkByName("Wasser Shot");
			//Drink drink = drinkMixer.g
			if(drink != null){
				drink.mix(activity);
			}
			
			Thread.sleep(TIME_TO_FILL_SHOT);
			
			moveLeft(DISTANCE_BETWEEN_SHOT_GLASSES);
			
			//Fill second
			Thread.sleep(10000);
			
			if(drink != null){
				drink.mix(activity);
			}
			
			Thread.sleep(TIME_TO_FILL_SHOT);
			
			moveToInitPosition();
			
	
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		}
		
		
	
		if (isCancelled()) {
	
			return 0.0;
		}
	
		//publishProgress(value);
	
		return 0.0;
	
	}

	/*
	 * move distance mm to right
	 */
	private void moveRight(int distance){
		
		currentSetPoint += convertMmToEncoderPulses(distance);
		
		drinkMixer.sendECMDCommand("hbridge setpoint " + currentSetPoint);
		
	}
	
	/*
	 * move distance mm to left
	 */
	private void moveLeft(int distance){
		currentSetPoint -= convertMmToEncoderPulses(distance);
		
		drinkMixer.sendECMDCommand("hbridge setpoint " + currentSetPoint);
	}
	
	private void moveToInitPosition(){
		
		drinkMixer.sendECMDCommand("hbridge setpoint " + 0);
		
	}
	
	private int convertMmToEncoderPulses(int distanceInMm){
		
		//calculate distance of one encoder pulse in mm (10mm / encoder pulses per cm) and multiply with distance.
		double steps = (double)distanceInMm / ((double)10/ (double)DrinkMixer.MOTOR_CONTROL_ENCODER_PULSES_PER_CM);
		
		int stepsInt = (int) steps;
		
		return stepsInt;
	}

	@Override
	protected void onPostExecute(Double result) {

		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Double... values) {

		if (activity.settingsFragment != null
				&& activity.settingsFragment.isVisible()) {

			DecimalFormat df = new DecimalFormat("#.####");
			
			activity.settingsFragment.setPressureValue(df.format(values[0]));
		}

		super.onProgressUpdate(values);
	}

}
