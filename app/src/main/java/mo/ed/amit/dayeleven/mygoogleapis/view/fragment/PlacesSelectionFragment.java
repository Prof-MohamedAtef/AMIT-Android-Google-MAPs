package mo.ed.amit.dayeleven.mygoogleapis.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import mo.ed.amit.dayeleven.mygoogleapis.R;
import mo.ed.amit.dayeleven.mygoogleapis.view.adapter.SavedPlacesRecyclerAdapter;

public class PlacesSelectionFragment extends Fragment {



    private View rootView;
    private Bundle bundle;
    private ArrayList<String> savedPlacesList;
    private RecyclerView recylerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle= getArguments();
        savedPlacesList= (ArrayList<String>)bundle.getSerializable("savedPlacesList");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.suggested_places_layout, container, false);
        ButterKnife.bind(this, rootView);
        recylerView= (RecyclerView) rootView.findViewById(R.id.recycler_view);
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadPlaces();
    }

    private void loadPlaces() {
        SavedPlacesRecyclerAdapter adapter=new SavedPlacesRecyclerAdapter(getActivity(),savedPlacesList,false);
        adapter.notifyDataSetChanged();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recylerView.setLayoutManager(mLayoutManager);
        recylerView.setItemAnimator(new DefaultItemAnimator());
        recylerView.setAdapter(adapter);
    }
}