package com.example.coopsysapp;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.example.coopsysapp.AddEinkaufActivity.MyAdapter;
import com.example.coopsysapp.exception.FunctionNotDefinedException;
import com.example.coopsysapp.exception.NotFoundException;
import com.example.coopsysapp.util.Data;
import com.example.coopsysapp.util.Dialogs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.graphics.Color;

public class HighscoreActivity extends Activity {

	private TextView text;
	private List<String> listValuesAll, listValuesPersonal;
	private ArrayAdapter<String> myAdapterAll, myAdapterPersonal;
	private ListView lvAll, lvPersonal;
	private DecimalFormat df = new DecimalFormat("0.00");

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_highscore);
		getActionBar().hide();
		initialize();
		new getTotalHighscore().execute(null, null , null);

		
         
	}
	
	private void initialize() {
		lvAll = (ListView) findViewById(R.id.ListViewAll);
		lvPersonal = (ListView) findViewById(R.id.ListviewPersonal);
		
		listValuesAll = new ArrayList<String>();

		listValuesPersonal = new ArrayList<String>();

		
		
		// initiate the listadapter
		myAdapterAll = new ArrayAdapter <String>(this, 
				R.layout.highscore_row_layout, R.id.textViewChild, listValuesAll);
 
		myAdapterPersonal = new ArrayAdapter <String>(this, 
				R.layout.highscore_row_layout, R.id.textViewChild, listValuesPersonal);
         // assign the list adapter
         lvAll.setAdapter(myAdapterAll);
         lvPersonal.setAdapter(myAdapterPersonal);

	}
	
	
	private void sortAscending () {
	    List<String> sortedMonthsList = listValuesAll;
	    Collections.sort(sortedMonthsList);

	    listValuesAll = sortedMonthsList;
	}
	
	@Override
	public void onBackPressed() {
	     super.onBackPressed();
	   	 overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

	        return;
	}
	
	private class getTotalHighscore extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog pdia;
		private float account;
		private String errorMessage = "";
		
		public getTotalHighscore() {
		}
		
		@Override
		protected void onPreExecute() {
			if (pdia != null) {
				pdia.dismiss();
				pdia=null;
			}
			pdia = new ProgressDialog(HighscoreActivity.this);
            pdia.setMessage("Lade Highscores ...");
            pdia.show(); 
		}

	    @Override
	    protected void onProgressUpdate(Integer... values) {
	      
	    }

		@Override
		protected Void doInBackground(Void... params) {
					
				for (User user : Data.getInstance().userList) {
					Float debt;
					try {
						debt = ServerConnector.getTotalDebt(user.getId());
					} catch (UnknownHostException e) {
						errorMessage = e.getMessage();
						continue;
					} catch (IOException e) {
						errorMessage = e.getMessage();
						continue;
					} catch (FunctionNotDefinedException e) {
						errorMessage = e.getMessage();
						continue;
					} catch (NotFoundException e) {
						errorMessage = e.getMessage();
						continue;
					}
					listValuesAll.add(user.getName() + ": " + df.format(debt*-1) + " €");
				}			
					Log.d("list before", listValuesAll.toString());
				 Collections.sort(listValuesAll, new Comparator<String>() {
		                @Override
		                public int compare(String lhs, String rhs) {
		                	Float left, right;
		                	left = Float.valueOf(lhs.split(":")[1].replace(" ", "").replace("€", "").replace(",", "."));
		                	right = Float.valueOf(rhs.split(":")[1].replace(" ", "").replace("€", "").replace(",", "."));
		                	int result = left < right ? -1 : left == right ? 0 : 1;	                    
		                	
		                	Log.d("Result Compare", String.valueOf(left) + " < " + String.valueOf(right) + " = " + String.valueOf(result));
		                	
		                	
		                	return result;
		                }
		            });
					Log.d("list after", listValuesAll.toString());

				 
				for (User user : Data.getInstance().userList) {
					if (user.getId()==ServerConnector.getUser().getId()) {
						continue;
					}
					Debt debt;
					try {
						debt = ServerConnector.getDebt( ServerConnector.getUser().getId(),user.getId());
					} catch (UnknownHostException e) {
						errorMessage = e.getMessage();
						continue;
					} catch (IOException e) {
						errorMessage = e.getMessage();
						continue;
					} catch (FunctionNotDefinedException e) {
						errorMessage = e.getMessage();
						continue;
					} catch (NotFoundException e) {
						errorMessage = e.getMessage();
						continue;
					}
					listValuesPersonal.add(user.getName() + ": " + df.format(debt.getBetrag()) + " €");
				}			
				
				 Collections.sort(listValuesPersonal, new Comparator<String>() {
		                @Override
		                public int compare(String lhs, String rhs) {
		                	Float left, right;
		                	left = Float.valueOf(lhs.split(":")[1].replace(" ", "").replace("€", "").replace(",", "."));
		                	right = Float.valueOf(rhs.split(":")[1].replace(" ", "").replace("€", "").replace(",", "."));
		                	int result = left < right ? 1 : left == right ? 0 : -1;	                    
		                	
		                	Log.d("Result Compare", String.valueOf(left) + " < " + String.valueOf(right) + " = " + String.valueOf(result));
		                	
		                	return result;
		                }
		            });
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			
    		pdia.dismiss();
    		
    		if (!errorMessage.matches("")) {
    			Dialogs.showError(HighscoreActivity.this, getApplicationContext(), null, 
						"Fehler beim Laden des Kontostands" 
						, errorMessage, null);
			}
    		
    		myAdapterAll.notifyDataSetChanged();
    		myAdapterPersonal.notifyDataSetChanged();

		}
	}

}