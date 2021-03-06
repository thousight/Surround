package www.markwen.space.surround.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import www.markwen.space.surround.MainActivity;
import www.markwen.space.surround.R;
import www.markwen.space.surround.models.Song;

/**
 * Created by markw on 6/5/2017.
 */

public class HomeFragment extends Fragment {

    // View items
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout newSongList;
    private ListView recentPlaylists;

    // Temporary view items
    private CardView tempNewSongCard;
    private ImageView tempNewSongAlbumArt;
    private TextView tempNewSongTitle, tempNewSongArtist;
    private ImageButton tempNewSongOption;
    private RelativeLayout tempNewSongPlayBackground;

    // Data
    private ArrayList<Song> newSongs = new ArrayList<>();

    // Others
    private MainActivity activity;
    private LayoutInflater inflater;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get variables
        activity = (MainActivity)getActivity();
        newSongs = activity.getNewSongs();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Set toolbar title
        activity.setTitleText("Home");

        // Hide libraryTabLayout
        activity.getLibraryTabLayout().setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        newSongList = (LinearLayout) rootView.findViewById(R.id.new_songs_list);
        recentPlaylists = (ListView) rootView.findViewById(R.id.home_playlists);

        // Set up layout items
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ArrayList<Song> newNewSongs = activity.scanForNewEntries();
                if (newNewSongs.size() > 0) {
                    Song tempSong;
                    for (int i = 0; i < newNewSongs.size(); i++) {
                        tempSong = newNewSongs.get(i);
                        if (!newSongs.contains(tempSong)) {
                            newSongs.add(tempSong);
                            renderNewSong(newNewSongs.get(i));
                        }
                    }
                    newNewSongs.clear();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        newSongs = activity.scanForNewEntries();
        for (Song song : newSongs) {
            renderNewSong(song);
        }

        return rootView;
    }

    /**
     * Configure and inflate the view for an individual new song item
     * @param song
     */
    private void renderNewSong(Song song) {
        // Grabbing view items
        tempNewSongCard = (CardView)inflater.inflate(R.layout.new_song_item, newSongList, false);
        tempNewSongAlbumArt = (ImageView)tempNewSongCard.findViewById(R.id.new_song_image);
        tempNewSongTitle = (TextView)tempNewSongCard.findViewById(R.id.new_song_title);
        tempNewSongArtist = (TextView)tempNewSongCard.findViewById(R.id.new_song_artist);
        tempNewSongOption = (ImageButton)tempNewSongCard.findViewById(R.id.new_song_options);
        tempNewSongPlayBackground = (RelativeLayout)tempNewSongCard.findViewById(R.id.new_song_card);

        // Set items
        if (song.getAlbumArt() != null) {
            tempNewSongAlbumArt.setImageBitmap(song.getAlbumArt());
        }
        tempNewSongTitle.setText(song.getTitle());
        tempNewSongArtist.setText(song.getArtist());
        tempNewSongOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(activity, v);
                popupMenu.getMenu()
                        .add("Add to queue")
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                return true;
                            }
                        });
                popupMenu.getMenu()
                        .add("Add to playlist")
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                return true;
                            }
                        });
            }
        });
        tempNewSongPlayBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        newSongList.addView(tempNewSongCard, 0);
    }
}
