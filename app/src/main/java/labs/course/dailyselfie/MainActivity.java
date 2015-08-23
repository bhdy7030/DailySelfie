package labs.course.dailyselfie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity {
    static final String TAG = "course.labs.dailyselfie";
    public  static final String PHOTO_PATH = "labs.course.dailyselfie.PHOTO_PATH";
    private static final long TWO_MINUTES = 2 * 60 * 1000L;
    private static final long INITIAL_ALARM_DELAY = 60 * 1000L;

    static final int REQUEST_TAKE_SELFIE = 1;
    static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    String mCurrentPhotoPath;
    String mCurrentPhotoTimeStamp;
    SelfieCursorAdapter adapter;
    ListView listView;
    DbOpenHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);

        mDbHelper = new DbOpenHelper(this);
        initAlarm();
        //init adapter
        initAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                        {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                Cursor cursor = getPhotoCursor();
                                                cursor.moveToPosition(position);
                                                //read path of the photo from the cursor._ID, PHOTO_PATH,PHOTO_DATE
                                                String path = cursor.getString(1);

                                                Intent intent = new Intent(MainActivity.this,ViewSelfieActivity.class);
                                                intent.putExtra(PHOTO_PATH,path);
                                                startActivity(intent);

                                            }
                                        }


        );


    }


    private void initAdapter(){
        Cursor cursor = getPhotoCursor();
        adapter = new SelfieCursorAdapter(this,cursor,0);
    }

    //query SqlLite.
    private Cursor getPhotoCursor(){
        return mDbHelper.getWritableDatabase().query(DbOpenHelper.TABLE_NAME,
                DbOpenHelper.columns, null, new String[]{}, null, null, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera_action , menu);
        return super.onCreateOptionsMenu(menu);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        // Handle presses on the action bar items
        int id = item.getItemId();
        if (id == R.id.action_camera) {
            takePicture();
        }
        return super.onOptionsItemSelected(item);


    }





    private File createImageFile() throws IOException {
        // Create an image file name


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        // Make sure the directory exists
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).mkdirs();

        File image = File.createTempFile(
                imageFileName
                , JPEG_FILE_SUFFIX
                , Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        mCurrentPhotoTimeStamp = timeStamp;
        return image;
    }



    private void takePicture() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
                File f = null;

            File selfieFile = null;

            try {
                selfieFile = createImageFile();
            } catch (IOException e) {
                Log.e(TAG, "Error creating selfie file", e);
            }

            if (selfieFile != null) {

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(selfieFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_SELFIE);
            }


        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_TAKE_SELFIE) {
            if(resultCode == RESULT_OK) {
                if(mCurrentPhotoPath!=null && !mCurrentPhotoPath.isEmpty()) {
                    ContentValues values = new ContentValues();
                    values.put(DbOpenHelper.PHOTO_PATH, mCurrentPhotoPath);
                    values.put(DbOpenHelper.PHOTO_DATE, mCurrentPhotoTimeStamp);
                    mDbHelper.getWritableDatabase().insert(DbOpenHelper.TABLE_NAME, null, values);

                    initAdapter();
                    adapter.notifyDataSetInvalidated();
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                    listView.postInvalidate();
                }
            }
            else {
                Log.i(TAG, "Error, result code not ok: " + mCurrentPhotoPath);
            }
        }
        else {
            Log.i(TAG,"Error, request code not selfie: " + mCurrentPhotoPath);
        }
    }


    private void initAlarm() {

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent mNotificationReceiverIntent = new Intent(MainActivity.this, AlarmNotificationReceiver.class);
        PendingIntent mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, mNotificationReceiverIntent, 0);


        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
                TWO_MINUTES,
                mNotificationReceiverPendingIntent);
    }


}
