package sp.drinkmixer;

import java.text.DecimalFormat;

import android.os.AsyncTask;

public class MotorControlAsyncTask extends AsyncTask<Void, Double, Double> {

	DrinkMixerActivity activity;
	DrinkMixer drinkMixer;

	boolean abort = false;
	
	private int currentSetPoint = 0;

	//Distance betwenn the center of the shot glasses in mm
	public final static int DISTANCE_BETWEEN_SHOT_GLASSES = 43;
	
	//Distance betwenn the center of the first shot glass and init position in mm
	public final static int DISTANCE_BETWEEN_FIRST_GLASS_AND_INIT_POSITION = 27;

	public MotorControlAsyncTask(DrinkMixerActivity activity) {

		this.drinkMixer = activity.drinkMixer;
		this.activity = activity;

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
	protected Double doInBackground(Void... params) {

		

		try {
			
			moveRight(DISTANCE_BETWEEN_FIRST_GLASS_AND_INIT_POSITION);
			
			//Fill first
			Thread.sleep(10000);
			
			moveLeft(DISTANCE_BETWEEN_SHOT_GLASSES);
			
			//Fill second
			Thread.sleep(10000);
			
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
