package mbrace.android.com.mbracelabs.manager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class FlickrConnect {

    private static String TAG = FlickrConnect.class.getSimpleName();
	private static int CONNECT_TIMEOUT_MS = 5000;
	private static int READ_TIMEOUT_MS = 15000;

	public static ByteArrayOutputStream readBytes(String urlS) {
		ByteArrayOutputStream baos = null;
		InputStream is = null;
		HttpURLConnection httpURLConnection = null;
		try {
			URL url = new URL(urlS);
			Log.i(TAG, "Flickr url: " + url.toString());
			httpURLConnection = (HttpURLConnection) url.openConnection();
			int response = httpURLConnection.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT_MS);
				httpURLConnection.setReadTimeout(READ_TIMEOUT_MS);
				is = new BufferedInputStream(httpURLConnection.getInputStream());

				int size = 1024;
				byte[] buffer = new byte[size];

				baos = new ByteArrayOutputStream();
				int read = 0;
				while ((read = is.read(buffer)) != -1) {
					if (read > 0) {
						baos.write(buffer, 0, read);
						buffer = new byte[size];
					}

				}
			}
		} catch (IOException e) {
            Log.e(TAG, e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
		return baos;
	}
}
