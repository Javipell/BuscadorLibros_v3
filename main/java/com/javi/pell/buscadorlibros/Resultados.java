package com.javi.pell.buscadorlibros;

import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import datos.Adaptador;
import datos.EstructuraDatos;
import datos.SQLite_OpenHelper;
import datos.ScrapingFichero;

public class Resultados extends AppCompatActivity {

    ListView mListView;
    String buscar = "";
    EstructuraDatos mEstructuraDatos;
    List<EstructuraDatos> mEstructuraDatosList = new ArrayList<>();
    Adaptador miAdaptador;
    SQLiteDatabase db;
    SQLite_OpenHelper helper;

    String tmpTitulo = "";
    String tmpEnlace = "";
    String tmpAutor = "";
    String tmpResumen = "";
    String tmpImagen = "";
    String tmpYear = "0";
    int elementos = 0;

    private ProgressDialog pDialog;
    private MiTareaAsincronaDialog tarea2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);

        mListView = (ListView) findViewById(R.id.listViewResultadoBusqueda);
        /*String [] datosPrueba = new String[] {"aaa","bbb","ccc","ddd"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, datosPrueba);
        mListView.setAdapter(arrayAdapter);*/

        Intent intento = getIntent();
        Bundle bundle = intento.getExtras();

        if (bundle!=null)
        {
            buscar = bundle.getString("cadenaBuscar");
        }
        Toast.makeText(getApplicationContext(),"Buscando...:  "+ buscar,Toast.LENGTH_SHORT).show();

        mEstructuraDatosList.clear();
        ScrapingFichero scrapingFichero = new ScrapingFichero(buscar,0, Resultados.this);
        leerFichero();
        //cargarDatos();
        //getWebGoogleBooks();
        //getWebGratisMas();

        /*pDialog = new ProgressDialog(Resultados.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setMessage("Procesando...");
        pDialog.setCancelable(true);
        pDialog.setMax(100);

        tarea2 = new MiTareaAsincronaDialog();
        tarea2.execute();*/


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long l) {
                Toast.makeText(getApplicationContext(),"posicion n: "+ posicion,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void leerFichero(){
        File rutaArchivo = Environment.getExternalStorageDirectory();
        rutaArchivo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File archivo = new File(rutaArchivo+"/"+"Libros_Buscados.txt");

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
                    tmpImagen = campos[3];
                    tmpResumen = campos[4];

                    rellenar();
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

    public void rellenar()
    {
        mEstructuraDatos = new EstructuraDatos(tmpTitulo, tmpAutor,
                Integer.parseInt(tmpYear), tmpResumen, tmpEnlace, tmpImagen );
        mEstructuraDatosList.add(mEstructuraDatos);
        miAdaptador = new Adaptador(Resultados.this, mEstructuraDatosList);
        mListView.setAdapter(miAdaptador);
    }
    public void cargarDatos()
    {
        helper = new SQLite_OpenHelper(this,"dba_datos", null, 1);
        db = helper.getReadableDatabase();
        mEstructuraDatosList.clear();
        // metodo para leer registros desde la clase
        helper.datosPrueba(db, mListView);

        String sql = "SELECT * FROM tbl_datos";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst())
        {
            do {
                EstructuraDatos d = new EstructuraDatos();
                d.set_Id( c.getInt(0) );
                d.set_titulo( c.getString(1));
                d.set_autor( c.getString(2));
                d.set_year( c.getInt(3));
                d.set_url( c.getString(4));
                d.set_resumen( c.getString(5));
                d.set_imagen( c.getString(6));
                mEstructuraDatosList.add(d);
            } while (c.moveToNext());
        }
        c.close();
        miAdaptador = new Adaptador(this, mEstructuraDatosList);
        mListView.setAdapter(miAdaptador);
    }


    public void getWebGratisMas()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                Document doc1 = null;

                String url = "https://gratismas.org/?s=";
                String tituloUrl = buscar.replace(" ", "+");
                url = url + tituloUrl;
                try {
                    doc = Jsoup.connect(url).get();
                    Elements entradas = doc.select("h2.entry-title > a");
                    elementos = entradas.size();
                    for (Element elemento : entradas)
                    {
                        tmpTitulo = "";
                        tmpAutor = "";
                        tmpImagen = "";
                        tmpEnlace = "";

                        tmpTitulo = elemento.attr("title");
                        int posicion = tmpTitulo.indexOf("[.");
                        if (posicion!=-1) tmpTitulo= tmpTitulo.substring(0, posicion);
                        posicion = tmpTitulo.indexOf(" – ");
                        if (posicion!=-1) {
                            String[] tmpT = tmpTitulo.split(" – ");
                            tmpTitulo = tmpT[0].trim();
                            tmpAutor = tmpT[1].trim();
                        }

                        tmpEnlace = elemento.attr("href");
                        try{
                            doc1 = Jsoup.connect(tmpEnlace).get();
                            //imagen
                            Elements entradas2 = doc1.select("div.entry-content.wrap.clearfix>p>img");
                            int elementos2 = entradas2.size();
                            tmpImagen = entradas2.attr("src");
                            // resumen
                            Elements resumenes = doc1.select("div.entry-content.wrap.clearfix>p");
                            int guardar = 0;
                            for (Element resumen:resumenes )
                            {
                                if (guardar ==1)
                                {
                                    tmpResumen += "\n" + resumen.text();
                                    guardar++;
                                }
                                if (resumen.text().contains("Sinopsis")) {
                                    guardar = 1;
                                    tmpResumen = resumen.text();
                                }
                            }
                            // enlace
                            Elements enlaces = doc1.select("a.adf-button");
                            for (Element enlace : enlaces)
                            {
                                tmpEnlace=enlace.attr("href");
                            }

                        }catch (Exception ex)
                        {

                        }


                        mEstructuraDatos = new EstructuraDatos(tmpTitulo, tmpAutor,
                                Integer.parseInt(tmpYear), tmpResumen, tmpEnlace, tmpImagen );
                        mEstructuraDatosList.add(mEstructuraDatos);
                    }
                } catch (Exception ex) {
                    System.out.println("Error "+ex.getMessage());
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        miAdaptador = new Adaptador(Resultados.this, mEstructuraDatosList);
                        mListView.setAdapter(miAdaptador);
                    }
                });
            }
        }).start();
    }

    public void getWebGoogleBooks()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Document doc = null;
                Document doc1 = null;

                String url = "https://google.com/search?tbm=bks&q=";
                //https://www.google.com/search?tbm=bks&q=pilares+tierra
                String tituloUrl = buscar.replace(" ", "+");
                url = url + tituloUrl;

                try {
                    doc = Jsoup.connect(url).get();

                    //mScrapping.metodoJsoup(doc);
                    Elements entradas = doc.select("div.rc");
                    elementos = entradas.size();
                    for (Element elemento : entradas)
                    {
                        tmpTitulo = ""; // Captura del titulo
                        tmpTitulo = elemento.getElementsByClass("r").text();
                        String[] tmpT = tmpTitulo.split(" -");
                        tmpTitulo = tmpT[0];
                        int posicion = tmpTitulo.indexOf(": Premio");
                        if (posicion != -1) // cadena encontrada
                            tmpTitulo = tmpTitulo.substring(0,posicion);

                        tmpEnlace = ""; // captura del enlace google
                        Elements subentradas = elemento.select("h3.r > a");
                        for (Element subelemento:subentradas) {
                            tmpEnlace = subelemento.attr("href");
                        }
                        tmpAutor = ""; // Captura del autor
                        Elements autores = elemento.select("div.s > div > div.slp.f");
                        for (Element autor: autores)
                        {
                            tmpAutor = autor.getElementsByClass("fl").text();
                            tmpAutor = tmpAutor.replace("Vista previa","");
                            tmpAutor = tmpAutor.replace("Más ediciones","");
                        }
                        // varios autores
                        Elements autores1 = elemento.select("div.s > div > div.slp.f > a.fl");
                        if (autores1.size()==0)
                            continue;
                        int n = 0;
                        for (Element enlacesAutor: autores1)
                        {
                            String [] tmpEnlacesAutor = new String[autores1.size()];
                            tmpEnlacesAutor[n]= enlacesAutor.attr("href");
                            n++;
                        }
                        tmpResumen = "" ; // Captura del resumen
                        Elements resumenes = elemento.select("div.s > div > span.st");
                        for (Element resumen : resumenes)
                        {
                            tmpResumen = resumen.getElementsByClass("st").text();
                        }
                        tmpImagen = "" ; // Captura del la imagen
                        Elements imagenes = elemento.select("div.s > div > div.th._lyb > a > g-img._ygd > img");
                        if (imagenes.size()==0)
                            continue;
                        for (Element imagen : imagenes)
                        {
                            tmpImagen = imagen.attr("src");
                            try
                            {
                                doc1 = Jsoup.connect(tmpEnlace).get();

                                Element imags = doc1.getElementById("summary-frontcover");
                                tmpImagen = imags.attr("src");

                            } catch (Exception ex)
                            {
                                System.out.println("Error "+ex.getMessage());
                            }
                        }

                        mEstructuraDatos = new EstructuraDatos(tmpTitulo, tmpAutor,
                                Integer.parseInt(tmpYear), tmpResumen, tmpEnlace, tmpImagen );
                        mEstructuraDatosList.add(mEstructuraDatos);

                    }

                } catch (Exception ex) {
                    System.out.println("Error "+ex.getMessage());
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        miAdaptador = new Adaptador(Resultados.this, mEstructuraDatosList);
                        mListView.setAdapter(miAdaptador);
                    }
                });
            }
        }).start();
    }

    private class MiTareaAsincronaDialog extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            for(int i=1; i<=elementos; i++) {
                //getWebGoogleBooks();
                tareaLarga();;

                publishProgress(i*0,10);

                if(isCancelled())
                    break;
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();

            pDialog.setProgress(progreso);
        }

        @Override
        protected void onPreExecute() {

            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    MiTareaAsincronaDialog.this.cancel(true);
                }
            });

            pDialog.setProgress(0);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
            {
                pDialog.dismiss();
                Toast.makeText(Resultados.this, "Tarea finalizada!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(Resultados.this, "Tarea cancelada!", Toast.LENGTH_SHORT).show();
        }
    }
    private void tareaLarga()
    {
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {}
    }
}
