package labs.course.dailyselfie;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by yuan on 8/22/15.
 */
public class SelfieCursorAdapter extends CursorAdapter {
    public SelfieCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(R.layout.list_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        File imgFile = new  File(cursor.getString(1));

        if(imgFile.exists()) {
            TextView txtViewDate = (TextView) view.findViewById(R.id.photo_date);
            txtViewDate.setText(cursor.getString(2));

            ImageView imageView = (ImageView) view.findViewById(R.id.photo);
            // set the dimensions of the View
            int targetW = 100;
            int targetH = 100;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(cursor.getString(1), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            //bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(cursor.getString(1), bmOptions);
            imageView.setImageBitmap(bitmap);
        }
    }
}
