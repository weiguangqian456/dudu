package com.weiwei.base.common;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ProcessUtils {
	/**
	 * 运行***外部命令，返回状*若超过指定的超时时间，抛出TimeoutException
	 * 
	 * @param command
	 * @param timeout
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws TimeoutException
	 */
	public static int executeCommand(final String command, final long timeout) throws IOException,
			InterruptedException, TimeoutException {
		Process process = Runtime.getRuntime().exec(command);
		Worker worker = new Worker(process);
		worker.start();
		try {
			worker.join(timeout);
			if (worker.exit != null) {
				return worker.exit;
			} else {
				throw new TimeoutException();
			}
		} catch (InterruptedException ex) {
			worker.interrupt();
			Thread.currentThread().interrupt();
			throw ex;
		} finally {
			process.destroy();
		}
	}

	private static class Worker extends Thread {
		private final Process process;
		private Integer exit;

		private Worker(Process process) {
			this.process = process;
		}

		public void run() {
			try {
				exit = process.waitFor();
			} catch (InterruptedException ignore) {
				return;
			}
		}
	}
}
