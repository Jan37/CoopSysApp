package com.example.coopsysapp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;

public class Dialogs {
	
	public static void messageDialog(Activity a, String title, String message){
	    AlertDialog.Builder dialog = new AlertDialog.Builder(a);
	    dialog.setTitle(title);
	    dialog.setMessage(message);
	    dialog.setNeutralButton("OK", null);
	    dialog.create().show();     
	}
	
	public static void showError(Activity activity, Context context,OnClickListener mOnClickListener, String message, String subMessage, String buttonText ){
		ToastFactory.makeText(activity, ActivityToast.LENGTH_LONG, mOnClickListener, message, subMessage, buttonText).show();
	}

}
