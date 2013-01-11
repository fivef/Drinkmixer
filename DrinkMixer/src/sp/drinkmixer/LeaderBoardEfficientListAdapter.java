package sp.drinkmixer;

import java.text.DecimalFormat;

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
import android.widget.TextView;

public class LeaderBoardEfficientListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;

	private DrinkMixerActivity activity;

	public float onTouchDownPosition = 0;

	public LeaderBoardEfficientListAdapter(DrinkMixerActivity activity) {
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

		myConvertView = mInflater.inflate(R.layout.leaderboard_adapter_content,
				null);

		// Creates a ViewHolder and store references to the two children
		// views
		// we want to bind data to.
		holder = new ViewHolder();
		holder.userName = (TextView) myConvertView
				.findViewById(R.id.leaderboardNameTextView);

		holder.amount = (TextView) myConvertView
				.findViewById(R.id.leaderboardAmountTextView);

		myConvertView.setOnTouchListener(new MyOnTouchListener(activity,
				position));

		myConvertView.setTag(holder);

		holder.userName.setText(activity.drinkMixer.getUsers().get(position)
				.getName());
		
		DecimalFormat df = new DecimalFormat("#.##");
		DecimalFormat df2 = new DecimalFormat("#");

		switch (activity.getLeaderBoardFragment().getCurrentSortCriterium()) {

		// 0 = pure alcohol
		case 0:

			holder.amount.setText(df.format(activity.drinkMixer.getUsers().get(position)
					.getPureAlcohol()));
					

			break;
		// 1 = promille
		case 1:

			holder.amount.setText(df.format(activity.drinkMixer.getUsers().get(position)
					.getPromille()));

			break;
		// 2 = drinks count
		case 2:

			holder.amount.setText(df2.format(activity.drinkMixer.getUsers().get(position)
					.getDrinkCount()));

			break;
		// 3 = costs
		case 3:

			holder.amount.setText(df.format(activity.drinkMixer.getUsers().get(position)
					.getCosts()));
			break;

		// 4 = alphabetically
		case 4:

			holder.amount.setText("");

			break;

		default:

			break;

		}

		holder.id = position;

		return myConvertView;
	}

	static class ViewHolder {
		TextView userName;

		TextView amount;

		int id;

	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(activity.drinkMixer.getUsers() != null){
		return activity.drinkMixer.getUsers().size();
		}else{
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return activity.drinkMixer.getUsers().get(position);
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

						activity.drinkMixer.setCurrentUser(activity.drinkMixer
								.getUsers().get(position));

						// Drink drink =
						// activity.drinkMixer.getDrinkByName(name)

						activity.openDrinksFragment();
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

}
