package com.zte.common;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

import android.util.Log;

public class ShellUtil {

	private final static String TAG = ShellUtil.class.getSimpleName();

	public static String adbshellexecute(final String cmmand, int timeout) throws TimeoutException {

		ProcessWork work = new ProcessWork(new ProcessCall() {
			
			@Override
			public String HandleProcess() {
				// TODO Auto-generated method stub
				String result = null;
				InputStream is = null;
				InputStreamReader isr = null;
				BufferedReader br = null;
				Process process = null;
				DataOutputStream dos = null;
				StringBuffer buffer = null;
				try {
					process = Runtime.getRuntime().exec(cmmand);
					dos = new DataOutputStream(process.getOutputStream());
					dos.flush();
					is = process.getInputStream();
					isr = new InputStreamReader(is);
					br = new BufferedReader(isr);

					buffer = new StringBuffer();
					String line = null;
					while ((line = br.readLine()) != null) {
						buffer.append(line);
						buffer.append("\n");
						Log.i(TAG, "result= " + line);
					}
					process.waitFor();
					result = buffer.toString();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (is != null) {
							is.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (isr != null) {
						try {
							isr.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (dos != null) {
						try {
							dos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// this.onDestroy();
				}
				return result;
			}
		});

		work.start();
		try {
			work.join(timeout);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (work.getExit() != 0) {
			throw new TimeoutException();
		}
		return work.getResult();
	}

	public static String adbshellexecute(String cmmand) throws TimeoutException {
		return adbshellexecute(cmmand, 0);
	}

	/**
	 * @Description: 
	 *               两种方式，Runtime可以执行，而ProcessBuilder设置工作目录无法执行命令，抛出异常，未知原因。
	 * @param cmmand
	 * @param env
	 * @param directory
	 * @return
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public static String adbshellexecute(final String cmmand, final String[] env, final String directory, int timeout) throws TimeoutException {

		ProcessWork work = new ProcessWork(new ProcessCall() {

			@Override
			public String HandleProcess() {
				// TODO Auto-generated method stub
				String result = null;
				InputStream is = null;
				InputStreamReader isr = null;
				BufferedReader br = null;
				Process process = null;
				DataOutputStream dos = null;
				StringBuffer buffer = null;
				try {
					process = null;
					if (directory != null) {
						process = Runtime.getRuntime().exec(cmmand, env, new File(directory));
					} else {
						process = Runtime.getRuntime().exec(cmmand, env);
					}
					dos = new DataOutputStream(process.getOutputStream());
					dos.flush();
					is = process.getInputStream();
					isr = new InputStreamReader(is);
					br = new BufferedReader(isr);

					buffer = new StringBuffer();
					String line = null;
					while ((line = br.readLine()) != null) {
						buffer.append(line);
						Log.i(TAG, "line= " + line);
					}
					process.waitFor();
					result = buffer.toString();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (is != null) {
							is.close();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (isr != null) {
						try {
							isr.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (dos != null) {
						try {
							dos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// this.onDestroy();
				}
				return result;
			}

		});
		work.start();
		try {
			work.join(timeout);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (work.getExit() != 0) {
			throw new TimeoutException();
		}
		return work.getResult();
	}

	public static String adbshellexecute(final String cmmand, final String[] env, final String directory) throws TimeoutException {
		return adbshellexecute(cmmand, env, directory, 0);
	}

	/**
	 * @Description: eg: adbshellexecute("su", "echo adb");
	 * @param cmd
	 * @param wrCmd
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String adbshellexecute(final String cmd, final String wrCmd, int timeout) throws TimeoutException {
		Log.i(TAG, "adbshellexecute cmd wrCmd =" + cmd + wrCmd);

		ProcessWork work = new ProcessWork(new ProcessCall() {

			@Override
			public String HandleProcess() {
				// TODO Auto-generated method stub
				DataOutputStream dos = null;
				InputStream is = null;
				InputStreamReader isr = null;
				BufferedReader br = null;
				String result = null;
				Process process = null;
				StringBuffer buffer = null;
				try {
					process = Runtime.getRuntime().exec(cmd);
					dos = new DataOutputStream(process.getOutputStream());
					dos.writeBytes(wrCmd + "\n");
					dos.flush();
					dos.writeBytes("exit\n");
					dos.flush();

					is = process.getInputStream();
					isr = new InputStreamReader(is);
					br = new BufferedReader(isr);
					buffer = new StringBuffer();
					String line = null;
					while ((line = br.readLine()) != null) {
						buffer.append(line);
						Log.i(TAG, "line= " + line);
					}
					result = buffer.toString();
					process.waitFor();
				} catch (Exception ex) {
					Log.i(TAG, "adbshellexecute ex =" + ex.getMessage());
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (dos != null) {
						try {
							dos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (isr != null) {
						try {
							isr.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// this.onDestroy();
				}
				return result;
			}

		});
		work.start();
		try {
			work.join(timeout);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (work.getExit() != 0) {
			throw new TimeoutException();
		}
		return work.getResult();
	}

	public static String adbshellexecute(final String cmd, final String wrCmd) throws TimeoutException {
		return adbshellexecute(cmd, wrCmd, 0);
	}

	public static interface ProcessCall {
		public String HandleProcess();
	};

	public static class ProcessWork extends Thread {

		private String result = null;
		private ProcessCall call = null;
		private int exit;

		public ProcessWork(ProcessCall call) {
			this.call = call;
			this.exit = -1;
		}

		public int getExit() {
			return exit;
		}

		public String getResult() {
			return result;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			result = call.HandleProcess();
			this.exit = 0;
		}

	}
}
