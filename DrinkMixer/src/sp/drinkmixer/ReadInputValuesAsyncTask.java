package sp.drinkmixer;

import java.text.DecimalFormat;

import li.rudin.ethernetcontrol.base.EthernetControlException;
import li.rudin.ethernetcontrol.ethersex.ecmd.EthersexDigitalIO;
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

				// horizontal position sensors
				leftSensorState = drinkMixer.getDigitalIO().getInput(
						EthersexDigitalIO.PORTA,
						drinkMixer.horizontalMotorLeftContactSensor);

				if (leftSensorState == false) {

					activity.drinkMixer
							.closeValve(activity.drinkMixer.horizontalMotorOnOffPin);
				}

				// System.out.println("sensor: " + leftSensorState);

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

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
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

		if (activity.settingsFragment != null
				&& activity.settingsFragment.isVisible()) {

			double value = values[0];

			value = value - 0.17;

			value = value / 10;

			if (value < 0) {
				value = 0.0;
			}

			DecimalFormat df = new DecimalFormat("#.##");

			activity.settingsFragment.setPressureValue(df.format(value));
		}

		super.onProgressUpdate(values);
	}

}
