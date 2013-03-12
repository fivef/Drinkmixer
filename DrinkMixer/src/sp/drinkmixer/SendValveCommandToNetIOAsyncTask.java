/*
 * Copyright (C) 2013 Steffen Pfiffner
 * 
 * Licence: GPL v3
 */

package sp.drinkmixer;

import li.rudin.ethernetcontrol.base.EthernetControlException;
import android.os.AsyncTask;

public class SendValveCommandToNetIOAsyncTask extends
		AsyncTask<Integer, Void, Boolean> {

	DrinkMixerActivity activity;
	DrinkMixer drinkMixer;

	public SendValveCommandToNetIOAsyncTask(DrinkMixerActivity activity) {

		this.drinkMixer = activity.drinkMixer;
		this.activity = activity;

	}

	@Override
	protected Boolean doInBackground(Integer... params) {

		try {

			if (params[0] < 8) {

				if (params[1] == 1) {

					drinkMixer.getDigitalIO().setOutput(4, params[0], true);
					System.out.println("Pin " + params[0] + " opened");
					
					//setstate uncommented because the state array has only 11 entries, TODO: make array bigger 
					//and uncomment the 4 setValveStates
					//drinkMixer.setValveState(params[0], true);
				} else {

					drinkMixer.getDigitalIO().setOutput(4, params[0], false);

					System.out.println("Pin " + params[0] + " closed");
					//drinkMixer.setValveState(params[0], false);
				}

			}

			//access the second shift register
			if (params[0] >= 8) {

				int pinToSet = params[0] - 8;

				if (params[1] == 1) {

					

					drinkMixer.getDigitalIO().setOutput(5, pinToSet, true);

					System.out.println("Pin " + params[0] + " opened");
					
					//drinkMixer.setValveState(params[0], true);

				}

				else {

		

					drinkMixer.getDigitalIO().setOutput(5, pinToSet, false);

					System.out.println("Pin " + params[0] + " closed");
					
					//drinkMixer.setValveState(params[0], false);

				}
			}

		} catch (EthernetControlException e) {


			return false;

		}

		return true;

	}

	@Override
	protected void onPostExecute(Boolean result) {

		if (result) {
			drinkMixer.setConnectedToNETIO(true);
		} else {
			drinkMixer.setConnectedToNETIO(false);
			
			System.out.println("Send Command: No Connection");
			
			//activity.showErrorDialog("Send Command: No Connection");
			
			
			//drinkMixer.connectToNetIO();
		}

		activity.invalidateOptionsMenu();

		super.onPostExecute(result);
	}

}
