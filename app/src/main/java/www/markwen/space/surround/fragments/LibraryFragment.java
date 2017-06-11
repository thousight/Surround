package www.markwen.space.surround.fragments;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import www.markwen.space.surround.MainActivity;
import www.markwen.space.surround.R;

/**
 * Created by markw on 6/6/2017.
 */

public class LibraryFragment extends Fragment {

    // View items
    private View rootView;
    private TabLayout libraryTabLayout;
    private ViewPager libraryViewPager;

    // Others
    private MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get variables
        activity = (MainActivity)getActivity();

        // Set toolbar title
        activity.setTitleText("Library");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_library, container, false);

        libraryTabLayout = activity.getLibraryTabLayout();
        libraryTabLayout.setVisibility(View.VISIBLE);
        libraryViewPager = (ViewPager) rootView.findViewById(R.id.library_viewpager);

        // If tabs hasn't been configured
        if (libraryTabLayout.getTabCount() == 0) {
            libraryTabLayout.addTab(libraryTabLayout.newTab().setText("Playlists"));
            libraryTabLayout.addTab(libraryTabLayout.newTab().setText("Songs"));
            libraryTabLayout.addTab(libraryTabLayout.newTab().setText("Artists"));
            libraryTabLayout.addTab(libraryTabLayout.newTab().setText("Albums"));
            libraryTabLayout.setSelectedTabIndicatorColor(ResourcesCompat.getColor(getResources(), R.color.colorTextBlack, null));
        }

        return rootView;
    }
}
