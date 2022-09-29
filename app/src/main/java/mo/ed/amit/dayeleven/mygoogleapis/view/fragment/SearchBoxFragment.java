package mo.ed.amit.dayeleven.mygoogleapis.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import mo.ed.amit.dayeleven.mygoogleapis.R;
import mo.ed.amit.dayeleven.mygoogleapis.utils.VerifyConnection;
import mo.ed.amit.dayeleven.mygoogleapis.view.activity.PlaceSelectionActivity;

public class SearchBoxFragment extends Fragment {

    @BindView(R.id.search_layout)
    LinearLayout search_layout;

    private View rootView;
    private VerifyConnection verifyConnection;
    private Bundle bundle;
    private ArrayList<String> savedPlacesList;
    private TextView txt_title;
    private TextView txt_currentCustomerAddress;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        savedPlace=new SavedPlaces();
        verifyConnection=new VerifyConnection(getActivity());
        bundle=getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.search_layout, container, false);
        ButterKnife.bind(this, rootView);
//        savedPlacesList=(ArrayList<String>) bundle.getSerializable("savedPlacesList");
        LayoutInflater lastTripInflater=(LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        LinearLayout child=(LinearLayout) lastTripInflater.inflate(R.layout.recent_trip_title_layout, null);
//        txt_title=(TextView) child.findViewById(R.id.title);
//        txt_currentCustomerAddress=(TextView) child.findViewById(R.id.currentCustomerAddress);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PlaceSelectionActivity.class).putExtras(bundle));
            }
        });
    }
}