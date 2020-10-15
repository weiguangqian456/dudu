package com.weiwei.base.bakcontact;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;

import com.weiwei.base.common.CustomLog;

/**
 * This class handles execution of batch mOperations on Contacts provider. xkl
 */
public class VsBatchOperation {
	private final String TAG = "BatchOperation";

	private final ContentResolver mResolver;
	// List for storing the batch mOperations
	ArrayList<ContentProviderOperation> mOperations;

	public VsBatchOperation(Context context, ContentResolver resolver) {
		mResolver = resolver;
		mOperations = new ArrayList<ContentProviderOperation>();
	}

	public int size() {
		return mOperations.size();
	}

	public void add(ContentProviderOperation cpo) {
		mOperations.add(cpo);
	}

	public void execute() {
		if (mOperations.size() == 0) {
			return;
		}
		// Apply the mOperations to the content provider
		try {
			mResolver.applyBatch(ContactsContract.AUTHORITY, mOperations);
		} catch (final OperationApplicationException e1) {
			CustomLog.i(TAG, "storing contact data failed", e1);
		} catch (final RemoteException e2) {
			CustomLog.i(TAG, "storing contact data failed", e2);
		}
		CustomLog.e(TAG, "storing contact data success");
		mOperations.clear();
	}

}
