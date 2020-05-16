
public class LineaInstruccion {
	int operando1;
	int operando2;
	int codInstruccion;

	public LineaInstruccion(int codInstruccion,int operando1, int operando2) {
		// TODO Auto-generated constructor stub
		this.codInstruccion = codInstruccion;
		this.operando1 = operando1;
		this.operando2 =operando2;
	}

	public int getOperando1() {
		return operando1;
	}

	public int getOperando2() {
		return operando2;
	}

	public int getCodInstruccion() {
		return codInstruccion;
	}

}
