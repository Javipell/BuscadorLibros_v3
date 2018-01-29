package datos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by javier on 10/1/18.
 */

public class ScrapingLibro
{
    Context mContext;
    String url;
    Document mDocument;
    String titulo;
    String autor;
    String imagen;
    String fichaTecnica;
    String sinopsis;
    String urlDescarga;
    String datosLibro = "";
    String buscadorUrl;

    public ScrapingLibro (String web, Context context, String buscador)
    {
        url = web;
        mContext = context;
        buscadorUrl = buscador;
        System.out.println("msg verlibro buscadorUrl 2" + buscadorUrl);

        getHtmlDocument();
        CargaLibro c = new CargaLibro();
        c.execute();

    }

    private class CargaLibro extends AsyncTask<Void, Void, Void>
    {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Cargando Libro ...");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            procesoLibro();
            return null;

        }
    }

    public void procesoLibro()
    {
        System.out.println("msg url 0 "+ buscadorUrl);
        if ( buscadorUrl.contains("gratismas") )
        {
            System.out.println("msg url 2" + buscadorUrl);
            procesoLibro_gratisMas();
        }
        if ( buscadorUrl.contains("lectulandia") )
        {
            System.out.println("msg url 3" + buscadorUrl);
            procesoLibro_lectulandia();
        }
        if ( buscadorUrl.contains("librosparadescargar") )
        {
            System.out.println("msg url 3" + buscadorUrl);
            procesoLibro_librosparadescargar();
        }

    }

    public void procesoLibro_librosparadescargar()
    {
        titulo = "no encontro datos";
        autor  = "no encontro datos";
        urlDescarga = "no encontro datos";
        fichaTecnica = "no encontro datos";
        imagen = "no encontro datos";

        Elements titulos = mDocument.select("h1.title.single-title.entry-title");
        titulo = titulos.get(0).text();

        Elements resumenes = mDocument.select("div.thecontent > p");
        fichaTecnica = resumenes.get(1).text();
        autor = resumenes.get(2).text();

        Elements enlaces = mDocument.select("div.thecontent>fieldset>p>strong>a");
        urlDescarga = enlaces.get(2).attr("href");

        datosLibro = titulo + " -- " + autor + " -- " + urlDescarga + " -- " + fichaTecnica + " -- " + imagen;


        // dar permisios de lectura escritura en androidmanifest.xml
        File rutaArchivo = Environment.getExternalStorageDirectory();
        rutaArchivo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File archivo = new File(rutaArchivo+"/"+"DatosLibro.txt");
        if (escribirArchivo(datosLibro,archivo))
        {
            System.out.println("msg procesoLibro archivo guardado");
        }else{
            System.out.println("msg procesoLibro archivo SIN guardar");
        }
    }

    public void procesoLibro_lectulandia()
    {
        titulo = "no encontro datos";
        autor  = "no encontro datos";
        urlDescarga = "no encontro datos";
        fichaTecnica = "no encontro datos";
        imagen = "no encontro datos";

        Elements titulos =  mDocument.select("div#title > h1");
        titulo = titulos.get(0).text();

        Elements autores = mDocument.select("a.dinSource");
        autor = autores.get(0).text();
        autor =  autor.replace("Autor - ","");

        Elements enlaces = mDocument.select("div#downloadContainer > a");
        urlDescarga = buscadorUrl.replace("com/","com") + enlaces.get(0).attr("href");

        Elements resumenes = mDocument.select("div.ali_justi");
        fichaTecnica = resumenes.text();

        Elements imagenes = mDocument.select("div#book > div#leftBlock > div#cover > img");
        imagen = "https:"+imagenes.attr("src");

        datosLibro = titulo + " -- " + autor + " -- " + urlDescarga + " -- " + fichaTecnica + " -- " + imagen;


        // dar permisios de lectura escritura en androidmanifest.xml
        File rutaArchivo = Environment.getExternalStorageDirectory();
        rutaArchivo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File archivo = new File(rutaArchivo+"/"+"DatosLibro.txt");
        if (escribirArchivo(datosLibro,archivo))
        {
            System.out.println("msg procesoLibro archivo guardado");
        }else{
            System.out.println("msg procesoLibro archivo SIN guardar");
        }
    }

    public void procesoLibro_gratisMas()
    {
        titulo = "no encontro datos";
        autor  = "no encontro datos";
        urlDescarga = "no encontro datos";
        fichaTecnica = "no encontro datos";
        imagen = "no encontro datos";

        Elements paginas =  mDocument.getElementsByClass("entry-header wrap");
        for (Element pagina:paginas)
        {
            Elements titulos = pagina.getElementsByClass("entry-title");
            for (Element tit:titulos)
            {
                titulo = tit.text();
                int posicion = titulo.indexOf("[.");
                if (posicion != -1) titulo= titulo.substring(0, posicion);
                posicion = titulo.indexOf(" – ");
                if (posicion != -1)
                {
                    String[] tmpT = titulo.split(" – ");
                    titulo = tmpT[0].trim();
                    autor = tmpT[1].trim();
                }
                posicion = titulo.indexOf("[");
                if (posicion != -1) titulo = titulo.substring(0, posicion);
                System.out.println("msg procesoLibro añadido titulo "+ titulo + " autor "+autor);
                datosLibro = titulo + " -- " + autor ;
            }

            Elements enlaces = mDocument.select("a.gm-button");
            System.out.println("msg procesoLibro n. enlaces "+ enlaces.size());

            for (Element enlace:enlaces)
            {
                urlDescarga = enlace.attr("href");
                System.out.println("msg procesoLibro añadido enlace "+ urlDescarga);
                datosLibro += " -- " + urlDescarga;
            }

            // resumen
            Elements resumenes = mDocument.select("div.entry-content.wrap.clearfix>p");
            int guardar = 0;
            fichaTecnica = "";
            System.out.println("msg procesoLibro añadido sinopsis1 "+ resumenes.text());

            fichaTecnica =  resumenes.text();
            datosLibro +=  " -- " + fichaTecnica ;

            Elements imagenes = mDocument.select("div.entry-content.wrap.clearfix>p>img");
            System.out.println("msg procesoLibro imagenes "+ imagenes.size());
            for (Element img:imagenes)
            {
                imagen = img.attr("src");
                System.out.println("msg procesoLibro añadido imagen "+ imagen);
                datosLibro +=  " -- " + imagen ;
            }

        }
        // dar permisios de lectura escritura en androidmanifest.xml
        File rutaArchivo = Environment.getExternalStorageDirectory();
        rutaArchivo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File archivo = new File(rutaArchivo+"/"+"DatosLibro.txt");
        if (escribirArchivo(datosLibro,archivo))
        {
            System.out.println("msg procesoLibro archivo guardado");
        }else{
            System.out.println("msg procesoLibro archivo SIN guardar");
        }
    }

    /**
     * Escribe un texto dentro del archivo
     * @param archivo
     * @param textoProcesado
     * @return
     */
    public Boolean escribirArchivo(String textoProcesado, File archivo){
        FileWriter escritor = null;
        Boolean escribeBien = false;
        if (textoProcesado=="")
        {
            System.out.println("msg procesolibro textoProcesado vacio");
        }else {
            try {
                escritor = new FileWriter(archivo);
                escritor.write(textoProcesado);
                escribeBien = true;
            } catch (FileNotFoundException e) {
                System.out.println("No existe el fichero o la carpeta");
                //JOptionPane.showMessageDialog(new JFrame(), "Ha ocurrido algún error creando el nuevo fichero"                    +" \n\nEn la carpeta:\n"+carpetaFicheros);
                escribeBien = false;
            } catch (IOException e) {
                //JOptionPane.showMessageDialog(new JFrame(), "Ha ocurrido algún error creando el nuevo fichero"                    +" \n\nEn la carpeta:\n"+carpetaFicheros);
                escribeBien = false;
            } finally {
                if (escritor != null) {
                    try {
                        escritor.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return escribeBien;
    }

    public void getHtmlDocument()
    {

        // fuente :  https://medium.com/@ssaurel/learn-to-parse-html-pages-on-android-with-jsoup-2a9b0da0096f
        int veces = 0;

        do {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mDocument = Jsoup.connect(url).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("documento "+ mDocument);
                    }
                });*/
                }
            }).start();

            if (mDocument!=null)
            {
                veces=50;
            }else{
                veces++;
                System.out.println("MSG url ("+veces+") "+url);
                tareaLarga();tareaLarga();
            }
        }while (veces<50);


    }

    private void tareaLarga()
    {
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {}
    }
}
