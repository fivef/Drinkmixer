package sp.drinkmixer;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;

public class DrinksFragment extends Fragment {
	DrinkMixerActivity activity;
	DrinksEfficientListAdapter adap;
	UsersSpinnerListAdapter adapSpinner;
	int selectedPosition;

	ListView listView;
	Spinner usersSpinner;
	private int scrollPosition;
	private int scrollTopOffset;

	ActionMode actionMode;

	static boolean actionModeEnabled = false;

	boolean showOnlyPossibleDrinks = true;


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
				.inflate(R.layout.drinks_fragment, container, false);

		listView = (ListView) view.findViewById(R.id.ListViewDrinksFragment);
		
		usersSpinner = (Spinner) view.findViewById(R.id.drinksFragmentUsersSpinner);

		adapSpinner = new UsersSpinnerListAdapter(activity);

		usersSpinner.setAdapter(adapSpinner);
		usersSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int selectedPosition, long id) {

				activity.drinkMixer.setCurrentUser(activity.drinkMixer.getUsers().get(
						selectedPosition));


			}

			public void onNothingSelected(AdapterView<?> parent) {
				// showToast("Spinner1: unselected");
			}
		});
		
		/*TODO show empty message not working like this
		TextView emptyView = new TextView(activity);
		emptyView.setText("Mit der aktuellen Belegung sind keine Cocktails möglich.");
		listView.setEmptyView(emptyView);
		*/



		adap = new DrinksEfficientListAdapter(activity);
		listView.setAdapter(adap);

		refreshData();

		return view;
	}

	private void refreshData() {

		if (showOnlyPossibleDrinks) {
			adap.getFilter().filter("inConfigOnly");
		} else {
			adap.getFilter().filter("");
		}

		adap.notifyDataSetChanged();

	}

	public void showDrinkDeletionDecisionDialog() {

		AlertDialog ad = new AlertDialog.Builder(activity).create();
		// ad.setCancelable(false); // This blocks the 'BACK' button
		ad.setMessage("Wirklich l�schen?");

		ad.setButton(AlertDialog.BUTTON_POSITIVE, "ja",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						activity.drinkMixer.deleteSelectedDrink();

						refreshData();

						dialog.dismiss();

						actionMode.finish();

						activity.openDrinksFragment();

					}
				});

		ad.setButton(AlertDialog.BUTTON_NEGATIVE, "Nein",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		ad.show();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.drink_fragment_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		super.onPrepareOptionsMenu(menu);

		menu.findItem(R.id.menu_drink_fragment_showAll).setVisible(
				showOnlyPossibleDrinks);
		menu.findItem(R.id.menu_drink_fragment_showPossible).setVisible(
				!showOnlyPossibleDrinks);

		//set the color of the connection indicator
		MenuItem item = menu.findItem(R.id.DrinkMixerActivityMenuconnected);

		if (activity.drinkMixer.isConnectedToNETIO()) {
			item.setIcon(R.drawable.ic_notification_green);
		} else {
			item.setIcon(R.drawable.ic_notification_red);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_drink_fragment_edit:

			actionModeEnabled = true;
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			listView.requestDisallowInterceptTouchEvent(false);

			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {

					selectedPosition = position;

				}
			});

			actionMode = activity.startActionMode(new ActionMode.Callback() {

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {

					listView.clearChoices();

					/*
					 * for (int i = 0; i < listView.getChildCount(); i++) {
					 * ((Checkable) listView.getChildAt(i)).setChecked(false); }
					 */
					listView.setChoiceMode(ListView.CHOICE_MODE_NONE);

					listView.requestDisallowInterceptTouchEvent(true);
					actionModeEnabled = false;

				}

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {

					mode.getMenuInflater().inflate(
							R.menu.drink_fragment_action_mode, menu);
					return true;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode,
						MenuItem item) {

					Drink selectedDrink = null;
					// get currently selected drink for all operations except newdrink
					if(item.getItemId() != R.id.drinkFragmentActionModeNewDrink){
					
						selectedDrink = (Drink) listView.getItemAtPosition(selectedPosition);
					}

					switch (item.getItemId()) {

					case R.id.drinkFragmentActionModeNewDrink:

						// position -1 means new drink
						activity.openDrinkPropertiesFragment(null);

						mode.finish();

						return true;

					case R.id.drinkFragmentActionModeDelete:

						activity.drinkMixer.setSelectedDrink(selectedDrink);
						showDrinkDeletionDecisionDialog();

						return true;

					case R.id.drinkFragmentActionModeEdit:

						activity.openDrinkPropertiesFragment(selectedDrink);
						mode.finish();
						return true;

					case R.id.drinkFragmentActionModeDuplicate:

						activity.drinkMixer.duplicateDrink(selectedDrink);

						refreshData();

						return true;

					default:
						return false;
					}

				}
			});

			break;

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
			
			
			
		case R.id.menu_drink_fragment_showAll:

			showOnlyPossibleDrinks = false;

			refreshData();

			break;

		case R.id.menu_drink_fragment_showPossible:

			showOnlyPossibleDrinks = true;
			refreshData();

			break;

		case R.id.DrinkMixerActivityMenuconnected:

			if (activity.drinkMixer.isConnectedToNETIO()) {

			} else {

				activity.drinkMixer.connectToNetIO();

			}

			break;

		default:

		}

		return super.onOptionsItemSelected(item);
	}

	public void notifyAdapterDataChanged() {

		adap.notifyDataSetChanged();

	}

	@Override
	public void onResume() {
		
		if(usersSpinner != null){
			
			usersSpinner.setSelection(activity.drinkMixer.getUsers().indexOf(activity.drinkMixer.getCurrentUser()));
			
		}
		
		if (listView != null) {


			
			 listView.postDelayed(new Runnable() {
			        @Override
			        public void run() {
			        	listView.setSelectionFromTop(scrollPosition,
								scrollTopOffset);
			        }
			    }, 100);

			// listView.smoothScrollToPosition(scrollPosition);
			// listView.in
		}
		super.onResume();
	}

	@Override
	public void onStop() {

		// save index and top position
		scrollPosition = listView.getFirstVisiblePosition();
		View v = listView.getChildAt(0);
		scrollTopOffset = (v == null) ? 0 : v.getTop();
		super.onStop();
	}

}
