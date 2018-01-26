package datos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.javi.pell.buscadorlibros.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by javier on 19/12/17.
 */

public class Adaptador extends BaseAdapter {

    Context mContext;
    List<EstructuraDatos> ListaDatos;

    public Adaptador(Context contexto, List<EstructuraDatos> listaDatos)
    {
        this.mContext = contexto;
        ListaDatos = listaDatos;
    }

    @Override
    public int getCount()
    {
        return ListaDatos.size();
    }

    @Override
    public Object getItem(int posicion)
    {
        return ListaDatos.get(posicion);
    }

    @Override
    public long getItemId(int posicion)
    {
        return ListaDatos.get(posicion).get_Id();
    }

    @Override
    public View getView(int posicion, View view, ViewGroup viewGroup)
    {
        View vista = view;
        LayoutInflater inflate = LayoutInflater.from(mContext);
        vista = inflate.inflate(R.layout.resumen_libro, null);
        TextView textView_titulo = vista.findViewById(R.id.textView_titulo);
        ImageView imageButton_caratula = vista.findViewById(R.id.imageButton_caratula);
        //TextView textViewEnlace = vista.findViewById(R.id.textViewEnlace);
        TextView textViewAutor = vista.findViewById(R.id.textViewAutor);
        //TextView textViewYear = vista.findViewById(R.id.textViewYear);
        TextView textViewResumen = vista.findViewById(R.id.textViewResumen);

        textView_titulo.setText(ListaDatos.get(posicion).get_titulo());
        //textViewEnlace.setText(ListaDatos.get(posicion).get_url());
        textViewAutor.setText(ListaDatos.get(posicion).get_autor());
        /*if (ListaDatos.get(posicion).get_year()!=0) {
            textViewYear.setText(String.valueOf(ListaDatos.get(posicion).get_year()));
        }else{
            textViewYear.setText("");
        }*/
        textViewResumen.setText(ListaDatos.get(posicion).get_resumen());
        String url = ListaDatos.get(posicion).get_imagen();


        try {
            Picasso.with(mContext)
                    .load(url)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .error(R.drawable.ic_menu_camera)
                    .noFade()
                    .into(imageButton_caratula);
        }catch (Exception ex)
        {
            url = "https://image.freepik.com/iconos-gratis/camara-de-la-foto-herramienta-negro_318-70440.jpg";
            Picasso.with(mContext)
                    .load(url)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .error(R.drawable.ic_menu_camera)
                    .noFade()
                    .into(imageButton_caratula);
        }
        return vista;
    }
}
