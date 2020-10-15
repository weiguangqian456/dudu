package com.zbar.lib.decode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * 作者: 陈涛(1076559197@qq.com)
 * 
 * 时间: 2014年5月9日 下午12:25:12
 *
 * 版本: V_1.0.0
 * 
 */
public final class InactivityTimer {

	private static final int INACTIVITY_DELAY_SECONDS = 5 * 60;
	private boolean registered;
	private final ScheduledExecutorService inactivityTimer = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
	private final Activity activity;
	private final BroadcastReceiver powerStatusReceiver;
	private ScheduledFuture<?> inactivityFuture = null;

	public InactivityTimer(Activity activity) {
		this.activity = activity;
		powerStatusReceiver = new PowerStatusReceiver();
		registered = false;
		onActivity();
	}

	public void onActivity() {
		cancel();
		inactivityFuture = inactivityTimer.schedule(new FinishListener(activity), INACTIVITY_DELAY_SECONDS, TimeUnit.SECONDS);
	}

	private void cancel() {
		if (inactivityFuture != null) {
			inactivityFuture.cancel(true);
			inactivityFuture = null;
		}
	}

	public void shutdown() {
		cancel();
		inactivityTimer.shutdown();
	}

	private static final class DaemonThreadFactory implements ThreadFactory {
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable);
			thread.setDaemon(true);
			return thread;
		}
	}

	
	public synchronized void onPause() {
	    cancel();
	    if (registered) {
	      activity.unregisterReceiver(powerStatusReceiver);
	      registered = false;
	    } else {
	    
	    }
	  }

	  public synchronized void onResume() {
	    if (registered) {
	    
	    } else {
	      activity.registerReceiver(powerStatusReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	      registered = true;
	    }
	    onActivity();
	  }
	  
	  private final class PowerStatusReceiver extends BroadcastReceiver {
		    @Override
		    public void onReceive(Context context, Intent intent){
		      if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
		        // 0 indicates that we're on battery
		        boolean onBatteryNow = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) <= 0;
		        if (onBatteryNow) {
		          InactivityTimer.this.onActivity();
		        } else {
		          InactivityTimer.this.cancel();
		        }
		      }
		    }
	  }
}
