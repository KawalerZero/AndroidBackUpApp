package wolvesfromuz.androidbackupapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFile.DownloadProgressListener;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * An activity to illustrate how to open contents and listen
 * the download progress if the file is not already sync'ed.
 */
public class RetrieveContentsWithProgressDialogActivity extends BaseDemoActivity {

    private static final String TAG = "RetrieveFileWithProgressDialogActivity";

    /**
     * Request code to handle the result from file opening activity.
     */
    private static final int REQUEST_CODE_OPENER = 1;

    /**
     * Progress bar to show the current download progress of the file.
     */
    private ImageView image;
    private Animation rotateAnimation;

    /**
     * File that is selected with the open file activity.
     */
    private DriveId mSelectedFileDriveId;
    private ContactsManager contactsManager;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_progress);
        image = (ImageView) findViewById(R.id.uploading_image);
        rotateAnimation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        contactsManager = new ContactsManager();
        ContentResolver contentResolver = getContentResolver();
        contactsManager.setCursor(contentResolver);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        // If there is a selected file, open its contents.
        if (mSelectedFileDriveId != null) {
            open();
            return;
        }
//let the user decide
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{"text/plain"})
                .build(getGoogleApiClient());
        try {
            startIntentSenderForResult(intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
        } catch (SendIntentException e) {
            Log.w(TAG, "Unable to send intent", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_OPENER && resultCode == RESULT_OK) {
            mSelectedFileDriveId = (DriveId) data.getParcelableExtra(
                    OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void open() {
        // Reset progress dialog back to zero as we're
        // initiating an opening request.
        DownloadProgressListener listener = new DownloadProgressListener() {
            @Override
            public void onProgress(long bytesDownloaded, long bytesExpected) {
                // Update progress dialog with the latest progress.
                int progress = (int)(bytesDownloaded*100/bytesExpected);
                Log.d(TAG, String.format("Loading progress: %d percent", progress));

                image.startAnimation(rotateAnimation);
                rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        finish();
                        Intent intent = new Intent(RetrieveContentsWithProgressDialogActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        };
        DriveFile driveFile =  mSelectedFileDriveId.asDriveFile();
        driveFile.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, listener).setResultCallback(driveContentsCallback);
        mSelectedFileDriveId = null;
    }

    private ResultCallback<DriveContentsResult> driveContentsCallback = new ResultCallback<DriveContentsResult>() {
                @Override
                public void onResult(DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while opening the file contents");
                        return;
                    }
                    DriveContents contents = result.getDriveContents();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(contents.getInputStream(), Charset.forName("UTF-8")));
                    StringBuilder builder = new StringBuilder();
                    String line;

                    try {
                        while((line =reader.readLine())!=null)
                        {
                            builder.append(line);
                        }

                        JSONArray json = new JSONArray(builder.toString());
                        contactsManager.deleteContacts();
                        contactsManager.saveContacts(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    showMessage("File contents opened");
                }
            };
}
