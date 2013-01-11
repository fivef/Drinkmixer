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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class IngredientsFragment extends Fragment {
	DrinkMixerActivity activity;

	Ingredient selectedIngredient;

	String name = "CalibrationFragment";

	IngredientsSpinnerListAdapter adap;

	Spinner ingredientSpinner;
	EditText flowrateEditText;

	EditText amountEditText;

	EditText timeEditText;

	EditText totalAmountEditText;
	EditText currentAmountEditText;
	EditText priceEditText;
	EditText nameEditText;
	EditText alcoholEditText;

	/*
	 * Amount of this ingredient used since db creation in ml
	 */
	TextView totalUsageAmount;

	/*
	 * Amount of this ingredient used since session started in ml
	 */

	TextView sessionUsageAmount;

	ToggleButton valveToggleButton;

	long toggleStartTime;

	double timeDiff;

	// boolean isNewIngredient = false;

	DecimalFormat df = new DecimalFormat("#.##");

	public IngredientsFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.ingredients_fragment, container,
				false);

		activity = (DrinkMixerActivity) getActivity();

		ingredientSpinner = (Spinner) view
				.findViewById(R.id.calibrationSpinner);

		adap = new IngredientsSpinnerListAdapter(activity);

		// adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ingredientSpinner.setAdapter(adap);
		ingredientSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int selectedPosition, long id) {
						selectedIngredient = activity.drinkMixer
								.getIngredients().get(selectedPosition);

						setAllEditTexts();

					}

					public void onNothingSelected(AdapterView<?> parent) {
						// showToast("Spinner1: unselected");
					}
				});

		ingredientSpinner.setSelection(activity.drinkMixer.getIngredients()
				.indexOf(selectedIngredient));

		nameEditText = (EditText) view
				.findViewById(R.id.ingredientNameEditText);

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

				selectedIngredient.setName(s.toString());
				adap.notifyDataSetChanged();
				ingredientSpinner.setSelection(activity.drinkMixer
						.getIngredients().indexOf(selectedIngredient));

			}
		});

		// Make EditText Invisible
		// nameEditText.setVisibility(View.GONE);

		flowrateEditText = (EditText) view
				.findViewById(R.id.calibrationFlowrateEditText);

		flowrateEditText.addTextChangedListener(new TextWatcher() {

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

				// String flowrateText = flowrateEditText.getText().toString();

				// activity.showErrorDialog(flowrateText);

				// double flowrateFloat = df.parse(flowrateText).doubleValue();

				// activity.showErrorDialog(flowrateFloat + "");

				// selectedIngredient.setFlowRate(flowrateFloat);

				try {
					selectedIngredient.setFlowRate(Float
							.parseFloat(flowrateEditText.getText().toString()));
				} catch (NumberFormatException e) {

					selectedIngredient.setFlowRate(4.6);
				}

			}
		});

		amountEditText = (EditText) view
				.findViewById(R.id.calibrationAmountEditText);



		timeEditText = (EditText) view
				.findViewById(R.id.calibrationTimeEditText);

		timeEditText.addTextChangedListener(new TextWatcher() {

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

				calculateFlowrate();

			}
		});

		valveToggleButton = (ToggleButton) view
				.findViewById(R.id.calibration_valve_toggleButton);

		valveToggleButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						if (isChecked) {

							activity.drinkMixer
									.openValveForIngredient(selectedIngredient);
							toggleStartTime = System.currentTimeMillis();

						} else {

							activity.drinkMixer
									.closeValveOfIngredient(selectedIngredient);

							// time in milliseconds seconds
							timeDiff = (System.currentTimeMillis() - toggleStartTime);

							timeDiff = timeDiff / 1000.0;

							timeEditText.setText(timeDiff + "");

							calculateFlowrate();

						}

					}
				});

		totalAmountEditText = (EditText) view
				.findViewById(R.id.ingredientTotalAmountEditText);

		totalAmountEditText.addTextChangedListener(new TextWatcher() {

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

					selectedIngredient.setTotalAmount(Integer.parseInt(s
							.toString()));
					
					currentAmountEditText.setText(s.toString());

				} catch (NumberFormatException e) {

					selectedIngredient.setTotalAmount(0);

				}
			}
		});

		currentAmountEditText = (EditText) view
				.findViewById(R.id.ingredientCurrentAmountEditText);

		currentAmountEditText.addTextChangedListener(new TextWatcher() {

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

					selectedIngredient.setCurrentAmount(Integer.parseInt(s
							.toString()));

				} catch (NumberFormatException e) {

					selectedIngredient.setCurrentAmount(0);

				}

			}
		});

		priceEditText = (EditText) view
				.findViewById(R.id.ingredientPriceEditText);

		priceEditText.addTextChangedListener(new TextWatcher() {

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

					selectedIngredient.setPricePerMl(Double.parseDouble((s
							.toString())));

				} catch (NumberFormatException e) {

					selectedIngredient.setPricePerMl(0.00);

				}
			}
		});
		
		alcoholEditText = (EditText) view
				.findViewById(R.id.ingredientAlcoholConcentrationEditText);

		alcoholEditText.addTextChangedListener(new TextWatcher() {

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

					selectedIngredient.setAlcoholConcentration(Double.parseDouble((s
							.toString())));

				} catch (NumberFormatException e) {

					selectedIngredient.setAlcoholConcentration(0.00);

				}
			}
		});

		totalUsageAmount = (TextView) view
				.findViewById(R.id.TotalIngredientUsageCountInMlValue);

		sessionUsageAmount = (TextView) view
				.findViewById(R.id.SessionIngredientUsageCountInMlValue);

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.ingredients_fragment_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menuIngredientFragmentNewIngredient:

			Ingredient newIngredient = activity.drinkMixer.newIngredient();
			ingredientSpinner.setSelection(activity.drinkMixer.getIngredients()
					.indexOf(newIngredient));
			
			InputMethodManager m = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (m != null) {
				m.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
			}

			nameEditText.requestFocus();

			break;

		case R.id.menuIngredientFragmentDeleteIngredient:

			showIngredientDeletionDecisionDialog();

			break;

		default:
		}

		return super.onOptionsItemSelected(item);
	}

	private void calculateFlowrate() {

		int amount = 0;
		double time = 0;
		double flowRate = 0.0;

		try {

			amount = Integer.parseInt(amountEditText.getText().toString());

		} catch (NumberFormatException e) {

			amount = 0;

		}

		try {

			time = Double.parseDouble(timeEditText.getText().toString());

		} catch (NumberFormatException e) {

			time = 0;

		}

		if (time != 0) {
			flowRate = amount / time;
		} else {
			// activity.showErrorDialog("Division durch 0");
			return;
		}

		selectedIngredient.setFlowRate(flowRate);

		DecimalFormat df = new DecimalFormat("#.##");

		flowrateEditText.setText(df.format(flowRate) + "");

		return;
	}

	private void setAllEditTexts() {

		// flowrateEditText.setText(df.format(selectedIngredient.getFlowRate())
		// + "");

		flowrateEditText.setText(selectedIngredient.getFlowRate() + "");

		nameEditText.setText(selectedIngredient.getName());
		totalAmountEditText.setText(selectedIngredient.getTotalAmount() + "");
		currentAmountEditText.setText(selectedIngredient.getCurrentAmount()
				+ "");
		priceEditText.setText(selectedIngredient.getPricePerL() + "");
		totalUsageAmount.setText(selectedIngredient.getTotalUsageCountInMl()
				+ "");
		sessionUsageAmount.setText(selectedIngredient.getSessionUsageAmount()
				+ "");
		
		alcoholEditText.setText(selectedIngredient.getAlcoholConcentration() + "");

	}

	public void showIngredientDeletionDecisionDialog() {

		AlertDialog ad = new AlertDialog.Builder(activity).create();

		ad.setMessage("Wirklich löschen?");

		ad.setButton(AlertDialog.BUTTON_POSITIVE, "ja",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (activity.drinkMixer
								.deleteIngredient(selectedIngredient)) {

							ingredientSpinner.setSelection(0);

						} else {

							activity.showErrorDialog("Die Zutat kann nicht gelöscht werden, weil sie noch in einem Drink ist.");

						}

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
