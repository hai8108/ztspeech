package com.ztspeech.unisay.trans.engine;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ztspeech.unisay.trans.main.Application;
import com.ztspeech.unisay.trans.model.ZtsSpeechVoiceHistoryModel;
import com.ztspeech.unisay.utils.LogInfo;
import com.ztspeech.unisay.utils.Utils;

public class VoiceDBEngine extends DBEngine {
	/**
	 * 保存单条记录
	 * 
	 * @param model
	 *            db.execSQL("CREATE TABLE history (" +
	 *            "id INTEGER PRIMARY KEY autoincrement, " + "title TEXT ," + //
	 *            标题 "time TEXT ," + // 创建时间 "content TEXT ," + // 内容
	 *            "recordId TEXT ," + // 录音文件ID "recordPath TEXT ," + // 录音文件地址
	 *            "imageId TEXT ," + // 图片ID "imagePath TEXT ," + // 图片地址
	 *            "state INTEGER ," + // ");");
	 */

	public static boolean insert(ZtsSpeechVoiceHistoryModel model) {
		try {
			DBEngine.create();
			String sql = "insert into history(id ,title,time,content,recordId,recordPath,imageId,imagePath,state) values (?,?,?,?,?,?,?,?,?)";
			db.beginTransaction();
			db.execSQL(sql, new String[] { null, model.title, model.time, model.content, model.recordId,
					model.recordPath, model.imageId, model.imagePath, String.valueOf(model.state) });
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			db.setTransactionSuccessful();
			db.endTransaction();
			DBEngine.close();
		}
	}

	/**
	 * 保存口译列表(多条记录)
	 * 
	 * @param model
	 */

