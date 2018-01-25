package com.javi.pell.buscadorlibros;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import datos.EstructuraDatos;
import datos.RecyclerViewAdaptador;
import datos.ScrapingFichero;

public class Libro extends AppCompatActivity {

    RecyclerView mRecyclerViewLibro;
    RecyclerViewAdaptador mAdaptadorLibro;

    String buscar = "";
    String pruebas = "0";
    String fichero = "";
    String buscadorUrl = "";
    String buscadorCondicion = "";
    String buscadorPagina = "";
    String buscadorEspacio = "";

    EstructuraDatos mEstructuraDatos;
    List<EstructuraDatos> mEstructuraDatosList = new ArrayList<>();

    String tmpTitulo = "";
    String tmpEnlace = "";
    String tmpAutor = "";
    String tmpResumen = "";
    String tmpImagen = "";
    String tmpYear = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libro);

        Intent intento = getIntent();
        Bundle bundle = intento.getExtras();

        if (bundle!=null)
        {
            buscadorUrl = bundle.getString("buscadorUrl");
            buscadorCondicion = bundle.getString("buscadorCondicion");
            buscadorPagina = bundle.getString("buscadorPagina");
            buscadorEspacio = bundle.getString("buscadorEspacio");
            buscar = bundle.getString("cadenaBuscar");
            pruebas = bundle.getString("cadenaPruebas");
            fichero = bundle.getString("cadenaArchivo");
        }
        Toast.makeText(getApplicationContext(),"Buscando...:  "+ buscar,Toast.LENGTH_SHORT).show();

        mEstructuraDatosList.clear();
        ScrapingFichero scrapingFichero = new ScrapingFichero(buscar,
                Integer.parseInt(pruebas), Libro.this,
                buscadorUrl, buscadorCondicion, buscadorPagina, buscadorEspacio);
        leerFichero();

        mRecyclerViewLibro = (RecyclerView) findViewById(R.id.recyclerLibro);
        // usa esta configuración para mejorar el rendimiento si sabes que los cambios
        // en el contenido no cambia el tamaño del diseño de RecyclerView
        mRecyclerViewLibro.setHasFixedSize(true);
        mRecyclerViewLibro.setLayoutManager(new LinearLayoutManager(this));
        mAdaptadorLibro = new RecyclerViewAdaptador(mEstructuraDatosList, Libro.this);
        // evento on click
        mAdaptadorLibro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Selecciono "
                                + mEstructuraDatosList.get(
                                        mRecyclerViewLibro.getChildAdapterPosition(view)).get_titulo()
                        , Toast.LENGTH_SHORT).show();
                Intent intento = new Intent(getApplicationContext(), VerLibro.class);
                Bundle bundle = new Bundle();
                bundle.putString("cadenaUrl", mEstructuraDatosList.get(
                        mRecyclerViewLibro.getChildAdapterPosition(view)).get_url());
                bundle.putString("cadenaTitulo", mEstructuraDatosList.get(
                        mRecyclerViewLibro.getChildAdapterPosition(view)).get_titulo());
                bundle.putString("buscadorUrl", buscadorUrl);
                intento.putExtras(bundle);
                startActivity(intento);
            }
        });

        mRecyclerViewLibro.setAdapter(mAdaptadorLibro);
    }

    public List<EstructuraDatos> ObtenerCantantes()
    {
        List<EstructuraDatos> cantante = new ArrayList<>();
        cantante.add(new EstructuraDatos("Cantante 1", "nacionalidad 1",0,"URL 1","RESUMEN 1","http://t3.rbxcdn.com/c34f7a5f3d41c0769ee87c00f08493f0"));
        cantante.add(new EstructuraDatos("Cantante 2", "nacionalidad 2",0,"URL 2","RESUMEN 2","http://t3.rbxcdn.com/c34f7a5f3d41c0769ee87c00f08493f0"));
        cantante.add(new EstructuraDatos("Cantante 3", "nacionalidad 3",0,"URL 3","RESUMEN 3","https://image.flaticon.com/icons/png/128/467/467787.png"));
        cantante.add(new EstructuraDatos("Cantante 4", "nacionalidad 4",0,"URL 4","RESUMEN 4","https://image.flaticon.com/icons/png/128/467/467787.png"));
        cantante.add(new EstructuraDatos("Cantante 5", "nacionalidad 5",0,"URL 5","RESUMEN 5","https://image.flaticon.com/icons/png/128/467/467787.png"));

        return cantante;
    }
    public void leerFichero(){
        File rutaArchivo = Environment.getExternalStorageDirectory();
        rutaArchivo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File archivo;
        /*if (pruebas=="0") {
            archivo = new File(rutaArchivo + "/" + "Libros_Buscados.txt");
        }else{
            archivo = new File(rutaArchivo + "/" + "Libros_Nuevos.txt");
        }*/
        archivo = new File(rutaArchivo + "/" + fichero);

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
                    //documentoProcesado += titu[y] + " -- " + auto[y] + " -- " + enla[y] + " -- " + imag[y] + " -- " + resu[y] + "\n";

                    tmpTitulo = campos[0];
                    tmpAutor = campos[1];
                    tmpYear = "0";
                    tmpEnlace = campos[2];
                    tmpResumen = campos[3];
                    tmpImagen = campos[4];

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
                Integer.parseInt(tmpYear), tmpEnlace, tmpResumen, tmpImagen );
        mEstructuraDatosList.add(mEstructuraDatos);
        //miAdaptador = new Adaptador(Resultados.this, mEstructuraDatosList);
        //mListView.setAdapter(miAdaptador);
    }

}
