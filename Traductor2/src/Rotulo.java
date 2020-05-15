
public class Rotulo {
	private String nombre;
	private int nLinea;
	public Rotulo(String nombre, int cantLineas) {
		// TODO Auto-generated constructor stub
		this.nombre=nombre;
		this.nLinea=cantLineas;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getnLinea() {
		return nLinea;
	}
	public void setnLinea(int nLinea) {
		this.nLinea = nLinea;
	}

}
