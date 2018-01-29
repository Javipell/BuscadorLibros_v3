package com.javi.pell.buscadorlibros;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import datos.ScrapingLibro;

public class VerLibro extends AppCompatActivity implements View.OnClickListener {

    // fuente : http://www.sgoliver.net/blog/animaciones-basicas-coordinatorlayout/
    // codigo : https://github.com/sgolivernet/curso-android-src-as/blob/master/android-coordinatorlayout/app/src/main/res/layout/listitem_titular.xml

    String url ;
    String tmpTitulo;
    String tmpAutor;
    String tmpImagen;
    String tmpImagenAnterior;
    String tmpResumen;
    String tmpResumen1;
    String tmpResumen2;
    String tmpEnlace;
    String tmpYear;

    String buscadorUrl;

    TextView textView_titulo, textViewAutor, textViewYear, textViewResumen, textViewResumen0, textViewEnlace;
    ImageView imageButton_caratula, imgToolbar;


    ShareActionProvider mShareActionProvider;

    FloatingActionButton btn_compartir, fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_libro);

        textView_titulo = (TextView) findViewById(R.id.textView_titulo);
        textViewAutor = (TextView) findViewById(R.id.textViewAutor);
        //textViewYear = (TextView) findViewById(R.id.textViewYear);
        textViewResumen = (TextView) findViewById(R.id.textViewResumen);
        textViewResumen0 = (TextView) findViewById(R.id.textViewResumen0);
        //textViewEnlace = (TextView) findViewById(R.id.textViewEnlace);
        imgToolbar = (ImageView) findViewById(R.id.imgToolbar);

        imageButton_caratula = (ImageView) findViewById(R.id.imageButton_caratula);
        imageButton_caratula.setOnClickListener(this);

        Intent intento = getIntent();
        Bundle bundle = intento.getExtras();

        if (bundle!=null)
        {
            url = bundle.getString("cadenaUrl");
            tmpTitulo = bundle.getString("cadenaTitulo");
            buscadorUrl = bundle.getString("buscadorUrl");
            tmpImagenAnterior = bundle.getString("buscadorImagen");
            System.out.println("msg verlibro titulo " + tmpTitulo);
            System.out.println("msg verlibro url " + url);
            System.out.println("msg verlibro buscadorUrl " + buscadorUrl);
        }

        //App bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tmpTitulo);
        //if (getSupportActionBar() != null)  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ScrapingLibro scrapingLibro = new ScrapingLibro(url, VerLibro.this, buscadorUrl);

        leerFichero();

        textView_titulo.setText( tmpTitulo );
        if (tmpAutor != null) {
            textViewAutor.setText(tmpAutor);
        }else{ textViewAutor.setText(""); }

        textViewResumen.setText( tmpResumen2);
        textViewResumen0.setText( tmpResumen1);

        if (tmpImagen.contains("no encontro datos") )
            tmpImagen = (tmpImagenAnterior != null) ? tmpImagenAnterior : "";

        Picasso.with(VerLibro.this)
                .load(tmpImagen)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.ic_menu_camera)
                .noFade()
                .into(imageButton_caratula);

        Picasso.with(VerLibro.this)
                .load(tmpImagen)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.ic_menu_camera)
                .noFade()
                .into(imgToolbar);

        btn_compartir = (FloatingActionButton) findViewById(R.id.btn_compartir);
        //btn_compartir.setOnClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        mShareActionProvider.setShareIntent(getDefaultShare());
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
        }else if (id == R.id.action_compartir) {
            compartir();
        }

        return super.onOptionsItemSelected(item);
    }

    public void compartir()
    {
        mShareActionProvider.setShareIntent(getDefaultShare());
    }

    public Intent getDefaultShare()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Descarga este libro: " + tmpTitulo
                + " desde este enlace: " + tmpEnlace);
        intent.setType("text/plain");
        return intent;
    }

    public void leerFichero(){
        File rutaArchivo = Environment.getExternalStorageDirectory();
        rutaArchivo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File archivo = new File(rutaArchivo+"/"+"DatosLibro.txt");

        if (archivo.exists())
            archivo.delete();
        while (!archivo.exists())
        {
            tareaLarga();
        }
        FileReader mFileReader = null;
        BufferedReader mReader ;
        try {
            //rutaArchivo = new File("Libros_Buscados.txt");
            mFileReader = new FileReader(archivo);
            mReader = new BufferedReader(mFileReader);
            String linea;
            try {
                while ( (linea=mReader.readLine() ) != null)
                {
                    System.out.println("MSG LINEA " + linea);
                    String [] campos = linea.split(" -- ");

                    tmpTitulo = campos[0];
                    tmpAutor = campos[1];
                    tmpYear = "0";
                    tmpEnlace = campos[2];
                    tmpResumen = campos[3];

                    int posicion = tmpResumen.indexOf("Sinopsis");
                    System.out.println("msg posicion "+posicion);
                    if (posicion != -1) {

                        tmpResumen = tmpResumen.replace("cnica: ","cnica:\n\n");
                        tmpResumen = tmpResumen.replace("Idioma: ","\nIdioma: ");
                        tmpResumen = tmpResumen.replace("Autor: ","\nAutor: ");
                        tmpResumen = tmpResumen.replace("Paginas: ","\nPaginas: ");
                        tmpResumen = tmpResumen.replace("Formatos: ","\nFormatos: ");
                        tmpResumen = tmpResumen.replace("Servidores: ","\nServidores: ");
                        tmpResumen = tmpResumen.replace("Sinopsis: ","\n\nSinopsis:\n");
                        tmpResumen = tmpResumen.replace("DERCARGARLO AQUI ","\n");
                        tmpResumen = tmpResumen.replace("Descripcion: ","\nDescripcion: ");
                        tmpResumen = tmpResumen.replace("Fecha de publicación: ","\nFecha de publicación: ");

                        tmpResumen1 = tmpResumen.substring(0, posicion) + "\n\n";
                        tmpResumen2 = tmpResumen.substring(posicion) + "\n\n";
                    } else{
                        tmpResumen1 = "Ficha ténica: \n\n" + "Título: " + tmpTitulo + "\nAutor: "+ tmpAutor;
                        tmpResumen2 = "Sinopsis: \n\n"+tmpResumen+"\n\n";
                    }
                    tmpResumen += "\n\n";
                    tmpImagen = campos[4];
                    System.out.println("msg verlibro archivo "+ tmpTitulo);
                    //rellenar();
                }
            } catch (IOException e) {

                System.out.println("MSG io: "+ e.getMessage());
            }
        } catch (FileNotFoundException e) {
            System.out.println("MSG: file no"+ e.getMessage());
            e.printStackTrace();
        }finally {
            if ( null != mFileReader)
                try {
                    mFileReader.close();
                } catch (IOException e) {
                    System.out.println("MSG cierre: "+ e.getMessage());
                    e.printStackTrace();
                }
        }
    }

    private void tareaLarga()
    {
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {}
    }

    public void descargar(String enlace, String archivo)
    {
        Uri uri = Uri.parse(enlace);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        //finishActivity(intent);

        try {
            //primero especificaremos el origen de nuestro archivo a descargar utilizando
            //la ruta completa
            URL url = new URL(enlace);

            //establecemos la conexión con el destino
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //establecemos el método jet para nuestra conexión
            //el método setdooutput es necesario para este tipo de conexiones
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            //por último establecemos nuestra conexión y cruzamos los dedos 😛
            try {
                urlConnection.connect();
            }catch (Exception ex)
            {
                Log.w("conexion descarga","fallo");
                //falloDescarga=1;
            }

            //vamos a establecer la ruta de destino para nuestra descarga
            //para hacerlo sencillo en este ejemplo he decidido descargar en
            //la raíz de la tarjeta SD
            File SDCardRoot = Environment.getExternalStorageDirectory();

            //vamos a crear un objeto del tipo de fichero
            //donde descargaremos nuestro fichero, debemos darle el nombre que
            //queramos, si quisieramos hacer esto mas completo
            //cogeríamos el nombre del origen
            File file = new File(SDCardRoot,archivo);

            //utilizaremos un objeto del tipo fileoutputstream
            //para escribir el archivo que descargamos en el nuevo
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = null;
            //leemos los datos desde la url
            try {
                inputStream = urlConnection.getInputStream();
            }catch (Exception e)
            {

            }
            //obtendremos el tamaño del archivo y lo asociaremos a una
            //variable de tipo entero
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;

            //creamos un buffer y una variable para ir almacenando el
            //tamaño temporal de este
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            try {
                //ahora iremos recorriendo el buffer para escribir el archivo de destino
                //siempre teniendo constancia de la cantidad descargada y el total del tamaño
                //con esto podremos crear una barra de progreso
                while ((bufferLength = inputStream.read(buffer)) > 0) {

                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    //podríamos utilizar una función para ir actualizando el progreso de lo
                    //descargado
                    //actualizaProgreso(downloadedSize, totalSize);
                }
            }catch (NullPointerException e)
            {
                System.out.println("msg error buffer " );
            }
            //cerramos
            fileOutput.close();
            inputStream.close();
            //y gestionamos errores
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.w("ErrorMalUrl ",e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.w("ErrorIO ",e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_compartir:
                compartir();
                break;
            case R.id.fab:
                compartir2();
            case R.id.imageButton_caratula:
                compartir();
                break;

        }
    }

    public void compartir2()
    {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                String archivo = tmpTitulo;

                Snackbar.make(view, "Descargando "+archivo,
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                String valor;

                SharedPreferences otraspreferencias = PreferenceManager
                        .getDefaultSharedPreferences(VerLibro.this);
                valor = otraspreferencias.getString("lista_resultados","0");
                String remitente = otraspreferencias.getString("remitente_correo", " ");
                String destinatario = otraspreferencias.getString("correo_destinatario", " ");

                System.out.println("msg valor "+ valor);

                switch (valor)
                {
                    case "0":
                        String subject = "Descargar libro";
                        String bodyText = tmpTitulo + " -- " + tmpEnlace;
                        String mailto = "mailto:"+ remitente  +
                                "?cc=" + destinatario +
                                "&subject=" + Uri.encode(subject) +
                                "&body=" + Uri.encode(bodyText);

                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse(mailto));

                        try {
                            startActivity(emailIntent);
                        } catch (ActivityNotFoundException e) {
                            //TODO: Handle case where no email app is available
                        }

                        break;
                    case "1":
                        descargarFichero d = new descargarFichero();
                        d.execute(tmpEnlace, archivo);
                        break;
                    case "2":
                        compartir();
                        break;
                    case "3":
                        String texto = "Descargar libro: " + tmpTitulo + " -- " + tmpEnlace;;
                        onClickWhatsApp(texto);

                        break;
                    case "4":
                        String numero = "657764533";
                        String imagen = "";
                        sendImageWhatsApp(numero, imagen);
                        break;
                }
            }
        });
    }

    private class descargarFichero extends AsyncTask<String, Void, Void>
    {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(String... params)
        {
            String url = params[0];
            String archivo = params[1];
            descargar(url, archivo);

            return null;
        }
    }

    public void onClickWhatsApp(String text) {

        PackageManager pm=getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            //String text = "Tu texto aquí";

            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Compartir con"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp no está instalado", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    private void sendImageWhatsApp(String phoneNumber, String nombreImagen) {
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + "/" + nombreImagen));
            intent.putExtra("jid", phoneNumber + "@s.whatsapp.net"); //numero telefonico sin prefijo "+"!
            intent.setPackage("com.whatsapp");
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "Whatsapp no esta instalado.", Toast.LENGTH_LONG).show();
        }
    }
}
