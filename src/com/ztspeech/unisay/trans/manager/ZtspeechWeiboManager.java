package com.ztspeech.unisay.trans.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.tencent.weibo.oauthv1.OAuthV1;
import com.ztspeech.unisay.trans.R;
import com.ztspeech.unisay.trans.dc.ZtspeechWeiboDC;
import com.ztspeech.unisay.trans.main.Application;
import com.ztspeech.unisay.utils.Json;
import com.ztspeech.unisay.weibo.ITencent;

public class ZtspeechWeiboManager extends BaseManager {
	public final static int CONSTENT_TENCENT_WEIBO_REGIST = 1;
	public final static int CONSTANT_LOADING_WEB_URL = 2;
	public final static int CONSTENT_SINA_WEIBO_REGIST = 3;

	private static String[] retArrs = { "分享成功", "参数错误", "频率受限", "鉴权失败", "服务器内部错误" };
	private static String[] errcodeArrs4 = { "分享成功", "", "", "", "有过多脏话", " 禁止访问", "该记录不存在", "", "内容超过最大长度：420字节",
			"包含垃圾信息", "发表太快，被频率限制", "源消息已删除", "源消息审核中", "重复发表", " 未实名认证" };
	private static String[] errcodeArrs3 = { "", "无效TOKEN,被吊销", "请求重放", "access_token不存在", "access_token超时",
			"oauth 版本不对", "oauth 签名方法不对", "参数错误", "处理失败", "验证签名失败", "网络错误", "参数长度不对", "处理失败 ", "处理失败", "处理失败", "处理失败" };

	public ZtspeechWeiboDC mainDC;
	private OAuthV1 oAuth = null;

	private boolean isBinderProcess = false;
	private String url;
	private String title;

	public void isBinderOrNot(boolean isBinderProcess) {
		this.isBinderProcess = isBinderProcess;
	}

	@Override
	public void initDC(Context c) {
		context = c;
	}

	@Override
	public void back() {
		mainDC.clearSelf();
		super.back();
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
				case CONSTENT_TENCENT_WEIBO_REGIST:
					mainDC.dismissLoading();
					boolean result = Application.iTencent.saveStatus((OAuthV1) msg.obj);
					if (result) {
						mainDC.showToast(context.getString(R.string.bindersuccess));
						if (!isBinderProcess) {
							mainDC._showLoading();
							Application.iTencent.sendWeibo(handler);
						} else {
							back();
						}
					} else {
						mainDC.showToast(context.getString(R.string.binderfailed));
						Application.iTencent.setOAuth();
						back();
					}
					break;
				case CONSTENT_SINA_WEIBO_REGIST:
					// Intent intent = new Intent(context, OAuthActivity.class);
					// intent.setAction("android.intent.action.VIEW");
					// intent.setData(Uri.parse((String) msg.obj));
					// context.startActivity(intent);
					break;
				case CONSTANT_LOADING_WEB_URL:
					if (url == null) {
						mainDC.loadURL();
					} else {
						mainDC.loadURL(url);
					}
					break;
				case ITencent.BIND_TENCENT_CODE:
					mainDC.dismissLoading();
					String resultMsg = (String) msg.obj;
					resultTecentWeibo(resultMsg);
					back();
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
				Application.iTencent.setOAuth();
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

	public void setUrl(String url) {
		this.url = url;
	}

	public void changeTitle(String title) {
		this.title = title;
	}

	@Override
	public void showDC() {
		if (mainDC == null) {
			mainDC = new ZtspeechWeiboDC(context, handler, oAuth);
		}
		mainDC.init(handler, Application.ScreenWidth, Application.ScreenHeight);
		mainDC.changeTitle(title);
		enterDC(mainDC);
		handler.sendMessageDelayed(Message.obtain(handler, CONSTANT_LOADING_WEB_URL), 100);

	}
}
