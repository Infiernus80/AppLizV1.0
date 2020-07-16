package com.example.appliz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AnimacionPrincipal extends AppCompatActivity {
    TextView devyoutv,bytv;
    ImageView logoIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animacion_principal);
        devyoutv = findViewById(R.id.DEVYOUtv);
        bytv = findViewById(R.id.tvBy);
        logoIv = findViewById(R.id.logoIv);

        //Agregar animaciones
        Animation animation1 = AnimationUtils.loadAnimation(this,R.anim.desplazamiento_arriba);
        Animation animation2 = AnimationUtils.loadAnimation(this,R.anim.desplazamiento_abajo);

        bytv.setAnimation(animation2);
        devyoutv.setAnimation(animation2);
        logoIv.setAnimation(animation1);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AnimacionPrincipal.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 4000 );

    }
}