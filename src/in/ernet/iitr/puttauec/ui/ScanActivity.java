package in.ernet.iitr.puttauec.ui;
import com.google.zxing.integration.android.*;
import in.ernet.iitr.puttauec.R;
import in.ernet.iitr.puttauec.R.id;
import in.ernet.iitr.puttauec.R.layout;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ScanActivity extends Activity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
	    System.out.println("ScanActivity");
	    Button scanBtn = (Button) findViewById (R.id.button_scan);
		scanBtn.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.scan, menu);
		return true;
	}
 
	public void onClick(View v){
		//respond to clicks
		if(v.getId()==R.id.button_scan){
			//scan
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
			}
		}
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanningResult != null) {
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			System.out.println(scanContent);
			System.out.println(scanFormat);
			setResult(RESULT_OK,intent);
			finish();
			}
		else{
		    Toast toast = Toast.makeText(getApplicationContext(), 
		        "No scan data received!", Toast.LENGTH_SHORT);
		    toast.show();
		}
	}
}
