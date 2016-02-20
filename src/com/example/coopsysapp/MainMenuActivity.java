package com.example.coopsysapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainMenuActivity extends Activity {

	
	public Button btnDetail, btnAddEinkauf, btnPay;
	public TextView tvAccount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		initialize();
		
		getTotalDebt task = new getTotalDebt(MainMenuActivity.this);
		task.execute();
		
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initialize() {	
		tvAccount = (TextView) findViewById(R.id.textView1);
		
		btnDetail = (Button) findViewById(R.id.button1);
		btnAddEinkauf = (Button) findViewById(R.id.button3);
		btnPay = btnAddEinkauf = (Button) findViewById(R.id.button2);
		
		btnDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
	            startActivity(intent);      				
			}
		});
		
		
		btnAddEinkauf.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), AddEinkaufActivity.class);
	            startActivity(intent);   				
			}
		});
	}
	
	private class getTotalDebt extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog progress ;

		public getTotalDebt(MainMenuActivity activity) {
			progress= new ProgressDialog(activity);
		}
		
		@Override
		protected void onPreExecute() {
			progress.setMessage("Lade Benutzer, bitte warten ...");
	        progress.show();
		}

	    @Override
	    protected void onProgressUpdate(Integer... values) {
	      
	    }

		@Override
		protected Void doInBackground(Void... params) {
					
			try {
				tvAccount.setText("+ 25,83 €");
				tvAccount.setTextColor(Color.GREEN);
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			       		return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			
			if (progress.isShowing()) {
	        	progress.dismiss();
	        }
			super.onPostExecute(result);
		}

	}
}
