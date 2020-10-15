package com.weiwei.base.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.os.Handler;

import com.weiwei.base.dataprovider.VsUserConfig;

/**
 * ping数据结构
 * 
 * @author dell create at 2013-5-29下午12:57:37
 */
public class PingData {
	private String url_name;// url域名
	private String url_port;// url端口号
	private int loss_chance;// 丢包率
	private float avg_time;// 平均时间
	private String url_and_port;//访问域名，有的没有port

	public PingData(String url_name, int loss_chance, float avg_time) {
		this.url_name = url_name;
		this.loss_chance = loss_chance;
		this.avg_time = avg_time;
	}

	public PingData(String url_name, String url_port, int loss_chance, float avg_time) {
		this.url_name = url_name;
		this.url_port = url_port;
		this.loss_chance = loss_chance;
		this.avg_time = avg_time;
	}
	public PingData(String url_name, int loss_chance, float avg_time,String url_and_port) {
		this.url_name = url_name;
		this.loss_chance = loss_chance;
		this.avg_time = avg_time;
		this.url_and_port=url_and_port;
	}

	public String getUrl_and_port() {
		return url_and_port;
	}
	
	public PingData() {
	}

	public String getUrl_name() {
		return url_name;
	}

	public String getUrl_port() {
		return url_port;
	}

	public int getLoss_chance() {
		return loss_chance;
	}

	public float getAvg_time() {
		return avg_time;
	}

