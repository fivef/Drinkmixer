package sp.drinkmixer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingsFragment extends Fragment {
	DrinkMixerActivity activity;

	TextView pressureValueTextView;
	
	
	Switch motorRightLeftSwitch;
	
	SeekBar positionSeekBar;

	public SettingsFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.settings_fragment, container,
				false);

		activity = (DrinkMixerActivity) getActivity();
		
		pressureValueTextView = (TextView) view.findViewById(R.id.settingsFragmentTextViewPressure);

		positionSeekBar = (SeekBar) view.findViewById(R.id.settingsFragmentSeekBarPWM);
		
		positionSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO: hbridge pwm progress
				
				activity.drinkMixer.sendECMDCommand("hbridge setpoint " + progress);
				
			}
		});
		
		
		
		motorRightLeftSwitch = (Switch) view.findViewById(R.id.settingsFragmentMotorDirection);
		
		motorRightLeftSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked){
					activity.drinkMixer.sendECMDCommand("hbridge f");
				}else{
					activity.drinkMixer.sendECMDCommand("hbridge b");
				}
				
			}
		});

		return view;

	}

	public void setPressureValue(String value) {

		pressureValueTextView.setText(value);

	}

}
