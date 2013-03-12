/*
 * Copyright (C) 2013 Steffen Pfiffner
 * 
 * Licence: GPL v3
 */

package sp.drinkmixer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ConfigurationEfficientListAdapter extends BaseAdapter implements
		Filterable {
	private LayoutInflater mInflater;

	private DrinkMixerActivity context;

	ViewHolder holder;

	public ConfigurationEfficientListAdapter(DrinkMixerActivity context) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);
		this.context = context;

	}

	/**
	 * Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {
		// A ViewHolder keeps references to children views to avoid
		// unneccessary calls
		// to findViewById() on each row.

		convertView = mInflater.inflate(R.layout.configuration_adapter_content,
				null);

		// Creates a ViewHolder and store references to the two children
		// views
		// we want to bind data to.
		holder = new ViewHolder();

		holder.valveNumber = (TextView) convertView
				.findViewById(R.id.valveNumber);

		holder.ingredient = (Spinner) convertView.findViewById(R.id.spinner1);

		holder.toggleButton = (ToggleButton) convertView
				.findViewById(R.id.togglePort);

		holder.toggleButton.setChecked(context.drinkMixer
				.getValveState(position));

		holder.toggleButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						context.drinkMixer.setValveState(position, isChecked);

						if (isChecked) {

							context.drinkMixer.openValve(position);

						} else {

							context.drinkMixer.closeValve(position);

						}

					}
				});

		ConfigurationSpinnerListAdapter adap = new ConfigurationSpinnerListAdapter(
				context);

		holder.ingredient.setAdapter(adap);

		holder.ingredient
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int selectedPosition, long id) {

						if (selectedPosition == 0) {

							context.drinkMixer.setConfiguration(position,
									new Ingredient("leer", 0));
						} else {

							context.drinkMixer.setConfiguration(
									position,
									context.drinkMixer.getIngredients().get(
											selectedPosition - 1));

						}

					}

					public void onNothingSelected(AdapterView<?> parent) {
						// showToast("Spinner1: unselected");
					}
				});

		convertView.setTag(holder);

		holder.valveNumber.setText("" + (position + 1));

		// get the currently selected ingredient for the current slot
		Ingredient currentIngerdient = context.drinkMixer.getConfiguration()
				.get(position);

		if (currentIngerdient.getName() == "leer") {

			holder.ingredient.setSelection(0);

		} else {

			// get the position in the ingredients array of the current
			// ingredient
			holder.ingredient.setSelection(context.drinkMixer.getIngredients()
					.indexOf(currentIngerdient) + 1);

		}

		holder.id = position;

		return convertView;
	}

	static class ViewHolder {
		TextView valveNumber;
		Spinner ingredient;

		ToggleButton toggleButton;

		boolean toggleChecked;

		// ListEntry listEntry;
		int id;

	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub

		return position;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return context.drinkMixer.getConfiguration().size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return context.drinkMixer.getConfiguration().get(position);
	}
}
