package com.example.coopsysapp;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

import com.example.coopsysapp.exception.FunctionNotDefinedException;
import com.example.coopsysapp.exception.NotFoundException;
import com.example.coopsysapp.util.AccountTextView;
import com.example.coopsysapp.util.Dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends Activity {

	
	public Button btnDetail, btnAddEinkauf, btnPay, btnHighscore, btnPersonalDebt;
	public TextView tvUsername;
	public AccountTextView tvAccount, tvAccountTopic;
	private boolean doubleBackToExitPressedOnce=false;
	public DecimalFormat df = new DecimalFormat("0.00");

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		getActionBar().hide();
		
		initialize();		
	}
	
	@Override
	protected void onStart() {

		super.onStart();
	}
	
	@Override
	protected void onResume() {
		new getTotalDebt().execute(null, null , null);
		super.onResume();
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
		tvAccount = (AccountTextView) findViewById(R.id.textView1);
		tvAccountTopic = (AccountTextView) findViewById(R.id.textView3);

		tvUsername = (TextView) findViewById(R.id.textViewUsername);
		
		btnDetail = (Button) findViewById(R.id.button1);
		btnAddEinkauf = (Button) findViewById(R.id.button2);
		btnPay = (Button) findViewById(R.id.button3);
		btnHighscore = (Button) findViewById(R.id.buttonHighscore);
		
		btnDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ExpandableListMainActivity.class);
	            startActivity(intent);      
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
			}
		});
		
		btnAddEinkauf.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), AddEinkaufActivity.class);
	            startActivity(intent);  
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
			}
		});
		
		btnPay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), PayActivity.class);
	            startActivity(intent); 
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
			}
		});
		
		btnHighscore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), HighscoreActivity.class);
	            startActivity(intent); 
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
			}
		});
		
		tvUsername.setText(ServerConnector.getUser().getName());
		ImageView ivLogo = (ImageView) findViewById(R.id.imageView1);
		ivLogo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (ServerConnector.offline) {
					ServerConnector.offline=false;
					new getTotalDebt().execute(null, null , null);
					Toast.makeText(getApplicationContext(), "Online-Modus aktiviert", Toast.LENGTH_SHORT).show();
				}else{
					ServerConnector.offline =true;
					new getTotalDebt().execute(null, null , null);
					Toast.makeText(getApplicationContext(), "Offline-Modus aktiviert", Toast.LENGTH_SHORT).show();
				}
			}
		});

		
	}
	
	@Override
	public void onBackPressed() {

	    if (doubleBackToExitPressedOnce) {
	        super.onBackPressed();
	   	 overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

	        return;
	    }

	    this.doubleBackToExitPressedOnce = true;
	    Toast.makeText(this, "Klicke erneut, um dich auszuloggen", Toast.LENGTH_SHORT).show();

	    new Handler().postDelayed(new Runnable() {

	        @Override
	        public void run() {
	            doubleBackToExitPressedOnce=false;                       
	        }
	    }, 2000);
	}
	
	private class getTotalDebt extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog pdia;
		private float account;
		private String errorMessage = "";
		
		public getTotalDebt() {
		}
		
		@Override
		protected void onPreExecute() {
			if (pdia != null) {
				pdia.dismiss();
				pdia=null;
			}
			pdia = new ProgressDialog(MainMenuActivity.this);
            pdia.setMessage("Lade Kontostand ...");
            pdia.show(); 
		}

	    @Override
	    protected void onProgressUpdate(Integer... values) {
	      
	    }

		@Override
		protected Void doInBackground(Void... params) {
					
			try {
				account = ServerConnector.getTotalDebt(ServerConnector.getUser().getId());
				if (account==0) {
					tvAccount.setText("+- 0 €");
					tvAccount.setTextColor(Color.BLACK);
				}else if (account>0) {
					tvAccount.setText("- " + String.valueOf(df.format(account)) + " €");
					tvAccount.setTextColor(Color.RED);
				}else if (account<0) {
					tvAccount.setText("+ " + String.valueOf(df.format(account*-1)) + " €");
					tvAccount.setTextColor(Color.parseColor("#2aa916"));
				}
				Thread.sleep(500);
			} catch (InterruptedException e) {
				tvAccount.setText("Fehler");
				tvAccount.setTextColor(Color.RED);
				errorMessage = e.getMessage();
			} catch (UnknownHostException e) {
				tvAccount.setText("Fehler");
				tvAccount.setTextColor(Color.RED);
				errorMessage = e.getMessage();
			} catch (IOException e) {
				tvAccount.setText("Fehler");
				tvAccount.setTextColor(Color.RED);
				errorMessage = e.getMessage();
			} catch (FunctionNotDefinedException e) {
				tvAccount.setText("Fehler");
				tvAccount.setTextColor(Color.RED);
				errorMessage = e.getMessage();
			} catch (NotFoundException e) {
				tvAccount.setText("Fehler");
				tvAccount.setTextColor(Color.RED);
				errorMessage = e.getMessage();
			} catch (NumberFormatException e) {
				tvAccount.setText("Fehler");
				tvAccount.setTextColor(Color.RED);
				errorMessage = e.getMessage();
			}
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			
    		pdia.dismiss();
    		
    		if (!errorMessage.matches("")) {
    			Dialogs.showError(MainMenuActivity.this, getApplicationContext(), null, 
						"Fehler beim Laden des Kontostands" 
						, errorMessage, null);
			}

		}

	}
}
