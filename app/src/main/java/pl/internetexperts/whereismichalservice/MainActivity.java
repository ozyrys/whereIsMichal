package pl.internetexperts.whereismichalservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, AppService.class);
        startService(intent);

        setContentView(R.layout.activity_main);
    }
}
