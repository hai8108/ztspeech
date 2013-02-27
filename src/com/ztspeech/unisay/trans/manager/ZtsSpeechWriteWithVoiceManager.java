package com.ztspeech.unisay.trans.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.dc.ZtsSpeechWriteWithVoiceDC;
import com.ztspeech.unisay.trans.engine.VoiceDBEngine;
import com.ztspeech.unisay.trans.main.Application;
import com.ztspeech.unisay.trans.main.Configs;
import com.ztspeech.unisay.trans.model.ZtsSpeechVoiceHistoryModel;
import com.ztspeech.unisay.utils.Json;
import com.ztspeech.unisay.utils.LogInfo;
import com.ztspeech.unisay.utils.Utils;
import com.ztspeech.unisay.weibo.IRenren;
import com.ztspeech.unisay.weibo.ISina;
import com.ztspeech.unisay.weibo.ITencent;
import com.ztspeech.unisay.widget.ZTSDialog;
import com.ztspeech.unisay.widget.ZTSSmsSendDialog;
import com.ztspeech.unisay.widget.ZTSSmsSendDialog.ZTSSmsSendDialogListener;

public class ZtsSpeechWriteWithVoiceManager extends BaseManager {

	public final static int CONSTANT_SEND_MSG = 1;
	public final static int CONSTANT_SEND_EMAIL = 2;
	public final static int CONSTANT_SEND_WEIBO = 3;
	public final static int CONSTANT_ADD = 4;
	public final static int CONSTANT_COPY = 5;
	public final static int CONSTANT_DB_ITEM_SAVE = 6;
	public final static int CONSTANT_SHOW_LOADING_DIALOG = 7;
	public final static int CONSTANT_TO_HISTORYS = 8;
	private String smsStr;
	public ZtsSpeechWriteWithVoiceDC mainDC;
	private ZtspeechWeiboManager mZtspeechWeiboManager;
	private File file;
	private String tmpFile;
	private ZTSDialog dialog;
	private ZTSSmsSendDialog smsDialog;
	private Intent intent;
	/**
	 * ret=0 成功返回 ret=1 参数错误 ret=2 频率受限 ret=3 鉴权失败 ret=4 服务器内部错误
	 * 
	 * 
	 * 
	 * errcode=1 无效TOKEN,被吊销 errcode=2 请求重放 errcode=3 access_token不存在 errcode=4
	 * access_token超时 errcode=5 oauth 版本不对 errcode=6 oauth 签名方法不对 errcode=7 参数错
	 * errcode=8 处理失败 errcode=9 验证签名失败 errcode=10 网络错误 errcode=11 参数长度不对
	 * errcode=12 处理失败 errcode=13 处理失败 errcode=14 处理失败 errcode=15 处理失败
	 * 
	 * 
	 * errcode=0 表示成功 errcode=4 表示有过多脏话 errcode=5 禁止访问，如城市，uin黑名单限制等 errcode=6
	 * 删除时：该记录不存在。发表时：父节点已不存在 errcode=8 内容超过最大长度：420字节 （以进行短url处理后的长度计）
	 * errcode=9 包含垃圾信息：广告，恶意链接、黑名单号码等 errcode=10 发表太快，被频率限制 errcode=11
	 * 源消息已删除，如转播或回复时 errcode=12 源消息审核中 errcode=13 重复发表 errcode=14 未实名认证
	 */
	private static String[] retArrs = { "分享成功", "参数错误", "频率受限", "鉴权失败", "服务器内部错误" };
	private static String[] errcodeArrs4 = { "分享成功", "", "", "", "有过多脏话", " 禁止访问", "该记录不存在", "", "内容超过最大长度：420字节",
			"包含垃圾信息", "发表太快，被频率限制", "源消息已删除", "源消息审核中", "重复发表", " 未实名认证" };
	private static String[] errcodeArrs3 = { "", "无效TOKEN,被吊销", "请求重放", "access_token不存在", "access_token超时",
			"oauth 版本不对", "oauth 签名方法不对", "参数错误", "处理失败", "验证签名失败", "网络错误", "参数长度不对", "处理失败 ", "处理失败", "处理失败", "处理失败" };

