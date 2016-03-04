package com.example.coopsysapp;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coopsysapp.MainActivity.MyAdapter;
import com.example.coopsysapp.exception.FunctionNotDefinedException;
import com.example.coopsysapp.exception.NotFoundException;
import com.example.coopsysapp.util.Data;
import com.example.coopsysapp.util.Dialogs;

public class AddEinkaufActivity extends Activity {
	
	public Spinner spnGuest1, spnGuest2, spnGuest3, spnGuest4, spnGuest5, spnGuest6;
	public EditText etBetrag1, etBetrag2, etBetrag3, etBetrag4, etBetrag5, etBetrag6;
	public EditText etNote1, etNote2, etNote3, etNote4, etNote5, etNote6;
	public EditText etEinkaufName, etEinkaufBetrag, etDate;
	public TextView tvOffenerBetrag, tvOwnPart, tvUsername;
	public CheckBox cbOwnPart;
	public float offenerBetrag;
	public Button btnEinkaufOk;
	public ImageButton btnVerteilen;
	public String[] adapterItems= {};
	public User[] items ={};
	final Calendar myCalendar = Calendar.getInstance();
	public DecimalFormat df = new DecimalFormat("0.##");
	public float ownPayment = 0;
	public boolean[] usersPartOfEinkauf = {false, false, false, false, false, false};
	public int PartCount=0;
	public Vector<Integer> usedNames = new Vector<Integer>();

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_einkauf);
		getActionBar().hide();
		initialize();
		new getNameList().execute(null, null , null);
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
		cbOwnPart = (CheckBox) findViewById(R.id.checkBox1);
		tvOffenerBetrag = (TextView) findViewById(R.id.textViewOffenerBetrag);
		tvUsername = (TextView) findViewById(R.id.textViewUsername);
		tvOwnPart = (TextView) findViewById(R.id.textView8);
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
		etDate= (EditText) findViewById(R.id.etDate);
		btnEinkaufOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				berechneOffenenBetrag();
				if (!labelsCorrect()) {
					return;
				}
				addEinkauf task = new addEinkauf(AddEinkaufActivity.this);
				task.execute();
				
				Toast.makeText(getApplicationContext(), "Einkauf hinzugefügt", Toast.LENGTH_SHORT).show();
				
				finish();
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
		            new DatePickerDialog(AddEinkaufActivity.this, date, myCalendar
		                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
		                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
		        }
		    });
		   
		   etBetrag1.setOnFocusChangeListener(new CustomOnFocusChangeListener());
		   etBetrag2.setOnFocusChangeListener(new CustomOnFocusChangeListener());
		   etBetrag3.setOnFocusChangeListener(new CustomOnFocusChangeListener());
		   etBetrag4.setOnFocusChangeListener(new CustomOnFocusChangeListener());
		   etBetrag5.setOnFocusChangeListener(new CustomOnFocusChangeListener());
		   etBetrag6.setOnFocusChangeListener(new CustomOnFocusChangeListener());
		   etEinkaufBetrag.setOnFocusChangeListener(new CustomOnFocusChangeListener());

		   btnVerteilen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				verteileBetrag();
			}
		});
		   
		   cbOwnPart.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Toast.makeText(getApplicationContext(), "Eigener Anteil wird nur beim Verteilen berücksichtigt!", Toast.LENGTH_SHORT).show();
				}
			}
		});
		   
		   spnGuest1.setOnItemSelectedListener(new CustomOnItemSelectedListener(0));
		   spnGuest2.setOnItemSelectedListener(new CustomOnItemSelectedListener(1));
		   spnGuest3.setOnItemSelectedListener(new CustomOnItemSelectedListener(2));
		   spnGuest4.setOnItemSelectedListener(new CustomOnItemSelectedListener(3));
		   spnGuest5.setOnItemSelectedListener(new CustomOnItemSelectedListener(4));
		   spnGuest6.setOnItemSelectedListener(new CustomOnItemSelectedListener(5));
		   
		   tvUsername.setText(ServerConnector.getUser().getName());

	}
	private class CustomOnFocusChangeListener implements OnFocusChangeListener{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
            if(!hasFocus) {
            	berechneOffenenBetrag();
            }  
		}
		
	}
	
	private class CustomOnItemSelectedListener implements OnItemSelectedListener{
		public int lastUser = 0;
		public int number;
		
		public CustomOnItemSelectedListener(int number) {
			this.number=number;
		}
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
				if (position == ServerConnector.getUser().getId()) {
					Dialogs.messageDialog(AddEinkaufActivity.this, "Hinweis", "Du kannst kein Teil des Einkaufs sein. Bitte"
							+ " wähle 'Eigenanteil', um Deinen eigenen Anteil zu berücksichtigen.");
					((Spinner)parent).setSelection(0);
					return;
				}
				if (usedNames.contains(position)&&position!=0) {
					Dialogs.messageDialog(AddEinkaufActivity.this, "Hinweis", "Der User ist bereits Teil des Einkaufs.");
					((Spinner)parent).setSelection(0);
				}else if(position!=0){
					usedNames.add(position);
					lastUser=position;
					
				}else if(position==0 && lastUser!=0) {
					usedNames.removeElement((Integer)lastUser);
					lastUser=0;
				}
				
				
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	
	
    private void updateLabel() {

  String myFormat = "dd.MM.yy"; //In which you need put here
  SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

  etDate.setText(sdf.format(myCalendar.getTime()));
  }
    
    private boolean labelsCorrect() {
    	boolean error = false;
    	if (etEinkaufName.getText().toString().matches("")) {
			etEinkaufName.setError("Plichtfeld");
			error = true;
		}
    	if (etEinkaufBetrag.getText().toString().matches("")) {
    		etEinkaufBetrag.setError("Plichtfeld");
    		error = true;
		}
    	if (etDate.getText().toString().matches("")) {
    		etDate.setError("Plichtfeld");
    		error = true;
		}
    	
    	if (spnGuest1.getSelectedItemPosition()!=0) {
        	if (etBetrag1.getText().toString().matches("")) {
        		etBetrag1.setError("Plichtfeld");
        		error = true;
    		}
        	if (etNote1.getText().toString().matches("")) {
        		//etNote1.setError("Plichtfeld");
        		//error = true;
    		}
		}
    	if (spnGuest2.getSelectedItemPosition()!=0) {
        	if (etBetrag2.getText().toString().matches("")) {
        		etBetrag2.setError("Plichtfeld");
        		error = true;
    		}
        	if (etNote2.getText().toString().matches("")) {
        		//etNote2.setError("Plichtfeld");
        		//error = true;
    		}
		}
    	if (spnGuest3.getSelectedItemPosition()!=0) {
        	if (etBetrag3.getText().toString().matches("")) {
        		etBetrag3.setError("Plichtfeld");
        		error = true;
    		}
        	if (etNote3.getText().toString().matches("")) {
        		//etNote3.setError("Plichtfeld");
        		//error = true;
    		}
		}
    	if (spnGuest4.getSelectedItemPosition()!=0) {
        	if (etBetrag4.getText().toString().matches("")) {
        		etBetrag4.setError("Plichtfeld");
        		error = true;
    		}
        	if (etNote4.getText().toString().matches("")) {
        		//etNote4.setError("Plichtfeld");
        		//error = true;
    		}
		}
    	if (spnGuest5.getSelectedItemPosition()!=0) {
        	if (etBetrag5.getText().toString().matches("")) {
        		etBetrag5.setError("Plichtfeld");
        		error = true;
    		}
        	if (etNote5.getText().toString().matches("")) {
        		//etNote5.setError("Plichtfeld");
        		//error = true;
    		}
		}
    	if (spnGuest6.getSelectedItemPosition()!=0) {
        	if (etBetrag6.getText().toString().matches("")) {
        		etBetrag6.setError("Plichtfeld");
        		error = true;
    		}
        	if (etNote6.getText().toString().matches("")) {
        		//etNote6.setError("Plichtfeld");
        		//error = true;
    		}
		}
    	boolean wrongOrder = false;
    	for (int i = 1; i < usersPartOfEinkauf.length; i++) {
			if (usersPartOfEinkauf[i]==true) {
				wrongOrder =true;
			}
		}
    	
    	if (error) {
        	return !error;
		}
    	
    	if (!usersPartOfEinkauf[0] && wrongOrder && !error) {
			Dialogs.messageDialog(AddEinkaufActivity.this, "Fehler", "Leere Gäste-Zeilen sind nicht erlaubt!");
			return false;
		}
    	
    	if (usedNames.size()<=0 && !error) {
			Dialogs.messageDialog(AddEinkaufActivity.this, "Fehler", "Keine Gäste angegeben.");
			return false;
		}
    	
    	if (Float.valueOf(df.format(offenerBetrag))!=0) {
			Dialogs.messageDialog(AddEinkaufActivity.this, "Fehler", "Der Betrag des Einkaufs ist nicht korrekt aufgeteilt.");
			return false;
		}
    	Log.d("Labelcorrect", String.valueOf(!error));
    	return !error;

	}
    
    private void berechneOffenenBetrag() {
    	if(etEinkaufBetrag.getText().toString().matches("")){
        	offenerBetrag = 0;
    	}else{

    	offenerBetrag = Float.valueOf(etEinkaufBetrag.getText().toString());
    	
		if (cbOwnPart.isChecked()) {
			offenerBetrag-=ownPayment;
		}
    	
    	if (spnGuest1.getSelectedItemPosition()!=0) {
    		if (!etBetrag1.getText().toString().matches("")) {
    			offenerBetrag-=Float.valueOf(etBetrag1.getText().toString());
			}
		}
    	if (spnGuest2.getSelectedItemPosition()!=0) {
    		if (!etBetrag2.getText().toString().matches("")) {
    			offenerBetrag-=Float.valueOf(etBetrag2.getText().toString());
			}
		}
    	if (spnGuest3.getSelectedItemPosition()!=0) {
    		if (!etBetrag3.getText().toString().matches("")) {
    			offenerBetrag-=Float.valueOf(etBetrag3.getText().toString());
			}
		}
    	if (spnGuest4.getSelectedItemPosition()!=0) {
    		if (!etBetrag4.getText().toString().matches("")) {
    			offenerBetrag-=Float.valueOf(etBetrag4.getText().toString());
			}
		}
    	if (spnGuest5.getSelectedItemPosition()!=0) {
    		if (!etBetrag5.getText().toString().matches("")) {
    			offenerBetrag-=Float.valueOf(etBetrag5.getText().toString());
			}
		}
    	if (spnGuest6.getSelectedItemPosition()!=0) {
    		if (!etBetrag6.getText().toString().matches("")) {
    			offenerBetrag-=Float.valueOf(etBetrag6.getText().toString());
			}
		}
    	}
		String s = df.format(offenerBetrag);
		tvOffenerBetrag.setText(s + " €");
	}
    
    private void verteileBetrag() {
    	int gaeste = 0;
		float anteil=0;

    	if(etEinkaufBetrag.getText().toString().matches("")){
        	offenerBetrag =0;
    	}else{
        	offenerBetrag = Float.valueOf(etEinkaufBetrag.getText().toString());
    	}
    	
    	if (spnGuest1.getSelectedItemPosition()!=0) {
    		gaeste++;
		}
    	if (spnGuest2.getSelectedItemPosition()!=0) {
    		gaeste++;
		}
    	if (spnGuest3.getSelectedItemPosition()!=0) {
    		gaeste++;
		}
    	if (spnGuest4.getSelectedItemPosition()!=0) {
    		gaeste++;
		}
    	if (spnGuest5.getSelectedItemPosition()!=0) {
    		gaeste++;
		}
    	if (spnGuest6.getSelectedItemPosition()!=0) {
    		gaeste++;
		}
    	
		if (gaeste > 0) {
			if (cbOwnPart.isChecked()) {
				gaeste++;
			}
			Log.d("Gästeanzahl", String.valueOf(gaeste));
			anteil = offenerBetrag/gaeste;
			String s = df.format(anteil);
			Log.d("Addeinkauf", df.format((offenerBetrag%anteil)));
			s = s.replace(',', '.');
			Log.d("Addeinkauf", s);

			if (spnGuest1.getSelectedItemPosition() != 0) {
				etBetrag1.setText(s);
			}
			if (spnGuest2.getSelectedItemPosition() != 0) {
				etBetrag2.setText(s);
			}
			if (spnGuest3.getSelectedItemPosition() != 0) {
				etBetrag3.setText(s);
			}
			if (spnGuest4.getSelectedItemPosition() != 0) {
				etBetrag4.setText(s);
			}
			if (spnGuest5.getSelectedItemPosition() != 0) {
				etBetrag5.setText(s);
			}
			if (spnGuest6.getSelectedItemPosition() != 0) {
				etBetrag6.setText(s);
			}
			if (cbOwnPart.isChecked()) {
				ownPayment = Float.valueOf(s);
			}
		}
		berechneOffenenBetrag();
		if (offenerBetrag!=0) {
			String s = df.format(anteil+offenerBetrag);
			s = s.replace(',', '.');
			Log.d("Addeinkauf", s);
			etBetrag1.setText(s);
			Dialogs.messageDialog(AddEinkaufActivity.this, "Hinweis", "Der angegebene Betrag lässt sich nicht gleichmäßig aufteilen. "
					+ "Gast 1 erhält einen abweichenden Anteil." );
			berechneOffenenBetrag();
		}
    	
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
			View mySpinner = inflater.inflate(R.layout.simple_spinner, parent,
					false);
			TextView main_text = (TextView) mySpinner
					.findViewById(R.id.text_main_seen);
			main_text.setText(adapterItems[position]);

			return mySpinner;
		}
	}
    
    private class getNameList extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog pdia;
		private String errorMessage = "";

    	public getNameList() {
    	}
    	
    	@Override
    	protected void onPreExecute() {
            pdia = new ProgressDialog(AddEinkaufActivity.this);
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
    			errorMessage = e.getMessage();;
    		}
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Void result) {
    		super.onPostExecute(result);
    		
    		if (!errorMessage.matches("")) {
				Dialogs.messageDialog(AddEinkaufActivity.this, "Fehler", errorMessage);
			}
    		
    		adapterItems = new String[Data.getInstance().getUserList().length+1];
    		for (int i = 0; i < adapterItems.length; i++) {
    			if (i==0) {
    				adapterItems[i]="";
				}else{
    			adapterItems[i]=Data.getInstance().getUserList()[i-1].getName();
				}
    		}	
    		spnGuest1.setAdapter(new MyAdapter(AddEinkaufActivity.this, R.layout.simple_spinner,adapterItems));
    		spnGuest2.setAdapter(new MyAdapter(AddEinkaufActivity.this, R.layout.simple_spinner,adapterItems));
    		spnGuest3.setAdapter(new MyAdapter(AddEinkaufActivity.this, R.layout.simple_spinner,adapterItems));
    		spnGuest4.setAdapter(new MyAdapter(AddEinkaufActivity.this, R.layout.simple_spinner,adapterItems));
    		spnGuest5.setAdapter(new MyAdapter(AddEinkaufActivity.this, R.layout.simple_spinner,adapterItems));
    		spnGuest6.setAdapter(new MyAdapter(AddEinkaufActivity.this, R.layout.simple_spinner,adapterItems));

    		pdia.dismiss();
    	}

    }
	
	private class addEinkauf extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog progress;
		private String errorMessage = "";

		public addEinkauf(AddEinkaufActivity activity) {
			if(progress != null){
				progress=null;
			}
			progress= new ProgressDialog(activity);
		}
		
		@Override
		protected void onPreExecute() {
			progress.setMessage("Erstelle Einkauf, bitte warten ...");
	        progress.show();
		}

	    @Override
	    protected void onProgressUpdate(Integer... values) {
	      
	    }

		@Override
		protected Void doInBackground(Void... params) {
					
			try {
				Log.d("EinkaufPreAdded", ServerConnector.einkaufString);

				int einkaufId = ServerConnector.addEinkauf(ServerConnector.getUser().getId(), 
						etEinkaufName.getText().toString(), etDate.getText().toString());
			

				int gastId = spnGuest1.getSelectedItemPosition();
				if (gastId >0) {
					float gastBetrag = Float.valueOf(etBetrag1.getText().toString());
					String gastNote = etNote1.getText().toString();
					if (gastNote.matches("")) {
						gastNote = "keine Notiz";
					}
					boolean success = ServerConnector.addEinkaufPart(einkaufId, gastId, gastBetrag, gastNote);
					Log.d("EinkaufPartAdd1",String.valueOf(success));
				}

				gastId = spnGuest2.getSelectedItemPosition();
				if (gastId >0) {
					float gastBetrag = Float.valueOf(etBetrag2.getText().toString());
					String gastNote = etNote2.getText().toString();
					if (gastNote.matches("")) {
						gastNote = "keine Notiz";
					}
					boolean success = ServerConnector.addEinkaufPart(einkaufId, gastId, gastBetrag, gastNote);
					Log.d("EinkaufPartAdd2",String.valueOf(success));
				}
				gastId = spnGuest3.getSelectedItemPosition();
				if (gastId >0) {
					float gastBetrag = Float.valueOf(etBetrag3.getText().toString());
					String gastNote = etNote3.getText().toString();
					if (gastNote.matches("")) {
						gastNote = "keine Notiz";
					}
					boolean success = ServerConnector.addEinkaufPart(einkaufId, gastId, gastBetrag, gastNote);
					Log.d("EinkaufPartAdd3",String.valueOf(success));
				}
				
				gastId = spnGuest4.getSelectedItemPosition();
				if (gastId >0) {
					float gastBetrag = Float.valueOf(etBetrag4.getText().toString());
					String gastNote = etNote4.getText().toString();
					if (gastNote.matches("")) {
						gastNote = "keine Notiz";
					}
					boolean success = ServerConnector.addEinkaufPart(einkaufId, gastId, gastBetrag, gastNote);
					Log.d("EinkaufPartAdd4",String.valueOf(success));
				}
				
				gastId = spnGuest5.getSelectedItemPosition();
				if (gastId >0) {
					float gastBetrag = Float.valueOf(etBetrag5.getText().toString());
					String gastNote = etNote1.getText().toString();
					if (gastNote.matches("")) {
						gastNote = "keine Notiz";
					}
					boolean success = ServerConnector.addEinkaufPart(einkaufId, gastId, gastBetrag, gastNote);
					Log.d("EinkaufPartAdd5",String.valueOf(success));
				}
				
				gastId = spnGuest6.getSelectedItemPosition();
				if (gastId >0) {
					float gastBetrag = Float.valueOf(etBetrag6.getText().toString());
					String gastNote = etNote6.getText().toString();
					if (gastNote.matches("")) {
						gastNote = "keine Notiz";
					}
					boolean success = ServerConnector.addEinkaufPart(einkaufId, gastId, gastBetrag, gastNote);
					Log.d("EinkaufPartAdd6",String.valueOf(success));
				}
				
				Log.d("EinkaufPartAdded", ServerConnector.einkaufPartString);

				
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

			if (!errorMessage.matches("")) {
				Dialogs.messageDialog(AddEinkaufActivity.this, "Fehler", errorMessage);
			}
			
			if (progress.isShowing()) {
	        	progress.dismiss();
	        }
			super.onPostExecute(result);
		}

	}	
}
