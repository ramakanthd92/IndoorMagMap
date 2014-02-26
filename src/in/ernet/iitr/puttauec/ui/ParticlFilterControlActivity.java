package in.ernet.iitr.puttauec.ui;

import in.ernet.iitr.puttauec.R;
import in.ernet.iitr.puttauec.algorithms.ParticleFiltering;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

public class ParticlFilterControlActivity extends Activity {
	protected static final String TAG = "ParticleFilterControlActivity";
	
	public static final String KEY_PARTICLE_COUNT_VALUE = "ParticleCountValue";
	public static final String KEY_STEP_NOISE_VALUE = "StepNoiseValue";
	public static final String KEY_SENSE_NOISE_VALUE = "SenseNoiseValue";
	
	private SeekBar mParticleCountSlider;
	private TextView  mParticleCountValue;
	private SeekBar mStepNoiseSlider;
	private SeekBar mSenseNoiseSlider;
	private TextView  mStepNoiseValue;
	private TextView  mSenseNoiseValue;
	private Intent mReturnValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_particl_filter_control);
		
		// This is the result of the activity
		mReturnValue = new Intent();
		
		mParticleCountValue = (TextView) findViewById(R.id.particle_count_value);
		mParticleCountSlider = (SeekBar) findViewById(R.id.particle_count_slider);
		mParticleCountSlider.setOnSeekBarChangeListener(	
			new SeekBar.OnSeekBarChangeListener() {
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					Log.i(TAG, "New Particle Count: " + progress);
					mParticleCountValue.setText("" + (progress/1000.f));
					mReturnValue.putExtra(KEY_PARTICLE_COUNT_VALUE, progress/1000.f);
					setResult(RESULT_OK, mReturnValue);
				}
	
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					return;
				}
	
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					return;
				}
				
			}
		);
		
      mParticleCountSlider.setProgress(Math.round(1000*getIntent().getFloatExtra(KEY_PARTICLE_COUNT_VALUE, ParticleFiltering.DEFAULT_PARTICLE_COUNT)));
		
      mStepNoiseSlider = (SeekBar) findViewById(R.id.step_noise_slider);
      mStepNoiseValue = (TextView) findViewById(R.id.step_noise_value);
      mStepNoiseSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				return;
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				return;
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				Log.i(TAG, "New Step Noise Value: " + (progress/1000.f));
				mStepNoiseValue.setText("" + progress/1000.f);
				mReturnValue.putExtra( KEY_STEP_NOISE_VALUE, progress/1000.f);
				setResult(RESULT_OK, mReturnValue);
			}
		});
      mStepNoiseSlider.setProgress(Math.round(1000*getIntent().getFloatExtra(KEY_STEP_NOISE_VALUE, ParticleFiltering.DEFAULT_STEP_NOISE_THRESHOLD/1000.f)));
	
      mSenseNoiseSlider = (SeekBar) findViewById(R.id.sense_noise_slider);
      mSenseNoiseValue = (TextView) findViewById(R.id.sense_noise_value);
      mSenseNoiseSlider.setOnSeekBarChangeListener(
    		  new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				return;
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				return;
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				Log.i(TAG, "New Step Noise Value: " + (progress/1000.f));
				mSenseNoiseValue.setText("" + progress/1000.f);
				mReturnValue.putExtra( KEY_SENSE_NOISE_VALUE, progress/1000.f);
				setResult(RESULT_OK, mReturnValue);
			}
		});
      mSenseNoiseSlider.setProgress(Math.round(1000*getIntent().getFloatExtra(KEY_SENSE_NOISE_VALUE, ParticleFiltering.DEFAULT_SENSE_NOISE_THRESHOLD/1000.f)));
	}
}
	