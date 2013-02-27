package com.ztspeech.unisay.trans.dc;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.main.Application;
import com.ztspeech.unisay.widget.ZTSDialog;
import com.ztspeech.unisay.widget.ZTSDialog.TlcyDialogListener;

public abstract class BaseDC extends LinearLayout implements OnClickListener {
	public Context context;
	public Handler handler;
	public int ScreenWidth;
	public int ScreenHeight;
	LayoutInflater inflater;
	boolean mIsShown = false;
	InputMethodManager inputMethodManager;
	long l = 0;
	public ProgressDialog loadingDialog;
	private Toast mToast;
	private Dialog tempDialog;// 处理连续快速点击出现多个对话框的情况
	private Dialog toDialog = null;// 金立，魅族等手机在不显示时无法弹出alertdialog
	private boolean isShowing = true;// 当前activity是否显示中

	public BaseDC(Context context) {
		super(context);
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	public boolean notAnimition() {
		return Application.appEngine.notAnimition();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		hideInput();
		return super.onTouchEvent(event);
	}

	public void onClick(View v) {
		if (notAnimition() && Math.abs(System.currentTimeMillis() - l) > 500) {
			l = System.currentTimeMillis();
			hideInput();
			onClicked(v);
		}
	}

	public abstract void onClicked(View v);

	public void init(Handler h, int SWidth, int SHeight) {
		handler = h;
		ScreenWidth = SWidth;
		ScreenHeight = SHeight;
	}

	public void onShow() {

	}

	public void setShown(boolean isShown) {
		mIsShown = isShown;
	}

	public boolean isShown() {
		return mIsShown;
	}

	public void hideInput() {
		inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
	}

	/**
	 * 显示小提示
	 */
	public void showToast(String text) {
		if (mToast != null) {
		} else {
			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}
		mToast.setText(text);
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.setDuration(1);
		mToast.show();
	}

	/**
	 * 显示正在获取数据的弹出框提示
	 */
	public void showLoading() {
		/**
		 * 等待画面初始化 有的手机不new不能显示动画
		 */
		// if (loadingDialog == null) {
		if (loadingDialog != null)
			loadingDialog.dismiss();

		loadingDialog = new ProgressDialog(context);
		loadingDialog.setMessage(context.getString(R.string.loading));
		loadingDialog.setIndeterminate(true);
		loadingDialog.setCancelable(true);
		loadingDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				onLoadingCacel();
			}
		});
		// }
		loadingDialog.show();
	}

	public void showLoading(String msg) {
		/**
		 * 等待画面初始化 有的手机不new不能显示动画
		 */
		if (loadingDialog != null) {
			loadingDialog.dismiss();
		}

		loadingDialog = new ProgressDialog(context);
		loadingDialog.setMessage(msg);
		loadingDialog.setIndeterminate(true);
		loadingDialog.setCancelable(true);
		loadingDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				onLoadingCacel();
			}
		});
		loadingDialog.show();
	}

	/**
	 * 隐藏正在获取数据的弹出框提示
	 */
	public void dismissLoading() {
		if (loadingDialog != null) {
			loadingDialog.dismiss();
			// loadingDialog.cancel();
			loadingDialog = null;
		}
	}

	/**
	 * 手动取消正在获取数据的弹出框提示时的回调函数
	 */
	public void onLoadingCacel() {
		dismissLoading();
	}

	public void _showLoading() {
		/**
		 * 等待画面初始化 有的手机不new不能显示动画
		 */
		if (loadingDialog == null) {
			loadingDialog = new ProgressDialog(context);
			loadingDialog.setMessage(context.getString(R.string.loading));
			loadingDialog.setIndeterminate(true);
			loadingDialog.setCancelable(true);
			loadingDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					onLoadingCacel();
				}
			});
		}
		loadingDialog.show();
	}

	/**
	 * 金立，魅族等手机在不显示时无法弹出dialog 此方法自动判断当前activity是否正在显示，并作出直接显示还是等显示后再弹出dialog
	 */
	public Dialog showAlert(String text) {
		return showDialog(new ZTSDialog(context).setTitle(context.getString(R.string.tip)).setMessage(text)
				.setOnlyOkPositiveMethod(context.getString(R.string.OK)));
	}

	/**
	 * 金立，魅族等手机在不显示时无法弹出dialog 此方法自动判断当前activity是否正在显示，并作出直接显示还是等显示再在弹出dialog
	 */
	public Dialog showAlert(String title, String text, String ok, String cancel, TlcyDialogListener okListener,
			TlcyDialogListener cancelListener) {
		return showDialog(new ZTSDialog(context).setTitle(title).setMessage(text)
				.setButton(ok, cancel, okListener, cancelListener));
	}

	/**
	 * 金立，魅族等手机在不显示时无法弹出dialog 此方法自动判断当前activity是否正在显示，并作出直接显示还是等显示再在弹出dialog
	 */
	public Dialog showAlert(String text, TlcyDialogListener okListener, TlcyDialogListener cancelListener) {
		return showDialog(new ZTSDialog(context)
				.setTitle(context.getString(R.string.tip))
				.setMessage(text)
				.setButton(context.getString(R.string.OK), context.getString(R.string.cancel), okListener,
						cancelListener));
	}

	public Dialog showDialog(Dialog dialog) {
		if (tempDialog != null && tempDialog.isShowing()) {
			tempDialog.dismiss();
		}
		if (isShowing) {
			tempDialog = dialog;
			tempDialog.show();
		} else {
			toDialog = dialog;
		}
		return dialog;
	}

}
