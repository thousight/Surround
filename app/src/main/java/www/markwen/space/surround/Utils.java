package www.markwen.space.surround;

import java.util.ArrayList;
import java.util.Comparator;

import www.markwen.space.surround.models.Song;

/**
 * Created by markw on 6/7/2017.
 */

public class Utils {

    // FLAGS
    final public static int SORT_BY_TITLE = 0;
    final public static int SORT_BY_ARTIST = 1;
    final public static int SORT_BY_ALBUM = 2;
    final public static int SORT_BY_DATE = 3;

    /**
     * Sort the given list of songs with the given flag:
     * SORT_BY_TITLE, SORT_BY_ARTIST, SORT_BY_ALBUM, SORT_BY_DATE
     * @param songs
     * @param sortingFlag
     * @return sorted list of songs
     */
    @SuppressWarnings("Since15")
    public static ArrayList<Song> sortSongs(ArrayList<Song> songs, final int sortingFlag) {
        songs.sort(new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                switch (sortingFlag) {
                    case SORT_BY_TITLE:
                        return o1.getTitle().compareToIgnoreCase(o2.getTitle());

                    case SORT_BY_ARTIST:
                        return o1.getArtist().compareToIgnoreCase(o2.getArtist());

                    case SORT_BY_ALBUM:
                        return o1.getAlbum().compareToIgnoreCase(o2.getAlbum());

                    case SORT_BY_DATE:
                        return o1.getDateModified().compareTo(o2.getDateModified());

                    default:
                        return o1.getTitle().compareToIgnoreCase(o2.getTitle());
                }
            }
        });
        return songs;
    }

}
