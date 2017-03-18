package app.packman.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import app.packman.R;
import app.packman.activity.HomeActivity;
import app.packman.adapters.ShipmentCardViewAdapter;
import app.packman.model.Shipment;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sujaysudheendra on 12/19/15.
 * <p/>
 * Fragment class for the incoming shipment and outgoing shipment. This sets the layout under the tab
 */
@SuppressLint("ValidFragment")
public class ShipmentListFragment extends Fragment {

    int color;
    ShipmentCardViewAdapter cardViewAdapter;

    @InjectView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.recycler_view)
    RecyclerView recyclerView;

    public ShipmentListFragment() {}

    public ShipmentListFragment(int color) {
        this.color = color;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_shipment, container, false);
        ButterKnife.inject(this, view);
        final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.shipment_frame_layout);
        frameLayout.setBackgroundColor(color);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        cardViewAdapter = new ShipmentCardViewAdapter(getContext());
        recyclerView.setAdapter(cardViewAdapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                ((HomeActivity)getActivity()).populateShipments();

            }
        });
        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        return view;
    }

    /**
     * This method is used to set up the cardView Adapter with the passed shipment list on the home page
     *
     * @param shipments, list of shipment objects
     */
    public void setUpShipments(List<Shipment> shipments) {
        // This cardViewAdapter assigns the incoming/outgoing tab layout's with appropriate data from shipment object
        cardViewAdapter.setShipments(shipments);
        swipeRefreshLayout.setRefreshing(false);
    }

    public void showSwipeLayout(){
        swipeRefreshLayout.setRefreshing(true);
    }
    public void hideSwipeLayout(){
        swipeRefreshLayout.setRefreshing(false);
    }
}