package datos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;

import com.javi.pell.buscadorlibros.R;

/**
 * Created by javier on 11/1/18.
 */

public class PreferenciasFragmento extends PreferenceFragment
{
    private static final String KEY_RESULTADO_CORREO_CHK = "resultado_correo";
    private static final String KEY_LISTA_RESUTADOS_LST = "lista_resultados";
    private static final String KEY_LISTA_BUSCADORES_LST = "lista_buscadores";
    private static final String KEY_NOMBRE_USUARIO_ED = "nombre_usuario";
    private static final String KEY_CONTRASEÑA_ED = "contraseña_usuario";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * cargar layout preferencias
         */
        addPreferencesFromResource(R.xml.preferencias);
    }


    /**
     * Recuperar preferencias tipo string
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, final String key)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, "");
    }

    /**
     * Modificar preferencias tipo string
     * @param context
     * @param key
     * @param value
     */
    public static void setString(Context context, final String key, String value)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Modificar preferencias tipo boolean
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(Context context, final String key, final boolean defaultValue)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Recuperar preferencias tipo boolean
     * @param context
     * @param key
     * @param value
     */
    public static void setBoolean(Context context, final String key, final boolean value)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    /**
     * GET AND SET CLAVES O KEY
     * @return
     */
    public static String getKeyResultadoCorreoChk() {
        return KEY_RESULTADO_CORREO_CHK;
    }

    public static String getKeyListaResutadosLst() {
        return KEY_LISTA_RESUTADOS_LST;
    }

    public static String getKeyListaBuscadoresLst() {
        return KEY_LISTA_BUSCADORES_LST;
    }

    public static String getKeyNombreUsuarioEd() {
        return KEY_NOMBRE_USUARIO_ED;
    }

    public static String getKeyContraseñaEd() {
        return KEY_CONTRASEÑA_ED;
    }
}
