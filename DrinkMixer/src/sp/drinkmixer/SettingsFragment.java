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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsFragment extends Fragment {
	DrinkMixerActivity activity;

	TextView pressureValueTextView;
	
	SeekBar positionSeekBar;
	
	Switch compressorOnOffSwitch;
	
	Button fillShotsButton;
	
	EditText ecmdCommand;
	Button ecmdSend;

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
		
		positionSeekBar.setMax(DrinkMixer.WORKING_SPACE_WIDTH_IN_ENCODER_PULSES);
		
		//set handle to middle position
		positionSeekBar.setProgress(DrinkMixer.WORKING_SPACE_WIDTH_IN_ENCODER_PULSES/2);
		
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

				activity.drinkMixer.sendECMDCommand("hbridge setpoint " + (progress - DrinkMixer.WORKING_SPACE_WIDTH_IN_ENCODER_PULSES/2));
				
			}
		});
		
		
		compressorOnOffSwitch = (Switch) view.findViewById(R.id.settingsFragmentCompressor);
		
		compressorOnOffSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
					
					activity.drinkMixer.setPressureControlEnabled(isChecked);

			}
		});
		
		
		fillShotsButton = (Button) view.findViewById(R.id.settingsFragmentFillShotsButton);
		
		fillShotsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				activity.drinkMixer.startFillShotsThread();				
			}
		});
		
		
		ecmdCommand = (EditText) view.findViewById(R.id.settingsFragmentECMDEditText);
		
		ecmdSend = (Button) view.findViewById(R.id.settingsFragmentECMDSendButton);
		ecmdSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				activity.drinkMixer.sendECMDCommand(ecmdCommand.getText().toString());
				
			}
		});

		return view;

	}

	public void setPressureValue(String value) {

		pressureValueTextView.setText(value);

	}

}
