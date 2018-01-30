package datos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SymbolTable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by javier on 4/1/18.
 * //ScrapingFichero scrapingFichero = new ScrapingFichero(buscar, prueba);
 */

public class ScrapingFichero
{

    Context mContext;
    long inicio;
    String url ="";
    String sufijoPaginacion = "pagina/"; // sufijo que se añade a la url cuando hay varias paginas
    int codigo = 0; // respuesta de la pagina a la que se quiere hacer scraping
    Connection.Response response = null;
    String ruta = "https://gratismas.org/"; // direccion de la pagina
    Document mDocument;
    String ficheroOferta = ""; // nombre del fichero donde se guardaran los datos
    int pruebas = 0;
    int numLibro = 0;
    String  documentoProcesado, documentoProcesadoAnterior, documentoAGuardar;
    String buscadorUrl = "";
    String buscadorCondicion = "";
    String buscadorPagina = "";
    String buscadorEspacio = "";
    int pagina = 1;


    public ScrapingFichero(String buscar, int prueba, Context context, String... params)
    {
        buscadorUrl = params[0];
        buscadorCondicion = params[1];
        buscadorPagina = params[2];
        buscadorEspacio = params[3];
        pagina = Integer.parseInt(params[4]);
        ruta = buscadorUrl;
        sufijoPaginacion = buscadorPagina;

        mContext = context;
        pruebas = prueba;
        inicio = System.currentTimeMillis();
        if (pruebas==0) {
            //ruta += "?s="+buscar.replace(" ", "+");
            if (buscadorUrl.contains("lectulandia"))
            {
                //https://www.lectulandia.com/search/amor/page/2/
                ruta += buscadorCondicion
                        + buscar.replace(" ", buscadorEspacio) + "/" + buscadorPagina
                        +  String.valueOf(pagina);
            }
            if (buscadorUrl.contains("librosparadescargar"))
            {
                //http://librosparadescargar.net/page/2/?s=amor
                ruta += buscadorPagina + String.valueOf(pagina) + "/" + buscadorCondicion
                        + buscar.replace(" ", buscadorEspacio);
            }
            if (buscadorUrl.contains("gratismas"))
            {
                //https://gratismas.org/pagina/2/?s=amor
                ruta += buscadorPagina + String.valueOf(pagina) + "/"
                        + buscadorCondicion + buscar.replace(" ", buscadorEspacio);
            }

            ficheroOferta = "Libros_Buscados.txt";
        }else {
            if (buscadorUrl.contains("lectulandia"))
            {
                //https://www.lectulandia.com/page/2/
                ruta += "book/" + buscadorPagina +  String.valueOf(pagina);
            }
            if (buscadorUrl.contains("librosparadescargar"))
            {
                //http://librosparadescargar.net/page/2
                ruta += buscadorPagina + String.valueOf(pagina) ;
            }
            if (buscadorUrl.contains("gratismas"))
            {
                //https://gratismas.org/libros/pagina/2/
                ruta += buscadorPagina + String.valueOf(pagina) ;
            }
            //ruta += "libros/";
            ficheroOferta = "Libros_Nuevos.txt";
        }

        url = ruta;
        System.out.println("msg url ruta "+ url);
        System.out.println("msg buscadorUrl "+ buscadorUrl);
        System.out.println("msg buscadorCondicion "+ buscadorCondicion);
        System.out.println("msg buscadorPagina "+ buscadorPagina);
        System.out.println("msg buscadorEspacio "+ buscadorEspacio);
        crearOComprobarArchivo(ficheroOferta);

    }

    public void borrarFichero( String fichero)
    {
        File rutaArchivo = Environment.getExternalStorageDirectory();
        rutaArchivo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File archivo = new File(rutaArchivo+"/"+fichero);

        archivo.delete();
    }

