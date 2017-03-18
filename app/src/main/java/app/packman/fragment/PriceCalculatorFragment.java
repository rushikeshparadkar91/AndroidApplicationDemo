package app.packman.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.packman.R;
import butterknife.ButterKnife;

/**
 * Created by sujaysudheendra on 4/2/16.
 */
public class PriceCalculatorFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_calculator, container, false);
        ButterKnife.inject(this, view);
        return view;
    }
}
