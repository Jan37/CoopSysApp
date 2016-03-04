package com.example.coopsysapp.util;

import android.app.Activity;
import android.app.AlertDialog;

public class Dialogs {
	
	public static void messageDialog(Activity a, String title, String message){
	    AlertDialog.Builder dialog = new AlertDialog.Builder(a);
	    dialog.setTitle(title);
	    dialog.setMessage(message);
	    dialog.setNeutralButton("OK", null);
	    dialog.create().show();     
	}

}
