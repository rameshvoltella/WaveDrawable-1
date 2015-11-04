package sample.byhook.wavedrawable;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import lib.byhook.drawable.WaveDrawable;

public class MainActivity extends Activity {

	private ImageView iv_logo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		iv_logo = (ImageView) findViewById(R.id.iv_logo);
		WaveDrawable waveDrawable = new WaveDrawable();
		iv_logo.setImageDrawable(waveDrawable);
		waveDrawable.init();
	}
}
