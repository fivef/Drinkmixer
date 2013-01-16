package sp.drinkmixer;

import java.text.DecimalFormat;

import li.rudin.ethernetcontrol.base.EthernetControlException;
import android.os.AsyncTask;

public class ReadInputValuesAsyncTask extends AsyncTask<Void, Double, Double> {

	DrinkMixerActivity activity;
	DrinkMixer drinkMixer;

	boolean abort = false;

	double volts;

	boolean leftSensorState = true;

	public ReadInputValuesAsyncTask(DrinkMixerActivity activity) {

		this.drinkMixer = activity.drinkMixer;
		this.activity = activity;

	}

	@Override
	protected Double doInBackground(Void... params) {

		while (!abort) {

			try {

				volts = drinkMixer.getAnalogIO().getVoltage(4);

			} catch (EthernetControlException e) {

				System.out.println("ReadInputValues: No Connection");

			} catch (java.lang.StringIndexOutOfBoundsException e) {

				System.out
						.println("ReadInputValues: No Connection (StringIndexOutofBoundsException");
			} catch (java.lang.NullPointerException e) {

				System.out
						.println("ReadInputValues: No Connection (NullPointerException)");
			} catch (java.lang.NumberFormatException e) {

				System.out
						.println("ReadInputValues: No Connection (NumberFormatException)");
			}

			publishProgress(volts);

			try {
				Thread.sleep(1000);
				
				//TODO: make sleep time adjustable in settings

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}

			if (isCancelled()) {

				return 0.0;
			}

		}

		return 0.0;

	}

	@Override
	protected void onPostExecute(Double result) {

		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Double... values) {
		
		//TODO: move to drinkmixer class
		final int COMPRESSOR_PIN = 12;
		double hysteresis = 0.03;
		double offset = 0.17;

		if (activity.settingsFragment != null
				&& activity.settingsFragment.isVisible()) {

			double value = values[0];

			
			
			value = value - offset;

			value = value / 10;

			if (value < 0) {
				value = 0.0;
			}
			
			if(activity.drinkMixer.isPressureControlEnabled()){
				
				if(value >= activity.drinkMixer.getPressureSetPoint())	{
					
					//to avoid network traffic and microcontroller processor work
					if(activity.drinkMixer.getValveState(COMPRESSOR_PIN) == true){
						activity.drinkMixer.closeValve(COMPRESSOR_PIN);
					}
				}
				
				if(value <= activity.drinkMixer.getPressureSetPoint() - hysteresis ){
					
					if(activity.drinkMixer.getValveState(COMPRESSOR_PIN) == false){
						activity.drinkMixer.openValve(COMPRESSOR_PIN);
					}
				}
				
				
			}else{
				if(activity.drinkMixer.getValveState(COMPRESSOR_PIN) == true){
					activity.drinkMixer.closeValve(COMPRESSOR_PIN);
				}
			}

			DecimalFormat df = new DecimalFormat("#.##");

			activity.settingsFragment.setPressureValue(df.format(value));
		}

		super.onProgressUpdate(values);
	}

}
