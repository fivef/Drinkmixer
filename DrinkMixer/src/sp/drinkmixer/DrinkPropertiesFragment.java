/*
 * Copyright (C) 2013 Steffen Pfiffner
 * 
 * Licence: GPL v3
 */

package sp.drinkmixer;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class DrinkPropertiesFragment extends Fragment {
	DrinkMixerActivity activity;
	DrinkPropertiesEfficientListAdapter adap;
	Drink selectedDrink;
	String name = "DrinkPropertiesFragment";

	TextView totalUsageCount;
	TextView sessionUsageCount;

	public DrinkPropertiesFragment(Drink drink) {

		this.selectedDrink = drink;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		activity = (DrinkMixerActivity) getActivity();

		View view = inflater.inflate(R.layout.drink_properties_fragment,
				container, false);

		ListView listView = (ListView) view
				.findViewById(R.id.DrinkPropertiesListView);

		EditText drinkTitle = (EditText) view
				.findViewById(R.id.drinkTitleTextView);

		totalUsageCount = (TextView) view
				.findViewById(R.id.TotalDrinkUsageCountValue);

		sessionUsageCount = (TextView) view
				.findViewById(R.id.SessionDrinkUsageCountValue);

		if (selectedDrink != null) {
			drinkTitle.setText(selectedDrink.getName());
			totalUsageCount.setText(selectedDrink.getTotalUsageCount() + "");
			sessionUsageCount
					.setText(selectedDrink.getSessionUsageCount() + "");
		} else {

			// create new drink

			selectedDrink = activity.drinkMixer.newDrink("");

			InputMethodManager m = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (m != null) {
				m.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
			}

			drinkTitle.requestFocus();
		}

		drinkTitle.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {

				selectedDrink.setName(s.toString());

			}
		});

		// GetActivity()
		activity = (DrinkMixerActivity) getActivity();

		this.registerForContextMenu(listView);

		adap = new DrinkPropertiesEfficientListAdapter(activity, selectedDrink);
		listView.setAdapter(adap);

		// to get OnItemClickListener to work
		listView.setItemsCanFocus(true);

		return view;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = activity.getMenuInflater();
		inflater.inflate(R.menu.drink_properties_fragment_context_menu, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.drinkPropertiesContextMenueDelete:

			selectedDrink.deleteIngredient(info.position);
			adap.notifyDataSetChanged();

			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.drink_properties_fragment_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menu_drinkproperties_fragment_add_ingredient:

			selectedDrink.addIngredient();

			// activity.openIngredientsFragment(addedIngredient.ingredient);

			// selectedDrink.addIngredient(, 0);
			// adddrink
			notifyAdapterDataChanged();

			break;

		case R.id.menu_drinkproperties_fragment_done:

			activity.openDrinksFragment();
			break;

		default:
			super.onOptionsItemSelected(item);

		}

		return super.onOptionsItemSelected(item);
	}

	public void notifyAdapterDataChanged() {
		adap.notifyDataSetChanged();
	}

	public String getName() {
		return name;
	}
}
