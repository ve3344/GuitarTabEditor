package com.ve.view;
import android.view.*;
import android.view.animation.*;

public class FadeAnimation
{
	
	public static ViewPropertyAnimator fadeOut(final View view){
		return view.animate().alpha(0).setDuration(200).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable(){
			public void run(){
				view.setVisibility(View.INVISIBLE);
			}
		});
	}
	public static ViewPropertyAnimator fadeIn(final View view){
		view.setVisibility(View.VISIBLE);
		return view.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
	}
	public static void fadeTo(final View from,final View to){
		fadeOut(from).start();
		fadeIn(to).start();
	}
}
