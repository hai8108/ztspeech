package com.ztspeech.unisay.weibo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.renren.api.connect.android.AsyncRenren;
import com.renren.api.connect.android.Renren;
import com.renren.api.connect.android.common.AbstractRequestListener;
import com.renren.api.connect.android.exception.RenrenAuthError;
import com.renren.api.connect.android.exception.RenrenError;
import com.renren.api.connect.android.status.StatusSetRequestParam;
import com.renren.api.connect.android.status.StatusSetResponseBean;
import com.renren.api.connect.android.view.RenrenAuthListener;

public class IRenren {

	// private Context context;
	private static final String API_KEY = "978e1e68cbe74d81aa178a8aa487ffdf";
	private static final String SECRET_KEY = "2eb96ee32f0147159a117ca7c86e96be ";
	private static final String APP_ID = "222082";
	private static final String[] DEFAULT_PERMISSIONS = { "publish_feed", "status_update" };
	private Renren renren;
	public static final int BIND_RENREN_CODE = 3001;
	public static final int SEND_RENREN_CODE = 3002;
	private ProgressDialog progress;
	private Handler mHandler;
	private static IRenren iRenren = null;

	public synchronized static IRenren getInstance() {
		if (iRenren == null) {
			iRenren = new IRenren();
		}
		return iRenren;
	}

	public void init(Context context) {
		renren = new Renren(API_KEY, SECRET_KEY, APP_ID, context);
	}

	// 授权
	public void bindRenren(Context context, Handler handler) {
		mHandler = handler;
		if (renren.isAccessTokenValid()) {
			renren.logout(context);
		}
		renren.authorize((Activity) context, DEFAULT_PERMISSIONS, rrBinderAuthListener);
	}

	public boolean unBinderRenren(Context context) {
		if (renren.isAccessTokenValid()) {
			renren.logout(context);
		}
		return true;
	}

	public boolean isBinder() {
		return renren.isAccessTokenValid();
	}

	// 发状态
	public void sendWeibo(Context context, String contentStr, Handler handler) {
		mHandler = handler;
		if (renren != null) {
			if (renren.isAccessTokenValid()) {
				progress = ProgressDialog.show(context, "提示", "正在发布，请稍候");
				StatusSetRequestParam param = new StatusSetRequestParam(contentStr);
				StatusSetListener listener = new StatusSetListener(context);
				try {
					AsyncRenren aRenren = new AsyncRenren(renren);
					aRenren.publishStatus(param, listener, // 对结果进行监听
							true); // 若超过140字符，则自动截短
				} catch (Throwable e) {
					String errorMsg = e.getMessage();
					Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
				}
			} else {
				renren.authorize((Activity) context, DEFAULT_PERMISSIONS, rrAuthListener);
			}
		}
	}

	// 存token
	public void saveStatus(int requestCode, int resultCode, Intent data) {
		if (renren != null) {
			renren.authorizeCallback(requestCode, resultCode, data);
		}
	}

	private RenrenAuthListener rrAuthListener = new RenrenAuthListener() {

		@Override
		public void onRenrenAuthError(RenrenAuthError renrenAuthError) {
			// 授权失败
			mHandler.sendMessage(Message.obtain(mHandler, SEND_RENREN_CODE, 404, 404));
		}

		@Override
		public void onComplete(Bundle values) {
			// 授权成功相应操作
			mHandler.sendMessage(Message.obtain(mHandler, SEND_RENREN_CODE, 200, 200));
		}

		@Override
		public void onCancelLogin() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancelAuth(Bundle values) {
			// TODO Auto-generated method stub

		}
	};
	private RenrenAuthListener rrBinderAuthListener = new RenrenAuthListener() {

		@Override
		public void onRenrenAuthError(RenrenAuthError renrenAuthError) {
			// 授权失败
			mHandler.sendMessage(Message.obtain(mHandler, BIND_RENREN_CODE, 404, 404));
		}

		@Override
		public void onComplete(Bundle values) {
			// 授权成功相应操作
			mHandler.sendMessage(Message.obtain(mHandler, BIND_RENREN_CODE, 200, 200));
		}

		@Override
		public void onCancelLogin() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancelAuth(Bundle values) {
			// TODO Auto-generated method stub

		}
	};

	private class StatusSetListener extends AbstractRequestListener<StatusSetResponseBean> {

		private Context context;

		private Handler handler;

		public StatusSetListener(Context context) {
			this.context = context;
			this.handler = new Handler(context.getMainLooper());
		}

		@Override
		public void onRenrenError(RenrenError renrenError) {
			final int errorCode = renrenError.getErrorCode();
			final String errorMsg = renrenError.getMessage();
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (context != null) {
						if (progress != null) {
							progress.dismiss();
						}
					}
					if (errorCode == RenrenError.ERROR_CODE_OPERATION_CANCELLED) {
						Toast.makeText(context, "发送被取消", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "发送失败", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}

		@Override
		public void onFault(Throwable fault) {
			final String errorMsg = fault.toString();
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (context != null) {
						if (progress != null) {
							progress.dismiss();
						}
					}
					Toast.makeText(context, "发送失败", Toast.LENGTH_SHORT).show();
				}
			});
		}

		@Override
		public void onComplete(StatusSetResponseBean bean) {
			final String responseStr = bean.toString();
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (context != null) {
						if (progress != null) {
							progress.dismiss();
						}
					}
					Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}
