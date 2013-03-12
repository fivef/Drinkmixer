/*
 * Copyright (C) 2013 Steffen Pfiffner
 * 
 * Licence: GPL v3
 */

package sp.drinkmixer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ConfigurationFragment extends Fragment {
	DrinkMixerActivity activity;
	ConfigurationEfficientListAdapter adap;

	String name = "ConfigurationFragment";

	public ConfigurationFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.configuration_fragment,
				container, false);

		ListView listView = (ListView) view
				.findViewById(R.id.ConfigurationListView);

		// GetActivity()
		activity = (DrinkMixerActivity) getActivity();

		this.registerForContextMenu(listView);

		adap = new ConfigurationEfficientListAdapter(activity);
		listView.setAdapter(adap);

		/*
		 * //to get OnItemClickListener to work
		 * //listView.setItemsCanFocus(true);
		 * 
		 * listView.setOnLongClickListener(new OnLongClickListener() {
		 * 
		 * @Override public boolean onLongClick(View v) {
		 * 
		 * activity.showErrorDialog("Longclick"); return false; } });
		 */

		return view;
	}

	/*
	 * 
	 * @Override public void onCreateContextMenu(ContextMenu menu, View v,
	 * ContextMenuInfo menuInfo) { super.onCreateContextMenu(menu, v, menuInfo);
	 * MenuInflater inflater = activity.getMenuInflater();
	 * inflater.inflate(R.menu.configuration_fragment_context_menu, menu);
	 * 
	 * // Get the info on which item was selected AdapterContextMenuInfo info =
	 * (AdapterContextMenuInfo) menuInfo;
	 * 
	 * // Retrieve the item that was clicked on Object item =
	 * adap.getItem(info.position); }
	 * 
	 * 
	 * 
	 * 
	 * @Override public boolean onContextItemSelected(MenuItem item) {
	 * AdapterContextMenuInfo info = (AdapterContextMenuInfo)
	 * item.getMenuInfo(); switch (item.getItemId()) {
	 * 
	 * 
	 * case R.id.config_context_menue_calibrate:
	 * 
	 * activity.showErrorDialog("Longclick oncontext"); //calibrate the
	 * ingredient at the currently set at the current position
	 * activity.drinkMixer
	 * .calibrateIngredient(activity.drinkMixer.getConfiguration
	 * ().get(info.position));
	 * 
	 * return true;
	 * 
	 * default: return super.onContextItemSelected(item); } }
	 */

	/*
	 * @Override public void onCreateOptionsMenu(Menu menu, MenuInflater
	 * inflater) { inflater.inflate(R.menu.configuration_fragment_menu, menu);
	 * super.onCreateOptionsMenu(menu, inflater); }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) {
	 * 
	 * if(item.getItemId() == R.id.menu_config_fragment_calibrate){
	 * activity.openIngredientsFragment(null); }
	 * 
	 * return super.onOptionsItemSelected(item); }
	 */

	public void notifyAdapterDataChanged() {
		adap.notifyDataSetChanged();
	}

	public String getName() {
		return name;
	}
}
