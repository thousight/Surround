package www.markwen.space.surround.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.util.Date;

/**
 * Created by markw on 6/6/2017.
 */

public class Song {

    private String title, artist, album, path;
    private Date dateModified;
    private Bitmap albumArt;

    public Song(String title, String artist, String album, String path, Date dateModified, Bitmap albumArt) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.path = path;
        this.dateModified = dateModified;
        this.albumArt = albumArt;
    }

    public Song(File file) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(file.getAbsolutePath());
        title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if (title == null || title.length() < 1) {
            title = file.getName();
        }
        artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        album = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        path = file.getName();
        dateModified = new Date(file.lastModified());
        byte[] tempByteArray = metadataRetriever.getEmbeddedPicture();
        if (tempByteArray != null) {
            albumArt = BitmapFactory.decodeByteArray(tempByteArray, 0, tempByteArray.length);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbum() {
        return album;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setAlbumArt(Bitmap albumArt) {
        this.albumArt = albumArt;
    }

    public Bitmap getAlbumArt() {
        return albumArt;
    }
}
