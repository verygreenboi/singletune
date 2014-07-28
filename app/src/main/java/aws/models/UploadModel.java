package aws.models;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;
import com.amazonaws.services.s3.transfer.PersistableUpload;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.exception.PauseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import aws.Constants;
import aws.Util;

/**
 * Created by mrsmith on 7/28/14.
 */
public class UploadModel extends TransferModel {
    private static final String TAG = "UploadModel";

    private Upload mUpload;
    private PersistableUpload mPersistableUpload;
    private ProgressListener mListener;
    private Status mStatus;
    private File mFile;
    private String mExtension;

    public UploadModel(Context context, Uri uri, TransferManager manager) {
        super(context, uri, manager);
        mStatus = Status.IN_PROGRESS;
        mExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(
                context.getContentResolver().getType(uri));;
        mListener = new ProgressListener() {
            @Override
            public void progressChanged(ProgressEvent event) {
                if(event.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) {
                    mStatus = Status.COMPLETED;
                    if(mFile != null) {
                        mFile.delete();
                    }
                }
            }
        };
    }

    public Runnable getUploadRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                upload();
            }
        };
    }

    @Override
    public void abort() {
        if(mUpload != null) {
            mStatus = Status.CANCELED;
            mUpload.abort();
            if(mFile != null) {
                mFile.delete();
            }
        }
    }

    @Override
    public Status getStatus() { return mStatus; }
    @Override
    public Transfer getTransfer() { return mUpload; }

    @Override
    public void pause() {
        if(mStatus == Status.IN_PROGRESS) {
            if(mUpload != null) {
                mStatus = Status.PAUSED;
                try {
                    mPersistableUpload = mUpload.pause();
                } catch(PauseException e) {
                    Log.d(TAG, "", e);
                }
            }
        }
    }
    @Override
    public void resume() {
        if(mStatus == Status.PAUSED) {
            mStatus = Status.IN_PROGRESS;
            if(mPersistableUpload != null) {
                //if it paused fine, resume
                mUpload = getTransferManager().resumeUpload(mPersistableUpload);
                mUpload.addProgressListener(mListener);
                mPersistableUpload = null;
            } else {
                //if it was actually aborted, start a new one
                upload();
            }
        }
    }
    public void upload() {
        if(mFile == null) {
            saveTempFile();
        }
        if(mFile != null) {
            try {
                mUpload = getTransferManager().upload(
                        Constants.BUCKET_NAME,
                        Util.getPrefix(getContext()) + super.getFileName() + "."
                                + mExtension,
                        mFile);
                mUpload.addProgressListener(mListener);
            } catch(Exception e) {
                Log.e(TAG, "", e);
            }
        }
    }

    private void saveTempFile() {
        ContentResolver resolver = getContext().getContentResolver();
        InputStream in = null;
        FileOutputStream out = null;

        try {
            in = resolver.openInputStream(getUri());
            mFile = File.createTempFile(
                    "s3_demo_file_" + getId(),
                    mExtension,
                    getContext().getCacheDir());
            out = new FileOutputStream(mFile, false);
            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch(IOException e) {
            Log.e(TAG, "", e);
        } finally {
            if(in != null) {
                try { in.close(); } catch(IOException e) { Log.e(TAG, "", e); }
            }
            if(out != null) {
                try { out.close(); } catch(IOException e) { Log.e(TAG, "", e); }
            }
        }
    }
}
