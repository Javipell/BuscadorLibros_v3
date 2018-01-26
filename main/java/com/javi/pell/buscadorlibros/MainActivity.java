package com.javi.pell.buscadorlibros;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifTextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText mEditTextPalabra;
    TextView textView2;
    Button mButtonBuscar, btnBorrar, btnGratisMas, btnLectulanda;
    GifTextView buscando;
    String buscadorUrl = "";
    String buscadorCondicion = "";
    String buscadorPagina = "";
    String buscadorEspacio = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEditTextPalabra = (EditText) findViewById(R.id.editText_palabra);
        textView2 = (TextView) findViewById(R.id.textView2);
        buscando = (GifTextView) findViewById(R.id.buscando);
        mButtonBuscar = (Button) findViewById(R.id.button_buscar);
        btnBorrar = (Button) findViewById(R.id.btnBorrar);
        btnGratisMas = (Button) findViewById(R.id.btnGratisMas);
        btnLectulanda = (Button) findViewById(R.id.btnLectulanda);

        mEditTextPalabra.setText(limpiarAcentos(mEditTextPalabra.getText().toString()));

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditTextPalabra.setText("");
            }
        });

        btnGratisMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // fuente : https://www.youtube.com/watch?v=AEBnm-bNqtE
                // crear un objeto SharedPreferences
                SharedPreferences otrasPreferencias = PreferenceManager
                        .getDefaultSharedPreferences(MainActivity.this);
                // hacer editable el objeto SharedPreferences
                SharedPreferences.Editor editor = otrasPreferencias.edit();
                // establecer la informacion a almacenar
                editor.putString("lista_buscadores","0");
                // tramsferor la informacion a SharedPreferences
                editor.apply();
                mButtonBuscar.callOnClick();
            }
        });

        btnLectulanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // fuente : https://www.youtube.com/watch?v=AEBnm-bNqtE
                // crear un objeto SharedPreferences
                SharedPreferences otrasPreferencias = PreferenceManager
                        .getDefaultSharedPreferences(MainActivity.this);
                // hacer editable el objeto SharedPreferences
                SharedPreferences.Editor editor = otrasPreferencias.edit();
                // establecer la informacion a almacenar
                editor.putString("lista_buscadores","1");
                // tramsferor la informacion a SharedPreferences
                editor.apply();
                mButtonBuscar.callOnClick();
        }
        });
        mButtonBuscar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                buscando.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                //textView2.setVisibility(View.GONE);
                textView2.setText("B U S C A N D O . . .");
                mButtonBuscar.setVisibility(View.GONE);
                btnBorrar.setVisibility(View.GONE);
                btnGratisMas.setVisibility(View.GONE);
                btnLectulanda.setVisibility(View.GONE);

                SharedPreferences otrasPreferencias = PreferenceManager
                        .getDefaultSharedPreferences(MainActivity.this);
                String valor = otrasPreferencias.getString("lista_buscadores","0");
                System.out.println("msg valor "+ valor);
                switch (valor)
                {
                    case "0":
                        buscadorUrl = "https://gratismas.org/";
                        buscadorCondicion = "?s=";
                        buscadorEspacio = "+";
                        buscadorPagina = "libros/pagina/";
                        break;
                    case  "1":
                        buscadorUrl = "https://www.lectulandia.com/";
                        buscadorCondicion = "search/";
                        buscadorEspacio = "+";
                        buscadorPagina = "book/page/";
                        break;
                    case "2":
                        buscadorUrl = "http://espamobi.com/";
                        buscadorCondicion = "books/search/";
                        buscadorEspacio = "%20";
                        buscadorPagina = "book/page/";
                        break;
                }


                if (mEditTextPalabra.length() != 0 )
                {
                    //Intent intento = new Intent(getApplicationContext(), Resultados.class);

                    Intent intento = new Intent(getApplicationContext(), Libro.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("cadenaBuscar", mEditTextPalabra.getText().toString());
                    bundle.putString("cadenaPruebas", "0");
                    bundle.putString("cadenaArchivo", "Libros_Buscados.txt");
                    bundle.putString("buscadorUrl", buscadorUrl);
                    bundle.putString("buscadorCondicion", buscadorCondicion);
                    bundle.putString("buscadorEspacio", buscadorEspacio);
                    bundle.putString("buscadorPagina", buscadorPagina);
                    intento.putExtras(bundle);
                    startActivity(intento);

                }else{
                    Toast.makeText(getApplicationContext(),"Cadena vacia", Toast.LENGTH_SHORT).show();
                    Intent intento = new Intent(getApplicationContext(), Libro.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("cadenaBuscar", "");
                    bundle.putString("cadenaPruebas", "1");
                    bundle.putString("cadenaArchivo", "Libros_Nuevos.txt");
                    bundle.putString("buscadorUrl", buscadorUrl);
                    bundle.putString("buscadorCondicion", buscadorCondicion);
                    bundle.putString("buscadorEspacio", buscadorEspacio);
                    bundle.putString("buscadorPagina", buscadorPagina);
                    intento.putExtras(bundle);
                    startActivity(intento);
                }
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                drawer.openDrawer(Gravity.START);
            }
        });

    }

    private void initialize()
    {
        // Aquí, esta actividad es la actividad actual
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // ¿Deberíamos mostrar una explicación?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Mostrar una expansión al usuario * asincrónicamente * - no bloquear
                // este hilo esperando la respuesta del usuario! Después del usuario
                // ve la explicación, intente nuevamente para solicitar el permiso.
            } else {
                // podemos solicitar el permiso.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        5);
            }
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        buscando.setVisibility(View.GONE);
        textView2.setVisibility(View.VISIBLE);
        textView2.setText("Escriba el titulo o palabra del titulo a buscar o deje en blanco para ver novedades y pulse uno de los botones para diferentes resultados");
        //mButtonBuscar.setVisibility(View.VISIBLE);
        btnLectulanda.setVisibility(View.VISIBLE);
        btnGratisMas.setVisibility(View.VISIBLE);
        btnBorrar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_buscar)
        {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this,PreferenciasActivity.class ));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_about) {
            startActivity(new Intent( this, About.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static String limpiarAcentos(String cadena) {
        String limpio =null;
        if (cadena !=null) {
            String valor = cadena;
            valor = valor.toUpperCase();
            // Normalizar texto para eliminar acentos, dieresis, cedillas y tildes
            limpio = Normalizer.normalize(valor, Normalizer.Form.NFD);
            // Quitar caracteres no ASCII excepto la enie, interrogacion que abre, exclamacion que abre, grados, U con dieresis.
            limpio = limpio.replaceAll("[^\\p{ASCII}(N\u0303)(n\u0303)(\u00A1)(\u00BF)(\u00B0)(U\u0308)(u\u0308)]", "");
            // Regresar a la forma compuesta, para poder comparar la enie con la tabla de valores
            limpio = Normalizer.normalize(limpio, Normalizer.Form.NFC);
        }
        return limpio;
    }
}
