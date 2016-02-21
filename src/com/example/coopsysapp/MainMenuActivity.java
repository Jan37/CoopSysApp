package com.example.coopsysapp;

import java.io.IOException;
import java.net.UnknownHostException;

import com.example.coopsysapp.exception.FunctionNotDefinedException;
import com.example.coopsysapp.exception.NotFoundException;
import com.example.coopsysapp.util.Dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainMenuActivity extends Activity {

	
	public Button btnDetail, btnAddEinkauf, btnPay;
	public TextView tvAccount, tvUsername;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		initialize();
		
		getTotalDebt task = new getTotalDebt(MainMenuActivity.this);
		task.execute();
		
	}
	
	@Override
	protected void onStart() {

		super.onStart();
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
		tvUsername = (TextView) findViewById(R.id.textViewUsername);
		
		btnDetail = (Button) findViewById(R.id.button1);
		btnAddEinkauf = (Button) findViewById(R.id.button2);
		btnPay = (Button) findViewById(R.id.button3);
		
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
		
		btnPay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), PayActivity.class);
	            startActivity(intent); 
				
			}
		});
		
		tvUsername.setText(ServerConnector.getUser().getName());
		
	}
	
	private class getTotalDebt extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog progress ;
		private float account;
		private MainMenuActivity activity;
		
		public getTotalDebt(MainMenuActivity activity) {
			if(progress !=null)
			{
			    progress = null;
			}
			progress= new ProgressDialog(activity);
			this.activity=activity;
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
				account = ServerConnector.getTotalDebt(ServerConnector.getUser().getId());
				if (account==0) {
					tvAccount.setText("+- 0€");
					tvAccount.setTextColor(Color.BLACK);
				}else if (account>0) {
					tvAccount.setText("+ " + String.valueOf(account));
					tvAccount.setTextColor(Color.GREEN);
				}else if (account<0) {
					tvAccount.setText("- " + String.valueOf(account));
					tvAccount.setTextColor(Color.RED);
				}
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				tvAccount.setText("Fehler 1");
				tvAccount.setTextColor(Color.BLACK);
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FunctionNotDefinedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				tvAccount.setText("-NumberFormatE-");
				tvAccount.setTextColor(Color.RED);
				Log.e("parseFloat", e.getMessage());
			}
			
			       		return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			
			if (progress.isShowing()) {
	        	progress.dismiss();
	        }
			//progress=null;
			super.onPostExecute(result);
		}

	}
}
