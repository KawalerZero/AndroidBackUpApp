package wolvesfromuz.androidbackupapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * An activity to list all available demo activities.
 */
public class HomeActivity extends Activity  {

    private Button createBackupButton;
    private Button listOfBackupButton;
    private Button uploadBackupButton;

    private ImageView createBackupImage;
    private ImageView listOfBackupsImage;
    private ImageView uploadBackupImage;
    private ContentResolver contentResolver;
    @Override
    protected void onResume()
    {
        super.onResume();
        createBackupImage.setImageResource(R.drawable.create_backup);
        listOfBackupsImage.setImageResource(R.drawable.list_of_buckups);
        uploadBackupImage.setImageResource(R.drawable.upload_buckup);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        createBackupButton = (Button) findViewById(R.id.create_backup_btn);
        listOfBackupButton = (Button) findViewById(R.id.list_of_backups_btn);
        uploadBackupButton = (Button) findViewById(R.id.upload_backup_btn);

        createBackupImage = (ImageView) findViewById(R.id.create_backup);
        listOfBackupsImage = (ImageView) findViewById(R.id.list_of_backups);
        uploadBackupImage = (ImageView) findViewById(R.id.upload_backup);

        createBackupButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view)
            {
                createBackupImage.setImageResource(R.drawable.create_backup_enter);
                Intent intent = new Intent(getBaseContext(), CreateFileActivity.class);
                startActivity(intent);
            }
        });

        listOfBackupButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view)
            {
                listOfBackupsImage.setImageResource(R.drawable.list_of_buckups_enter);
                Intent intent = new Intent(getBaseContext(), ListFilesActivity.class);
                startActivity(intent);
            }
        });

        uploadBackupButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view)
            {
                uploadBackupImage.setImageResource(R.drawable.upload_buckup_enter);
                Intent intent = new Intent(getBaseContext(), RetrieveContentsWithProgressDialogActivity.class);
                startActivity(intent);
            }
        });
    }
}
