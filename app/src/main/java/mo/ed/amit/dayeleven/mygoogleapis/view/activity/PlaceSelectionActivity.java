package mo.ed.amit.dayeleven.mygoogleapis.view.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import mo.ed.amit.dayeleven.mygoogleapis.Configs;
import mo.ed.amit.dayeleven.mygoogleapis.MapActivity;
import mo.ed.amit.dayeleven.mygoogleapis.R;
import mo.ed.amit.dayeleven.mygoogleapis.SessionManagement;
import mo.ed.amit.dayeleven.mygoogleapis.utils.VerifyConnection;
import mo.ed.amit.dayeleven.mygoogleapis.view.fragment.PlacesSelectionFragment;

public class PlaceSelectionActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private SessionManagement sessionManagement;
    private VerifyConnection verifyConnection;
    private Intent intent;
    private Bundle bundle;
    private ArrayList<String> savedPlacesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_selection);
        ButterKnife.bind(this);
        initAppBar();

        verifyConnection=new VerifyConnection(getApplicationContext());
        intent=getIntent();
        bundle= intent.getExtras();
        if (bundle!=null){
            savedPlacesList=(ArrayList<String>) bundle.getSerializable("savedPlacesList");
            animatedSavedPlacesTransition(savedPlacesList);
        }else {
            animatedSavedPlacesTransition(savedPlacesList);
        }

    }

    private void animatedSavedPlacesTransition(ArrayList<String> savedPlacesList) {
        PlacesSelectionFragment placesSelectionFragment=new PlacesSelectionFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("savedPlacesList", savedPlacesList);
        placesSelectionFragment.setArguments(bundle);
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down, R.anim.slide_out_down, R.anim.slide_out_up);
        transaction.replace(R.id.saved_places_frame, placesSelectionFragment).commitAllowingStateLoss();
    }

    private void initAppBar() {
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handleNavigationIcon();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                onBackPressed();
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(PlaceSelectionActivity.this);
                Intent intent = new Intent(PlaceSelectionActivity.this, MapActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Apply activity transition
                    startActivity(intent, options.toBundle());
                } else {
                    // Swap without transition
                    startActivity(intent);
                }
                finish();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void handleNavigationIcon() {
        sessionManagement=new SessionManagement(getApplicationContext());
        if (sessionManagement.getLang().equals("en")) {
            toolbar.setNavigationIcon(R.drawable.arrow_back);
        } else if (sessionManagement.getLang().equals("ar")) {
            toolbar.setNavigationIcon(R.drawable.arrow_right);
        }
    }
}