package datos;

/**
 * Created by javier on 18/12/17.
 */

public class EstructuraDatos
{
    private int _Id;
    private String _titulo;
    private String _autor;
    private int _year;
    private String _url;
    private String _resumen;
    private String _imagen;

    public EstructuraDatos()
    {
        _Id = 0;
        _titulo = "";
        _autor = "";
        _year = 0;
        _url = "";
        _resumen = "";
        _imagen = "";
    }

    public EstructuraDatos( String titulo, String autor, int year,
                            String url, String resumen, String imagen)
    {
        _titulo = titulo;
        _autor = autor;
        _year = year;
        _url = url;
        _resumen = resumen;
        _imagen = imagen;
    }

    public int get_Id() {
        return _Id;
    }

    public void set_Id(int _Id) {
        this._Id = _Id;
    }

    public String get_titulo() {
        return _titulo;
    }

    public void set_titulo(String _titulo) {
        this._titulo = _titulo;
    }

    public String get_autor() {
        return _autor;
    }

    public void set_autor(String _autor) {
        this._autor = _autor;
    }

    public int get_year() {
        return _year;
    }

    public void set_year(int _year) {
        this._year = _year;
    }

    public String get_url() {
        return _url;
    }

    public void set_url(String _url) {
        this._url = _url;
    }

    public String get_resumen() {
        return _resumen;
    }

    public void set_resumen(String _resumen) {
        this._resumen = _resumen;
    }

    public String get_imagen() {
        return _imagen;
    }

    public void set_imagen(String _imagen) {
        this._imagen = _imagen;
    }
}
