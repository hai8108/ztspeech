package com.ztspeech.unisay.trans.engine;

import java.io.File;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ztspeech.unisay.trans.main.Configs;
import com.ztspeech.unisay.utils.LogInfo;

public class DBEngine {
	static SQLiteDatabase db;
	public static final String TAG = "DBEngine";

	/**
	 * 创建数据库
	 */
	public static void create() {
		try {
			File file = new File(Configs.ztsTalkPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			db = SQLiteDatabase.openDatabase(Configs.ztsTalkPath + Configs.DATABASE_NAME, null,
					SQLiteDatabase.OPEN_READWRITE);
			LogInfo.LogOut(TAG + " open db");
		} catch (Exception e) {
			LogInfo.LogOut(TAG + " open db error and create db start ");
			db = SQLiteDatabase.openOrCreateDatabase(Configs.ztsTalkPath + Configs.DATABASE_NAME, null);
			createTableHistorys();
			LogInfo.LogOut(TAG + " open db error and create db end");
		}
	}

	private static boolean createTableHistorys() {
		try {
			db.execSQL("CREATE TABLE history (" + "id INTEGER PRIMARY KEY autoincrement, " + "title TEXT ," + // 标题
					"time TEXT ," + // 创建时间
					"content TEXT ," + // 内容
					"recordId TEXT ," + // 录音文件ID
					"recordPath TEXT ," + // 录音文件地址
					"imageId TEXT ," + // 图片ID
					"imagePath TEXT ," + // 图片地址
					"state INTEGER" + //
					");");
			LogInfo.LogOut(TAG + " Create history table ok");
			return true;
		} catch (Exception e) {
			LogInfo.LogOut(TAG + " Create Table history err,table exists." + e.getMessage());
		}
		return false;
	}

	/**
	 * 过滤whereArgs中为null的数据项
	 * 
	 * @param whereArgs
	 */
	private static void filterWhereArgs(String... whereArgs) {
		if (whereArgs != null && whereArgs.length > 0) {
			for (int i = 0, j = whereArgs.length; i < j; i++) {
				if (whereArgs[i] == null) {
					whereArgs[i] = "";
				}
			}
		}
	}

	public static boolean isEmpty() {
		create();
		String count = exeScalar("SELECT COUNT(news_id) FROM av_files");
		return count == null || "0".equals(count);
	}

	/**
	 * 查询
	 * 
	 * @param sql
	 * @param whereArgs
	 * @return
	 */
	public static String exeScalar(String sql, String... whereArgs) {
		String result = "";
		filterWhereArgs(whereArgs);
		Cursor c = db.rawQuery(sql, whereArgs);
		if (c.moveToNext()) {
			result = c.getString(0);
		}
		c.close();
		close();
		return result;
	}

	public static void close() {
		try {
			if (db != null) {
				db.close();
			}
			SQLiteDatabase.releaseMemory();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogInfo.LogOut(TAG + " db close!!!!");
		db = null;
	}
}
