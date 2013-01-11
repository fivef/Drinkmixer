package sp.drinkmixer;


import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.crittercism.app.Crittercism;
import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;

public class DrinkMixerActivity extends Activity {

	public final static boolean DEBUG_MODE = true;

	public DrinkMixer drinkMixer;

	ActionBar actionBar;

	DrinkPropertiesFragment drinkPropiertiesFragment;

	DrinksFragment drinksFragment;

	IngredientsFragment ingredientsFragment;

	UsersFragment usersFragment;

	LeaderBoardFragment leaderBoardFragment;

	public LeaderBoardFragment getLeaderBoardFragment() {
		return leaderBoardFragment;
	}

	MixingDrinkFragment mixingDrinkFragment;

	ConfigurationFragment configurationFragment;

	SettingsFragment settingsFragment;

	TextToSpeech textToSpeech;

	// needed for showIngredientEmptyRefillDecitionDialog
	Ingredient ingredientToRefill;

	// Db stuff
	private ObjectContainer db;
	private final static String DB4OFILENAME = "dataModel.db";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);

		Crittercism.init(getApplicationContext(), "503f8db62cd95220ce000006");
		
		initTTS();

		drinkMixer = new DrinkMixer(this);

		restoreDataModel();

		openLeaderboardFragment();

		/*
		 * This catches all unhandled Exceptions and logs the error with a user
		 * description to file
		 */
		/*Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				
				showCatchAllErrorDialog(ex.toString());

			}
		});
		

		ArrayList<Drink> test = new ArrayList<Drink>();

		test.get(0);
		*/
		
		//showCatchAllErrorDialog("test");

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
	}

	@Override
	protected void onStart() {

		// to refresh the isconnected status icon
		invalidateOptionsMenu();

		drinkMixer.connectToNetIO();

		super.onStart();
	}

	@Override
	protected void onStop() {

		saveStateToDB();
		drinkMixer.disconnectFromNetIO();

		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (db != null) {
			db.close();

			System.out.println("DB closed");
		}

		textToSpeech.shutdown();

	}

	public void showErrorDialog(String error) {

		AlertDialog ad = new AlertDialog.Builder(this).create();
		ad.setCancelable(false); // This blocks the 'BACK' button
		ad.setMessage(error);
		ad.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				openDrinksFragment();
			}
		});

		ad.show();
	}

	public void showCatchAllErrorDialog(String error) {

		AlertDialog ad = new AlertDialog.Builder(this).create();
		ad.setCancelable(false); // This blocks the 'BACK' button
		ad.setMessage(error);
		final EditText input = new EditText(this);
		ad.setView(input);

		ad.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				openDrinksFragment();
			}
		});

		ad.show();
	}

	public void showIngredientEmptyRefillDecitionDialog(Ingredient ingredient) {

		ingredientToRefill = ingredient;

		AlertDialog ad = new AlertDialog.Builder(this).create();
		// ad.setCancelable(false); // This blocks the 'BACK' button
		ad.setMessage(ingredient.getName() + " enthält nur noch "
				+ ingredient.getCurrentAmount()
				+ " ml, soll nachgefüllt werden?");
		ad.setTitle(ingredient.getName());
		ad.setButton(AlertDialog.BUTTON_POSITIVE, "Ja",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						ingredientToRefill.refill();

						// TODO: ask the user again if he has refilled

						dialog.dismiss();
						openDrinksFragment();

					}
				});

		ad.setButton(AlertDialog.BUTTON_NEGATIVE, "Nein",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						openDrinksFragment();
					}
				});

		ad.show();
	}

	public void showNewSessionDecitionDialog() {

		AlertDialog ad = new AlertDialog.Builder(this).create();
		// ad.setCancelable(false); // This blocks the 'BACK' button
		ad.setMessage("Sollen wirklich alle Session-Zähler zurückgesetzt werden?");
		ad.setButton(AlertDialog.BUTTON_POSITIVE, "Ja",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						drinkMixer.newSession();

						dialog.dismiss();

						openDrinksFragment();

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
	public void onBackPressed() {

		saveStateToDB();

		if (mixingDrinkFragment != null && mixingDrinkFragment.isVisible()) {

			drinkMixer.stopAllRunningTasks();

		}

		if (!leaderBoardFragment.isVisible()) {

			openLeaderboardFragment();
		}

		return;

	}

	public void openDrinkPropertiesFragment(Drink drink) {

		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setTitle("Drink bearbeiten");

		if (drink == null) {

			// new Drink
			drinkPropiertiesFragment = new DrinkPropertiesFragment(null);

		} else {

			// known drink

			drinkPropiertiesFragment = new DrinkPropertiesFragment(drink);

		}

		// get menu defined in fragment to work
		drinkPropiertiesFragment.setHasOptionsMenu(true);

		// Execute a transaction, replacing any existing fragment
		// with this one inside the frame.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, drinkPropiertiesFragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

	}

	public void openDrinksFragment() {

		if (drinksFragment == null) {
			drinksFragment = new DrinksFragment();

			// get menu defined in fragment to work
			drinksFragment.setHasOptionsMenu(true);
		}

		if (drinksFragment.isVisible()) {
			return;
		}

		actionBar = getActionBar();
		actionBar.setTitle("Drinks");
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Execute a transaction, replacing any existing fragment
		// with this one inside the frame.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, drinksFragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

	}

	public void openIngredientsFragment() {

		if (ingredientsFragment == null) {
			ingredientsFragment = new IngredientsFragment();

			// get menu defined in fragment to work
			ingredientsFragment.setHasOptionsMenu(true);
		}
		actionBar = getActionBar();
		actionBar.setTitle("Zutaten");
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Execute a transaction, replacing any existing fragment
		// with this one inside the frame.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, ingredientsFragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

	}

	public void openUsersFragment() {

		if (usersFragment == null) {
			usersFragment = new UsersFragment();

			// get menu defined in fragment to work
			usersFragment.setHasOptionsMenu(true);
		}
		actionBar = getActionBar();
		actionBar.setTitle("Benutzer");
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Execute a transaction, replacing any existing fragment
		// with this one inside the frame.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, usersFragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

	}

	public void openLeaderboardFragment() {

		saveStateToDB();

		if (leaderBoardFragment == null) {
			leaderBoardFragment = new LeaderBoardFragment();

			// get menu defined in fragment to work
			leaderBoardFragment.setHasOptionsMenu(true);
		}
		actionBar = getActionBar();
		actionBar.setTitle("Leaderboard");
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);

		// Execute a transaction, replacing any existing fragment
		// with this one inside the frame.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, leaderBoardFragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

	}

	public void openConfigurationFragment() {

		if (configurationFragment == null) {
			configurationFragment = new ConfigurationFragment();

			// get menu defined in fragment to work
			configurationFragment.setHasOptionsMenu(true);
		}

		actionBar = getActionBar();
		actionBar.setTitle("Belegung");
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Execute a transaction, replacing any existing fragment
		// with this one inside the frame.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, configurationFragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

	}

	public void openSettingsFragment() {

		if (settingsFragment == null) {
			settingsFragment = new SettingsFragment();

			// get menu defined in fragment to work
			settingsFragment.setHasOptionsMenu(true);
		}

		actionBar = getActionBar();
		actionBar.setTitle("Einstellungen");
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Execute a transaction, replacing any existing fragment
		// with this one inside the frame.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, settingsFragment);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

	}

	public void openMixingDrinkFragment(Drink drinkToMix) {

		actionBar = getActionBar();

		actionBar.setTitle("Zubereitung");

		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mixingDrinkFragment = new MixingDrinkFragment(drinkToMix);

		// get menu defined in fragment to work
		mixingDrinkFragment.setHasOptionsMenu(true);

		// Execute a transaction, replacing any existing fragment
		// with this one inside the frame.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.animator.slide_in, R.animator.slide_out);
		ft.replace(android.R.id.content, mixingDrinkFragment);
		ft.commit();
	}

	public static void listResult(List<?> result) {
		System.out.println(result.size());
		for (Object o : result) {
			System.out.println(o);
		}
	}

	private void restoreDataModel() {

		// accessDb4o to restore drinkMixer data. If no drinkmixer found create
		// new
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();

		config.common().objectClass(DataModel.class).cascadeOnUpdate(true);
		config.common().objectClass(DataModel.class).cascadeOnDelete(true);
		config.common().objectClass(DataModel.class).cascadeOnActivate(true);

		// config.common().objectClass(DataModel.class).updateDepth(10);

		config.common().updateDepth(5);

		db = Db4oEmbedded.openFile(config, getFilesDir() + "/" + DB4OFILENAME);

		ObjectSet<DataModel> result = db.queryByExample(DataModel.class);

		listResult(result);

		if (result.hasNext()) {

			DataModel dataModel = (DataModel) result.next();
			drinkMixer.setData(dataModel);
			
			/* the users arraylist got lost, whats the problem. This was used to reinitialize it.
			drinkMixer.getDataModel().users = new ArrayList<User>();
			
			User user = drinkMixer.newUser();

			user.setName("Standard Benutzer");
			*/

		} else {

			// init configuration array with 11 elements of ingredient "leer"
			// and valve state accordingly
			drinkMixer.initConfiguration();

			// initial stuff to test

			Drink drink = drinkMixer.newDrink("Wodka O");

			drink.addIngredient(drinkMixer.newIngredient("Wodka", 700), 40);

			drink.addIngredient(drinkMixer.newIngredient("Orangensaft", 1000),
					150);

			drink = drinkMixer.newDrink("Whisky Cola");

			drink.addIngredient(drinkMixer.newIngredient("Whisky", 700), 40);

			drink.addIngredient(drinkMixer.newIngredient("Cola", 1500), 150);

			drink = drinkMixer.newDrink("Jägi Bull");

			drink.addIngredient(drinkMixer.newIngredient("Jägi", 500), 40);

			drink.addIngredient(drinkMixer.newIngredient("Bull", 1500), 150);

			User user = drinkMixer.newUser();

			user.setName("Standard Benutzer");

			saveStateToDB();

		}

	}

	/*
	 * database change code (nearly working) this can be used if changes in
	 * datamodel were made
	 */

	public void afterDataModelChangeMigrateDatabase() {
		/*
		 * DataModel dataModel = (DataModel) result.next();
		 * 
		 * DataModel newDataModel = new DataModel();
		 * drinkMixer.setData(newDataModel); drinkMixer.initConfiguration();
		 * 
		 * ArrayList<Drink> drinks = new ArrayList<Drink>(); for(Drink
		 * drink:dataModel.drinks){
		 * 
		 * try { drinks.add((Drink) drink.clone()); } catch
		 * (CloneNotSupportedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 * 
		 * 
		 * drinkMixer.setDrinks(drinks);
		 * 
		 * 
		 * ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
		 * for(Ingredient ingredient: dataModel.ingredients){
		 * 
		 * try { ingredients.add((Ingredient) ingredient.clone()); } catch
		 * (CloneNotSupportedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 * 
		 * drinkMixer.setIngredients(ingredients);
		 */
	}

	public void saveStateToDB() {

		if (db != null) {

			DataModel dataModel = drinkMixer.getDataModel();

			db.store(dataModel);

			db.commit();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.drink_mixer_activity_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.DrinkMixerActivityMenuClean:

			Drink cleanDrink = new Drink("reinigen");

			/*
			 * for(Ingredient ingredient: drinkMixer.getConfiguration()){
			 * 
			 * cleanDrink.addIngredient(ingredient, 20); }
			 */

			openMixingDrinkFragment(cleanDrink);

			break;

		case android.R.id.home:

			// openDrinksFragment();

			onBackPressed();

			break;

		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	public void speak(String stringToSpeak) {

		textToSpeech.setLanguage(Locale.GERMANY);

		HashMap<String, String> myHashMap = new HashMap<String, String>();
		myHashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "drink");

		textToSpeech.speak(stringToSpeak, TextToSpeech.QUEUE_FLUSH, myHashMap);

	}

	public void initTTS() {

		textToSpeech = new TextToSpeech(this, new OnInitListener() {

			@Override
			public void onInit(int status) {

			}
		});

		textToSpeech
				.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {

					@Override
					public void onUtteranceCompleted(String utteranceId) {

						if (utteranceId.compareTo("drink") == 0) {
							if (leaderBoardFragment.isVisible()) {

								leaderBoardFragment.hasTheUserTakenTheLead();

							}
						}

					}
				});
	}

	// hides keyboard if user clicks anywhere but on a edittext
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		View v = getCurrentFocus();
		boolean ret = super.dispatchTouchEvent(event);

		if (v instanceof EditText) {
			View w = getCurrentFocus();
			int scrcoords[] = new int[2];
			w.getLocationOnScreen(scrcoords);
			float x = event.getRawX() + w.getLeft() - scrcoords[0];
			float y = event.getRawY() + w.getTop() - scrcoords[1];

			Log.d("Activity",
					"Touch event " + event.getRawX() + "," + event.getRawY()
							+ " " + x + "," + y + " rect " + w.getLeft() + ","
							+ w.getTop() + "," + w.getRight() + ","
							+ w.getBottom() + " coords " + scrcoords[0] + ","
							+ scrcoords[1]);
			if (event.getAction() == MotionEvent.ACTION_UP
					&& (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w
							.getBottom())) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
						.getWindowToken(), 0);
			}
		}
		return ret;
	}

}
