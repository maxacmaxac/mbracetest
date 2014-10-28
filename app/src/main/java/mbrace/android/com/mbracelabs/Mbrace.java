package mbrace.android.com.mbracelabs;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import mbrace.android.com.mbracelabs.utils.FileUtils;
import mbrace.android.com.mbracelabs.utils.Utils;
import mbrace.android.com.mbracelabs.utils.dbUtils.DataSource;
import mbrace.android.com.mbracelabs.manager.FlickrFactory;
import mbrace.android.com.mbracelabs.model.FlickrImage;


public class Mbrace extends Activity {

    public final String LAST_IMAGE = "lastImage";
    public UIHandler uihandler;
    public ImageAdapter imgAdapter;
    private ArrayList<FlickrImage> imageList;

    private Button downloadPhotos;
    private Gallery gallery;
    private ImageView imgView;
    private EditText editText;

    private DataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mbrace);

        datasource = new DataSource(this);
        datasource.open();

        uihandler = new UIHandler();

        downloadPhotos = (Button) findViewById(R.id.button1);
        editText = (EditText) findViewById(R.id.editText1);
        gallery = (Gallery) findViewById(R.id.gallery1);
        imgView = (ImageView) findViewById(R.id.imageView1);

        gallery.setOnItemClickListener(onThumbClickListener);
        downloadPhotos.setOnClickListener(onSearchButtonListener);

        imageList = (ArrayList<FlickrImage>) getLastNonConfigurationInstance();
        if (imageList != null) {
            imgAdapter = new ImageAdapter(getApplicationContext(), imageList);
            ArrayList<FlickrImage> ic = imgAdapter.getImageContener();
            gallery.setAdapter(imgAdapter);
            imgAdapter.notifyDataSetChanged();
            int lastImage = -1;
            if (savedInstanceState.containsKey(LAST_IMAGE)) {
                lastImage = savedInstanceState.getInt(LAST_IMAGE);
            }
            if (lastImage >= 0 && ic.size() >= lastImage) {
                gallery.setSelection(lastImage);
                Bitmap photo = ic.get(lastImage).getPhoto();
                if (photo == null)
                    new GetLargePhotoThread(ic.get(lastImage), uihandler).start();
                else
                    imgView.setImageBitmap(ic.get(lastImage).getPhoto());
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        datasource.close();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        if (imgAdapter != null)
            return this.imgAdapter.getImageContener();
        else
            return null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(LAST_IMAGE, gallery.getSelectedItemPosition());
        super.onSaveInstanceState(outState);

    }

    public class GetLargePhotoThread extends Thread {
        FlickrImage ic;
        UIHandler uih;

        public GetLargePhotoThread(FlickrImage ic, UIHandler uih) {
            this.ic = ic;
            this.uih = uih;
        }

        @Override
        public void run() {
            if (ic.getPhoto() == null) {
                ic.setPhoto(FlickrFactory.getImage(ic));
            }
            Bitmap bmp = ic.getPhoto();
            if (ic.getPhoto() != null) {
                Message msg = Message.obtain(uih, UIHandler.ID_SHOW_IMAGE);
                msg.obj = bmp;
                uih.sendMessage(msg);
            }
        }
    }

    Runnable getMetadata = new Runnable() {
        @Override
        public void run() {
            String tag = editText.getText().toString().trim();

            if(!Utils.isOnline(getApplicationContext())){//Device is not online, read from DB
                Message msg = Message.obtain(uihandler, Mbrace.UIHandler.ID_METADATA_DOWNLOADED);
                msg.obj = datasource.getAllDataByTag(tag);
                uihandler.sendMessage(msg);
            } else {
                if (tag != null && tag.length() >= 3) {
                    ArrayList<FlickrImage> images = FlickrFactory.searchImagesByTag(uihandler,
                            getApplicationContext(), tag);
                    if (images == null) {
                        showErrorMessage("Could not get data from Flickr");//TODO this need to be in Strings
                    } else {
                        //save data to db
                        //TODO I didnt handle here update, I didnt have a time for that
                        for(FlickrImage i :images) {
                            i.setSavedThumbURL(FileUtils.saveToInternalSorage(i.getThumb(),
                                    getApplicationContext(), i.getThumb().toString()));
                            i.setSavedPhotoURL(FileUtils.saveToInternalSorage(i.getThumb(),
                                    getApplicationContext(), i.getThumb().toString()));
                            datasource.insert(i);
                        }
                    }
                } else {
                    showErrorMessage("Tag must have 3 chars min");//TODO this need to be in Strings
                }
            }
        }
    };

    /**
     * show error message if there is no data, or there is no connection or bad connection
     */
    public void showErrorMessage(final String error){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), error,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * UiHandler handling data to be shown
     */
    public class UIHandler extends Handler {
        public static final int ID_METADATA_DOWNLOADED = 0;
        public static final int ID_SHOW_IMAGE = 1;
        public static final int ID_UPDATE_ADAPTER = 2;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ID_METADATA_DOWNLOADED:
                    // Set of information required to download thumbnails is
                    // available now
                    if (msg.obj != null) {
                        imageList = (ArrayList<FlickrImage>) msg.obj;
                        imgAdapter = new ImageAdapter(getApplicationContext(), imageList);
                        gallery.setAdapter(imgAdapter);
                        for (int i = 0; i < imgAdapter.getCount(); i++) {
                            new FlickrFactory.GetThumbnailsThread(uihandler, imgAdapter.getImageContener().get(i)).start();
                        }
                    }
                    break;
                case ID_SHOW_IMAGE:
                    // Display large image
                    if (msg.obj != null) {
                        imgView.setImageBitmap((Bitmap) msg.obj);
                        imgView.setVisibility(View.VISIBLE);
                    }
                    break;
                case ID_UPDATE_ADAPTER:
                    // Update adapter with thumnails
                    imgAdapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private OnItemClickListener onThumbClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            // Get large image of selected thumnail
            new GetLargePhotoThread(imageList.get(position), uihandler).start();
        }
    };

    private OnClickListener onSearchButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (gallery.getAdapter() != null) {
                imgAdapter.setImageContener(new ArrayList<FlickrImage>());
                gallery.setAdapter(imgAdapter);
                imgView.setVisibility(View.INVISIBLE);
            }
            new Thread(getMetadata).start();
        }
    };
}
