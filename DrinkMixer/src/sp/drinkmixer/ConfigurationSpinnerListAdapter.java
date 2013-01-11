package sp.drinkmixer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class ConfigurationSpinnerListAdapter extends BaseAdapter implements
		Filterable {
	private LayoutInflater mInflater;
	private DrinkMixerActivity context;

	ViewHolder holder;

	public ConfigurationSpinnerListAdapter(DrinkMixerActivity context) {
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

		// the first element is the empty element

		convertView = mInflater.inflate(
				R.layout.configuration_spinner_adapter_content, null);

		// Creates a ViewHolder and store references to the two children
		// views
		// we want to bind data to.
		holder = new ViewHolder();

		holder.value = (TextView) convertView
				.findViewById(R.id.spinner_item_text);

		convertView.setTag(holder);

		if (position == 0) {

			holder.value.setText("leer");

		} else {

			// -1 because of first empty element
			holder.value.setText(context.drinkMixer.getIngredients()
					.get(position - 1).getName());
		}

		holder.id = position;

		return convertView;
	}

	static class ViewHolder {
		TextView value;
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
		return context.drinkMixer.getIngredients().size() + 1;
	}

	@Override
	public Object getItem(int position) {

		if (position == 0) {
			return new Ingredient("leer", 0);
		} else {
			return context.drinkMixer.getIngredients().get(position - 1);
		}
	}
}
