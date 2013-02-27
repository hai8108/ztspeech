package com.ztspeech.unisay.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.util.Log;

/**
 * @author haitian
 * 
 */
public class LogInfo {
	public static boolean isDebug = true;

	public static void LogOut(String info) {

		if (isDebug && info != null) {
			Log.d("ZTSpeech", info);
		}
	}

	public static void logToFile(String info) {
		try {
			String aLogFile = Utils.getSdcardPath() + "/ZTS_HAI.log";
			File iFile = new File(aLogFile);
			if (!iFile.exists()) {
				iFile.createNewFile();
			}
			FileWriter aFileStream = new FileWriter(iFile, true);
			BufferedWriter aWriter = new BufferedWriter(aFileStream);
			aWriter.write("[" + Utils.returnNowTime() + "]" + info);
			aWriter.write("\r\n");
			aWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void LogOut(String string, String msg) {
		// TODO Auto-generated method stub
		if (isDebug && msg != null) {
			Log.d(string, msg);
		}
	}

}
