package com.weiwei.base.common;
//使用自定义runable，便于扩展
public class BaseRunable implements Runnable {

	public String param;
	
	public BaseRunable()
	{
		
	}
	public BaseRunable(String param)
	{
		this.param=param;
	}
	public void run() {
		
	}
	public synchronized void baseMothed() //有资源共享时，采用同步方法
	{
		
	}
}
