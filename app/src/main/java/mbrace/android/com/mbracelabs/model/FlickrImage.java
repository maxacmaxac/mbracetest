package mbrace.android.com.mbracelabs.model;

import android.graphics.Bitmap;

import mbrace.android.com.mbracelabs.Mbrace;
import mbrace.android.com.mbracelabs.manager.FlickrFactory;


public class FlickrImage {
    private int id;
	private String serverId;
    private int position;
    private String thumbURL;
    private Bitmap thumb;
    private Bitmap photo;
    private String largeURL;
    private String owner;
    private String secret;
    private String server;
    private String farm;
    private String TAG;

    public String getSavedPhotoURL() {
        return savedPhotoURL;
    }

    public void setSavedPhotoURL(String savedPhotoURL) {
        this.savedPhotoURL = savedPhotoURL;
    }

    private String savedPhotoURL;
    private String savedThumbURL;

    public FlickrImage(){

    }

	public FlickrImage(String serverId, String thumbURL, String largeURL, String owner, String secret,
                       String server, String farm) {
		super();
		this.serverId = serverId;
		this.owner = owner;
		this.secret = secret;
		this.server = server;
		this.farm = farm;
	}

	public FlickrImage(String serverId, String owner, String secret, String server, String farm) {
		super();
		this.serverId = serverId;
		this.owner = owner;
		this.secret = secret;
		this.server = server;
		this.farm = farm;
		setThumbURL(createPhotoURL(FlickrFactory.PHOTO_THUMB, this));
		setLargeURL(createPhotoURL(FlickrFactory.PHOTO_LARGE, this));
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

	public String getThumbURL() {
		return thumbURL;
	}

	public void setThumbURL(String thumbURL) {
		this.thumbURL = thumbURL;
		onSaveThumbURL(FlickrFactory.uihandler, this);
	}

	public String getLargeURL() {
		return largeURL;
	}

	public void setLargeURL(String largeURL) {
		this.largeURL = largeURL;
	}

	@Override
	public String toString() {
		return "FlickrImage [serverId=" + serverId + ", thumbURL=" + thumbURL + ", largeURL="
                + largeURL
                + ", owner=" + owner + ", secret=" + secret + ", server=" + server + ", farm="
				+ farm + "]";
	}

	private String createPhotoURL(int photoType, FlickrImage imgCon) {
		String tmp = null;
		tmp = "http://farm" + imgCon.farm + ".staticflickr.com/" + imgCon.server
                + "/" + imgCon.serverId + "_" + imgCon.secret;// +".jpg";
		switch (photoType) {
		case FlickrFactory.PHOTO_THUMB:
			tmp += "_t";
			break;
		case FlickrFactory.PHOTO_LARGE:
			tmp += "_z";
			break;

		}
		tmp += ".jpg";
		return tmp;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Bitmap getThumb() {
		return thumb;
	}

	public void setThumb(Bitmap thumb) {
		this.thumb = thumb;
	}

	public Bitmap getPhoto() {
		return photo;
	}

	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getFarm() {
		return farm;
	}

	public void setFarm(String farm) {
		this.farm = farm;
	}

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public String getSavedThumbURL() {
        return savedThumbURL;
    }

    public void setSavedThumbURL(String savedThumbURL) {
        this.savedThumbURL = savedThumbURL;
    }

    public void onSaveThumbURL(Mbrace.UIHandler uih, FlickrImage ic) {
		new FlickrFactory.GetThumbnailsThread(uih, ic).start();
	}
}
