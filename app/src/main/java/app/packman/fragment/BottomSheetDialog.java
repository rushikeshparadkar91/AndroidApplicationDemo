package app.packman.fragment;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.packman.R;
import app.packman.activity.AddressActivity;
import app.packman.model.Address;
import app.packman.utils.Constants;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sujaysudheendra on 3/20/16.
 */
public class BottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    public static String BOTTOM_SHEET_TAG = "BOTTOM_SHEET_LOG";
    private Address address;
    private int addressPostion;

    @InjectView(R.id.edit_address_action)
    TextView editAddressAction;
    @InjectView(R.id.delete_address_action)
    TextView deleteAddressAction;
    @InjectView(R.id.default_address_action)
    TextView defautAddressAction;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        address = (Address)getArguments().getSerializable(Constants.SELECTED_ADDRESS);
        addressPostion = getArguments().getInt(Constants.ADDRESS_POSITION);
    }

    public static BottomSheetDialog newInstance(Address address, int postion) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.SELECTED_ADDRESS, address);
        bundle.putInt(Constants.ADDRESS_POSITION, postion);
        bottomSheetDialog.setArguments(bundle);
        return bottomSheetDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        ButterKnife.inject(this, view);
        editAddressAction.setOnClickListener(this);
        defautAddressAction.setOnClickListener(this);
        deleteAddressAction.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_address_action:
                Log.d(BOTTOM_SHEET_TAG, "edit address clicked");
                ((AddressActivity)getActivity()).openAddAddressActivity(address, addressPostion);
                break;
            case R.id.delete_address_action:
                Log.d(BOTTOM_SHEET_TAG, "delete address clicked");
                ((AddressActivity)getActivity()).deleteAddress(address);
                break;
            case R.id.default_address_action:
                ((AddressActivity)getActivity()).setDefaultAddress(address);
                Log.d(BOTTOM_SHEET_TAG, "set default clicked");
                break;
        }
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
