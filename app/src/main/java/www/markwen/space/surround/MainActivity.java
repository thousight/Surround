package www.markwen.space.surround;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SlidingPaneLayout;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import www.markwen.space.surround.fragments.HomeFragment;
import www.markwen.space.surround.fragments.LibraryFragment;
import www.markwen.space.surround.models.Song;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // View items
    private FrameLayout mainFrame;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

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
        MaterialDialog loadingDialog = new MaterialDialog.Builder(this)
                .title("Loading songs")
                .content("Please wait...")
                .progress(true, 0)
                .cancelable(false)
                .show();

        // Grab view items
        mainFrame = (FrameLayout) findViewById(R.id.main_frame);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Set up global views
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toolbar.setTitle("Home");
        toolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
        setSupportActionBar(toolbar);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Get files
        directory = new File(Environment.getExternalStorageDirectory().toString() + "/Music");
        scanSongs();

        loadingDialog.dismiss();

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

    /**
     * Scan all the songs in the 'Music' folder
     * @return new found song entries
     */
    public ArrayList<Song> scanSongs() {
        ArrayList<Song> newNewSongs = new ArrayList<>();
        allSongs.clear();
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        if (directory.exists()) {
            File[] allFiles = directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".mp3");
                }
            });

            File tempFile;
            String tempTitle;
            Bitmap tempBitmap;
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE, -1);
            Date tempFileDate, oneDayBefore = yesterday.getTime();
            byte[] tempByteArray;
            Song tempSong;
            for (File allFile : allFiles) {
                tempFile = allFile;
                metadataRetriever.setDataSource(tempFile.getAbsolutePath());
                tempTitle = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                tempByteArray = metadataRetriever.getEmbeddedPicture();
                tempFileDate = new Date(tempFile.lastModified());

                if (tempByteArray != null) {
                    tempBitmap = BitmapFactory.decodeByteArray(tempByteArray, 0, tempByteArray.length);
                } else {
                    tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.web_hi_res_512);
                }

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

                if (!tempFileDate.before(oneDayBefore) && !newSongs.contains(tempSong)) {
                    newSongs.add(tempSong);
                    newNewSongs.add(tempSong);
                }

                allSongs.add(tempSong);
            }
        } else {
            Toast.makeText(this, "Please put your music files into your /Music folder", Toast.LENGTH_LONG).show();
        }

        return newNewSongs;
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


}
