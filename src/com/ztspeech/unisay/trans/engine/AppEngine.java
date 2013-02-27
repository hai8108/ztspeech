package com.ztspeech.unisay.trans.engine;

import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ViewAnimator;

import com.ztspeech.unisay.trans.dc.BaseDC;
import com.ztspeech.unisay.trans.dc.InitDC;
import com.ztspeech.unisay.trans.main.Application;

public class AppEngine implements AnimationListener {
	private ViewAnimator viewSwiter;
	private TranslateAnimation animSlideInLeft;
	private TranslateAnimation animSlideOutLeft;
	private TranslateAnimation animSlideInRight;
	private TranslateAnimation animSlideOutRight;
	private ScaleAnimation scaleAnimation;
	private AlphaAnimation alphaAnimation;

	public Context context;
	private Stack<BaseDC> stack = new Stack<BaseDC>();
	private boolean isClickEnabled = true;

	public AppEngine(Context c) {
		context = c;
		viewSwiter = new ViewAnimator(context);
		viewSwiter.setDrawingCacheEnabled(false);
		animSlideInLeft = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animSlideInLeft.setDuration(500);
		animSlideOutLeft = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animSlideOutLeft.setDuration(500);
		animSlideInRight = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animSlideInRight.setDuration(500);
		animSlideOutRight = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animSlideOutRight.setDuration(500);
		// scaleAnimation=new ScaleAnimation(0.1f, 1.0f, 0.1f,
		// 1f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
		// 0.5f);
		getScaleAnimation();
		scaleAnimation.setDuration(2000);
		alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(500);

		scaleAnimation.setAnimationListener(this);
		alphaAnimation.setAnimationListener(this);
		animSlideInLeft.setAnimationListener(this);
		animSlideOutLeft.setAnimationListener(this);
		animSlideInRight.setAnimationListener(this);
		animSlideOutRight.setAnimationListener(this);
	}

	public boolean notAnimition() {
		return isClickEnabled;
	}

	public void popStack() {
		if (stack != null && stack.size() > 0) {
			stack.pop();
		}
	}

	public void removeItemByIndex(int index) {
		if (stack != null && stack.size() > index && index >= 0) {
			stack.remove(index);
		}
	}

	public int getStackSize() {
		if (stack != null) {
			return stack.size();
		}
		return -1;
	}

	public boolean back() {
		if (!isClickEnabled) {
			return true;
		}
		if (stack.size() > 1) {
			viewSwiter.setInAnimation(animSlideInRight);
			viewSwiter.setOutAnimation(animSlideOutRight);
			stack.pop();
			viewSwiter.addView(stack.peek());
			viewSwiter.setDisplayedChild(1);
			viewSwiter.removeViewAt(0);
			((BaseDC) viewSwiter.getCurrentView()).onShow();
			return true;
		} else {
			return false;
		}
	}

	public void quit() {
		if (!isClickEnabled) {
			return;
		}
		viewSwiter.addView(stack.firstElement());
		while (stack.size() > 1) {
			stack.pop();
		}
		viewSwiter.setInAnimation(animSlideInRight);
		viewSwiter.setOutAnimation(animSlideOutRight);
		viewSwiter.setDisplayedChild(1);
		viewSwiter.removeViewAt(0);
		((BaseDC) viewSwiter.getCurrentView()).onShow();
	}

	public boolean enterDC(BaseDC dc) {
		if (!isClickEnabled) {
			return false;
		}
		if (viewSwiter.getCurrentView() == dc) {
			return false;
		}
		if (dc.getParent() != null && dc.getParent() != viewSwiter) {
			return false;
		}
		setClickEnabled(false);
		stack.remove(dc);
		stack.push(dc);
		viewSwiter.setInAnimation(animSlideInLeft);
		viewSwiter.setOutAnimation(animSlideOutLeft);
		viewSwiter.addView(stack.peek());
		dc.invalidate();
		viewSwiter.setDisplayedChild(1);
		viewSwiter.removeViewAt(0);
		dc.onShow();
		return true;
	}

	public void removeDCfromStack(BaseDC dc) {
		stack.remove(dc);
	}

	public void removeLastDC() {
		if (stack.size() > 1) {
			stack.remove(stack.size() - 2);
		}
	}

	public ViewAnimator getDCEngine() {
		return viewSwiter;
	}

	public void setInitDC(InitDC initDC) {
		// setClickEnabled(false);
		viewSwiter.removeAllViews();
		viewSwiter.addView(initDC);
		viewSwiter.setAnimation(scaleAnimation);
		viewSwiter.setDisplayedChild(0);
	}

	/**
	 */
	public void setMainDC(BaseDC mainDC) {
		// setClickEnabled(false);
		viewSwiter.removeAllViews();
		stack.push(mainDC);
		viewSwiter.addView(stack.peek());
		viewSwiter.setAnimation(alphaAnimation);
		viewSwiter.setDisplayedChild(0);
		mainDC.onShow();
	}

	public void getScaleAnimation() {
		if (Application.ScreenWidth == 320 && Application.ScreenHeight == 427) {
			scaleAnimation = new ScaleAnimation(0.1f, 1.3f, 0.1f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
		} else if (Application.ScreenWidth == 320 && Application.ScreenHeight == 533) {
			scaleAnimation = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
		} else if (Application.ScreenWidth == 320 && Application.ScreenHeight == 480) {
			scaleAnimation = new ScaleAnimation(0.1f, 1.2f, 0.1f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
		} else if (Application.ScreenWidth == 320 && Application.ScreenHeight == 569) {
			scaleAnimation = new ScaleAnimation(0.1f, 1.2f, 0.1f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
		} else {
			scaleAnimation = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
		}
		scaleAnimation.setFillAfter(true);
	}

	public boolean isShowDC(BaseDC dc) {
		if (viewSwiter.getCurrentView() == dc) {
			return true;
		} else {
			return false;
		}
	}

	private void setClickEnabled(boolean isCleckEnabled) {
		if (isCleckEnabled) {
			Timer t = new Timer();
			t.schedule(new TimerTask() {
				@Override
				public void run() {
					isClickEnabled = true;
				}
			}, 200);
			// viewSwiter.setClickable(true);
		} else {
			isClickEnabled = false;
			// viewSwiter.setClickable(false);
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		setClickEnabled(true);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		setClickEnabled(false);
	}
}
