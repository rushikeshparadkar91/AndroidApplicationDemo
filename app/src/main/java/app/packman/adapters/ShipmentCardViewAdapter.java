package app.packman.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import app.packman.R;
import app.packman.activity.ShipmentDetailsActivity;
import app.packman.model.Shipment;

/**
 * Created by mlshah on 12/20/15.
 */
public class ShipmentCardViewAdapter extends RecyclerView.Adapter<ShipmentCardViewAdapter.VersionViewHolder> {
    List<String> versionModels;
    private static final String SHIPMENT_DETAILS = "SHIPMENT_DETAILS";
    Context context;
    List<Shipment> shipments;

    public ShipmentCardViewAdapter(Context context) {
        this.context = context;
    }

    public void setShipments(List<Shipment> shipments) {
        this.shipments = shipments;
        notifyDataSetChanged();
    }

    // sets up the shipment card view
    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shipment_card_view, viewGroup, false);
        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    // assigns data to the layout views
    @Override
    public void onBindViewHolder(final VersionViewHolder versionViewHolder, int i) {
        versionViewHolder.itemView.setTag(i);
        //TODO getReciever and save if receiver is present
        versionViewHolder.tvReceiverName.setText("TODO: name");
        versionViewHolder.tvPrice.setText(shipments.get(i).getPrice() + " " + shipments.get(i).getCurrency());
        versionViewHolder.tvStatus.setText(shipments.get(i).getStatus());
        if (shipments.get(i).getBox().getImage() == null || shipments.get(i).getBox().getImage().length == 0) {
            versionViewHolder.imgThumbnail.setImageResource(R.drawable.box_clipart_default);
        } else {
            Bitmap bmp = BitmapFactory.decodeByteArray(shipments.get(i).getBox().getImage(), 0, shipments.get(i).getBox().getImage().length);
            versionViewHolder.imgThumbnail.setImageBitmap(bmp);
        }
        versionViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            // This gets called when there is a click anywhere on card
            @Override
            public void onClick(View v) {
                try {
                    Intent newIntent = new Intent();
                    newIntent.putExtra(SHIPMENT_DETAILS,
                            shipments.get(
                                    Integer.parseInt(versionViewHolder.itemView.getTag().toString())
                            )
                    );
                    newIntent.setClass(v.getContext(), ShipmentDetailsActivity.class);
                    context.startActivity(newIntent);

                } catch (Exception ex) {
                    // TODO: handle eexeption
                    Log.d("ShipmentAdapter", "exception occured while serializig to json" + ex);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return shipments == null ? 0 : shipments.size();
    }


    class VersionViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgThumbnail;
        public TextView tvReceiverName, tvPrice, tvStatus;

        public VersionViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.boxImage);
            tvReceiverName = (TextView) itemView.findViewById(R.id.receiverNameTV);
            tvPrice = (TextView) itemView.findViewById(R.id.PriceTV);
            tvStatus = (TextView) itemView.findViewById(R.id.StatusTV);
        }
    }

}