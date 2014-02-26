package in.ernet.iitr.puttauec.ui;

import in.ernet.iitr.puttauec.R;
import in.ernet.iitr.puttauec.R.layout;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class InputActivity extends Activity {
	public final static String EXTRA_MESSAGE = "in.ernet.iitr.puttauec.ui.MESSAGE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.input, menu);
		return true;
	}

	public void StartMethod(View view){
		Intent intent = new Intent(this, ParticleFilteringActivity.class);
		EditText editText1 = (EditText) findViewById(R.id.file_name);
		String file = editText1.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, file);
		startActivity(intent);
	}
}
