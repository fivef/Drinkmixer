/*
 * Copyright (C) 2013 Steffen Pfiffner
 * 
 * Licence: GPL v3
 */

package sp.drinkmixer;

import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MixDrinkAsyncTask extends
		AsyncTask<IngredientInDrink, Integer, Integer> {

	DrinkMixerActivity activity;
	DrinkMixer drinkMixer;
	IngredientInDrink ingredientToPutInDrink;
	ProgressBar progressBar;
	Drink currentDrink;
	ImageView finishedIcon;

	long waitTimeFor1MlInMilliseconds;

	public MixDrinkAsyncTask(DrinkMixerActivity activity, Drink currentDrink,
			IngredientInDrink ingredientToPutInDrink) {

		this.activity = activity;

		this.drinkMixer = activity.drinkMixer;

		this.ingredientToPutInDrink = ingredientToPutInDrink;

		this.currentDrink = currentDrink;

	}

	@Override
	protected void onPreExecute() {

		LinearLayout ingredientLinearLayout = new LinearLayout(activity);
		ingredientLinearLayout.setOrientation(LinearLayout.VERTICAL);

		ingredientLinearLayout.setPadding(10, 5, 10, 5);

		LinearLayout innerLinearLayout = new LinearLayout(activity);
		ingredientLinearLayout.setOrientation(LinearLayout.VERTICAL);

		TextView ingredient = new TextView(activity, null,
				android.R.style.TextAppearance_DeviceDefault_Medium);

		ingredient.setText(ingredientToPutInDrink.ingredient.getName());

		ingredient.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);

		innerLinearLayout.addView(ingredient);

		finishedIcon = new ImageView(activity);
		finishedIcon.setImageResource(R.drawable.ic_cab_done_holo_dark);
		finishedIcon.setVisibility(View.INVISIBLE);
		finishedIcon.setPadding(10, 0, 0, 0);

		innerLinearLayout.addView(finishedIcon);

		ingredientLinearLayout.addView(innerLinearLayout);

		progressBar = new ProgressBar(activity);

		progressBar = new ProgressBar(activity, null,
				android.R.attr.progressBarStyleHorizontal);

		progressBar.setScaleY(2);

		progressBar.setIndeterminate(false);

		progressBar.setMax(1000);

		ingredientLinearLayout.addView(progressBar);

		if(activity.mixingDrinkFragment != null){
			LinearLayout parentLayout = (LinearLayout) activity.mixingDrinkFragment.view
					.findViewById(R.id.mixingDrinkLayout);

			parentLayout.addView(ingredientLinearLayout);
		
		}

		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(IngredientInDrink... ingredient) {

		IngredientInDrink myingredientToPutInDrink = ingredient[0];

		drinkMixer.openValveForIngredient(myingredientToPutInDrink.ingredient);

		double totalWaitTimeInMilliseconds = (myingredientToPutInDrink.amount / myingredientToPutInDrink.ingredient
				.getFlowRate()) * 1000;


		if (myingredientToPutInDrink.amount > 0) {
			waitTimeFor1MlInMilliseconds = (long) totalWaitTimeInMilliseconds
					/ myingredientToPutInDrink.amount;
		} else {
			waitTimeFor1MlInMilliseconds = 0;
		}

		for (int i = 1; i <= myingredientToPutInDrink.amount; i++) {
			try {
				Thread.sleep(waitTimeFor1MlInMilliseconds);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

			double currentAmount = i;
			double totalAmount = myingredientToPutInDrink.amount;

			int progress;

			if (totalAmount != 0) {
				progress = (int) ((currentAmount / totalAmount) * 1000);
			} else {
				progress = 1000;
			}

			publishProgress(progress);

			if (isCancelled()) {

				// return ml already put into drink
				return (int) Math.ceil(currentAmount);
			}
		}

		drinkMixer.closeValveOfIngredient(myingredientToPutInDrink.ingredient);

		return myingredientToPutInDrink.amount;
	}

	@Override
	protected void onPostExecute(Integer result) {

		// show finished icon
		finishedIcon.setVisibility(View.VISIBLE);

		/*
		 * if connected or in debug mode alter ingredient amounts
		 */
		if (drinkMixer.isConnectedToNETIO() || DrinkMixerActivity.DEBUG_MODE) {

			ingredientToPutInDrink.ingredient.use(result);

		}

		// if the current task is the last running, the drink is ready:
		if (drinkMixer.getRunningTasks().size() == 1) {

			// all finished
			drinkMixer.getRunningTasks().remove(this);

			// if cleaning finished set cleaning to false
			drinkMixer.setCleaning(false);

			// if connected or in debug mode increment global drink usage count
			// and let the current user consume a drink
			if (drinkMixer.isConnectedToNETIO()
					|| DrinkMixerActivity.DEBUG_MODE) {

				currentDrink.incrementUsageCount();
				drinkMixer.getCurrentUser().drink(currentDrink);
			}

			if(activity.mixingDrinkFragment != null){
				
				activity.mixingDrinkFragment.setDone();
				
				//TODO: do this only if activity is open or reopen activity automatically
				activity.openLeaderboardFragment();
			}

			

		} else { // there are more running tasks:

			// delete this task from running list

			drinkMixer.getRunningTasks().remove(this);
		}

		super.onPostExecute(result);
	}

	@Override
	protected void onCancelled(Integer result) {

		drinkMixer.closeValveOfIngredient(ingredientToPutInDrink.ingredient);

		if (drinkMixer.isConnectedToNETIO()) {

			ingredientToPutInDrink.ingredient.use(result);

		}

		drinkMixer.getRunningTasks().remove(this);

		super.onCancelled(result);
	}

	protected void onProgressUpdate(Integer... progress) {

		// set progress in UI

		progressBar.setProgress(progress[0]);

	}

}
