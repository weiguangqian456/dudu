package com.weiwei.base.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.weiwei.base.common.VsUtil;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.hwtx.dududh.R;

/**
 * 
 * Create custom Dialog windows for your application Custom dialogs rely on custom layouts wich allow you to create and
 * use your own look & feel.
 * 
 * Under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * @author antoine vianey
 * 
 */
public class NoticeDialog extends Dialog {

	private int dialogId;

	public NoticeDialog(Context context, int theme) {
		super(context, theme);
	}

	public NoticeDialog(Context context) {
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
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private EditText tv;
		private String phoneNum; 
		private Boolean boolean_pass;
		 private View contentView;

		private DialogInterface.OnClickListener positiveButtonClickListener, negativeButtonClickListener;
		private DialogInterface.OnCancelListener CancelListener;

		public Builder(Context context) {
			this.context = context;
			CancelListener = new DefaultCancleHandler();
		}

		private class DefaultCancleHandler implements DialogInterface.OnCancelListener {
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
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		/**
		 * Set a custom content view for the Dialog. If a message is set, the contentView is not added to the Dialog...
		 * 
		 * @param v
		 * @return
		 */
		 public Builder setContentView(View v) {
		 this.contentView = v;
		 return this;
		 }

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the positive button text and it's listener
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button resource and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(int negativeButtonText, DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button text and it's listener
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(String negativeButtonText, DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
			this.CancelListener = onCancelListener;
			return this;
		}

		public Boolean getBoolean_pass() {
			return boolean_pass;
		}

		public void setBoolean_pass(Boolean boolean_pass) {
			this.boolean_pass = boolean_pass;
		}
		public String getPhone() {
            return phoneNum;
        }
public void setPhone(String phoneNum) {
            this.phoneNum = phoneNum;
        } 
		/**
		 * Create the custom dialog
		 */
		@SuppressWarnings("deprecation")
		public NoticeDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final NoticeDialog dialog = new NoticeDialog(context, R.style.registerDialogTheme);
			View layout = inflater.inflate(R.layout.notice_dialog, null);

			dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			 tv = ((EditText) layout.findViewById(R.id.message_1));
			 tv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				   @Override
				   public void onFocusChange(View v, boolean hasFocus) {
				       if (hasFocus) {
				            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					    //pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				       }
				   }
				});
			// set the confirm button
			
				((Button) layout.findViewById(R.id.positiveButton_1)).setText("确定");

				((Button) layout.findViewById(R.id.positiveButton_1)).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (VsUtil.isFastDoubleClick()) {
							return;
						}
							dialog.dismiss();
                        
                        message = tv.getText().toString().trim();
                        setPhone(message);
//                        String pass = VsUserConfig.getDataString(context, VsUserConfig.JKey_Password);
//                        setBoolean_pass(message.equals(pass));  
						
						if (positiveButtonClickListener != null) {
							positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
						}
					}
				});
			

			if (CancelListener != null) {
				dialog.setOnCancelListener(CancelListener);
			}
			// set the cancel button
		
				((Button) layout.findViewById(R.id.negativeButton_1)).setText("取消");
				((Button) layout.findViewById(R.id.negativeButton_1)).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (VsUtil.isFastDoubleClick()) {
							return;
						}
						dialog.dismiss();
						if (negativeButtonClickListener != null) {
							negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
						}
					}
				});
		
		
				
			
	
			dialog.setContentView(layout);
			return dialog;
		}

	}

}