	public static String ping(String url) {
		String retstr = "";
		Process process = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;

		try {
			process = Runtime.getRuntime().exec("ping -c 5 " + url);
			int status = process.waitFor();
			if (status == 0) {
				is = process.getInputStream();
				isr = new InputStreamReader(is);
				reader = new BufferedReader(isr);
				int i;
				char[] buffer = new char[4096];
				StringBuffer output = new StringBuffer();
				while ((i = reader.read(buffer)) > 0)
					output.append(buffer, 0, i);
				retstr = output.toString();
			} else {
				retstr = "";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
				if (isr != null) {
					isr.close();
					isr = null;
				}
				if (reader != null) {
					reader.close();
					reader = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (process != null) {
				process.destroy();
			}
		}
		return retstr;
	}

	public static class WatchThread extends Thread {
		Process p;
		boolean over;

		public WatchThread(Process p) {
			this.p = p;
			over = false;
		}

		public void run() {
			try {
				if (p == null)
					return;
				InputStream stderr = p.getInputStream();
				InputStreamReader isr = new InputStreamReader(stderr);
				BufferedReader br = new BufferedReader(isr);
				while (true) {
					if (p == null || over) {
						break;
					}
					while (br.readLine() != null)
						;
				}
				if (stderr != null) {
					stderr.close();
					stderr = null;
				}
				if (isr != null) {
					isr.close();
					isr = null;
				}
				if (br != null) {
					br.close();
					br = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void setOver(boolean over) {
			this.over = over;
		}
	}

	/**
	 * 检测网络
	 * 
	 * @param url
	 * @return
	 */
	public static boolean pingHost(String url) {
		boolean resault = false;
		CustomLog.i("VsTestAccessPointThread", "pingHost................");
		try {
			long time = System.currentTimeMillis();
			CustomLog.i("VsTestAccessPointThread", "pingHost1................");
			int status = -1;
			try {
				status = ProcessUtils.executeCommand("ping -c 3 " + url, 5000);
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
			CustomLog.i("VsTestAccessPointThread", "pingHost2..........." + (System.currentTimeMillis() - time) / 1000);
			if (status == 0)
				resault = true;
			else
				resault = false;
		} catch (IOException e) {
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		CustomLog.i("VsTestAccessPointThread", "pingHost3................");
		return resault;
	}

	public static HashMap<Double, String> privatePingHost(ArrayList<String> rArray, final long totalTime,
			final boolean isVpsPing) {
		try {
			if (rArray == null)
				return null;
			final HashMap<Double, String> voipMap = new HashMap<Double, String>();
			ThreadPoolExecutor executorPool = new ThreadPoolExecutor(15, 30, 60, TimeUnit.SECONDS,
					new ArrayBlockingQueue<Runnable>(30), new ThreadPoolExecutor.CallerRunsPolicy());
			for (int i = 0; i < rArray.size(); i++) {
				String ipAndPort = rArray.get(i);
				String httpName = "http://";
				final String currentVoip = ipAndPort;
				if (ipAndPort.contains(httpName)) {
					ipAndPort = ipAndPort.substring(ipAndPort.indexOf(httpName) + httpName.length());
				}
				int index = ipAndPort.lastIndexOf(":");
				int portDefault = 0;
				if (index > 6) {
					try {
						portDefault = Integer.valueOf(ipAndPort.substring(index + 1).replaceAll("/", ""));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					ipAndPort = ipAndPort.substring(0, index);
				}
				if (portDefault == 0)
					portDefault = 518;

				final String ip = ipAndPort.replaceAll("/", "");
				final int port = portDefault;
				executorPool.execute(new Runnable() {
					public void run() {
						int timeOut = 1000; // I recommend 3 seconds at least
						int index = 0; // 索引 ping 的总次数
						long pingTotalTime = 0;// ping成功的总时间
						long startTime = System.currentTimeMillis();// 起始时间
						int lostCount = 0;
						String vpsIpAndPort = "";
						for (; index < 999; index++) {
							if ((System.currentTimeMillis() - startTime) > totalTime) {
								break;
							}
							try {
								long time1 = System.currentTimeMillis();
								String sendStr = "ping " + index;
								byte[] sendBuf = sendStr.getBytes();
								InetAddress address = InetAddress.getByName(ip);
								vpsIpAndPort = address.getHostAddress() + ":" + port;
								DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
								DatagramSocket client = new DatagramSocket();
								client.setSoTimeout(timeOut);
								client.send(sendPacket);
								byte[] recvBuf = new byte[1024];
								DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
								client.receive(recvPacket);
								// String recvStr = new
								// String(recvPacket.getData(), 0,
								// recvPacket.getLength());
								client.close();
								pingTotalTime += System.currentTimeMillis() - time1;
							} catch (UnknownHostException e) {
								lostCount++;
								e.printStackTrace();
							} catch (IOException e) {
								lostCount++;
								e.printStackTrace();
							}
						}
						double averageTime = 1999.99;
						if (lostCount == index || index == 0) {
							// 全部丢包
							averageTime += (double) (Math.random() * 100);
						} else {
							// 计算未丢包数据平均耗时时间
							averageTime = (double) pingTotalTime / (double) (index - lostCount);
						}
						if (voipMap.containsKey(averageTime)) {
							averageTime += (double) (Math.random() * 10);
						}
						if (isVpsPing) {
							if (vpsIpAndPort != null && vpsIpAndPort.length() > 0) {
								voipMap.put(averageTime, vpsIpAndPort);
							}
						} else {
							voipMap.put(averageTime, currentVoip);
						}
					}
				});
			}
			boolean mIsSleep = true;
			while (mIsSleep) {
				try {
					Thread.sleep(100);
					if (executorPool.getActiveCount() > 0)
						mIsSleep = true;
					else
						mIsSleep = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			executorPool.shutdown();
			return voipMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 检测网络状态
	 * 
	 * @param ipAndPort
	 *            ip地址和端口
	 * @param totalTime
	 *            延时容忍时间(<=3s)
	 * @param isVpsPing
	 * @return
	 */
	public void privatePingHost(String ipAndPort, long totalTime, boolean isVpsPing, Handler mBHandler) {
		try {
			if (ipAndPort == null) {
				mBHandler.sendEmptyMessage(VsUserConfig.MSG_ID_NETWORK_BAD);
			}
			/*
			 * ThreadPoolExecutor executorPool = new ThreadPoolExecutor(15, 30, 60, TimeUnit.SECONDS, new
			 * ArrayBlockingQueue<Runnable>(30), new ThreadPoolExecutor.CallerRunsPolicy());
			 */
			String httpName = "http://";
//			String currentVoip = ipAndPort;
			if (ipAndPort.contains(httpName)) {
				ipAndPort = ipAndPort.substring(ipAndPort.indexOf(httpName) + httpName.length());
			}
			int index = ipAndPort.lastIndexOf(":");
			int portDefault = 0;
			if (index > 6) {
				try {
					portDefault = Integer.valueOf(ipAndPort.substring(index + 1).replaceAll("/", ""));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				ipAndPort = ipAndPort.substring(0, index);
			}
			if (portDefault == 0)
				portDefault = 518;

			String ip = ipAndPort.replaceAll("/", "");
			int port = portDefault;

			// 创建线程
			IpHostRunnale runnable = new IpHostRunnale(ip, port, totalTime, mBHandler);
			new Thread(runnable).start();
			/*
			 * // 线程池执行线程 executorPool.execute(runnable);
			 * 
			 * boolean mIsSleep = true; while (mIsSleep) { try { Thread.sleep(100); if (executorPool.getActiveCount() >
			 * 0) mIsSleep = true; else mIsSleep = false; } catch (InterruptedException e) { e.printStackTrace(); } }
			 * executorPool.shutdown();
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 执行网络测试线程
	 * 
	 * @author 9lz3r12
	 * 
	 */
	class IpHostRunnale implements Runnable {
		/**
		 * ip地址
		 */
		private String ip;
		/**
		 * 端口
		 */
		private int port;
		/**
		 * 测试时间
		 */
		private long totalTime;
		/**
		 * handler
		 */
		Handler handler;

		/**
		 * 构造方法
		 * 
		 * @param ip
		 * @param port
		 */
		public IpHostRunnale(String ip, int port, long totalTime, Handler handler) {
			this.ip = ip;
			this.port = port;
			this.totalTime = totalTime;
			this.handler = handler;
		}

		public void run() {
			int timeOut = 1000; // I recommend 3 seconds at least
			int index = 0; // 索引 ping 的总次数
			long pingTotalTime = 0;// ping成功的总时间
			long startTime = System.currentTimeMillis();// 起始时间
			int lostCount = 0;
//			String vpsIpAndPort = "";
			for (; index < 999; index++) {
				if ((System.currentTimeMillis() - startTime) > totalTime) {
					break;
				}
				try {
					long time1 = System.currentTimeMillis();
					String sendStr = "ping " + index;
					byte[] sendBuf = sendStr.getBytes();
					InetAddress address = InetAddress.getByName(ip);
//					vpsIpAndPort = address.getHostAddress() + ":" + port;
					DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
					DatagramSocket client = new DatagramSocket();
					client.setSoTimeout(timeOut);
					client.send(sendPacket);
					byte[] recvBuf = new byte[1024];
					DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
					client.receive(recvPacket);
					// String recvStr = new
					// String(recvPacket.getData(), 0,
					// recvPacket.getLength());
					client.close();
					pingTotalTime += System.currentTimeMillis() - time1;
				} catch (UnknownHostException e) {
					lostCount++;
					e.printStackTrace();
					System.out.println("收到出错:" + e.toString());
				} catch (IOException e) {
					lostCount++;
					e.printStackTrace();
					System.out.println("收到出错:" + e.toString());
				}
			}
			System.out.println("privatePingHost  ip = " + ip + ", currentVoip = " + ", totalTime = " + pingTotalTime
					+ ", index = " + index + ", lostCount = " + lostCount);
			double averageTime = 1999.99;
			if (lostCount == index || index == 0) {
				// 全部丢包
				averageTime += (double) (Math.random() * 100);
				handler.sendEmptyMessage(VsUserConfig.MSG_ID_NETWORK_BAD);
			} else {
				// 计算未丢包数据平均耗时时间
				averageTime = (double) pingTotalTime / (double) (index - lostCount);// 延时时间
				double averageLost = (double) (lostCount / index);
				if ((averageTime - 100 < 0) && (averageLost - 0.05 < 0)) {// 网络良好， 延时<100ms,丢包率<5%
					handler.sendEmptyMessage(VsUserConfig.MSG_ID_NETWORK_GOOD);
				} else if ((averageTime - 200 < 0) && (averageLost - 0.1 < 0)) {// 网络一般， 延时<200ms,丢包率<10%
					handler.sendEmptyMessage(VsUserConfig.MSG_ID_NETWORK_GENERA);
				} else if ((averageTime - 300 < 0) && (averageLost - 0.2 < 0)) {// 网络较差， 延时<300ms,丢包率<20%
					handler.sendEmptyMessage(VsUserConfig.MSG_ID_NETWORK_LittleBAD);
				} else {// 网络很差， 延时>300ms,丢包率>20%
					handler.sendEmptyMessage(VsUserConfig.MSG_ID_NETWORK_BAD);
				}
				// 测试用
//				 handler.sendEmptyMessage(KcUserConfig.MSG_ID_NETWORK_BAD);
			}
//			System.out.println("averageTime = " + averageTime);
			
		}
	}
}