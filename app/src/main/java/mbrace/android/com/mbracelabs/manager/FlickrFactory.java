package mbrace.android.com.mbracelabs.manager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;

import mbrace.android.com.mbracelabs.Mbrace;
import mbrace.android.com.mbracelabs.utils.Utils;
import mbrace.android.com.mbracelabs.model.FlickrImage;

public class FlickrFactory {


    private static final String TAG = FlickrFactory.class.getSimpleName();
	private static final String FLICKR_BASE_URL = "http://api.flickr.com/services/rest/?method=";
	private static final String FLICKR_PHOTOS_SEARCH_STRING = "flickr.photos.search";
	private static final String FLICKR_GET_SIZES_STRING = "flickr.photos.getSizes";
	private static final int FLICKR_PHOTOS_SEARCH_ID = 1;
	private static final int FLICKR_GET_SIZES_ID = 2;
	private static final int NUMBER_OF_PHOTOS = 20;
	
	//Demn this key, I spent long time to get it
	private static final String APIKEY_SEARCH_STRING = "&api_key=83e46991df560240c4b49c46e8cfb565";
	
	private static final String TAGS_STRING = "&tags=";
	private static final String PHOTO_ID_STRING = "&photo_id=";
	private static final String FORMAT_STRING = "&format=json";
	public static final int PHOTO_THUMB = 111;
	public static final int PHOTO_LARGE = 222;

	public static Mbrace.UIHandler uihandler;

	private static String createURL(int methodId, String parameter) {
		String method_type = "";
		String url = null;
		switch (methodId) {
		case FLICKR_PHOTOS_SEARCH_ID:
			method_type = FLICKR_PHOTOS_SEARCH_STRING;
			url = FLICKR_BASE_URL + method_type + APIKEY_SEARCH_STRING + TAGS_STRING + parameter
                    + FORMAT_STRING + "&per_page="+NUMBER_OF_PHOTOS+"&media=photos";
			break;
		case FLICKR_GET_SIZES_ID:
			method_type = FLICKR_GET_SIZES_STRING;
			url = FLICKR_BASE_URL + method_type + PHOTO_ID_STRING + parameter
                    + APIKEY_SEARCH_STRING + FORMAT_STRING;
			break;
		}
		return url;
	}

	public static void getImageURLS(FlickrImage imgCon) {
		String url = createURL(FLICKR_GET_SIZES_ID, imgCon.getServerId());
		ByteArrayOutputStream baos = FlickrConnect.readBytes(url);
		String json = baos.toString();
		try {
			JSONObject root = new JSONObject(json.replace("jsonFlickrApi(", "").replace(")", ""));
			JSONObject sizes = root.getJSONObject("sizes");
			JSONArray size = sizes.getJSONArray("size");
			for (int i = 0; i < size.length(); i++) {
				JSONObject image = size.getJSONObject(i);
				if (image.getString("label").equals("Square")) {
					imgCon.setThumbURL(image.getString("source"));
				} else if (image.getString("label").equals("Medium")) {
					imgCon.setLargeURL(image.getString("source"));
				}
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
	}

	public static Bitmap getImage(FlickrImage imgCon) {
		Bitmap bm = null;
		try {
			URL aURL = new URL(imgCon.getLargeURL());
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		return bm;
	}

	public static void getThumbnails(ArrayList<FlickrImage> imgCon, Mbrace.UIHandler uih) {
		for (int i = 0; i < imgCon.size(); i++)
			new GetThumbnailsThread(uih, imgCon.get(i)).start();
	}

	public static Bitmap getThumbnail(FlickrImage imgCon) {
		Bitmap bm = null;
		try {
			URL aURL = new URL(imgCon.getThumbURL());
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		return bm;
	}

	public static class GetThumbnailsThread extends Thread {
		Mbrace.UIHandler uih;
		FlickrImage imgContener;

		public GetThumbnailsThread(Mbrace.UIHandler uih, FlickrImage imgCon) {
			this.uih = uih;
			this.imgContener = imgCon;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			imgContener.setThumb(getThumbnail(imgContener));
			if (imgContener.getThumb() != null) {
				Message msg = Message.obtain(uih, Mbrace.UIHandler.ID_UPDATE_ADAPTER);
				uih.sendMessage(msg);

			}
		}

	}

	public static ArrayList<FlickrImage> searchImagesByTag(Mbrace.UIHandler uih,
                                                             Context ctx, String tag) {
		uihandler = uih;
		String url = createURL(FLICKR_PHOTOS_SEARCH_ID, tag);
		ArrayList<FlickrImage> tmp = new ArrayList<FlickrImage>();
		String jsonString = null;
        try {
            if (Utils.isOnline(ctx)) {
                ByteArrayOutputStream baos = FlickrConnect.readBytes(url);
                if (baos == null) {
                    return null;
                }
                jsonString = baos.toString();
            }

            JSONObject root = new JSONObject(
                    jsonString.replace("jsonFlickrApi(", "").replace(")", ""));
            JSONObject photos = root.getJSONObject("photos");
            JSONArray imageJSONArray = photos.getJSONArray("photo");
            for (int i = 0; i < imageJSONArray.length(); i++) {
                JSONObject item = imageJSONArray.getJSONObject(i);
                FlickrImage imgCon = new FlickrImage(item.getString("id"),
                        item.getString("owner"), item.getString("secret"),
                        item.getString("server"),
                        item.getString("farm"));
                imgCon.setPosition(i);
                tmp.add(imgCon);
            }
            Message msg = Message.obtain(uih, Mbrace.UIHandler.ID_METADATA_DOWNLOADED);
            msg.obj = tmp;
            uih.sendMessage(msg);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return tmp;
    }

}
