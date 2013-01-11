package sp.drinkmixer;

import java.text.DecimalFormat;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class UsersFragment extends Fragment {
	DrinkMixerActivity activity;

	User selectedUser;

	String name = "UsersFragment";

	UsersSpinnerListAdapter adap;

	DrinkStatisticsEfficientListAdapter drinkStatsAdap;

	Spinner usersSpinner;
	ListView drinkStatusListView;
	EditText nameEditText;
	EditText weightEditText;
	EditText ageEditText;
	EditText sizeEditText;

	RadioGroup sexRadioGroup;

	TextView drinkCountTextView;
	TextView pureAlcoholTextView;
	TextView beerEquivalenTextView;
	TextView promilleTextView;
	TextView costsTextView;

	DecimalFormat df = new DecimalFormat("#.##");

	public UsersFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.users_fragment, container, false);

		activity = (DrinkMixerActivity) getActivity();

		usersSpinner = (Spinner) view.findViewById(R.id.usersSpinner);

		drinkCountTextView = (TextView) view
				.findViewById(R.id.drinkCountTextView);
		pureAlcoholTextView = (TextView) view
				.findViewById(R.id.pureAlcoholTextView);
		beerEquivalenTextView = (TextView) view
				.findViewById(R.id.beerEquivalentTextView);
		promilleTextView = (TextView) view.findViewById(R.id.promilleTextView);
		costsTextView = (TextView) view.findViewById(R.id.costTextView);

		adap = new UsersSpinnerListAdapter(activity);

		usersSpinner.setAdapter(adap);

		usersSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int selectedPosition, long id) {

				selectedUser = activity.drinkMixer.getUsers().get(
						selectedPosition);

				drinkStatusListView
						.setAdapter(new DrinkStatisticsEfficientListAdapter(
								activity, selectedUser));
				refreshAllViews();

			}

			public void onNothingSelected(AdapterView<?> parent) {
				// showToast("Spinner1: unselected");
			}
		});

		/*
		 * usersSpinner.setSelection(activity.drinkMixer.getUsers().indexOf(
		 * selectedUser));
		 */

		// ListView for drinks statistics

		drinkStatusListView = (ListView) view.findViewById(R.id.drinksstatus);

		nameEditText = (EditText) view.findViewById(R.id.userNameEditText);

		nameEditText.addTextChangedListener(new TextWatcher() {

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

				selectedUser.setName(s.toString());
				adap.notifyDataSetChanged();
				usersSpinner.setSelection(activity.drinkMixer.getUsers()
						.indexOf(selectedUser));

			}
		});

		weightEditText = (EditText) view.findViewById(R.id.userWeightEditText);

		weightEditText.addTextChangedListener(new TextWatcher() {

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

				try {
					selectedUser.setWeight(Integer.parseInt(weightEditText
							.getText().toString()));

				} catch (NumberFormatException e) {

					selectedUser.setWeight(0);

				}

				adap.notifyDataSetChanged();

			}
		});

		ageEditText = (EditText) view.findViewById(R.id.userAgeEditText);

		ageEditText.addTextChangedListener(new TextWatcher() {

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
				try {
					selectedUser.setAge(Integer.parseInt(ageEditText.getText()
							.toString()));

				} catch (NumberFormatException e) {

					selectedUser.setAge(0);

				}
				adap.notifyDataSetChanged();

			}
		});

		sizeEditText = (EditText) view.findViewById(R.id.userSizeEditText);

		sizeEditText.addTextChangedListener(new TextWatcher() {

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
				try {
					selectedUser.setSize(Integer.parseInt(sizeEditText
							.getText().toString()));

				} catch (NumberFormatException e) {

					selectedUser.setSize(0);

				}
				adap.notifyDataSetChanged();

			}
		});

		weightEditText = (EditText) view.findViewById(R.id.userWeightEditText);

		weightEditText.addTextChangedListener(new TextWatcher() {

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
				try {
					selectedUser.setWeight(Integer.parseInt(weightEditText
							.getText().toString()));

				} catch (NumberFormatException e) {

					selectedUser.setWeight(0);

				}
				adap.notifyDataSetChanged();

			}
		});

		sexRadioGroup = (RadioGroup) view.findViewById(R.id.sexRadioGroup);

		sexRadioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {

						if (checkedId == R.id.radioButtonMale) {
							selectedUser.setSex(true);
						} else {
							selectedUser.setSex(false);
						}

					}
				});

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.users_fragment_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menuUsersFragmentNewUser:

			User newUser = activity.drinkMixer.newUser();

			usersSpinner.setSelection(activity.drinkMixer.getUsers().indexOf(
					newUser));

			InputMethodManager m = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (m != null) {
				m.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
			}

			nameEditText.requestFocus();

			break;

		case R.id.menuUsersFragmentDeleteUser:

			showUserDeletionDecisionDialog();

			break;

		case R.id.menuUsersFragmentResetSession:

			selectedUser.newSession();

			refreshAllViews();
			break;

		case R.id.menuUsersFragmentResetAllStatistics:

			selectedUser.resetAllStatistics();

			refreshAllViews();
			break;

		default:
		}

		return super.onOptionsItemSelected(item);
	}

	private void refreshAllViews() {

		adap.notifyDataSetChanged();
		((DrinkStatisticsEfficientListAdapter) drinkStatusListView.getAdapter())
				.notifyDataSetChanged();

		nameEditText.setText(selectedUser.getName());
		weightEditText.setText(selectedUser.getWeight() + "");
		ageEditText.setText(selectedUser.getAge() + "");
		sizeEditText.setText(selectedUser.getSize() + "");

		if (selectedUser.isMale()) {
			sexRadioGroup.check(R.id.radioButtonMale);
		} else {
			sexRadioGroup.check(R.id.radioButtonFemale);
		}

		DecimalFormat df = new DecimalFormat("#.##");
		DecimalFormat df2 = new DecimalFormat("#");

		drinkCountTextView.setText(df2.format(selectedUser.getDrinkCount()));
		pureAlcoholTextView.setText(df.format(selectedUser.getPureAlcohol()));
		beerEquivalenTextView.setText(df.format(selectedUser
				.getBeerEquivalent()));
		promilleTextView.setText(df.format(selectedUser.getPromille()));
		costsTextView.setText(df.format(selectedUser.getCosts()));

	}

	public void showUserDeletionDecisionDialog() {

		AlertDialog ad = new AlertDialog.Builder(activity).create();

		ad.setMessage("Wirklich löschen?");

		ad.setButton(AlertDialog.BUTTON_POSITIVE, "ja",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						activity.drinkMixer.deleteUser(selectedUser);

						usersSpinner.setSelection(0);

						dialog.dismiss();

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

}
