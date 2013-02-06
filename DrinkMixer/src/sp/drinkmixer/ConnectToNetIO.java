package sp.drinkmixer;

import java.util.List;

import li.rudin.ethernetcontrol.base.EthernetControlException;
import li.rudin.ethernetcontrol.ethersex.device.EthersexTCPDevice;
import li.rudin.ethernetcontrol.ethersex.ecmd.EthersexAnalogIO;
import li.rudin.ethernetcontrol.ethersex.ecmd.EthersexDigitalIO;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

public class ConnectToNetIO extends AsyncTask<String, Void, Boolean> {

	DrinkMixerActivity activity;
	DrinkMixer drinkMixer;

	public ConnectToNetIO(DrinkMixerActivity activity) {

		this.activity = activity;
		this.drinkMixer = activity.drinkMixer;

	}

	@Override
	protected Boolean doInBackground(String... ip) {

		try {

			WifiManager wifimanager = (WifiManager) activity
					.getSystemService(Context.WIFI_SERVICE);

			if (!wifimanager.isWifiEnabled()) {
				wifimanager.setWifiEnabled(true);
			}

			while (!wifimanager.isWifiEnabled()) {

				Thread.sleep(500);

			}

			String ssid = wifimanager.getConnectionInfo().getSSID();

			if (ssid == null || ssid.compareTo(DrinkMixer.WLAN_SSID) != 0) {

				drinkMixer.oldNetworkId = wifimanager.getConnectionInfo()
						.getNetworkId();

				List<WifiConfiguration> configuredNetworks = wifimanager
						.getConfiguredNetworks();

				for (WifiConfiguration result : configuredNetworks) {

					System.out.println(result.SSID);

					if (result.SSID
							.compareTo("\"" + DrinkMixer.WLAN_SSID + "\"") == 0) {

						wifimanager.enableNetwork(result.networkId, true);

						ssid = wifimanager.getConnectionInfo().getSSID();

						boolean connected = false;

						while (!connected) {
							try {

								// Create instance connect to standard port 2701
								drinkMixer.setDev(new EthersexTCPDevice(ip[0]));

								connected = true;

							} catch (EthernetControlException e) {

								connected = false;
							}

							Thread.sleep(500);

						}

						/*
						 * while (ssid == null){ Thread.sleep(500); }
						 * 
						 * 
						 * while (ssid.compareTo(DrinkMixer.wlanSSID) != 0){
						 * ssid = wifimanager.getConnectionInfo().getSSID();
						 * 
						 * Thread.sleep(500);
						 * 
						 * }
						 * 
						 * System.out.println("Connected to " + ssid);
						 * 
						 * //wifimanager.getConnectionInfo().getSupplicantState()
						 * ;
						 */

						break;

					}

				}

				// activity.showErrorDialog("Accespoint WLAN nicht gefunden");
				// System.out.println("Accespoint WLAN nicht gefunden");

			} else {

				// Create instance connect to standard port 2701

				drinkMixer.setDev(new EthersexTCPDevice(ip[0]));

			}

			if (drinkMixer.getDev() != null) {

				// Create digital IO over TCP-Device
				drinkMixer.setDigitalIO(new EthersexDigitalIO(drinkMixer
						.getDev()));

				// Set all pins on port c as Output
				drinkMixer.getDigitalIO().setDDR(EthersexDigitalIO.PORTC, 0xFF);

				drinkMixer.setAnalogIO(new EthersexAnalogIO(
						drinkMixer.getDev(), 5));

				// set PORTA Pin 0 as input and enable pullup
				drinkMixer.getDigitalIO().setDDR(EthersexDigitalIO.PORTA, 0,
						false);
				
				drinkMixer.getDigitalIO().setOutput(EthersexDigitalIO.PORTA, 0,
						true);
				
				

			} else {
				return false;
			}

		} catch (EthernetControlException e) {

			//activity.showErrorDialog("Error: " + e.getMessage());
			System.out.println("Error: " + e.getMessage());

			return false;

		} catch (InterruptedException e) {

			activity.showErrorDialog("Error: " + e.getMessage());

			return false;
		}

		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {

		// TODO: show connection status in graphical interface

		if (result) {
			drinkMixer.setConnectedToNETIO(true);

			// start input value thread
			drinkMixer.startPressureSensorAsyncTask();
		} else {
			drinkMixer.setConnectedToNETIO(false);

			System.out.println("Could not connect");
			// activity.showErrorDialog("Verbindung zu DrinkMixer konnte nicht hergestellt werden.");
		}

		activity.invalidateOptionsMenu();

		super.onPostExecute(result);
	}

}
