package com.ztspeech.unisay.trans.manager;

import android.content.Context;
import android.os.Handler;

import com.ztspeech.unisay.trans.dc.BaseDC;
import com.ztspeech.unisay.trans.main.Application;

public abstract class BaseManager {

	// 定制的消息中的what
	/**
	 * 返回
	 */
	public static final int BACK = -1000;
	/**
	 * 主页
	 */
	public static final int HOME = -1001;
	/**
	 * 返回后做处理
	 */
	public static final int BACK_OUT = -100;
	/**
	 * 回到主页后,做处理
	 */
	public static final int QUIT_OUT = -101;

	public static final int ENTER_OTHER_END = -102;

	public int check_flag = 1;
	BaseManager lastManager = null;

	/**
	 * 必须在每个具体manager中重载
	 */
	public Handler handler;

	/**
	 * 跳转到由lastManager确定的特定页面,当有指定页面时,不应调用BaseManager的此方法
	 * 即,删除重写方法中的super.gotoTheLastManagerSpecilDC()
	 */
	public void gotoTheLastManagerSpecilDC(Context context, BaseManager manager) {
		this.context = context;
		back();
	}

	public void clearHelpPart() {
		Application.appEngine.removeItemByIndex(Application.appEngine.getStackSize() - 2);
	}

	public Handler getHandler() {
		return handler;
	}

	/**
	 * initDC中使用
	 */
	public Context context;

	public BaseManager() {
		initHandler();
	}

	/**
	 * 返回到上一界面 2010-9-27
	 * 
	 */
	public void back() {
		Application.appEngine.back();
	}

	/**
	 * 退出此Manager,返回到主界面
	 * 
	 */
	public void quit() {
		Application.appEngine.quit();
	}

	/**
	 * 动画进入界面
	 * 
	 */
	public boolean enterDC(BaseDC dc) {
		Application.currentMng = this;
		return Application.appEngine.enterDC(dc);
	}

	/**
	 * 动画进入界面,并在历史栈中清除掉上一个界面
	 * 
	 */
	public void enterDCandCleanLast(BaseDC dc) {
		if (enterDC(dc)) {
			Application.appEngine.removeLastDC();
		}
	}

	/**
	 * 必须在子类中实现,可以是空实现,显示相应的界面 2010-9-27
	 * 
	 */
	public abstract void showDC();

	/**
	 * 必须在子类中实现,可以是空实现,完成初始化工作 2010-9-27
	 * 
	 */
	public abstract void initDC(Context c);

	/**
	 * 必须在子类中实现,在主线程中调用,接受消息 2010-9-29
	 * 
	 */
	protected abstract void initHandler();

	/**
	 * 如果DC中的点击事件,需要更改数据等,则直接调用此方法,应在子类中重写
	 * 
	 */
	public void onClicked(int id) {

	}

}
