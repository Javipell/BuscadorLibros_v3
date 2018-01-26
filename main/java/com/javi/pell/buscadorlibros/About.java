package com.javi.pell.buscadorlibros;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class About extends AppCompatActivity implements View.OnClickListener {

    // fuente : https://www.youtube.com/watch?v=BTYuLho5_rE
    /*
    *   1. añadir desde el menu BUILD - EDIT LIBRARIES AND DEPENDENCIES...
    *       'com.android.support:design:26.1.0'
    *       'com.android.support:cardview-v7:26.1.0'
    *   2. en res/drawable
    *       añadir la imagen a utilizar
    *       añadir new - image asset - tipo action bar, nobmre ic_fav y ic_clock
    *   3. en res/values/strings.xml
    *       <string name="recent_news">Javier Pellicena Polo</string>
    *       <string name="in_news">Texto a mostrar</string>
    *       <string name="publish_date">javipell@gmail.com</string>
    *   4. en res/values/colors.xml
    *       <color name="colorPrimaryText">#050505</color>
    *   5. modificar en layout el activity_about.xml
    *   6. SI NO EXISTE YA
    *      añadir en onCreate
    *      Toolbar toolbar = findViewById(R.id.toolbar);
    *      setSupportActionBar(toolbar);
    *      if (getSupportActionBar() != null)
    *           getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        TextView tv_fecha = findViewById(R.id.tv_fecha);
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("EEEEEEE, d MMM yyyy HH:mm");

        String fecha = dateFormat.format(calendar.getTime());
        tv_fecha.setText("Ültima actualización: "+ fecha);

        TextView tv_correo = findViewById(R.id.tv_correo);
        tv_correo.setText("javipell@gmail.com");

        TextView tv_telefono = findViewById(R.id.tv_telefono);
        tv_telefono.setText("657 764 533");

        TextView tv_blogger = findViewById(R.id.tv_blogger);
        tv_blogger.setText("http://dgjavipell.blogspot.com.es/");

        TextView tv_github = findViewById(R.id.tv_github);
        tv_github.setText("https://github.com/Javipell");

        ImageView imageView, imageView2, imageView3, imageView4, imageView5, imageView6, imageView7;
        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(this);
        imageView2 = findViewById(R.id.imageView2);
        imageView2.setOnClickListener(this);
        imageView3 = findViewById(R.id.imageView3);
        imageView3.setOnClickListener(this);
        imageView4 = findViewById(R.id.imageView4);
        imageView4.setOnClickListener(this);
        imageView5 = findViewById(R.id.imageView5);
        imageView5.setOnClickListener(this);
        imageView6 = findViewById(R.id.imageView6);
        imageView6.setOnClickListener(this);
        imageView7 = findViewById(R.id.imageView7);
        imageView7.setOnClickListener(this);

        ImageView iv_android1, iv_android2, iv_android3, iv_android4, iv_android5;
        iv_android1 = findViewById(R.id. iv_android1);
        iv_android1.setOnClickListener(this);
        iv_android2 = findViewById(R.id. iv_android2);
        iv_android2.setOnClickListener(this);
        iv_android3 = findViewById(R.id. iv_android3);
        iv_android3.setOnClickListener(this);
        iv_android4 = findViewById(R.id. iv_android4);
        iv_android4.setOnClickListener(this);
        iv_android5 = findViewById(R.id. iv_android5);
        iv_android5.setOnClickListener(this);

        TextView tv_texto = findViewById(R.id.tv_texto);
        String cadena =" ";
        tv_texto.setText(cadena);
    }

    @Override
    public void onClick(View view) {
        System.out.println("indice "+ view.getId());
        switch (view.getId())
        {
            case R.id.imageView:
                webDiseño();
                break;
            case R.id.imageView2:
                webDiseño();
                break;
            case R.id.imageView3:
                webDiseño();
                break;
            case R.id.imageView4:
                webDiseño();
                break;
            case R.id.imageView5:
                webDiseño();
                break;
            case R.id.imageView6:
                webDiseño();
                break;
            case R.id.imageView7:
                webDiseño();
                break;
            case R.id.iv_android1:
                webGitHub();
                break;
            case R.id.iv_android2:
                webGitHub();
                break;
            case R.id.iv_android3:
                webGitHub();
                break;
            case R.id.iv_android4:
                webGitHub();
                break;
            case R.id.iv_android5:
                webGitHub();
                break;
        }


    }

    public void webDiseño()
    {
        Uri uri = Uri.parse("http://dgjavipell.blogspot.com.es/");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void webGitHub()
    {
        Uri uri = Uri.parse("http://github.com/Javipell");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}
