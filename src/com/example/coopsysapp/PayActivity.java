package com.example.coopsysapp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.example.coopsysapp.MainActivity.MyAdapter;
import com.example.coopsysapp.exception.FunctionNotDefinedException;
import com.example.coopsysapp.exception.NotFoundException;
import com.example.coopsysapp.util.Data;
import com.example.coopsysapp.util.Dialogs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class PayActivity extends Activity {
	public Spinner spnDropDown;
	public EditText etBetrag, etNotiz, etDate;
	public Button btnZahlen;
	public TextView tvUser;
	public String[] adapterItems= {};
	final Calendar myCalendar = Calendar.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);
		getActionBar().hide();
		new getNameList().execute(null, null , null);

		initialize();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pay, menu);
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
		spnDropDown = (Spinner) findViewById(R.id.spinner1);
		etBetrag = (EditText) findViewById(R.id.editText1);
		etNotiz = (EditText) findViewById(R.id.editText2);
		etDate = (EditText) findViewById(R.id.etDate);
		btnZahlen = (Button) findViewById(R.id.button1);
		tvUser = (TextView) findViewById(R.id.textViewUsername);
		
		tvUser.setText(ServerConnector.getUser().getName());
		btnZahlen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (spnDropDown.getSelectedItemPosition()==0) {
					Dialogs.messageDialog(PayActivity.this, "Eingabe überprüfen", 
							"Bitte wähle einen Zahlungsempfänger!");
					return;
				}
				if (etBetrag.getText().toString().matches("")) {
					etBetrag.setError("Plichtfeld");
					return;
				}
				if (etNotiz.getText().toString().matches("")) {
					etNotiz.setText("Keine Notiz");
				}
				new Pay().execute(null, null , null);

				

			}
		});
		
		final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

		    @Override
		    public void onDateSet(DatePicker view, int year, int monthOfYear,
		            int dayOfMonth) {
		        // TODO Auto-generated method stub
		        myCalendar.set(Calendar.YEAR, year);
		        myCalendar.set(Calendar.MONTH, monthOfYear);
		        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		        updateLabel();
		    }
		   

		};
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String currentDate= sdf.format(new Date());
			etDate.setText(currentDate);
		   etDate.setOnClickListener(new OnClickListener() {

		        @Override
		        public void onClick(View v) {
		            // TODO Auto-generated method stub
		            new DatePickerDialog(PayActivity.this, date, myCalendar
		                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
		                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
		        }
		    });
		
		spnDropDown.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
					if (position == ServerConnector.getUser().getId()) {
						Dialogs.messageDialog(PayActivity.this, "Netter Versuch!", "Du kannst Dir nichts überweisen. Bitte"
								+ " wähle einen anderen Nutzer");
						spnDropDown.setSelection(0);
					}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		adapterItems = new String[Data.getInstance().getUserList().length+1];
		for (int i = 0; i < adapterItems.length; i++) {
			if (i==0) {
				adapterItems[i]="";
			}else{
			adapterItems[i]=Data.getInstance().getUserList()[i-1].getName();
			}
		}		 		
		
		spnDropDown.setAdapter(new MyAdapter(this, R.layout.user_spinner,adapterItems));
	}
	
	 private void updateLabel() {

		  String myFormat = "dd.MM.yy"; //In which you need put here
		  SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

		  etDate.setText(sdf.format(myCalendar.getTime()));
		  }
	
	@Override
	public void onBackPressed() {
	 super.onBackPressed();
	 overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
	 finish();
	}
	
	public class MyAdapter extends ArrayAdapter<String> {
		public MyAdapter(Context ctx, int txtViewResourceId, String[] objects) {
			super(ctx, txtViewResourceId, objects);
		}

		@Override
		public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
			return getCustomView(position, cnvtView, prnt);
		}

		@Override
		public View getView(int pos, View cnvtView, ViewGroup prnt) {
			return getCustomView(pos, cnvtView, prnt);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View mySpinner = inflater.inflate(R.layout.user_spinner, parent,
					false);
			TextView main_text = (TextView) mySpinner
					.findViewById(R.id.text_main_seen);
			main_text.setText(adapterItems[position]);

			return mySpinner;
		}
	}
	
	private class getNameList extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog pdia;
		private String errorMessage="";

    	public getNameList() {
    	}
    	
    	@Override
    	protected void onPreExecute() {
            pdia = new ProgressDialog(PayActivity.this);
            pdia.setMessage("Lade Benutzerliste ...");
            pdia.show();  
    	}

        @Override
        protected void onProgressUpdate(Integer... values) {
          
        }

    	@Override
    	protected Void doInBackground(Void... params) {
    				
    		try {
    			User[] userlist = ServerConnector.getNameList();
    			Data.getInstance().setUserList(userlist);
    			Thread.sleep(500);
    		} catch (IOException e) {
    			errorMessage = e.getMessage();
    		} catch (InterruptedException e) {
    			errorMessage = e.getMessage();
    		} catch (FunctionNotDefinedException e) {
    			errorMessage = e.getMessage();
    		}
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void result) {
    		super.onPostExecute(result);
    		
    		if (!errorMessage.matches("")) {
				Dialogs.messageDialog(PayActivity.this, "Fehler", errorMessage);
			}
    		
    		adapterItems = new String[Data.getInstance().getUserList().length+1];
    		for (int i = 0; i < adapterItems.length; i++) {
    			if (i==0) {
    				adapterItems[i]="";
				}else{
    			adapterItems[i]=Data.getInstance().getUserList()[i-1].getName();
				}
    		}	
    		spnDropDown.setAdapter(null);
    		spnDropDown.setAdapter(new MyAdapter(PayActivity.this, R.layout.user_spinner,adapterItems));
    		pdia.dismiss();
    	}

    }
	
	private class Pay extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog pdia;
		private String errorMessage ="";
    	public Pay() {
    	}
    	
    	@Override
    	protected void onPreExecute() {
            pdia = new ProgressDialog(PayActivity.this);
            pdia.setMessage("Zahlung wird gebucht ...");
            pdia.show();  
    	}

        @Override
        protected void onProgressUpdate(Integer... values) {
          
        }

    	@Override
    	protected Void doInBackground(Void... params) {
    		int einkauf = -1;
    		EinkaufPart ePart = null;
    		
    		try {
    			einkauf = ServerConnector.addEinkauf(ServerConnector.getUser().getId(), "Zahlung", etDate.getText().toString());
    			Thread.sleep(500);
    		} catch (IOException e) {
    			errorMessage = e.getMessage();
    		} catch (InterruptedException e) {
    			errorMessage = e.getMessage();
    		} catch (FunctionNotDefinedException e) {
    			errorMessage = e.getMessage();
    		} catch (NotFoundException e) {
    			errorMessage = e.getMessage();
			}
    		
    		try {
    			ePart = new EinkaufPart(einkauf, spnDropDown.getSelectedItemPosition(), Float.valueOf(etBetrag.getText().toString()), etNotiz.getText().toString());
    			ServerConnector.addEinkaufPart(ePart.getEinkaufId(), ePart.getGastId(), ePart.getBetrag(), ePart.getNotiz());
    			Thread.sleep(500);
    		} catch (IOException e) {
    			errorMessage = e.getMessage();
    		} catch (InterruptedException e) {
    			errorMessage = e.getMessage();
    		} catch (FunctionNotDefinedException e) {
    			errorMessage = e.getMessage();
    		} catch (NotFoundException e) {
    			errorMessage = e.getMessage();
			}
    		return null;
    	}
    	@Override
    	protected void onPostExecute(Void result) {  
    		pdia.dismiss();
    		if (!errorMessage.matches("")) {
				Dialogs.messageDialog(PayActivity.this, "Fehler", errorMessage);
			}else{

            finish();
			}
    	}

    }
}
