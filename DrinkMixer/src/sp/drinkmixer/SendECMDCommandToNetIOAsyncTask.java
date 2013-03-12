/*
 * Copyright (C) 2013 Steffen Pfiffner
 * 
 * Licence: GPL v3
 */

package sp.drinkmixer;

import li.rudin.ethernetcontrol.base.EthernetControlException;
import android.os.AsyncTask;

public class SendECMDCommandToNetIOAsyncTask extends
		AsyncTask<String, Void, Boolean> {

	DrinkMixerActivity activity;
	DrinkMixer drinkMixer;

	public SendECMDCommandToNetIOAsyncTask(DrinkMixerActivity activity) {

		this.drinkMixer = activity.drinkMixer;
		this.activity = activity;

	}

	@Override
	protected Boolean doInBackground(String... params) {

	
		try {

			String result = drinkMixer.getDev().request(params[0]);
			
			if(result.equalsIgnoreCase("OK")){
			
			System.out.println("ECMD command sent: " + params[0]);
			//System.out.println("ECMD result: " + result);
			
			}else{
				
				System.out.println("Send ECMD Command Error: " + result);
				//TODO: correct error handling return false;
			}

		} catch (EthernetControlException e) {

			System.out.println("Send ECMD Command: No Connection");
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
		}

		activity.invalidateOptionsMenu();

		super.onPostExecute(result);
	}

}
