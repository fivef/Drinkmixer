package sp.drinkmixer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class DrinkStatisticsEfficientListAdapter extends BaseAdapter implements
		Filterable {
	private LayoutInflater mInflater;
	
	private User selectedUser;

	ViewHolder holder;

	public DrinkStatisticsEfficientListAdapter(DrinkMixerActivity activity, User selectedUser) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(activity);
		
		this.selectedUser = selectedUser;

	}

	/**
	 * Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {

		try {
			
		
		convertView = mInflater.inflate(
				R.layout.drink_statistics_list_adapter_content, null);

		// Creates a ViewHolder and store references to the two children
		// views
		// we want to bind data to.
		holder = new ViewHolder();

		holder.name = (TextView) convertView
				.findViewById(R.id.drinkStatisticsName);

		holder.count = (TextView) convertView.findViewById(R.id.drinkStatisticsCount);


		convertView.setTag(holder);

		


		holder.name.setText(selectedUser.getDrinksStatus().get(position).getName() + ": ");
		holder.count.setText(selectedUser.getDrinksStatus().get(position).getCount() + "");
		
		

		holder.id = position;
		} 
		
		catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	static class ViewHolder {
		TextView name;
		TextView count;

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
		return selectedUser.getDrinksStatus().size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return selectedUser.getDrinksStatus()
				.get(position);
	}
}