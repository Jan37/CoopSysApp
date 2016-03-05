package com.example.coopsysapp.util;

import com.example.coopsysapp.R;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

public class ToastFactory {

    public static ActivityToast makeText(
            Activity activity, long length, View.OnClickListener listener, String message, String subMessage, String buttonText) {

         View view = activity.getLayoutInflater().inflate(
                R.layout.view_toast,
                (ViewGroup) activity.getWindow().getDecorView(),
                false
        );
         if (buttonText == null || listener == null) {
        	 view.findViewById(R.id.btnAction).setVisibility(View.GONE);
        	 view.findViewById(R.id.separator).setVisibility(View.GONE);
		}else{
	        view.findViewById(R.id.btnAction).setOnClickListener(listener);
	        ((Button) view.findViewById(R.id.btnAction)).setText(buttonText);
		}
        ((TextView) view.findViewById(R.id.txtSubMessage)).setText(subMessage);
        ((TextView) view.findViewById(R.id.txtMessage)).setText(message);
        
        
        final ActivityToast toast = new ActivityToast(activity, view);
        toast.setLength(length);
        toast.setGravity(Gravity.BOTTOM);
        toast.setLength(5000);
        return toast;
    }

}