package com.pixel.singletune.app.helpers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.pixel.singletune.app.ParseConstants;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mrsmith on 5/28/14.
 */
public class FileHelper {
    public static final String TAG = FileHelper.class.getSimpleName();

    public static final int SHORT_SIDE_TARGET = 1280;

    public static byte[] getByteArrayFromFile(Context context, Uri uri){

        byte[] fileBytes = null;
        InputStream inStream = null;
        ByteArrayOutputStream outStream = null;

        if (uri.getScheme().equals("content")) {
            try {
                inStream = context.getContentResolver().openInputStream(uri);
                outStream = new ByteArrayOutputStream();

                byte[] bytesFromFile = new byte[1024*1024]; // buffer size (1 MB)
                int bytesRead = inStream.read(bytesFromFile);
                while (bytesRead != -1) {
                    outStream.write(bytesFromFile, 0, bytesRead);
                    bytesRead = inStream.read(bytesFromFile);
                }

                fileBytes = outStream.toByteArray();
            }
            catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            finally {
                try {
                    inStream.close();
                    outStream.close();
                }
                catch (IOException e) { /*( Intentionally blank */ }
            }
        }
        else {
            try {
                File file = new File(uri.getPath());
                FileInputStream fileInput = new FileInputStream(file);
                fileBytes = IOUtils.toByteArray(fileInput);
            }
            catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        return fileBytes;
    }
    public static String getFileName(Context context, Uri uri, String fileType) {
        String fileName = "uploaded_file.";

        if (fileType.equals(ParseConstants.TYPE_IMAGE)) {
            fileName += "png";
        }
        else if(fileType.equals(ParseConstants.TYPE_MP3)){
            fileName+= "mp3";
        }
        else {
            // For Audio, we want to get the actual file extension
            if (uri.getScheme().equals("content")) {
                // do it using the mime type
                String mimeType = context.getContentResolver().getType(uri);
                int slashIndex = mimeType.indexOf("/");
                String fileExtension = mimeType.substring(slashIndex + 1);
                fileName += fileExtension;
            }
            else {
                fileName = uri.getLastPathSegment();
            }
        }

        return fileName;
    }
}
