package com.example.coopsysapp;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.coopsysapp.exception.FunctionNotDefinedException;
import com.example.coopsysapp.exception.NotFoundException;

public class AddEinkaufActivity extends Activity {
	
	public Spinner spnGuest1, spnGuest2, spnGuest3, spnGuest4, spnGuest5, spnGuest6;
	public EditText etBetrag1, etBetrag2, etBetrag3, etBetrag4, etBetrag5, etBetrag6;
	public EditText etNote1, etNote2, etNote3, etNote4, etNote5, etNote6;
	public EditText etEinkaufName, etEinkaufBetrag;
	public Button btnEinkaufOk;
	public ImageButton btnVerteilen;
	public String[] adapterItems= {};
	public User[] items ={};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_einkauf);
		
		initialize();
		
		getNameList task = new getNameList(AddEinkaufActivity.this);
		task.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_einkauf, menu);
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
		spnGuest1 = (Spinner) findViewById(R.id.spnGuest1);
		spnGuest2 = (Spinner) findViewById(R.id.spnGuest2);
		spnGuest3 = (Spinner) findViewById(R.id.spnGuest3);
		spnGuest4 = (Spinner) findViewById(R.id.spnGuest4);
		spnGuest5 = (Spinner) findViewById(R.id.spnGuest5);
		spnGuest6 = (Spinner) findViewById(R.id.spnGuest6);
		etBetrag1 = (EditText) findViewById(R.id.etBetrag1);
		etBetrag2 = (EditText) findViewById(R.id.etBetrag2);
		etBetrag3 = (EditText) findViewById(R.id.etBetrag3);
		etBetrag4 = (EditText) findViewById(R.id.etBetrag4);
		etBetrag5 = (EditText) findViewById(R.id.etBetrag5);
		etBetrag6 = (EditText) findViewById(R.id.etBetrag6);
		etNote1 = (EditText) findViewById(R.id.etNote1);
		etNote2 = (EditText) findViewById(R.id.etNote2);
		etNote3 = (EditText) findViewById(R.id.etNote3);
		etNote4 = (EditText) findViewById(R.id.etNote4);
		etNote5 = (EditText) findViewById(R.id.etNote5);
		etNote6 = (EditText) findViewById(R.id.etNote6);
		etEinkaufName = (EditText) findViewById(R.id.etEinkaufName);
		etEinkaufBetrag = (EditText) findViewById(R.id.etEinkaufBetrag);
		btnEinkaufOk = (Button) findViewById(R.id.btnEinkaufOk);
		btnVerteilen = (ImageButton) findViewById(R.id.btnVerteilen);
		
		btnEinkaufOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addEinkauf task = new addEinkauf(AddEinkaufActivity.this);
				task.execute();
			}
		});
	}
	private class getNameList extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog progress;

		public getNameList(AddEinkaufActivity activity) {
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
				items = ServerConnector.getNameList();
				Thread.sleep(500);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FunctionNotDefinedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			       		return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			if (items.length==0) {
				items= new User[] {new User(1, "Testuser1"), new User(2, "Testuser2"), new User(3, "Testuser3")};
			}
			
			adapterItems = new String[items.length+1];
			adapterItems[0]="";
			for (int i = 1; i < adapterItems.length; i++) {
				adapterItems[i]=items[i-1].getName();
			}		 		
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.custom_spinner_item, adapterItems);
			spnGuest1.setAdapter(adapter);
			spnGuest2.setAdapter(adapter);
			spnGuest3.setAdapter(adapter);
			spnGuest4.setAdapter(adapter);
			spnGuest5.setAdapter(adapter);
			spnGuest6.setAdapter(adapter);
			if (progress.isShowing()) {
	        	progress.dismiss();
	        }
			super.onPostExecute(result);
		}

	}	
	
	private class addEinkauf extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog progress;

		public addEinkauf(AddEinkaufActivity activity) {
			if(progress != null){
				progress=null;
			}
			progress= new ProgressDialog(activity);
		}
		
		@Override
		protected void onPreExecute() {
			progress.setMessage("Erstelle Einkauf, bitte warten ...");
	        //progress.show();
		}

	    @Override
	    protected void onProgressUpdate(Integer... values) {
	      
	    }

		@Override
		protected Void doInBackground(Void... params) {
					
			try {
				//TODO Date
				Log.d("EinkaufPreAdded", ServerConnector.einkaufString);

				int einkaufId = ServerConnector.addEinkauf(ServerConnector.getUser().getId(), 
						etEinkaufName.getText().toString(), "21-02-2016");
				
				Log.d("EinkaufAdded", ServerConnector.einkaufString);
				Log.d("EinkaufPartPreAdded", ServerConnector.einkaufPartString);

				int gastId = spnGuest1.getSelectedItemPosition();
				float gastBetrag = Float.valueOf(etBetrag1.getText().toString());
				String gastNote = etNote1.getText().toString();
				if (gastId>0) {
					boolean success = ServerConnector.addEinkaufPart(einkaufId, gastId, gastBetrag, gastNote);
					Log.d("EinkaufPartAdd1",String.valueOf(success));
				}
//				
//				gastId = spnGuest2.getSelectedItemPosition();
//				gastBetrag = Float.valueOf(etBetrag2.getText().toString());
//				gastNote = etNote2.getText().toString();
//				if (gastId>0) {
//					ServerConnector.addEinkaufPart(einkaufId, gastId, gastBetrag, gastNote);
//				}
//				
//				gastId = spnGuest3.getSelectedItemPosition();
//				gastBetrag = Float.valueOf(etBetrag3.getText().toString());
//				gastNote = etNote3.getText().toString();
//				if (gastId>0) {
//					ServerConnector.addEinkaufPart(einkaufId, gastId, gastBetrag, gastNote);
//				}
//				
//				gastId = spnGuest4.getSelectedItemPosition();
//				gastBetrag = Float.valueOf(etBetrag4.getText().toString());
//				gastNote = etNote4.getText().toString();
//				if (gastId>0) {
//					ServerConnector.addEinkaufPart(einkaufId, gastId, gastBetrag, gastNote);
//				}
//				
//				gastId = spnGuest5.getSelectedItemPosition();
//				gastBetrag = Float.valueOf(etBetrag5.getText().toString());
//				gastNote = etNote5.getText().toString();
//				if (gastId>0) {
//					ServerConnector.addEinkaufPart(einkaufId, gastId, gastBetrag, gastNote);
//				}
//				
//				gastId = spnGuest6.getSelectedItemPosition();
//				gastBetrag = Float.valueOf(etBetrag6.getText().toString());
//				gastNote = etNote6.getText().toString();
//				if (gastId>0) {
//					ServerConnector.addEinkaufPart(einkaufId, gastId, gastBetrag, gastNote);
//				}
				
				Log.d("EinkaufPartAdded", ServerConnector.einkaufPartString);

				
				Thread.sleep(500);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FunctionNotDefinedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotFoundException e) {
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
