package mo.ed.amit.dayeleven.mygoogleapis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tvUsername)
    TextView tvUsername;

    @BindView(R.id.btnEnter)
    AppCompatButton btnEnter;
    private SessionManagement sessionMgmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        sessionMgmt=new SessionManagement(getApplicationContext());
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username= tvUsername.getText().toString();
                if (username!=null){
                    if(username.length()>0){
                        sessionMgmt.setUsername(username);
                        startActivity(new Intent(MainActivity.this, MapActivity.class));
                    }
                }
            }
        });


    }
}