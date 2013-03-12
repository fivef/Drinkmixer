/*
 * Copyright (C) 2013 Steffen Pfiffner
 * 
 * Licence: GPL v3
 */

package sp.drinkmixer;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;

public class DrinkPropertiesEfficientListAdapter extends BaseAdapter implements
		Filterable {
	private LayoutInflater mInflater;
	private DrinkMixerActivity activity;
	private Drink selectedDrink;

	ViewHolder holder;

	public DrinkPropertiesEfficientListAdapter(DrinkMixerActivity activity,
			Drink selectedDrink) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(activity);
		this.activity = activity;
		this.selectedDrink = selectedDrink;

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

		// When convertView is not null, we can reuse it directly, there is
		// no need
		// to reinflate it. We only inflate a new View when the convertView
		// supplied
		// by ListView is null.
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.drink_properties_adapter_content, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.drinkNameSpinner = (Spinner) convertView
					.findViewById(R.id.drinkPropertiesDrinkNameSpinner);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
					android.R.layout.simple_dropdown_item_1line,
					activity.drinkMixer.getIngredientsAsStringArray());

			holder.drinkNameSpinner.setAdapter(adapter);

			holder.drinkNameSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int ingredientPosition, long arg3) {

							// assign the selected ingredient to the
							// IngerdientInDrink Object of the current
							// ingredient in the selected drink
							selectedDrink.setIngredientAtPosition(
									position,
									activity.drinkMixer.getIngredients().get(
											ingredientPosition));

						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub

						}
					});

			holder.amount = (EditText) convertView
					.findViewById(R.id.amountEditText);

			// holder.amount.setSelectAllOnFocus(true);

			holder.amount.setFocusableInTouchMode(true);

			// holder.amount.setInputType(InputType.TYPE_CLASS_NUMBER);

			holder.amount.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {

					try {

						selectedDrink.setIngredientAmountAtPosition(position,
								Integer.parseInt(s.toString()));

					} catch (NumberFormatException e) {

						selectedDrink
								.setIngredientAmountAtPosition(position, 0);
					}
				}
			});

			convertView.setTag(holder);

			holder.drinkNameSpinner
					.setOnLongClickListener(new OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {
							activity.openContextMenu(v);
							return true;
						}
					});
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		// a little bit too much for one line
		// sets the selection of the current ingredient slot of the drink to the
		// ingredient saved in the datamodel
		holder.drinkNameSpinner
				.setSelection(activity.drinkMixer
						.getIngredients()
						.indexOf(
								selectedDrink.getIngredients().get(position).ingredient));

		int amount = selectedDrink.getIngredientAmountAtPosition(position);

		// holder.amount.setTextIsSelectable(true);

		holder.amount.setText("" + amount);

		holder.id = position;

		return convertView;
	}

	static class ViewHolder {
		Spinner drinkNameSpinner;
		EditText amount;

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
		return selectedDrink.getIngredients().size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return selectedDrink.getIngredients().get(position);
	}
}