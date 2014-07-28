package aws.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.PersistableDownload;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.exception.PauseException;

import java.io.File;
import java.io.IOException;

import aws.Constants;

/**
 * Created by mrsmith on 7/28/14.
 */
public class DownloadModel extends TransferModel {
    private static final String TAG = "DownloadModel";

    private Download mDownload;
    private PersistableDownload mPersistableDownload;
    private ProgressListener mListener;
    private String mKey;
    private Status mStatus;
    private Uri mUri;

    public DownloadModel(Context context, String key, TransferManager manager) {
        super(context, Uri.parse(key),  manager);
        mKey = key;
        mStatus = Status.IN_PROGRESS;
        mListener = new ProgressListener() {
            @Override
            public void progressChanged(ProgressEvent event) {
                if(event.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) {

                    Intent mediaScanIntent = new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(mUri);
                    getContext().sendBroadcast(mediaScanIntent);

                    mStatus = Status.COMPLETED;

                }
            }
        };
    }

    @Override
    public Status getStatus() { return mStatus; }

    @Override
    public Transfer getTransfer() { return mDownload; }

    @Override
    public Uri getUri() { return mUri; }

    @Override
    public void abort() {
        if(mDownload != null) {
            mStatus = Status.CANCELED;
            try {
                mDownload.abort();
            } catch(IOException e) {
                Log.e(TAG, "", e);
            }
        }
    }
    public void download() {
        mStatus = Status.IN_PROGRESS;
        File file = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES),
                getFileName());
        mUri = Uri.fromFile(file);

        mDownload = getTransferManager().download(
                Constants.BUCKET_NAME, mKey, file);
        if(mListener != null) {
            mDownload.addProgressListener(mListener);
        }
    }

    @Override
    public void pause() {
        if(mStatus == Status.IN_PROGRESS) {
            mStatus = Status.PAUSED;
            try {
                mPersistableDownload = mDownload.pause();
            } catch(PauseException e) {
                Log.d(TAG, "", e);
            }
        }
    }
    @Override
    public void resume() {
        if(mStatus == Status.PAUSED) {
            mStatus = Status.IN_PROGRESS;
            if(mPersistableDownload != null) {
                mDownload = getTransferManager().resumeDownload(
                        mPersistableDownload);
                mDownload.addProgressListener(mListener);
                mPersistableDownload = null;
            } else {
                download();
            }
        }
    }

}
