/*
 * Copyright (C) 2013 Steffen Pfiffner
 * 
 * Licence: GPL v3
 */

package sp.drinkmixer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MixingDrinkFragment extends Fragment {
	private DrinkMixerActivity activity;
	private TextView title;
	private Drink drinkToMix;
	public View view;
	private boolean isReady = false;

	public MixingDrinkFragment() {

	}

	public MixingDrinkFragment(Drink drink) {

		this.drinkToMix = drink;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.mixing_drink_fragment, container,
				false);

		activity = (DrinkMixerActivity) getActivity();

		title = (TextView) view.findViewById(R.id.mixingDrinkTitle);

		title.setText(drinkToMix.getName());

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		if (drinkToMix.getName() != "reinigen") {

			drinkToMix.mix(activity);

		} else {

			activity.drinkMixer.clean();
		}
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater.inflate(R.menu.mixing_drink_fragment_menu, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menu_mixing_drink_fragment_done:

			activity.openDrinksFragment();
			break;

		case R.id.menu_mixing_drink_fragment_stop_mixing:

			activity.drinkMixer.stopAllRunningTasks();

			activity.openDrinksFragment();
			break;

		default:
		}

		return super.onOptionsItemSelected(item);
	}

	public void setDone() {

		this.isReady = true;

		activity.speak(activity.drinkMixer.getCurrentUser().getName() + "s" + drinkToMix.getName() + " ist fertig!");

		activity.invalidateOptionsMenu();

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		menu.findItem(R.id.menu_mixing_drink_fragment_stop_mixing).setVisible(
				!isReady);

		menu.findItem(R.id.menu_mixing_drink_fragment_done).setVisible(isReady);

		super.onPrepareOptionsMenu(menu);
	}

}
