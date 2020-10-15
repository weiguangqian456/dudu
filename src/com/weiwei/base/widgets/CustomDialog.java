package com.weiwei.base.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.weiwei.base.common.VsUtil;
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
public class CustomDialog extends Dialog {

	private int dialogId;

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomDialog(Context context) {
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
		// private View contentView;

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

		/**
		 * Create the custom dialog
		 */
		@SuppressWarnings("deprecation")
		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context, R.style.registerDialogTheme);
			View layout = inflater.inflate(R.layout.vs_myself_dialog, null);

			dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			TextView titleTv=((TextView) layout.findViewById(R.id.dialog_title));
			if(title!=null){
				titleTv.setText(title);
			}else{
				titleTv.setVisibility(View.GONE);
			}
//			((ImageView) layout.findViewById(R.id.kc_register_dialog_close))
//					.setOnClickListener(new View.OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							dialog.dismiss();
//						}
//					});
			if ("温馨提醒".equals(title)) {
                ((Button) layout.findViewById(R.id.negativeButton)).setVisibility(View.GONE);
            }else {
                ((Button) layout.findViewById(R.id.negativeButton)).setVisibility(View.VISIBLE);
            }  
			// set the confirm button
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton)).setText(positiveButtonText);

				((Button) layout.findViewById(R.id.positiveButton)).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if (VsUtil.isFastDoubleClick()) {
							return;
						}
						dialog.dismiss();
						if (positiveButtonClickListener != null) {
							positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
						}
					}
				});
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.positiveButton).setVisibility(View.GONE);
			}

			if (CancelListener != null) {
				dialog.setOnCancelListener(CancelListener);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton)).setText(negativeButtonText);
				((Button) layout.findViewById(R.id.negativeButton)).setOnClickListener(new View.OnClickListener() {
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
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
			}
			// set the content message
			if (message != null) {
				TextView tv = (TextView)layout.findViewById(R.id.message);
				if (message.contains("更新")) {
					tv.setGravity(Gravity.LEFT);
				}else {
					tv.setGravity(Gravity.CENTER);
				}
				tv.setText(message);
			}
			// else if (contentView != null) {
			// // if no message set
			// // add the contentView to the dialog body
			//
			// }
			dialog.setContentView(layout);
			return dialog;
		}

	}

}
