package www.markwen.space.surround;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

import www.markwen.space.surround.fragments.HomeFragment;
import www.markwen.space.surround.fragments.LibraryFragment;
import www.markwen.space.surround.models.Song;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // View items
    private FrameLayout mainFrame;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView titleText;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private MaterialDialog loadingDialog;
    private TabLayout libraryTabLayout;

    // Fragments
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private LibraryFragment libraryFragment;

    // Data
    private ArrayList<Song> allSongs = new ArrayList<>(), newSongs = new ArrayList<>();
    private File directory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start dialog
        loadingDialog = new MaterialDialog.Builder(this)
                .title("Scanning songs")
                .content("Please wait...")
                .progress(true, 0)
                .cancelable(false)
                .build();

        // Grab view items
        mainFrame = (FrameLayout) findViewById(R.id.main_frame);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        titleText = (TextView) findViewById(R.id.toolbar_title);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        libraryTabLayout = (TabLayout) findViewById(R.id.library_tab);

        // Set up global views
        collapsingToolbar.setExpandedTitleColor
                (ResourcesCompat.getColor(getResources(), R.color.colorTextBlack, null));
        collapsingToolbar.setCollapsedTitleTextColor
                (ResourcesCompat.getColor(getResources(), R.color.colorTextBlack, null));
        collapsingToolbar.setExpandedTitleGravity(Gravity.BOTTOM);
        toolbar.setTitleTextColor
                (ResourcesCompat.getColor(getResources(), R.color.colorTextBlack, null));
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Get files
        directory = new File(Environment.getExternalStorageDirectory().toString() + "/Music");
//        scanSongs();

        // Set up fragment
        homeFragment = new HomeFragment();
        libraryFragment = new LibraryFragment();
        goToFragment(homeFragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            toolbar.setTitle("Home");
            goToFragment(homeFragment);
        } else if (id == R.id.nav_library) {
            toolbar.setTitle("Library");
            goToFragment(libraryFragment);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setTitleText(String titleText) {
        toolbar.setTitle(titleText);
//        this.titleText.setText(titleText);
        collapsingToolbar.setTitle(titleText);
    }

    /**
     * Scan all the songs in the 'Music' folder
     * @return
     */
    public void scanSongs() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                allSongs.clear();
                MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                if (directory.exists()) {
                    File[] allFiles = directory.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.endsWith(".mp3") && dir.isFile();
                        }
                    });

                    File tempFile;
                    String tempTitle;
                    Bitmap tempBitmap = null;
                    Calendar yesterday = Calendar.getInstance();
                    yesterday.add(Calendar.DATE, -1);
                    Date tempFileDate;
                    byte[] tempByteArray;
                    Song tempSong;
                    for (File allFile : allFiles) {
                        tempFile = allFile;
                        metadataRetriever.setDataSource(tempFile.getAbsolutePath());
                        tempTitle = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//                        tempByteArray = metadataRetriever.getEmbeddedPicture();
                        tempFileDate = new Date(tempFile.lastModified());

//                        if (tempByteArray != null) {
//                            tempBitmap = BitmapFactory.decodeByteArray(tempByteArray, 0, tempByteArray.length);
//                        }

                        if (tempTitle.length() > 0) {
                            tempSong = new Song(tempTitle,
                                    metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                                    metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                                    tempFile.getAbsolutePath(),
                                    tempFileDate,
                                    tempBitmap);
                        } else {
                            tempSong = new Song(tempFile.getName(),
                                    metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                                    metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                                    tempFile.getAbsolutePath(),
                                    tempFileDate,
                                    tempBitmap);
                        }

                        allSongs.add(tempSong);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please put your music files into your /Music folder", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public ArrayList<Song> scanForNewEntries() {
        // Get date of last week
        GregorianCalendar dayBeforeThisWeek = new GregorianCalendar();
        int dayFromMonday = (dayBeforeThisWeek.get(Calendar.DAY_OF_WEEK) + 7 - Calendar.MONDAY) % 7;
        dayBeforeThisWeek.add(Calendar.DATE, -dayFromMonday-1);
        Date lastWeek = dayBeforeThisWeek.getTime();

        newSongs.clear();
        if (directory.exists()) {
            File[] allFiles = directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".mp3");
                }
            });
            Date tempDate;
            for (File file : allFiles) {
                tempDate = new Date(file.lastModified());
                if (tempDate.compareTo(lastWeek) > 0) {
                    newSongs.add(new Song(file));
                }
            }
        }

        return newSongs;
    }

    /**
     * Navigate to the given entry
     * @param targetFragment
     */
    public void goToFragment(Fragment targetFragment) {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_frame, targetFragment, "HomeFragment");
        transaction.commit();
    }

    /**
     * Get all the songs in the directory
     * @return all songs stored in an ArrayList
     */
    public ArrayList<Song> getAllSongs() {
        return allSongs;
    }

    /**
     * Get all the new songs in the directory
     * @return all new songs stored in an ArrayList
     */
    public ArrayList<Song> getNewSongs() {
        return newSongs;
    }

    /**
     * Get libraryTabLayout
     * @return libraryTabLayout
     */
    public TabLayout getLibraryTabLayout() {
        return libraryTabLayout;
    }
}
