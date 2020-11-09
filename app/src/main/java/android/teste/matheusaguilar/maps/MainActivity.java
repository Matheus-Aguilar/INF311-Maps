package android.teste.matheusaguilar.maps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void abreMapa(View view) {
        Intent it = new Intent(getBaseContext(), MapsActivity.class);
        it.putExtra("local", view.getTag().toString());
        startActivity(it);
    }

    public void fechar(View view){
        finish();
    }

}