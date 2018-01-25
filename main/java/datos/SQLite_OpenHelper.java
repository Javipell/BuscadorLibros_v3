package datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;

/**
 * Created by javier on 22/12/17.
 */

public class SQLite_OpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dba_datos";
    private static final String TABLE_NAME = "tbl_datos";
    private static final int DATABASE_VERSION = 1;

    public static final String COL_ID = "_id";
    public static final String COL_TITULO = "titulo";
    public static final String COL_AUTOR = "autor";
    public static final String COL_RESUMEN = "resumen";
    public static final String COL_ENLACE = "enlace";
    public static final String COL_IMAGEN = "imagen";
    public static final String COL_FECHA = "fecha";

    public SQLite_OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String DATABASE_CREATE =
                "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                        COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
                        COL_TITULO + " TEXT, " +
                        COL_AUTOR + " TEXT, " +
                        COL_RESUMEN + " TEXT, " +
                        COL_ENLACE + " TEXT, " +
                        COL_IMAGEN + " TEXT, " +
                        COL_FECHA + " INT " +
                        " );";
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void abrir()
    {
        this.getReadableDatabase();
    }

    public void cerrar()
    {
        this.close();
    }

    public Cursor leerRegistroId(SQLiteDatabase db, int id)
    {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE _id =" + id;
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst())
        {
            Log.w("Fallo busqueda id ", sql);
        }
        return c;
    }

    public void insertarRegistro( String titulo, String autor, String resumen, String enlace,
                                  String imagen, String fecha )
    {
        ContentValues values = new ContentValues();
        values.put("Titulo", titulo);
        values.put("Autor", autor);
        values.put("Resumen", resumen);
        values.put("Imagen", imagen);
        values.put("Fecha", fecha);

        this.getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    public boolean insertarRegistro2(SQLiteDatabase db, EstructuraDatos datos)
    {
        boolean insertado = false;
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE titulo='" + datos.get_titulo() + "'";
        Cursor c  = db.rawQuery(sql, null);
        if (c.moveToFirst())
        {
            Log.w("Fallo ", sql);
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put("Titulo", datos.get_titulo());
            values.put("Autor", datos.get_autor());
            values.put("Resumen", datos.get_resumen());
            values.put("Imagen", datos.get_imagen());
            values.put("Fecha", datos.get_imagen());

            this.getWritableDatabase().insert(TABLE_NAME, null, values);
            Log.w("insertado", sql);
            insertado = true;
        }
        return insertado;
    }

    public boolean borrarRegistros(SQLiteDatabase db, int id)
    {
        boolean error = false;
        String condicion = "_Id=" + id;
        try{
            db.delete(TABLE_NAME, condicion, null);
        }catch (Exception ex)
        {
            Log.w("Error borrado", ex.getMessage());
            error=true;
        }
        return error;
    }
    public boolean actualizarRegistro(SQLiteDatabase db, EstructuraDatos datos)
    {
        boolean error = false;
        ContentValues values = new ContentValues();
        values.put("Titulo", datos.get_titulo());
        values.put("Autor", datos.get_autor());
        values.put("Resumen", datos.get_resumen());
        values.put("Imagen", datos.get_imagen());
        values.put("Fecha", datos.get_imagen());
        try{
            String condicion =  "_Id=" + datos.get_Id();
            int cant = db.update(TABLE_NAME, values, condicion + datos.get_Id(), null);
            if (cant == 1)
            {
                Log.w("resultado", ""+cant);
            }
            Log.w("Condicion", condicion);
        }catch (Exception ex)
        {
            Log.w("Error actualizar", ex.getMessage());
            error=true;
        }
        return error;
    }

    public boolean duplicado2(SQLiteDatabase db, EstructuraDatos datos)
    {
        boolean insertado = false;
        String txtdato = datos.get_titulo();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE titulo='" + datos.get_titulo() + "'";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst())
        {
            Log.w("fallo", sql);
        }
        else
        {
            insertarRegistro(
                    datos.get_titulo(),
                    datos.get_autor(),
                    datos.get_resumen(),
                    datos.get_url(),
                    datos.get_imagen(),
                    String.valueOf(datos.get_year())
            );
            Log.w("insertado", sql);
            insertado = true;
        }
        return insertado;
    }

    public void duplicado(SQLiteDatabase db, EstructuraDatos datos, View view)
    {
        String txtdato = datos.get_titulo();
        String sql = "SELECT * FROM tbl_datos WHERE titulo='" + datos.get_titulo() + "'";

        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst())
        {
            Log.w("fallo",sql);
        }
        else{
            insertarRegistro(
                    datos.get_titulo(), datos.get_autor(),
                    datos.get_resumen(), datos.get_url(),
                    datos.get_imagen(), String.valueOf(datos.get_year())
            );
            Log.w("insertado", sql);
        }
    }

    public void datosPrueba(SQLiteDatabase db, View view)
    {
        duplicado(db, new EstructuraDatos("titulo1","autor1", 0,
                "url1", "resumen1", "imagen1"), view );
        duplicado(db, new EstructuraDatos("titulo2","autor2", 0,
                "url2", "resumen2", "imagen2"), view );
    }
}
