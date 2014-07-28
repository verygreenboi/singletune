package aws.models;

import android.content.Context;
import android.net.Uri;

import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;

import java.util.LinkedHashMap;

import aws.Util;

/**
 * Created by mrsmith on 7/28/14.
 */
public abstract class TransferModel {
    private static final String TAG = "TransferModel";
        private static LinkedHashMap<Integer, TransferModel> sModels =
            new LinkedHashMap<Integer, TransferModel>();;//all TransferModels have associated id which is their key to sModels
    private static int sNextId = 1;
    private String mFileName;
    private Context mContext;
    private Uri mUri;
    private int mId;
    private TransferManager mManager;
    public TransferModel(Context context, Uri uri, TransferManager manager) {
        mContext = context;
        mUri = uri;
        mManager = manager;
        String uriString = uri.toString();
        mFileName = Util.getFileName(uriString);
        mId = sNextId++;
        sModels.put(mId, this);
    }

    public static TransferModel getTransferModel(int id) {
        return sModels.get(id);
    }

    public static TransferModel[] getAllTransfers() {
        TransferModel[] models = new TransferModel[sModels.size()];
        return sModels.values().toArray(models);
    }

    public String getFileName() { return mFileName; }

    public int getId() { return mId; }

    public int getProgress() {
        Transfer transfer = getTransfer();
        if(transfer != null) {
            int ret = (int)transfer.getProgress().getPercentTransferred();
            return ret;
        }
        return 0;
    }

    public Uri getUri() { return mUri; }

    public abstract void abort();

    public abstract Status getStatus();

    public abstract Transfer getTransfer();

    public abstract void pause();

    public abstract void resume();

    protected Context getContext() { return mContext; }

    protected TransferManager getTransferManager() { return mManager; }

public static enum Status { IN_PROGRESS, PAUSED, CANCELED, COMPLETED }

}
