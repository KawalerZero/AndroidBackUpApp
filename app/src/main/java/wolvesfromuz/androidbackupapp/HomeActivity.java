package wolvesfromuz.androidbackupapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * An activity to list all available demo activities.
 */
public class HomeActivity extends Activity {

    @SuppressWarnings("rawtypes")
    private final Class[] sActivities = new Class[] {
            CreateFileActivity.class,
            ListFilesActivity.class,
            RetrieveContentsWithProgressDialogActivity.class,
    };

    private ListView mListViewSamples;
    private ContentResolver contentResolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        contentResolver = getContentResolver();

        String[] titles = getResources().getStringArray(R.array.titles_array);
        mListViewSamples = (ListView) findViewById(R.id.listViewSamples);
        mListViewSamples.setAdapter(
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles));
        mListViewSamples.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int i, long arg3) {
                Intent intent = new Intent(getBaseContext(), sActivities[i]);

                startActivity(intent);
            }
        });
    }
}