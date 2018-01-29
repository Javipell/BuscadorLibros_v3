package datos;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.javi.pell.buscadorlibros.R;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by javier on 7/1/18.
 */

public class RecyclerViewAdaptador extends RecyclerView.Adapter<RecyclerViewAdaptador.ViewHolder>
        implements View.OnClickListener {

    Context mContext;
    ImageView mImageView;
    File rutaImagenes;

    // ***************
    // fuente para implementar OnclickListerner en RecyclerView
    // https://www.youtube.com/watch?v=ZBEtt2rNS6M
    private View.OnClickListener mListener;

    public void setOnClickListener(View.OnClickListener listener)
    {
        mListener = listener;
    }

    @Override
    public void onClick(View view)
    {
        if (mListener != null)
            mListener.onClick(view);
    }

    // *************

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView titulo, autor, enlace, resumen, year;
        private ImageView foto;

        public ViewHolder(View itemView) {
            super(itemView);
            titulo = (TextView) itemView.findViewById(R.id.tv_titulo);
            autor = (TextView) itemView.findViewById(R.id.tv_autor);
            foto = (ImageView) itemView.findViewById(R.id.imageView_foto);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView_foto);
            enlace = (TextView) itemView.findViewById(R.id.tv_enlace);
            resumen = (TextView) itemView.findViewById(R.id.tv_resumen);
            year = (TextView) itemView.findViewById(R.id.tv_year);
        }
    }

    public List<EstructuraDatos> mEstructuraDatosList = new ArrayList<>();


    public RecyclerViewAdaptador(List<EstructuraDatos> estructuraDatos, Context context) {
        this.mEstructuraDatosList = estructuraDatos;
        mContext = context;
        File rutaArchivo = Environment.getExternalStorageDirectory();
        rutaArchivo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String storagePath = "/imagenes/";
        rutaImagenes = new File(rutaArchivo + storagePath);

        //listado();
    }

    public void listado()
    {
        for (int x=0; x<mEstructuraDatosList.size(); x++ )
        {

            System.out.println("valores: ("+ x + ") " + mEstructuraDatosList.get(x).get_titulo());
            System.out.println("valores: ("+ x + ") " + mEstructuraDatosList.get(x).get_imagen());

            CargaImagenes nuevaTarea = new CargaImagenes();
            nuevaTarea.execute(mEstructuraDatosList.get(x).get_imagen());

            //Bitmap bitmap = guardaImagen3(mEstructuraDatosList.get(x).get_imagen());
            //guardaImagen(mEstructuraDatosList.get(x).get_imagen(),x);
            /*try {
                guardarImagen2();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }


    @Override
    public RecyclerViewAdaptador.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_libro, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdaptador.ViewHolder holder, int position) {
        String urlFoto = mEstructuraDatosList.get(position).get_imagen();

        holder.titulo.setText(mEstructuraDatosList.get(position).get_titulo());
        holder.autor.setText(mEstructuraDatosList.get(position).get_autor());
        //holder.enlace.setText(mEstructuraDatos.get(position).get_url());
        //holder.enlace.setText(""+position);
        holder.resumen.setText(mEstructuraDatosList.get(position).get_resumen());

        /*if (mEstructuraDatos.get(position).get_year()!=0) {
            holder.year.setText(String.valueOf(mEstructuraDatos.get(position).get_year()));
        }else{
            holder.year.setText("");
        }*/
        String ruta = String.valueOf(rutaImagenes) + "/libro" + position + ".jpg";
        System.out.println("msg foto "+ ruta);

        Bitmap bmImg = BitmapFactory.decodeFile(ruta);
        holder.foto.setImageBitmap(bmImg);
        System.out.println("msg fotos (" + position + ") " + ruta);

        /*try {
            //Picasso.with(mContext).load(urlFoto).into(mImageView);
            Picasso.with(mContext).load(urlFoto)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .error(R.drawable.ic_menu_camera)
                    .noFade()
                    .into(mImageView);
        }catch (Exception e)
        {
            holder.foto.setImageResource(R.drawable.uno);
        }*/
    }

    @Override
    public int getItemCount() {
        return mEstructuraDatosList.size();
    }

    private class CargaImagenes extends AsyncTask<String, Void, Bitmap> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Cargando Imagen");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            Log.i("doInBackground" , "Entra en doInBackground");
            String url = params[0];
            Bitmap imagen = descargarImagen(url);
            return imagen;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            mImageView.setImageBitmap(result);
            pDialog.dismiss();
        }
    }

    private Bitmap descargarImagen (String imageHttpAddress){
        URL imageUrl = null;
        Bitmap imagen = null;
        try{
            imageUrl = new URL(imageHttpAddress);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            imagen = BitmapFactory.decodeStream(conn.getInputStream());
        }catch(IOException ex){
            ex.printStackTrace();
        }

        return imagen;
    }


}
