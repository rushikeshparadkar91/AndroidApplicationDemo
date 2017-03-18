package app.packman.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.packman.R;
import app.packman.model.Address;
import app.packman.utils.PackmanSharePreference;
import app.packman.utils.UtilityClass;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sujaysudheendra on 2/28/16.
 */
public class AddressListViewAdapter extends RecyclerView.Adapter<AddressListViewAdapter.VersionViewHolder> {

    Context context;
    List<Address> addresses;

    public AddressListViewAdapter(Context context, List<Address> addresses) {
        this.context = context;
        this.addresses = addresses;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item, parent, false);
        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VersionViewHolder versionViewHolder, int position) {
        versionViewHolder.title.setText(addresses.get(position).getStreet1());
        versionViewHolder.city.setText(addresses.get(position).getCity());
        versionViewHolder.pincode.setText(addresses.get(position).getPinCode());

        if (UtilityClass.serializeObject(
                PackmanSharePreference.getUser(context).getDefaultAddress()
        ).equals(UtilityClass.serializeObject(addresses.get(position)))
                ) {
            versionViewHolder.defaultLocation.setVisibility(View.VISIBLE);
        } else {
            versionViewHolder.defaultLocation.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return addresses == null ? 0 : addresses.size();
    }

    public static class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.address_title_textview)
        TextView title;
        @InjectView(R.id.address_city_textview)
        TextView city;
        @InjectView(R.id.pincode_textview)
        TextView pincode;
        @InjectView(R.id.address_image)
        ImageView addrImage;
        @InjectView(R.id.ivDefaultLocation)
        ImageView defaultLocation;

        public VersionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