    /**
     * Compara el archivo donde guardamos la información con lo que hay actualmente en la página.
     * Si hay diferencias da la opción de abrir las novedades en pestañas de firefox
     * Si no existe archivo lo crea.
     * @param nombreArchivo
     */
    public void crearOComprobarArchivo(String nombreArchivo){

        // dar permisios de lectura escritura en androidmanifest.xml
        File rutaArchivo = Environment.getExternalStorageDirectory();
        rutaArchivo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File archivo = new File(rutaArchivo+"/"+nombreArchivo);

        if (pruebas==1)
        {
            if (!archivo.exists())
            {
                System.out.println("MSG el archivo no existe");
                String textoProcesado = leerPagina();
                if (escribirArchivo(textoProcesado, archivo))
                {
                    long fin = System.currentTimeMillis() - inicio;
                    String ar[] = nombreArchivo.split("/");
                    String nombreLimpio = ar[(ar.length - 1)];
                    System.out.println("msg fichero guardado tiempo " + fin);
                    //JOptionPane.showMessageDialog(new JFrame(), "Tiempo transcurrido: "+fin+"\nSe ha creado el archivo correctamente: "+nombreLimpio                        +"\nEn la carpeta:\n"+carpetaFicheros);
                }
            } else {
                String textoProcesado = leerPagina();
                ArrayList<String[]> diferenciaArtículos = compararArchivoYCodigo(archivo, textoProcesado);
                if (false)
                //if (!diferenciaArtículos.isEmpty())
                {
                    int cantidadNuevos = diferenciaArtículos.size();
                    System.out.println("msg se añadden nuevo libros "+ cantidadNuevos);
                    String nuevosTexto = "";
                    //Rellenar string nuevosTexto para mostrarlo en JOptionPane
                    for (int i = 0; i <= cantidadNuevos; i++)
                    {
                        nuevosTexto += diferenciaArtículos.get(i)[0] + " -- ";
                        nuevosTexto += diferenciaArtículos.get(i)[1] + " -- ";
                        nuevosTexto += diferenciaArtículos.get(i)[2] + " -- ";
                        nuevosTexto += diferenciaArtículos.get(i)[3] + " -- ";
                        nuevosTexto += diferenciaArtículos.get(i)[4] + "\n" ;
                    }

                    textoProcesado += nuevosTexto;
                    String[] botones = {"Visitar página", "En otro momento"};
                    int respuesta = 1;// JOptionPane.showOptionDialog(null, "Hay "+cantidadNuevos+" novedades en abrigos:\n"  +nuevosTexto, "Nuevos abrigos", JOptionPane.INFORMATION_MESSAGE, 1, null, botones, botones[0]);
                    if (respuesta == 0)
                    {
	        			/*Se pueden abrir pestañas de firefox de 2 en 2. Hay que añadir un espacio entre dos URL
	        			Si se abren más de 2 a la vez, abre una nueva ventana con todas las pestañas.
	        			Yo prefiero que se vayan abriendo poco a poco en la misma ventana que estoy*/
                        try {
                            for (int x = 0; x < diferenciaArtículos.size(); x++) {
                                String dirAbrigo = diferenciaArtículos.get(x)[1].trim();
                                Process p = Runtime.getRuntime().exec("firefox -new-tab http://www.esprit.es/" + dirAbrigo);
                                Thread.sleep(1000);
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        if (escribirArchivo(textoProcesado, archivo))
                        {
                            String ar[] = nombreArchivo.split("/");
                            String nombreLimpio = ar[(ar.length - 1)];
                            long fin = System.currentTimeMillis() - inicio;
                            //JOptionPane.showMessageDialog(new JFrame(), "Tiempo transcurrido: "+fin+"\nSe ha creado el archivo correctamente: "+nombreLimpio                        +"\nEn la carpeta:\n"+carpetaFicheros);
                        }
                    }
                } else {

                    if (escribirArchivo(textoProcesado, archivo))
                    {
                        String ar[] = nombreArchivo.split("/");
                        String nombreLimpio = ar[(ar.length - 1)];
                        long fin = System.currentTimeMillis() - inicio;
                        //JOptionPane.showMessageDialog(new JFrame(), "Tiempo transcurrido: "+fin+"\nSe ha creado el archivo correctamente: "+nombreLimpio                        +"\nEn la carpeta:\n"+carpetaFicheros);
                    }
                }
            }
        } else{
            while (archivo.exists())
            {
                archivo.delete();
            }
            // si se ha realizado una busqueda reemplazamos el archivo existente

            String textoProcesado = leerPagina();
            if (escribirArchivo(textoProcesado, archivo))
            {
                long fin = System.currentTimeMillis() - inicio;
                String ar[] = nombreArchivo.split("/");
                String nombreLimpio = ar[(ar.length - 1)];
            }
        }
    }

    public String leerPagina()
    {
        String dg = "";
        System.out.println("msg url " + buscadorUrl);


        if ( buscadorUrl.contains("gratismas") )
        {
            System.out.println("msg url 2" + buscadorUrl);
            dg = leerPagina_gratismas();
        }
        if ( buscadorUrl.contains("lectulandia") )
        {
            System.out.println("msg url 3" + buscadorUrl);
            dg = leerPagina_lectulandia();
        }
        if ( buscadorUrl.contains("librosparadescargar") )
        {
            System.out.println("msg url 4 " + buscadorUrl);
            dg = leerLibrosparadescargar();
        }
        return dg;
    }

    public String leerLibrosparadescargar()
    {
        documentoProcesadoAnterior="";
        documentoAGuardar="";
        int x=pagina;
        String stringUrl=ruta;
        Boolean iguales = false;
        int elemValidos = 0;

        do{
            documentoProcesado="";
            if (pruebas==1 && x>=1)
            {
                url = url.replace( String.valueOf(x-1), String.valueOf(x)) ;
            }else{
                url =stringUrl;
            }
            System.out.println("MSG url " + url + " PRUEBAS "+ pruebas);

            int veces = 0;
            int elem = 0;
            int y= 0;

            do {
                getHtmlDocument();

                if (mDocument!=null)
                {
                    veces=50;
                }else{
                    veces++;
                    System.out.println("MSG intento ("+veces+") "+url);
                    tareaLarga();tareaLarga();
                }
            }while (veces<50);



            Elements paginas = mDocument.select("article.latestPost.excerpt");
            elem = paginas.size();
            elemValidos = elem;
            String[] imag = new String[elem];
            String[] titu = new String[elem];
            String[] auto = new String[elem];
            String[] resu = new String[elem];
            String[] enla = new String[elem];
            String[] cate = new String[elem];

            for (Element pagina:paginas)
            {
                enla[y] = pagina.getElementsByTag("a").attr("href");
                System.out.println("msg imagen lpd " + imag[y]);
                imag[y] = pagina.getElementsByTag("img").attr("src");
                System.out.println("msg enlace lpd " + enla[y]);

                auto[y] = "desconocido";
                cate[y] = "Libros";

                Elements titulos = pagina.select("h2.title.front-view-title");
                int z = titulos.size();
                int zz = 0;
                for (Element titulo:titulos)
                {
                    //if (zz==y) {
                        titu[y] = titulo.getElementsByTag("a").attr("title");
                        //break;
                    //}
                    //zz++;
                }
                Elements resumenes = pagina.getElementsByClass("front-view-content");
                resu[y] = resumenes.text();

                y++;

            }

            for (y = 0; y < elem; y++)
            {
                if (cate[y].equals("Libros"))
                {
                    documentoProcesado += titu[y] + " -- " + auto[y] + " -- " + enla[y] + " -- "
                            + resu[y] + " -- " + imag[y] + "\n";
                    System.out.println("msg libro (" + y + ") " + imag[y]);
                    CargaImagenes nuevaTarea = new CargaImagenes();
                    nuevaTarea.execute(imag[y]);
                }
            }

            if (pruebas==0) iguales=true;

            if(!documentoProcesado.equals(documentoProcesadoAnterior)){
                documentoProcesadoAnterior = documentoProcesado;
                documentoAGuardar += documentoProcesado;
                x++;
                if (x==2) iguales=true;
            }else{
                iguales=true;
            }

        }while (!iguales);
        return documentoAGuardar;
    }

    /**
     * Lee la página que pasemos como parámetro, filtra el texto y devuelve el resultado
     * @return
     */
    public String leerPagina_gratismas(){
        //String  documentoProcesado, documentoProcesadoAnterior, documentoAGuardar;
        documentoProcesadoAnterior="";
        documentoAGuardar = "";
        int x=pagina;
        String stringUrl = ruta;
        Boolean iguales = false;
    	/*
    	 * Esta web, devuelve contenido repetido si intentamos leer un indice de página que no existe.
    	 * Si solo hay 2 páginas e intentamos leer la 3, nos volverá a enseñar la 2.
    	 * Leemos hasta que se repita el contenido
    	 */
        do{
            documentoProcesado = "";
            if(pruebas ==1 && x>1)
            {
                //url = String.format(stringUrl+sufijoPaginacion,x);
                //url = stringUrl;
                url = url.replace(String.valueOf(x-1),String.valueOf(x));

            }else{
                url = stringUrl;
            }
            System.out.println("MSG url " + url + " PRUEBAS "+ pruebas);

            int veces = 0;

            do {
                getHtmlDocument();

                if (mDocument!=null)
                {
                    veces=50;
                }else{
                    veces++;
                    System.out.println("MSG intento ("+veces+") "+url);
                    tareaLarga();tareaLarga();
                }
            }while (veces<50);

            int salir = 0;
            int elem = 0;
            int elemValidos = 0;
            Elements paginas = mDocument.getElementsByClass("blog-layout clearfix blog-layout3");
            for (Element pagina : paginas) {

                int y;
                Elements imagenes = pagina.getElementsByClass("entry-image");
                elem = imagenes.size();
                elemValidos = elem;
                String[] imag = new String[elem];
                String[] titu = new String[elem];
                String[] auto = new String[elem];
                String[] resu = new String[elem];
                String[] enla = new String[elem];
                String[] cate = new String[elem];

                Elements categorias = pagina.getElementsByClass("entry-category");
                y = 0;
                for (Element categoria : categorias) {
                    cate[y] = categoria.text();
                    y++;
                }

                y = 0;
                for (Element imagen : imagenes) {
                    imag[y] = imagen.getElementsByClass("attachment-medium size-medium wp-post-image")
                            .attr("src");
                    y++;
                }

                y = 0;
                Elements titulos = pagina.getElementsByClass("entry-title");
                for (Element titulo : titulos) {
                    titu[y] = titulo.getElementsByTag("a").attr("title");
                    int posicion = titu[y].indexOf("[.");
                    if (posicion != -1) titu[y] = titu[y].substring(0, posicion);
                    posicion = titu[y].indexOf(" – ");
                    if (posicion != -1) {
                        String[] tmpT = titu[y].split(" – ");
                        titu[y] = tmpT[0].trim();
                        auto[y] = tmpT[1].trim();
                    }
                    posicion = titu[y].indexOf("[");
                    if (posicion != -1) titu[y] = titu[y].substring(0, posicion);
                    System.out.println("Msg: Titulo añadido " + titu[y]);
                    enla[y] = titulo.getElementsByTag("a").attr("href");
                    y++;
                }
                y = 0;
                Elements resumenes = pagina.getElementsByClass("entry-summary");
                for (Element resumen : resumenes) {
                    resu[y] = resumen.text();
                    y++;
                }

                for (y = 0; y < elem; y++)
                {
                    if (cate[y].equals("Libros"))
                    {
                        documentoProcesado += titu[y] + " -- " + auto[y] + " -- " + enla[y] + " -- "
                                + resu[y] + " -- " + imag[y] + "\n";
                        System.out.println("msg libro (" + y + ") " + imag[y]);
                        CargaImagenes nuevaTarea = new CargaImagenes();
                        nuevaTarea.execute(imag[y]);
                    }
                }
            }

            //CargaLibros cargaLibros = new CargaLibros();
            //cargaLibros.execute();

            if (pruebas==0) iguales=true;

            if(!documentoProcesado.equals(documentoProcesadoAnterior)){
                documentoProcesadoAnterior = documentoProcesado;
                documentoAGuardar += documentoProcesado;
                x++;
                if (x==2) iguales=true;
            }else{
                iguales=true;
            }

        }while(!iguales);
        return documentoAGuardar;
    }

    public String leerPagina_lectulandia(){
        //String  documentoProcesado, documentoProcesadoAnterior, documentoAGuardar;
        documentoProcesadoAnterior="";
        documentoAGuardar = "";
        int x=pagina;
        String stringUrl = ruta;
        Boolean iguales = false;

        do{
            documentoProcesado = "";
            if(pruebas ==1 && x>1)
            {
                //url = String.format(stringUrl+sufijoPaginacion,x);
                //url = stringUrl;
                url = url.replace(String.valueOf(x-1),String.valueOf(x));
            }else{
                url = stringUrl;
            }

            System.out.println("MSG url " + url + " sufijoPaginacion "+ sufijoPaginacion + " (x) "+ x);

            int veces = 0;

            do {
                getHtmlDocument();

                if (mDocument!=null)
                {
                    veces=50;
                }else{
                    veces++;
                    System.out.println("MSG intento ("+veces+") "+url);
                    tareaLarga();tareaLarga();
                }
            }while (veces<50);

            int elem = 0;
            int y = 0;
            Elements paginas = mDocument.getElementsByClass("card");
            for (Element pagina : paginas)
            {
                elem = paginas.size();

                String[] imag = new String[elem];
                String[] titu = new String[elem];
                String[] auto = new String[elem];
                String[] resu = new String[elem];
                String[] enla = new String[elem];
                String[] cate = new String[elem];


                Elements imagenes = pagina.getElementsByClass("cover");
                imag[y] =  "https:"+ imagenes.attr("src");
                titu[y] = imagenes.attr("title");

                Elements enlaces = pagina.getElementsByClass("card-click-target");
                enla[y] = enlaces.attr("href");
                Elements autores = pagina.getElementsByClass("subdetail");
                auto[y] = autores.text();
                Elements resumenes = pagina.getElementsByClass("description");
                resu[y] = resumenes.text();

                documentoProcesado += titu[y] + " -- " + auto[y] + " -- " + enla[y] + " -- "
                        + resu[y] + " -- " + imag[y] + "\n";
                System.out.println("msg libro (" + y + ") " + imag[y]);
                CargaImagenes nuevaTarea = new CargaImagenes();
                nuevaTarea.execute(imag[y]);

                y++;
            }


            //CargaLibros cargaLibros = new CargaLibros();
            //cargaLibros.execute();

            if (pruebas==0) iguales=true;

            if(!documentoProcesado.equals(documentoProcesadoAnterior)){
                documentoProcesadoAnterior = documentoProcesado;
                documentoAGuardar += documentoProcesado;
                x++;
                if (x==2) iguales=true;
            }else{
                iguales=true;
            }

        }while(!iguales);
        return documentoAGuardar;
    }

    /**
     * Escribe un texto dentro del archivo
     * @param archivo
     * @param textoProcesado
     * @return
     */
    public Boolean escribirArchivo(String textoProcesado, File archivo){
        FileWriter escritor = null;
        Boolean escribeBien;
        try{
            escritor = new FileWriter(archivo);
            escritor.write(textoProcesado);
            escribeBien = true;
        }catch(FileNotFoundException e){
            System.out.println("No existe el fichero o la carpeta");
            //JOptionPane.showMessageDialog(new JFrame(), "Ha ocurrido algún error creando el nuevo fichero"                    +" \n\nEn la carpeta:\n"+carpetaFicheros);
            escribeBien = false;
        }catch(IOException e){
            //JOptionPane.showMessageDialog(new JFrame(), "Ha ocurrido algún error creando el nuevo fichero"                    +" \n\nEn la carpeta:\n"+carpetaFicheros);
            escribeBien = false;
        }finally{
            if (escritor != null){
                try {
                    escritor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return escribeBien;
    }

    /**
     * Compara el texto del archivo donde guardamos con otra cadena
     * @param fil
     * @param textoProcesado
     * @return
     */
    public ArrayList<String[]> compararArchivoYCodigo(File fil, String textoProcesado){
        FileReader archivo;
        BufferedReader lector = null;
        ArrayList<String[]> diferencia = new ArrayList<String []>();
        try{
            BufferedReader reader = new BufferedReader(new StringReader(textoProcesado));
            archivo = new FileReader(fil);
            lector = new BufferedReader(archivo);
            String linea1, linea2;
            while ((linea1 = lector.readLine()) != null){
                linea2=reader.readLine();
                System.out.println("msg linea1: "+ linea1);
                System.out.println("msg linea2: "+ linea2);
                if(!linea1.equals(linea2)){
                    String[] datos= linea1.split(" -- ");
                    diferencia.add(datos);
                }
            }
        }catch(FileNotFoundException e){
            e.getMessage();
        }catch (IOException e) {
            e.getMessage();
        }finally{
            if (lector != null){
                try {
                    lector.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return diferencia;
    }

    /**
     * Con esta método compruebo el Status code de la respuesta que recibo al hacer la petición
     * EJM:
     * 		200 OK			300 Multiple Choices
     * 		301 Moved Permanently	305 Use Proxy
     * 		400 Bad Request		403 Forbidden
     * 		404 Not Found		500 Internal Server Error
     * 		502 Bad Gateway		503 Service Unavailable
     * @return Status Code
     */
    public void getStatusConnectionCode() {

        //Connection.Response response = null;
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                try {
                    response = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
                } catch (IOException ex) {
                    System.out.println("Excepción al obtener el Status Code: " + ex.getMessage());
                }
            }
        }).start();
        //return response.statusCode();
    }

    /**
     * Con este método devuelvo un objeto de la clase Document con el contenido del
     * HTML de la web que me permitirá parsearlo con los métodos de la librelia JSoup
     * @return Documento con el HTML
     */
    public void getHtmlDocument()
    {
        // fuente :  https://medium.com/@ssaurel/learn-to-parse-html-pages-on-android-with-jsoup-2a9b0da0096f
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
    }



    private void tareaLarga()
    {
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {}
    }

    private class CargaImagenes extends AsyncTask<String, Void, Bitmap> {

        ProgressDialog pDialog;
        Bitmap result;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            borrarDirectorio();
            /*pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Cargando Imagen");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();*/

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            Log.i("doInBackground" , "Entra en doInBackground");
            String url = params[0];
            Bitmap imagen = descargarImagen(url);

            return imagen;
        }

        //@Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            guardarImagen(result);

            //pDialog.dismiss();
        }
    }

    private Bitmap descargarImagen (String imageHttpAddress)
    {
        URL imageUrl = null;
        Bitmap imagen = null;
        try{
            imageUrl = new URL(imageHttpAddress);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            imagen = BitmapFactory.decodeStream(conn.getInputStream());
        }catch(IOException ex){
            System.out.println("msg libro guardado sin descargar("+numLibro+") ");

            ex.printStackTrace();
        }
        while (imagen == null)
        {
            System.out.println("msg libro guardado ("+numLibro+") esperar");
            tareaLarga();
        }
        return imagen;
    }

    private void guardarImagen(Bitmap imagen)
    {

        File rutaArchivo = Environment.getExternalStorageDirectory();
        rutaArchivo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String storagePath = "/imagenes/";

        File cfImagenes = new File(rutaArchivo + storagePath);
        if (cfImagenes.isDirectory() == false)
        {
            cfImagenes.mkdir();
        }else{

            String imgNombre = "libro"+ numLibro + ".jpg";
            File file = new File(cfImagenes, imgNombre);
            System.out.println("msg libro guardado ("+numLibro+") "+imgNombre);

            if (imagen != null)
                System.out.println("msg libro guardado vacio");
            if (file.exists() == false)
            {
                try{
                    FileOutputStream out = new FileOutputStream(file);
                    imagen.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    numLibro++;
                }catch (IOException ex){
                    System.out.println("msg libro guardado error ("+numLibro+") "+imgNombre);

                    ex.printStackTrace();
                }
            }else{
                System.out.println("msg libro guardado existe ("+numLibro+") "+imgNombre);
            }
        }
    }

    public void borrarDirectorio()
    {
        File rutaArchivo = Environment.getExternalStorageDirectory();
        rutaArchivo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String storagePath = "/imagenes/";
        File directorio = new File(rutaArchivo + storagePath);

        if (directorio.exists()) {
            File[] ficheros = directorio.listFiles();
            System.out.println("msg ficheros " + ficheros.length);

            for (int x = 0; x < ficheros.length; x++) {
                ficheros[x].delete();
            }

        }
    }

    private class CargaLibros extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Cargando Libros ...");
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
        protected Void doInBackground(Void... voids)
        {
            procesoLibros();
            return null;
        }
    }

    public void procesoLibros()
    {
        int salir = 0;
        int elem = 0;
        int elemValidos = 0;
        Elements paginas = mDocument.getElementsByClass("blog-layout clearfix blog-layout3");
        for (Element pagina : paginas) {

            int y;
            Elements imagenes = pagina.getElementsByClass("entry-image");
            elem = imagenes.size();
            elemValidos = elem;
            String[] imag = new String[elem];
            String[] titu = new String[elem];
            String[] auto = new String[elem];
            String[] resu = new String[elem];
            String[] enla = new String[elem];
            String[] cate = new String[elem];

            Elements categorias = pagina.getElementsByClass("entry-category");
            y = 0;
            for (Element categoria : categorias) {
                cate[y] = categoria.text();
                y++;
            }

            y = 0;
            for (Element imagen : imagenes) {
                imag[y] = imagen.getElementsByClass("attachment-medium size-medium wp-post-image")
                        .attr("src");
                y++;
            }

            y = 0;
            Elements titulos = pagina.getElementsByClass("entry-title");
            for (Element titulo : titulos) {
                titu[y] = titulo.getElementsByTag("a").attr("title");
                int posicion = titu[y].indexOf("[.");
                if (posicion != -1) titu[y] = titu[y].substring(0, posicion);
                posicion = titu[y].indexOf(" – ");
                if (posicion != -1) {
                    String[] tmpT = titu[y].split(" – ");
                    titu[y] = tmpT[0].trim();
                    auto[y] = tmpT[1].trim();
                }
                posicion = titu[y].indexOf("[");
                if (posicion != -1) titu[y] = titu[y].substring(0, posicion);
                System.out.println("Msg: Titulo añadido " + titu[y]);
                enla[y] = titulo.getElementsByTag("a").attr("href");
                y++;
            }
            y = 0;
            Elements resumenes = pagina.getElementsByClass("entry-summary");
            for (Element resumen : resumenes) {
                resu[y] = resumen.text();
                y++;
            }

            int yy = 0;
            for (y = 0; y < elem; y++)
            {
                if (!cate[y].equals("Libros"))
                {
                    documentoProcesado += titu[yy] + " -- " + auto[yy] + " -- " + enla[yy] + " -- " + resu[yy] + " -- " + imag[yy] + "\n";
                    System.out.println("msg libro (" + yy + ") " + imag[yy]);
                    CargaImagenes nuevaTarea = new CargaImagenes();
                    nuevaTarea.execute(imag[yy]);
                    yy++;
                }
            }
        }

    }

}
