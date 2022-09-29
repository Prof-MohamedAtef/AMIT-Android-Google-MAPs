package mo.ed.amit.dayeleven.mygoogleapis.view.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import mo.ed.amit.dayeleven.mygoogleapis.Configs;
import mo.ed.amit.dayeleven.mygoogleapis.MapActivity;
import mo.ed.amit.dayeleven.mygoogleapis.R;

public class PlaceSelectionActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_selection);
        ButterKnife.bind(this);
        initAppBar();
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
        if (Configs.Language.equals("en")) {
            toolbar.setNavigationIcon(R.drawable.arrow_back);
        } else if (Configs.Language.equals("ar")) {
            toolbar.setNavigationIcon(R.drawable.arrow_right);
        }
    }
}