package com.javi.pell.buscadorlibros;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class About extends AppCompatActivity {

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

        TextView tv_texto = findViewById(R.id.tv_texto);
        String cadena =" ";

        tv_texto.setText(cadena);
    }
}
