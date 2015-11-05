package sample.byhook.wavedrawable;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
		final WaveDrawable waveDrawable = new WaveDrawable();
		waveDrawable.init();

		iv_logo.setImageDrawable(waveDrawable);
		//iv_logo.setBackground(waveDrawable);

		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<100;i++){
					try {
						Thread.sleep(50);
						waveDrawable.setProgress((float)(i+1)/100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}


				}
			}
		}).start();
	}

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {

		}
	};
}
