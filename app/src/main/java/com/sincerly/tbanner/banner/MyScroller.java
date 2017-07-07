package com.sincerly.tbanner.banner;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by Administrator on 2017/7/7.
 */

public class MyScroller extends Scroller {
	private short duration=300;
	public MyScroller(Context context) {
		super(context);
	}

	public MyScroller(Context context, Interpolator interpolator) {
		super(context, interpolator);
	}

	public MyScroller(Context context, Interpolator interpolator, boolean flywheel) {
		super(context, interpolator, flywheel);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy, duration);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy) {
		super.startScroll(startX, startY, dx, dy,duration);
	}

	public void setDuration(Short duration){
		this.duration=duration;
	}
}
