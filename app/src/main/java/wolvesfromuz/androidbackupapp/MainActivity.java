package wolvesfromuz.androidbackupapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //todo Need to call some other Activity which will provide the choice of createBackUp or UpdateBackUp
        Intent intent = new Intent(getBaseContext(), CreateFileActivity.class);
        startActivity(intent);

    }
}
