/*
 * Copyright (C) 2013 Steffen Pfiffner
 * 
 * Licence: GPL v3
 */

package sp.drinkmixer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class UsersSpinnerListAdapter extends BaseAdapter implements
		Filterable {
	private LayoutInflater mInflater;
	private DrinkMixerActivity context;

	ViewHolder holder;

	public UsersSpinnerListAdapter(DrinkMixerActivity context) {
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

		// When convertView is not null, we can reuse it directly, there is
		// no need
		// to reinflate it. We only inflate a new View when the convertView
		// supplied
		// by ListView is null.
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.configuration_spinner_adapter_content, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();

			holder.value = (TextView) convertView
					.findViewById(R.id.spinner_item_text);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		holder.value.setText(context.drinkMixer.getUsers().get(position)
				.getName());

		holder.id = position;

		return convertView;
	}

	static class ViewHolder {
		TextView value;

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
		return context.drinkMixer.getUsers().size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return context.drinkMixer.getUsers().get(position);
	}
}
