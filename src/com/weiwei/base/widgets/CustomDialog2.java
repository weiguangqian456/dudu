package com.weiwei.base.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hwtx.dududh.R;

/**
 * 
 * Create custom Dialog windows for your application Custom dialogs rely on
 * custom layouts wich allow you to create and use your own look & feel.
 * 
 * Under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * @author antoine vianey
 * 
 */
public class CustomDialog2 extends Dialog {

	private int dialogId;

	public CustomDialog2(Context context, int theme) {
		super(context, theme);
	}

	public CustomDialog2(Context context) {
		super(context);
	}

	public int getDialogId() {
		return dialogId;
	}

	public void setDialogId(int dialogId) {
		this.dialogId = dialogId;
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {

		private Context context;

		// private View contentView;

		public Builder(Context context) {
			this.context = context;

		}

		private class DefaultCancleHandler implements
				DialogInterface.OnCancelListener {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
			}
		}

		/**
		 * Set the Dialog message from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(String message) {
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(int message) {
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			return this;
		}

		/**
		 * Set a custom content view for the Dialog. If a message is set, the
		 * contentView is not added to the Dialog...
		 * 
		 * @param v
		 * @return
		 */
		// public Builder setContentView(View v) {
		// this.contentView = v;
		// return this;
		// }

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			return this;
		}

		/**
		 * Set the negative button resource and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			return this;
		}

		/**
		 * Set the negative button text and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			return this;
		}

		public Builder setOnCancelListener(
				DialogInterface.OnCancelListener onCancelListener) {
			return this;
		}

		/**
		 * 不再提醒
		 */
		private ImageView dialog_chioce;
		/**
		 * TD电话
		 */
		private Button dialog_call;
		/**
		 * 邀请
		 */
		private Button dialog_inivt;
		/**
		 * 取消
		 */
		private Button dialog_cannel;
		/**
		 * 消息内容
		 */
		private TextView dialogMessage;
		/**
		 * 不再提醒选择框文字
		 */
		private TextView dialog_chioce_msg;
		/**
		 * 分隔线
		 */
		private View dialog_inivt_line;
		/**
		 * 分隔线
		 */
		private View dialog_call_line;
		/**
		 * Create the custom dialog
		 */
		@SuppressWarnings("deprecation")
		public CustomDialog2 create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog2 dialog = new CustomDialog2(context,
					R.style.registerDialogTheme);
			View layout = inflater.inflate(R.layout.vs_myself_dialog2, null);
			dialog_chioce = (ImageView) layout.findViewById(R.id.dialog_chioce);
			dialog_call = (Button) layout.findViewById(R.id.dialog_call);
			dialog_inivt = (Button) layout.findViewById(R.id.dialog_inivt);
			dialog_cannel = (Button) layout.findViewById(R.id.dialog_cannel);
			dialogMessage=(TextView) layout.findViewById(R.id.message);
			dialog_chioce_msg=(TextView) layout.findViewById(R.id.dialog_chioce_msg);
			dialog_inivt_line=layout.findViewById(R.id.dialog_inivt_line);
			dialog_call_line=layout.findViewById(R.id.dialog_call_line);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			dialog.setContentView(layout);
			return dialog;
		}



		/**
		 * 获取dialog_chioce
		 * 
		 * @return dialog_chioce
		 */

		public ImageView getDialog_chioce() {
			return dialog_chioce;
		}

		/**
		 * 设置dialog_chioce
		 * 
		 * @param dialog_chioce
		 */
		public void setDialog_chioce(ImageView dialog_chioce) {
			this.dialog_chioce = dialog_chioce;
		}

		/**
		 * 获取dialog_call
		 * 
		 * @return dialog_call
		 */

		public Button getDialog_call() {
			return dialog_call;
		}

		/**
		 * 设置dialog_call
		 * 
		 * @param dialog_call
		 */
		public void setDialog_call(Button dialog_call) {
			this.dialog_call = dialog_call;
		}

		/**
		 * 获取dialog_inivt
		 * 
		 * @return dialog_inivt
		 */

		public Button getDialog_inivt() {
			return dialog_inivt;
		}

		/**
		 * 设置dialog_inivt
		 * 
		 * @param dialog_inivt
		 */
		public void setDialog_inivt(Button dialog_inivt) {
			this.dialog_inivt = dialog_inivt;
		}

		/** 
		 * 获取dialog_cannel    
		 * @return dialog_cannel
		 */
		
		public Button getDialog_cannel() {
			return dialog_cannel;
		}

		/**  
		 * 设置dialog_cannel      
		 * @param dialog_cannel
		 */
		public void setDialog_cannel(Button dialog_cannel) {
			this.dialog_cannel = dialog_cannel;
		}

		/** 
		 * 获取dialogMessage    
		 * @return dialogMessage
		 */
		
		public TextView getDialogMessage() {
			return dialogMessage;
		}

		/**  
		 * 设置dialogMessage      
		 * @param dialogMessage
		 */
		public void setDialogMessage(TextView dialogMessage) {
			this.dialogMessage = dialogMessage;
		}

		/** 
		 * 获取dialog_chioce_msg    
		 * @return dialog_chioce_msg
		 */
		
		public TextView getDialog_chioce_msg() {
			return dialog_chioce_msg;
		}

		/**  
		 * 设置dialog_chioce_msg      
		 * @param dialog_chioce_msg
		 */
		public void setDialog_chioce_msg(TextView dialog_chioce_msg) {
			this.dialog_chioce_msg = dialog_chioce_msg;
		}

		/** 
		 * 获取dialog_inivt_line    
		 * @return dialog_inivt_line
		 */
		
		public View getDialog_inivt_line() {
			return dialog_inivt_line;
		}

		/**  
		 * 设置dialog_inivt_line      
		 * @param dialog_inivt_line
		 */
		public void setDialog_inivt_line(View dialog_inivt_line) {
			this.dialog_inivt_line = dialog_inivt_line;
		}

		/** 
		 * 获取dialog_call_line    
		 * @return dialog_call_line
		 */
		
		public View getDialog_call_line() {
			return dialog_call_line;
		}

		/**  
		 * 设置dialog_call_line      
		 * @param dialog_call_line
		 */
		public void setDialog_call_line(View dialog_call_line) {
			this.dialog_call_line = dialog_call_line;
		}
		
	}

}
