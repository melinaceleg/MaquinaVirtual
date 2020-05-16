
public class ConstanteDirecta {
	private String valorDirecto;
	private String nombre;
	private int indiceCelda=-1;
	
	public ConstanteDirecta(String nombre, String valorDirecto) {
		this.nombre = nombre;
		this.valorDirecto=valorDirecto;
	
		// TODO Auto-generated constructor stub
	}

	public int getIndiceCelda() {
		return indiceCelda;
	}

	public void setIndiceCelda(int indiceCelda) {
		this.indiceCelda = indiceCelda;
	}

	public String getValorDirecto() {
		return valorDirecto;
	}

	public void setValorDirecto(String valorDirecto) {
		this.valorDirecto = valorDirecto;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}
