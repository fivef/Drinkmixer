package sp.drinkmixer;

import java.util.Collections;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class LeaderBoardFragment extends Fragment {
	private DrinkMixerActivity activity;
	private LeaderBoardEfficientListAdapter adap;
	private ArrayAdapter<CharSequence> criteriaAdapter;
	private int currentSortCriterium;


	public int getCurrentSortCriterium() {
		return currentSortCriterium;
	}



	private ListView listView;
	private Spinner criteriaSpinner;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// GetActivity()
		activity = (DrinkMixerActivity) getActivity();
		/*
		 * if (savedInstanceState != null) {
		 * listView.setScrollY(savedInstanceState.getInt("drinksPosition")); }
		 */

		View view = inflater
				.inflate(R.layout.leaderboard_fragment, container, false);
		
		criteriaSpinner = (Spinner) view.findViewById(R.id.LeaderBoardFragmentCriteriaSpinner);
		
		criteriaAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Criteria, android.R.layout.simple_list_item_1);
		
		criteriaSpinner.setAdapter(criteriaAdapter);
		
		criteriaSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				//sort by the position selected
				Collections.sort(activity.drinkMixer.getUsers(), new UserComparator(position));
				
				adap.notifyDataSetChanged();
				
				currentSortCriterium = position;
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		criteriaSpinner.setSelection(currentSortCriterium);

		listView = (ListView) view.findViewById(R.id.ListViewLeaderBoardFragment);

		adap = new LeaderBoardEfficientListAdapter(activity);
		listView.setAdapter(adap);

		adap.notifyDataSetChanged();

		return view;
	}

	
	public void hasTheUserTakenTheLead() {
		
		if (activity.drinkMixer.getCurrentUser() != null) {

			//if the current user is the leading user in the currently selected criterium
			if (activity.drinkMixer.getUsers().indexOf(activity.drinkMixer.getCurrentUser()) == 0) {

				MediaPlayer mPlayer = MediaPlayer.create(activity,
						R.raw.quake_3_you_have_taken_the_lead);

				mPlayer.start();
			}
		}

	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.leaderboard_fragment_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		
		//set the color of the connection indicator
		MenuItem item = menu.findItem(R.id.DrinkMixerActivityMenuconnected);
		
		if (activity.drinkMixer.isConnectedToNETIO()) {
			item.setIcon(R.drawable.ic_notification_green);
		} else {
			item.setIcon(R.drawable.ic_notification_red);
		}
		
		super.onPrepareOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {


		case R.id.menu_drink_fragment_open_configuration_fragment:

			activity.openConfigurationFragment();

			break;

		case R.id.menu_drink_fragment_open_ingredients_fragment:

			activity.openIngredientsFragment();

			break;
			
		case R.id.menu_drink_fragment_open_users_fragment:

			activity.openUsersFragment();

			break;

		case R.id.menu_drink_fragment_open_settings_fragment:
			activity.openSettingsFragment();

			break;
			
		case R.id.menu_drink_fragment_NewSession:

			activity.showNewSessionDecitionDialog();

			break;	
			
		case R.id.DrinkMixerActivityMenuconnected:

			if (activity.drinkMixer.isConnectedToNETIO()) {

			} else {

				activity.drinkMixer.connectToNetIO();

			}

			break;
			
		case R.id.menu_drink_fragment_CloseApp:
			
			activity.finish();
			
			break;
			


		default:

		}

		return super.onOptionsItemSelected(item);
	}


	


}
