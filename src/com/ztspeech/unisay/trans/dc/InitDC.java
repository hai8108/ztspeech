package com.ztspeech.unisay.trans.dc;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ztspeech.unisay.trans.R;

public class InitDC extends LinearLayout {

	public InitDC(Context context) {
		super(context);
		ImageView i = new ImageView(context);
		i.setImageResource(R.drawable.splash);
		addView(i, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
	}
}
