package sp.drinkmixer;

import android.os.AsyncTask;

public class MotorControlAsyncTask extends AsyncTask<Integer, Integer, Integer> {

	DrinkMixerActivity activity;
	DrinkMixer drinkMixer;

	long waitTimeFor1MlInMilliseconds;

	public MotorControlAsyncTask(DrinkMixerActivity activity) {

		this.activity = activity;

		this.drinkMixer = activity.drinkMixer;

	}

	@Override
	protected Integer doInBackground(Integer... ingredient) {

		drinkMixer.openValve(activity.drinkMixer.horizontalMotorOnOffPin);
		drinkMixer.openValve(activity.drinkMixer.horizontalMotorDirectionPin);
		
		try {
			Thread.sleep(10000);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		
		drinkMixer.closeValve(activity.drinkMixer.horizontalMotorOnOffPin);
		drinkMixer.closeValve(activity.drinkMixer.horizontalMotorDirectionPin);

		if (isCancelled()) {

			// return ml already put into drink
			return 0;
		}

		return 0;
	}

	@Override
	protected void onPostExecute(Integer result) {

		super.onPostExecute(result);
	}

	@Override
	protected void onCancelled(Integer result) {

		super.onCancelled(result);
	}

}
