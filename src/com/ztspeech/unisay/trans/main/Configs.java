package com.ztspeech.unisay.trans.main;

import org.json.JSONStringer;

import android.app.Activity;

import com.ztspeech.unisay.utils.Json;
import com.ztspeech.unisay.utils.LogInfo;
import com.ztspeech.unisay.utils.Utils;

public class Configs {

	/**
	 * 是否开启程序日志
	 */
	public static boolean isDebug = true;
	/**
	 * 程序文件存储路径
	 */
	public static String ztsTalkPath = Utils.getSdcardPath() + "/ZTS/unisay/";
	/**
	 * 图片存储
	 */
	public static String ztsImagePath = ztsTalkPath + "Image/";

	public static String ztsImageCache = ztsTalkPath + "Cache/";
	public static String ztsRecordCache = ztsImageCache + "Record/";

	public static final String DATABASE_NAME = "content.db";// 数据库名

	public static String ztsVideoPath = ztsTalkPath + "Video/";
	public static String ztsAudioPath = ztsTalkPath + "Audio/";

	public static String ztspeechLrcPath = ztsTalkPath + "lrc/";
	public static String ztspeechMusicPath = ztsTalkPath + "music/";

	/**
	 * 邮箱和电话号码的正则表达式
	 */
	public static final String EmailPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
	public static final String PhonePattern = "((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)";

	/**
	 * 当前屏幕横竖屏状态 参考Configuration.ORIENTATION_PORTRAIT
	 * Configuration.ORIENTATION_LANDSCAPE
	 */
	public static int nowOrientation;
	/**
	 * 上一个屏幕状态 参考Configuration.ORIENTATION_PORTRAIT
	 * Configuration.ORIENTATION_LANDSCAPE
	 */
	public static int lastOrientation;
	public static String typeAndVsersion = null;
	public static String oldtypeAndVsersion = null;
	public static String IMEI = null;

	public static void initTypeAndVsersion(Activity context) {
		try {
			nowOrientation = context.getResources().getConfiguration().orientation;
			lastOrientation = nowOrientation;
			JSONStringer stringer = new JSONStringer();
			stringer.object();
			stringer.key("product").value("XHRD");
			stringer.key("clienttype").value("Android");
			stringer.key("clientversion").value("1.0.000");
			stringer.key("model").value(Utils.getMobileModel());
			stringer.key("resolution").value(Utils.getScreenWidth(context) + "X" + Utils.getScreenHeight(context));
			stringer.key("systemversion").value(Utils.getSDKVersion());
			stringer.key("channel").value("DEV");
			stringer.key("updatechannel").value("2");
			IMEI = Utils.getIMEI(context);
			LogInfo.LogOut("imei:" + IMEI);
			stringer.key("imei").value(IMEI);
			stringer.key("imsi").value(Utils.getIMSI(context) == null ? "" : Utils.getIMSI(context));
			stringer.key("login").value(0);
			stringer.key("memberId").value(2);
			stringer.key("language").value(Utils.getLocalLanguage());
			typeAndVsersion = stringer.endObject().toString();
			oldtypeAndVsersion = typeAndVsersion;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateUidToTypeAndVsersion(String uid, String username, int memberId) {
		Json json = new Json(oldtypeAndVsersion);
		if (uid != null) {
			json.put("userid", uid);
			json.put("userId", uid);
			json.put("userName", username);
			json.put("memberId", memberId);
		}
		typeAndVsersion = json.toNormalString();
	}

}
