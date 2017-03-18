package app.packman.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.packman.R;
import app.packman.activity.AboutActivity;
import app.packman.activity.AddressActivity;
import app.packman.activity.HomeActivity;
import app.packman.activity.ViewProfileActivity;
import app.packman.adapters.NavigationDrawerAdapter;
import app.packman.model.NavDrawerItem;
import app.packman.model.User;
import app.packman.utils.PackmanSharePreference;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by mlshah on 12/20/15.
 */
public class NavigationDrawerFragment extends Fragment {

    private static String TAG = NavigationDrawerFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    private static String[] titles = null;
    private static int[] iconList = {R.drawable.ic_user, R.drawable.ic_location, R.drawable.ic_pay, R.drawable.ic_help, R.drawable.ic_share, R.drawable.ic_about, R.drawable.ic_logout};
    private FragmentDrawerListener drawerListener;

    private User currentUser;

    @InjectView(R.id.nav_user_email_id)
    TextView userEmail;
    @InjectView(R.id.nav_user_name)
    TextView userName;
    @InjectView(R.id.navProfilePic)
    ImageView profilePic;


    public NavigationDrawerFragment() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public static List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();


        // preparing navigation drawer items
        for (int i = 0; i < titles.length; i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(titles[i]);
            data.add(navItem);
        }
        return data;
    }

    public static List<Integer> getIcons() {
        List<Integer> data = new ArrayList<>();


        // preparing navigation drawer items
        for (int i = 0; i < iconList.length; i++) {
            data.add(iconList[i]);
        }
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // drawer labels
        titles = getActivity().getResources().getStringArray(R.array.nav_drawer_labels);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        ButterKnife.inject(this, layout);
        updateHeaderContent();
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);

        adapter = new NavigationDrawerAdapter(getActivity(), getData(), getIcons());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent newActivityFromMenu;
                drawerListener.onDrawerItemSelected(view, position);
                switch (position) {
                    case 0:
                        newActivityFromMenu = new Intent(getActivity(), ViewProfileActivity.class);
                        startActivity(newActivityFromMenu);
                        break;
                    case 1:
                        newActivityFromMenu = new Intent(getActivity(), AddressActivity.class);
                        startActivity(newActivityFromMenu);
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        newActivityFromMenu = new Intent(getActivity(), AboutActivity.class);
                        startActivity(newActivityFromMenu);
                        break;
                    case 6:
                        ((HomeActivity)getActivity()).signOut();
                        break;
                }
                mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return layout;
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateHeaderContent();
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    private void updateHeaderContent() {
        currentUser = PackmanSharePreference.getUser(getActivity().getApplicationContext());

        if(currentUser == null) return;
        userEmail.setText(currentUser.getPerson().getEmail());
        userName.setText(
                currentUser.getPerson().getFirstName()
                        + " "
                        + currentUser.getPerson().getLastName()
        );

        if (currentUser.getPerson().getProfilePic() != null) {

            Bitmap profilepic = BitmapFactory.decodeByteArray(
                    currentUser.getPerson().getProfilePic(), 0,
                    currentUser.getPerson().getProfilePic().length
            );

            profilePic.setImageBitmap(profilepic);
        }
    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }
}

