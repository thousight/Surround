package www.markwen.space.surround.models;

import android.graphics.Bitmap;

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