	@Override
	public void initDC(Context c) {
		context = c;
		if (mainDC == null) {
			mainDC = new ZtsSpeechWriteWithVoiceDC(context);
			mainDC.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		}
	}

	@Override
	protected void initHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					back();
					break;
				case CONSTANT_SEND_MSG:// sms
					smsStr = (String) msg.obj;
					sendSMS(smsStr);
					break;
				case CONSTANT_SEND_EMAIL:// mail
					smsStr = (String) msg.obj;
					sendEmail(smsStr);
					break;
				case CONSTANT_SEND_WEIBO:
					smsStr = (String) msg.obj;
					shareWeibo(msg.arg1, smsStr);
					break;
				case CONSTANT_ADD:
					mainDC.showLoading(context.getString(R.string.savedataloading));
					saveWrite((ZtsSpeechVoiceHistoryModel) msg.obj);
					break;
				case CONSTANT_COPY:
					copyText((String) msg.obj);
					break;
				case CONSTANT_DB_ITEM_SAVE:
					mainDC.dismissLoading();
					if ((Boolean) msg.obj) {
						Application.isDbListGroupChange = true;
						mainDC.showToast(context.getString(R.string.successaddtohistory));
						mainDC.clearTextDisp();
					} else {
						mainDC.showToast(context.getString(R.string.failedaddtohistory));
					}
					break;
				case CONSTANT_TO_HISTORYS:
					Application.mZtsSpeechVoiceListManager.showDC();
					break;
				case ITencent.BIND_TENCENT_CODE:
					mainDC.dismissLoading();
					String resultMsg = (String) msg.obj;
					resultTecentWeibo(resultMsg);
					break;
				case CONSTANT_SHOW_LOADING_DIALOG:
					mainDC.showLoading(context.getString(R.string.submitdataloading));
					break;
				case ISina.BIND_SINA_CODE:
					mainDC.showToast((String) msg.obj);
					break;
				case ISina.SEND_SINA_CODE:
					mainDC.showToast((String) msg.obj);
					break;
				case IRenren.SEND_RENREN_CODE:
					if (msg.arg1 == 200 && msg.arg2 == 200) {
						Application.iRenren.sendWeibo(context, smsStr, handler);
					} else if (msg.arg1 == 404 && msg.arg2 == 404) {
						mainDC.showToast("账号鉴权失败！");
					}
					break;
				default:
					break;
				}
			}

		};
	}

	private void resultTecentWeibo(String resultMsg) {
		if (resultMsg != null) {
			Json json = new Json(resultMsg);
			int errcode = json.getInt("errcode");
			int ret = json.getInt("ret");
			if (ret == 0) {
				mainDC.showToast(retArrs[ret]);
			} else if (ret == 3) {
				if (errcodeArrs3.length > errcode) {
					mainDC.showToast(errcodeArrs3[errcode]);
				} else {
					mainDC.showToast(context.getString(R.string.tencentsharedfailed));
				}
			} else if (ret == 4) {
				if (errcodeArrs4.length > errcode) {
					mainDC.showToast(errcodeArrs4[errcode]);
				} else {
					mainDC.showToast(context.getString(R.string.tencentsharedfailed));
				}
			} else {
				if (retArrs.length > ret) {
					mainDC.showToast(retArrs[ret]);
				} else {
					mainDC.showToast(context.getString(R.string.tencentsharedfailed));
				}
			}
		} else {
			mainDC.showToast(context.getString(R.string.sharedfailed));
		}
	}

	private void copyText(String str) {
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setText(str);
	}

	private void deleteRecordTmpFile(String recordTmpFileName) {
		file = new File(recordTmpFileName);
		if (file.exists()) {
			file.delete();
		}
	}

	// 拼装当前的URL
	private void goOAuth(Context context) {
		// System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
		// System.setProperty("weibo4j.oauth.consumerSecret",
		// Weibo.CONSUMER_SECRET);
		// Weibo weibo = new Weibo();
		// RequestToken requestToken;
		// try {
		// requestToken =
		// weibo.getOAuthRequestToken("ztspeechAndroid://OAuthActivity");
		// OAuthConstant.getInstance().setRequestToken(requestToken);
		//
		// // Uri uri = Uri.parse(requestToken.getAuthenticationURL() +
		// // "&display=mobile");
		// //
		// // context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
		// String url = requestToken.getAuthenticationURL() + "&display=mobile";
		// LogInfo.LogOut("haitian", "url =" + url);
		// mZtspeechWeiboManager = null;
		// mZtspeechWeiboManager = new ZtspeechWeiboManager();
		// mZtspeechWeiboManager.isBinderOrNot(false);
		// mZtspeechWeiboManager.initDC(context);
		// mZtspeechWeiboManager.setUrl(url);
		// mZtspeechWeiboManager.changeTitle(context.getString(R.string.sina_sync));
		// mZtspeechWeiboManager.showDC();
		//
		// } catch (WeiboException e) {
		// e.printStackTrace();
		// }
	}

	private void shareWeibo(int arg1, String msg) {
		switch (arg1) {
		case R.id.iv_tencent:
			Application.iTencent.sendWeibo(msg, handler);
			break;
		case R.id.iv_sohu:
			Application.iRenren.sendWeibo(context, msg, handler);
			// Intent intent = new Intent(context, RenrenActivity.class);
			// intent.putExtra("msg", msg);
			// ((Activity)
			// context).overridePendingTransition(R.anim.slideinleft,
			// R.anim.slideoutright);
			// context.startActivity(intent);
			break;
		case R.id.iv_sina:
			Application.iSina.sendWeibo(msg, handler);
			break;
		case R.id.iv_more:
			intent = new Intent(Intent.ACTION_SEND);
			intent.setType("*/*");
			// intent.setType("text/plain"); //文字分享
			/*
			 * 图片分享 　　　　it.setType("image/png"); 　　　　　//添加图片 　　　　 File f = new
			 * File(Environment.getExternalStorageDirectory()+"/name.png"); 　Uri
			 * uri = Uri.fromFile(f); 　　　　 intent.putExtra(Intent.EXTRA_STREAM,
			 * uri); 　　　　　
			 */
			intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
			intent.putExtra(Intent.EXTRA_TEXT, msg);
			context.startActivity(Intent.createChooser(intent, "紫冬口述"));
			break;
		default:
			break;
		}
		// if (arg1 != R.id.iv_more) {
		//
		// if (mZtspeechWeiboManager == null) {
		// mZtspeechWeiboManager = new ZtspeechWeiboManager();
		// }
		// mZtspeechWeiboManager.initDC(context);
		// mZtspeechWeiboManager.showDC();
		// }
	}

	private void saveWrite(final ZtsSpeechVoiceHistoryModel model) {
		tmpFile = model.recordPath;
		model.recordId = Utils.generateActiveCode();
		model.recordPath = Configs.ztsRecordCache + model.recordId + ".dat";
		new Thread(new Runnable() {
			@Override
			public void run() {
				createRecordCacheFile(model.recordPath, tmpFile);
				boolean flag = VoiceDBEngine.insert(model);
				deleteRecordTmpFile(tmpFile);
				handler.sendMessage(Message.obtain(handler, CONSTANT_DB_ITEM_SAVE, flag));
			}
		}).start();
		// dialog = new
		// ZTSDialog(context).setTitle("提示").setEditMessage("请输入标题")
		// .setButton("确定", "取消", new TlcyDialogListener() {
		// @Override
		// public void onClick() {
		// dialog.dismiss();
		// String title = dialog.getEditMessage().trim();
		// if (title != null && title.length() > 0) {
		// tmpFile = model.recordPath;
		// model.title = title;
		// model.recordId = Utils.generateActiveCode();
		// model.recordPath = Configs.ztsRecordCache + model.recordId + ".dat";
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// createRecordCacheFile(model.recordPath, tmpFile);
		// boolean flag = VoiceDBEngine.insert(model);
		// deleteRecordTmpFile(tmpFile);
		// handler.sendMessage(Message.obtain(handler, CONSTANT_DB_ITEM_SAVE,
		// flag));
		// }
		// }).start();
		// } else {
		// mainDC.showToast("_标题不能为空_");
		// handler.sendMessage(Message.obtain(handler,
		// ZtsSpeechWriteWithVoiceManager.CONSTANT_ADD,
		// model));
		// }
		// }
		// }, null);
		// dialog.show();

	}

	private void createRecordCacheFile(String recordFileName, String recordTmpFileName) {
		FileOutputStream fileOutputStream = null;
		FileInputStream fileInputStream = null;
		try {
			File tmpFile = null;
			file = new File(recordFileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			tmpFile = new File(recordTmpFileName);
			if (!tmpFile.exists()) {
				LogInfo.LogOut("haitian", recordTmpFileName + " file not exist!");
				return;
			}
			fileOutputStream = new FileOutputStream(file);
			fileInputStream = new FileInputStream(tmpFile);
			byte[] buffer = new byte[1024];
			int res = 0;
			while ((res = fileInputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, res);
			}
			fileOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void showDC() {
		if (mainDC == null) {
			mainDC = new ZtsSpeechWriteWithVoiceDC(context);
			mainDC.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		}
		enterDC(mainDC);
	}

	private void sendEmail(String msg) {
		// TODO Auto-generated method stub
		Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND);
		mailIntent.setType("plain/test");

		mailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
		mailIntent.putExtra(android.content.Intent.EXTRA_CC, "");
		mailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
		mailIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
		context.startActivity(Intent.createChooser(mailIntent, "发送邮件"));
	}

	private void sendSMS(final String msg) {
		// TODO Auto-generated method stub
		if (Application.datas != null && Application.datas.size() > 0) {
			smsDialog = new ZTSSmsSendDialog(context, mOnCheckedChangeListener, mOnClickListener).setTitle("选择最近联系人")
					.setButton("确定", "取消", new ZTSSmsSendDialogListener() {
						@Override
						public void onClick() {
							new Thread(new Runnable() {
								@Override
								public void run() {
									int len = Application.datas.size();
									StringBuilder number = new StringBuilder();
									for (int i = 0; i < len; i++) {
										if (Application.datas.get(i).isChekced) {
											Application.datas.get(i).isChekced = false;
											number.append(Application.datas.get(i).smsNumber).append(",");
										}
									}
									String numberStr = number.toString();
									numberStr = numberStr.substring(0, numberStr.length() - 1);
									Intent sendIntent = new Intent(Intent.ACTION_VIEW);
									sendIntent.putExtra("address", numberStr);
									sendIntent.putExtra("sms_body", msg);
									sendIntent.setType("vnd.android-dir/mms-sms");
									context.startActivity(sendIntent);
								}
							}).start();

						}
					}, new ZTSSmsSendDialogListener() {
						@Override
						public void onClick() {
							new Thread(new Runnable() {
								@Override
								public void run() {
									int len = Application.datas.size();
									for (int i = 0; i < len; i++) {
										Application.datas.get(i).isChekced = false;
									}
								}
							}).start();
						}
					});
			smsDialog.setItems(Application.datas, mOnItemClickListener);
			smsDialog.show();
		} else {
			Intent sendIntent = new Intent(Intent.ACTION_VIEW);
			sendIntent.putExtra("sms_body", msg);
			sendIntent.setType("vnd.android-dir/mms-sms");
			context.startActivity(sendIntent);
		}
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent sendIntent = new Intent(Intent.ACTION_VIEW);
			sendIntent.putExtra("sms_body", smsStr);
			sendIntent.setType("vnd.android-dir/mms-sms");
			context.startActivity(sendIntent);
		}

	};
	private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			int position = (Integer) arg0.getTag();
			Application.datas.get(position).isChekced = arg1;
			smsDialog.notifyDataSetChanged();
		}

	};
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (Application.datas.get(arg2).isChekced) {
				Application.datas.get(arg2).isChekced = false;
			} else {
				Application.datas.get(arg2).isChekced = true;
			}
			smsDialog.notifyDataSetChanged();
		}

	};
}