	public static boolean saveHistoryList(List<ZtsSpeechVoiceHistoryModel> models) {
		try {
			DBEngine.create();
			String sql = "insert into history(id ,title,time,content,recordId,recordPath,imageId,imagePath,state) values (?,?,?,?,?,?,?,?,?)";
			db.beginTransaction();
			for (ZtsSpeechVoiceHistoryModel model : models) {
				db.execSQL(sql, new String[] { null, model.title, model.time, model.content, model.recordId,
						model.recordPath, model.imageId, model.imagePath, String.valueOf(model.state) });
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			db.setTransactionSuccessful();
			db.endTransaction();
			DBEngine.close();
		}
	}

	/**
	 * 查询数据库获取历史记录
	 * 
	 * @return
	 */
	public static ArrayList<ZtsSpeechVoiceHistoryModel> getHistoryList() {
		DBEngine.create();
		ArrayList<ZtsSpeechVoiceHistoryModel> historyModels = new ArrayList<ZtsSpeechVoiceHistoryModel>();
		SQLiteDatabase db = DBEngine.db;
		Cursor cursor = db
				.rawQuery(
						"select id ,title,time,content,recordId,recordPath,imageId,imagePath,state from history order by time desc",
						null);
		try {
			ZtsSpeechVoiceHistoryModel model = null;
			while (cursor.moveToNext()) {
				model = new ZtsSpeechVoiceHistoryModel();
				model.id = cursor.getInt(cursor.getColumnIndex("id"));
				model.title = cursor.getString(cursor.getColumnIndex("title"));
				model.time = cursor.getString(cursor.getColumnIndex("time"));
				model.content = cursor.getString(cursor.getColumnIndex("content"));
				model.recordId = cursor.getString(cursor.getColumnIndex("recordId"));
				model.recordPath = cursor.getString(cursor.getColumnIndex("recordPath"));
				model.imageId = cursor.getString(cursor.getColumnIndex("imageId"));
				model.imagePath = cursor.getString(cursor.getColumnIndex("imagePath"));
				model.state = cursor.getInt(cursor.getColumnIndex("state"));
				model.checked = false;
				LogInfo.LogOut("model.title =  " + model.title + "model.time =" + model.time);
				historyModels.add(model);
			}
			cursor.close();
			DBEngine.close();
			return historyModels;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			cursor.close();
			DBEngine.close();
			return null;
		}
	}

	/**
	 * 查询距离目前时间几天前的记录
	 * 
	 * @return
	 */
	public static ArrayList<ZtsSpeechVoiceHistoryModel> getHistoryListBytime(int day) {
		DBEngine.create();
		int listNumTmp = 1;
		ArrayList<ZtsSpeechVoiceHistoryModel> historyModels = new ArrayList<ZtsSpeechVoiceHistoryModel>();
		SQLiteDatabase db = DBEngine.db;
		Cursor cursor = db
				.rawQuery(
						"select id ,title,time,content,recordId,recordPath,imageId,imagePath,state from history order by time desc",
						null);
		try {
			ZtsSpeechVoiceHistoryModel model = null;
			while (cursor.moveToNext()) {
				model = new ZtsSpeechVoiceHistoryModel();
				model.id = cursor.getInt(cursor.getColumnIndex("id"));
				model.title = cursor.getString(cursor.getColumnIndex("title"));
				model.time = cursor.getString(cursor.getColumnIndex("time"));
				model.content = cursor.getString(cursor.getColumnIndex("content"));
				model.recordId = cursor.getString(cursor.getColumnIndex("recordId"));
				model.recordPath = cursor.getString(cursor.getColumnIndex("recordPath"));
				model.imageId = cursor.getString(cursor.getColumnIndex("imageId"));
				model.imagePath = cursor.getString(cursor.getColumnIndex("imagePath"));
				model.state = cursor.getInt(cursor.getColumnIndex("state"));
				model.checked = false;
				listNumTmp = Utils.returnCalculateDays(Utils.returnNowTime(), model.time);
				if (listNumTmp > Application.listNum) {
					Application.listNum = listNumTmp;
				}
				if (listNumTmp == day) {
					historyModels.add(model);
				}
			}
			cursor.close();
			DBEngine.close();
			return historyModels;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			cursor.close();
			DBEngine.close();
			return null;
		}
	}

	/**
	 * 根据id查询历史信息
	 * 
	 * @param id
	 * @return
	 */
	public static ZtsSpeechVoiceHistoryModel findById(int id) {
		String sql = "select * from history where id = ?";
		DBEngine.create();
		ZtsSpeechVoiceHistoryModel model = new ZtsSpeechVoiceHistoryModel();
		SQLiteDatabase db = DBEngine.db;
		Cursor cursor = db.rawQuery(sql, new String[] { String.valueOf(id) });
		try {
			while (cursor.moveToNext()) {
				model.title = cursor.getString(cursor.getColumnIndex("title"));
				model.time = cursor.getString(cursor.getColumnIndex("time"));
				model.content = cursor.getString(cursor.getColumnIndex("content"));
				model.recordId = cursor.getString(cursor.getColumnIndex("recordId"));
				model.recordPath = cursor.getString(cursor.getColumnIndex("recordPath"));
				model.imageId = cursor.getString(cursor.getColumnIndex("imageId"));
				model.imagePath = cursor.getString(cursor.getColumnIndex("imagePath"));
				model.state = cursor.getInt(cursor.getColumnIndex("state"));
				model.checked = false;
			}
			cursor.close();
			DBEngine.close();
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			cursor.close();
			DBEngine.close();
			return null;
		}
	}

	/**
	 * 根据id删除记录
	 * 
	 * @param id
	 */
	public static void deleteById(int id) {

		SQLiteDatabase db = DBEngine.db;
		db.beginTransaction();
		try {
			db.execSQL("delete from history where id = ? ", new String[] { String.valueOf(id) });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.setTransactionSuccessful();
			db.endTransaction();
		}
	}

	/**
	 * 更新所有记录
	 * 
	 * @param cm
	 */
	public static void update(List<ZtsSpeechVoiceHistoryModel> models) {
		DBEngine.create();
		SQLiteDatabase db = DBEngine.db;
		db.beginTransaction();
		try {
			String sql = "update history set title=?,time=?,content=?,recordId=?, recordPath=?,imageId=?,imagePath=?,state=? where id = ? ";
			for (ZtsSpeechVoiceHistoryModel model : models) {
				db.execSQL(sql,
						new String[] { model.title, model.time, model.content, model.recordId, model.recordPath,
								model.imageId, model.imagePath, String.valueOf(model.state), String.valueOf(model.id) });
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.setTransactionSuccessful();
			db.endTransaction();
			DBEngine.close();
		}
	}

	/**
	 * 更新一条记录
	 * 
	 * @param cm
	 */
	public static boolean updateById(ZtsSpeechVoiceHistoryModel model) {
		DBEngine.create();
		SQLiteDatabase db = DBEngine.db;
		db.beginTransaction();
		try {
			String sql = "update history set title=?,time=?,content=?,recordId=?, recordPath=?,imageId=?,imagePath=?,state=? where id = ? ";
			db.execSQL(sql, new String[] { model.title, model.time, model.content, model.recordId, model.recordPath,
					model.imageId, model.imagePath, String.valueOf(model.state), String.valueOf(model.id) });
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			db.setTransactionSuccessful();
			db.endTransaction();
			DBEngine.close();
		}
	}
}
