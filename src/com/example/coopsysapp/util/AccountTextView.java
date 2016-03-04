package com.example.coopsysapp.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

	public class AccountTextView extends TextView 
	   {
	    Context context;
	    String firstText = "FirstText";  
	    Paint mPaint = new Paint();
		
	
	 public AccountTextView(Context context,AttributeSet attrs)
	   {
	    super(context, attrs);
	    this.context = context;
	    this.isInEditMode();
	    if(!isInEditMode()){
	    Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "whatever it takes.ttf");
	    setTypeface(custom_font);
	    }
	    //this.firstText = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.example", "first_name");
	   }
	
	 
	  public AccountTextView(Context context)
	   {
	  super(context);
	    this.context = context;

	   }
	
	    @Override
	    protected void onDraw(Canvas canvas) 
	    {
	    super.onDraw(canvas);
	    //setText(firstText);
	    //setTextSize(40);
	    //setTextColor(Color.BLACK);
	    this.isInEditMode();
	    canvas.skew(2.0f, 0.3f);  //change values to suit your needs
	    Rotate3dAnimation skew = new Rotate3dAnimation(45, 30, 200, 200, 50, false);   //change values to suit your needs
	    startAnimation(skew);	    
	    }
	    
	    @Override
	    public void setText(CharSequence text, BufferType type) {
	    	// TODO Auto-generated method stub
	    	super.setText(text, type);
	    }
	      
}