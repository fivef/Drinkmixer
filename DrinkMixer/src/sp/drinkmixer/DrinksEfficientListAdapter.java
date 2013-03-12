/*
 * Copyright (C) 2013 Steffen Pfiffner
 * 
 * Licence: GPL v3
 */

package sp.drinkmixer;

import java.util.ArrayList;

import android.R.interpolator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class DrinksEfficientListAdapter extends BaseAdapter implements
		Filterable {
	private LayoutInflater mInflater;

	private DrinkMixerActivity activity;

	public float onTouchDownPosition = 0;

	ArrayList<Drink> filteredDrinksList = new ArrayList<Drink>();

	public DrinksEfficientListAdapter(DrinkMixerActivity activity) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(activity);
		this.activity = activity;

	}

	/**
	 * Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	public View getView(final int position, View myConvertView, ViewGroup parent) {
		// A ViewHolder keeps references to children views to avoid
		// unneccessary calls
		// to findViewById() on each row.
		ViewHolder holder;

		myConvertView = mInflater
				.inflate(R.layout.drinks_adapter_content, null);

		// Creates a ViewHolder and store references to the two children
		// views
		// we want to bind data to.
		holder = new ViewHolder();
		holder.textLine = (TextView) myConvertView.findViewById(R.id.textLine);

		myConvertView.setOnTouchListener(new MyOnTouchListener(activity,
				position));

		myConvertView.setTag(holder);

		holder.textLine.setText(filteredDrinksList.get(position).getName());

		holder.id = position;

		return myConvertView;
	}

	static class ViewHolder {
		TextView textLine;

		int id;

	}

	@Override
	public Filter getFilter() {

		return new DrinksFilter();

	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return filteredDrinksList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return filteredDrinksList.get(position);
	}

	public Animation runBounceBackAnimationOn(View target, float viewPosition) {

		// Animation animation = AnimationUtils.loadAnimation(activity,
		// R.anim.bounce_back_animation);

		TranslateAnimation translate = new TranslateAnimation(viewPosition, 0,
				0, 0);

		translate.setDuration(700);

		translate.setFillAfter(false);

		translate.setInterpolator(activity, interpolator.bounce);

		translate.setAnimationListener(new myBounceAnimationListener(target));

		target.startAnimation(translate);

		return translate;
	}

	class myBounceAnimationListener implements AnimationListener {

		View view;

		public myBounceAnimationListener(View view) {
			this.view = view;
		}

		@Override
		public void onAnimationEnd(Animation animation) {

			//
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationStart(Animation animation) {

		}

	}

	class MyOnTouchListener implements OnTouchListener {

		DrinkMixerActivity activity;

		int position;
		boolean flingedOut = false;

		public MyOnTouchListener(DrinkMixerActivity activity, int position) {
			this.activity = activity;
			this.position = position;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			float viewPosition = 0;

			if (event.getAction() == MotionEvent.ACTION_DOWN) {

				flingedOut = false;
				onTouchDownPosition = event.getRawX() + v.getX();

				if (DrinksFragment.actionModeEnabled) {
					return false;
				} else {
					return true;
				}
			}

			if (event.getAction() == MotionEvent.ACTION_MOVE) {

				if (!flingedOut) {

					viewPosition = event.getRawX() - onTouchDownPosition;

					if (viewPosition >= 10) {

						v.setX(viewPosition);

						v.getParent().requestDisallowInterceptTouchEvent(true);

					} else {
						v.setX(0);
					}

					if (viewPosition > v.getWidth() / 2) {

						Drink drink = filteredDrinksList.get(position);
						// Drink drink =
						// activity.drinkMixer.getDrinkByName(name)

						activity.openMixingDrinkFragment(drink);
						// runFlingOutAnimationOn(v, position);
						flingedOut = true;

					}

				}

			}

			if (event.getAction() == MotionEvent.ACTION_UP
					|| event.getAction() == MotionEvent.ACTION_OUTSIDE) {

				if (viewPosition < v.getWidth() / 2) {
					float viewPos = v.getX();

					v.setX(0);
					runBounceBackAnimationOn(v, viewPos);

				}

			}

			return false;

		}

	}

	
	
	private class DrinksFilter extends Filter {

		ArrayList<Drink> tempFilteredDrinksList = new ArrayList<Drink>();

		public static final String FILTER_ONLY_IN_CONFIGURATION = "inConfigOnly";

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			tempFilteredDrinksList.clear();

			FilterResults results = new FilterResults();

			boolean allIngredientsInConfig = false;

			if (constraint == FILTER_ONLY_IN_CONFIGURATION) {

				for (Drink drink : activity.drinkMixer.getDrinks()) {

					for (IngredientInDrink ingredientInDrink : drink
							.getIngredients()) {

						if (activity.drinkMixer.getConfiguration().contains(
								ingredientInDrink.ingredient)) {

							allIngredientsInConfig = true;
						} else {

							allIngredientsInConfig = false;
							break;
						}
					}

					if (allIngredientsInConfig) {
						tempFilteredDrinksList.add(drink);
					}
				}

				results.values = tempFilteredDrinksList;
				results.count = tempFilteredDrinksList.size();

			} else {
				results.values = activity.drinkMixer.getDrinks();
				results.count = activity.drinkMixer.getDrinks().size();
			}

			// results.values = filteredResults;
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {

			filteredDrinksList = (ArrayList<Drink>) results.values;

			notifyDataSetChanged();

		}

	}
}
