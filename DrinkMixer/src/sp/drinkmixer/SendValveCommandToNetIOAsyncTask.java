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

		// successfull = 0 is successful, 4 tries
		int successfull = 3;

		try {

			if (params[0] < 8) {

				if (params[1] == 1) {

					drinkMixer.getDigitalIO().setOutput(4, params[0], true);
					System.out.println("Pin " + params[0] + " opened");
				} else {

					drinkMixer.getDigitalIO().setOutput(4, params[0], false);

					System.out.println("Pin " + params[0] + " closed");
				}

			}

			if (params[0] >= 8) {

				params[0] = params[0] - 8;

				if (params[1] == 1) {

					System.out.println("Try to open pin " + params[0]);

					drinkMixer.getDigitalIO().setOutput(5, params[0], true);

					System.out.println("Pin " + params[0] + " opened");

				}

				else {

					System.out.println("Try to close pin " + params[0]);

					drinkMixer.getDigitalIO().setOutput(5, params[0], false);

					System.out.println("Pin " + params[0] + " opened");

				}
			}

		} catch (EthernetControlException e) {

			System.out.println("Send Command: No Connection");
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
