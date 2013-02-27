package com.ztspeech.unisay.trans.model;

public class ZtsSpeechVoiceHistoryModel {
	public int id;
	public String title;
	public String content;
	public String time;

	public String recordId;
	public String recordPath;
	public String imageId;
	public String imagePath;

	public boolean checked;
	public int position;
	public int state;

	/*
	 * db.execSQL("CREATE TABLE history (" +
	 * "id INTEGER PRIMARY KEY autoincrement, " + "title TEXT ," + // 标题
	 * "time TEXT ," + // 创建时间 "content TEXT ," + // 内容 "recordId TEXT ," + //
	 * 录音文件ID "recordPath TEXT ," + // 录音文件地址 "imageId TEXT ," + // 图片ID
	 * "imagePath TEXT ," + // 图片地址 "state INTEGER ," + // ");");
	 */
	public ZtsSpeechVoiceHistoryModel clone() {
		ZtsSpeechVoiceHistoryModel DLmodel = new ZtsSpeechVoiceHistoryModel();
		DLmodel.id = id;
		DLmodel.title = title;
		DLmodel.content = content;
		DLmodel.time = time;

		DLmodel.recordId = recordId;
		DLmodel.recordPath = recordPath;

		DLmodel.imageId = imageId;
		DLmodel.imagePath = imagePath;

		DLmodel.checked = checked;
		DLmodel.position = position;
		DLmodel.state = state;
		return DLmodel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZtsSpeechVoiceHistoryModel other = (ZtsSpeechVoiceHistoryModel) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
