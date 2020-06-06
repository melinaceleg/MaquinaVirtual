import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

public class Traductor implements constantesDePrograma {
	private String linea;
	private String lineaOrg;
	private int cantConstantes;
	private int lineaActual=0;
	private boolean verifTipoDato;
	private int cantLineas = 0;
	private int celdasActuales=0;
	private int espacioDATA=500;
	private int espacioSTACK=500;
	private int espacioCODE;
	private int espacioEXTRA=500;
	private int espacioCONSTANTES=0;
	private int espacioPROGRAMA=0;
	private boolean generaIMG = true; /// por defecto, cambiara si una linea es es invalida


	private static Traductor instance = null;

	ArrayList<Rotulo> rotulos = new ArrayList<Rotulo>();
	ArrayList<Integer> codeSegment = new ArrayList<Integer>();
	ArrayList<Constante> constantes = new ArrayList<Constante>();
	ArrayList<ConstanteDirecta> directas = new ArrayList<ConstanteDirecta>();

	private Registro[] registros = new Registro[16];
	private String[] mnemonicos = new String[144];
	private int[] memoria = new int[8192]; ///memoria total

	public int[] getMemoria() {
		return memoria;
	}

	public Registro[] getRegistros() {
		return registros;
	}

	public void setRegistros(Registro[] registros) {
		this.registros = registros;
	}
	
	public String getLineaOrg() {
		return lineaOrg;
	}

	public void setLineaOrg(String lineaOrg) {
		this.lineaOrg = lineaOrg;
	}

	public boolean isGeneraIMG() {
		return generaIMG;
	}
	public int getCeldasActuales() {
		return celdasActuales;
	}

	public void setCeldasActuales(int celdasActuales) {
		this.celdasActuales = celdasActuales;
	}
	public int getLineaActual() {
		return lineaActual;
	}
	public void setLineaActual(int lineaActual) {
		this.lineaActual = lineaActual;
	}
	private void iniciaCodigos() {
		
		mnemonicos[0x01] = "MOV";
		mnemonicos[0x02] = "ADD";
		mnemonicos[0x03] = "SUB";
		mnemonicos[0x04] = "MUL";
		mnemonicos[0x05] = "DIV";
		mnemonicos[0x06] = "MOD";
		mnemonicos[0x13] = "CMP";
		mnemonicos[0x17] = "SWAP";
		mnemonicos[0x19] = "RND";
		mnemonicos[0x20] = "JMP";
		mnemonicos[0x21] = "JE";
		mnemonicos[0x22] = "JG";
		mnemonicos[0x23] = "JL";
		mnemonicos[0x24] = "JZ";
		mnemonicos[0x25] = "JP";
		mnemonicos[0x26] = "JN";
		mnemonicos[0x27] = "JNZ";
		mnemonicos[0x28] = "JNP";
		mnemonicos[0x29] = "JNN";
		mnemonicos[0x31] = "AND";
		mnemonicos[0x32] = "OR";
		mnemonicos[0x33] = "NOT";
		mnemonicos[0x34] = "XOR";
		mnemonicos[0x37] = "SHL";
		mnemonicos[0x38] = "SHR";
		mnemonicos[0x40] = "CALL";
		mnemonicos[0x44] = "PUSH";
		mnemonicos[0x45] = "POP";
		mnemonicos[0x48] = "RET";
		mnemonicos[0x50] = "SLEN";
		mnemonicos[0x51] = "SMOV";
		mnemonicos[0x53] = "SCMP";
		mnemonicos[0x81] = "SYS";
		mnemonicos[0x8F] = "STOP";

	}

	private void inicRegistros() {
		for (int i = 0; i < registros.length; i++) {
			registros[i] = new Registro();
		}

		registros[PS].setNombre("PS");
		registros[PS].setValor(0);
		registros[CS].setNombre("CS");
		registros[CS].setValor(0);
		registros[DS].setNombre("DS");
		registros[DS].setValor(0);
		registros[ES].setNombre("ES");
		registros[ES].setValor(500);
		registros[IP].setNombre("IP");
		registros[SS].setNombre("SS");
		registros[SS].setValor(500);
		registros[SP].setNombre("SP");
		registros[SP].setValor(0);
		registros[BP].setNombre("BP");
		registros[BP].setValor(0);
		registros[AC].setNombre("AC");
		registros[AC].setValor(0);
		registros[CC].setNombre("CC");
		registros[CC].setValor(0);
		registros[AX].setNombre("AX");
		registros[AX].setValor(0);
		registros[BX].setNombre("BX");
		registros[BX].setValor(0);
		registros[CX].setNombre("CX");
		registros[CX].setValor(0);
		registros[DX].setNombre("DX");
		registros[DX].setValor(0);
		registros[EX].setNombre("EX");
		registros[EX].setValor(0);
		registros[FX].setNombre("FX");
		registros[FX].setValor(0);

	}

	private Traductor() {
		// TODO Auto-generated constructor stub
		iniciaCodigos();
		inicRegistros();
	}

	public static Traductor getInstance() {
		if (instance == null) {
			instance = new Traductor();
		}

		return instance;
	}

	public void setLinea(String linea) {
		// TODO Auto-generated method stub
		this.linea = linea;

	}
	public void setCantLineas(int cantLineas) {
		this.cantLineas = cantLineas;
	}

	public String getLinea() {
		return linea;
	}

	public int getCantConstantes() {
		return cantConstantes;
	}

	public int getCantLineas() {
		return cantLineas;
	}

	public int getEspacioDATA() {
		return espacioDATA;
	}

	public int getEspacioSTACK() {
		return espacioSTACK;
	}

	public int getEspacioCODE() {
		return espacioCODE;
	}

	public int getEspacioEXTRA() {
		return espacioEXTRA;
	}

	public ArrayList<Rotulo> getRotulos() {
		return rotulos;
	}

	public ArrayList<Integer> getCodeSegment() {
		return codeSegment;
	}

	public ArrayList<Constante> getConstantes() {
		return constantes;
	}


	public void leerDirectivas(String linea) throws ParseException // (CHEQUEADO LOS STRINGS)
	{
		String subLinea = linea;
		String dato;
		subLinea = subLinea.replace("^[\\+ASM]+", "");
		subLinea = subLinea.trim();
//		System.out.println("asmms" + subLinea);
		subLinea = subLinea.replaceAll(" ", "");
		if (subLinea.contains("DATA=")) {
			dato = subLinea.substring(subLinea.indexOf("DATA="));
			dato = dato.replace("DATA=", "");
			espacioDATA = (Integer) NumberFormat.getInstance().parse(dato).intValue();
//			System.out.println("data" + espacioDATA);
		}
		if (subLinea.contains("EXTRA=")) {			
			dato = subLinea.substring(subLinea.indexOf("EXTRA="));
			dato = dato.replace("EXTRA=", "");
		//	dato = this.tipoDato(dato);
			dato = dato.trim();
//			System.out.println(dato);
			if (dato.charAt(0)=='-')
			{	
				dato.replace("-", "");
				dato=dato.trim();
				espacioEXTRA = ((Integer)NumberFormat.getInstance().parse(dato).intValue());
				//System.out.println("extra" + espacioEXTRA);
			}
			else
			espacioEXTRA=(Integer)NumberFormat.getInstance().parse(dato).intValue();
			
		}
		if (subLinea.contains("STACK=")) {
			dato = subLinea.substring(subLinea.indexOf("STACK="));
			dato = dato.replace("STACK=", "");
			espacioSTACK = (Integer) NumberFormat.getInstance().parse(dato).intValue();
//			System.out.println("stack" + espacioSTACK);
		}
	}
	
	public void generarMemoria() ///cuando finaliza de leerse el archivo
	{
		this.espacioCODE = this.getCantLineas()*3;	
		this.registros[DS].setValor(this.espacioCODE + this.espacioCONSTANTES);
		System.out.println(this.getCantLineas());
		if (this.getEspacioEXTRA() == -1)
		{
			System.out.println(this.getEspacioEXTRA());
			registros[ES].setValor(this.getEspacioEXTRA());
			registros[SS].setValor(this.registros[DS].getValor() + this.getEspacioDATA());
			registros[SP].setValor(this.getEspacioSTACK());
			registros[PS].setValor(registros[DS].getValor() + this.getEspacioDATA() + this.getEspacioSTACK());
		}
		else {
		registros[ES].setValor(this.registros[DS].getValor() + this.getEspacioDATA());
		registros[SS].setValor(this.registros[ES].getValor() + this.getEspacioEXTRA());
		registros[SP].setValor(this.espacioSTACK);
		registros[PS].setValor(registros[DS].getValor() + this.getEspacioDATA() + this.getEspacioEXTRA() + this.getEspacioSTACK());
		}
	}
	
	public boolean verificarLineaContar(String linea) // verifica si es una linea que se deba contar /CHEQUEADO/
	{
		boolean b = true;

		if ((linea == null) || linea.startsWith("\\n") || linea.startsWith("\\\\ASM") || linea.startsWith("//") || linea.equalsIgnoreCase(""))
			b = false;

		return b;

	}

	public boolean siEsRepetido(String R) /// siEsRepetido cambiara la linea a -1 y retornara true
	{
		boolean valor = false;
		Iterator<Rotulo> it;
		it = this.rotulos.iterator();
		Rotulo rotuloIT;
		while (it.hasNext()) {
			rotuloIT = it.next();
			if (rotuloIT.getNombre().equalsIgnoreCase(R)) {
				rotuloIT.setnLinea(-1); /// para hacer saber que ese rotulo es ilegal
				valor = true;
			}
		}
		return valor;
	}

	public int devRotulo(String nombre) {
		Iterator<Rotulo> it = rotulos.iterator();
		Rotulo r = null;
		int numLinea = -1;
		while (it.hasNext() && numLinea == -1) {
			r = it.next();
			if (r.getNombre().equalsIgnoreCase(nombre)) {
				numLinea = r.getnLinea();
			}
		}
		return numLinea;
	}

	public boolean esRotulo(String nombre) {
		Iterator<Rotulo> it = rotulos.iterator();
		Boolean esRot = false;
		Rotulo r;
		while (it.hasNext() && !esRot) {
			r = it.next();
			if (r.getNombre().equalsIgnoreCase(nombre)) {
				esRot = true;
			}
		}
		return esRot;
	}
	
	public String filtraRotulo(String linea)
	{
		String rotulo;
		//long cant = linea.chars().filter(ch -> ch == ':').count();
		if (linea.matches("^[A-Z-a-z ]+[0-9]*\t*\\:+.*")) {

		//if (cant > 1 || (cant == 1 && !linea.contains("["))) {
			rotulo =linea.substring(0,linea.indexOf(":")+1);
			linea = linea.replace(rotulo,"");
			linea=linea.trim();
//			System.out.println("con rotulo filtrado:"+linea);
		}
		return linea;
	}

	public void guardaRotulos(String linea) /// metodo que guarda los rotulos en el arrayList ///chequeado
	{
		String subLinea;
		Rotulo nuevo = null;
		boolean esRep = true;
			if (linea.matches("^[A-Z-a-z ]+[0-9]*+\t*\\:+.*")) {
			subLinea = linea.substring(0, linea.indexOf(":"));
			subLinea = subLinea.trim();
			esRep = siEsRepetido(subLinea);
			if (!esRep) {
				nuevo = new Rotulo(subLinea, this.getCantLineas());
				rotulos.add(nuevo);

			} else {
				nuevo = new Rotulo(subLinea, -1);
			}
		}
	}
	public int tipoDato(String dato) {
		int valor=0;
		this.verifTipoDato=true;
		if (dato.startsWith("#") ||  dato.matches("^[0-9]+") || (dato.startsWith("-"))) {
			dato = dato.replace("#", "");
			if (dato.startsWith("-"))
			{
				dato = dato.replace("-", "");
				dato = dato.trim();
				//System.out.println(dato);
				valor = (Integer.parseInt(dato)) * (-1);
//				System.out.println(String.format("%08x", valor));
//				System.out.println(valor);
				
			}
			else
			valor = Integer.parseInt(dato);
		} else if (dato.startsWith("%")) {
			dato = dato.replace("%", "");
			dato=dato.trim();
			//if (dato.equalsIgnoreCase("FFFFFFFF"))
			//	valor = -1;
			//else
			//{
				valor = (int)Long.parseLong(dato,16);
			//}
//			System.out.println("entro en hexa:"+dato);			
		} else if (dato.startsWith("@")) {
			dato = dato.replace("@", "");
			valor = Integer.parseInt(dato, 8);
		} else {
			if (dato.charAt(0) == 0x0027) {
//				System.out.println("entre en ' ");
				dato = dato.replaceAll("'", "");
//				System.out.println(dato);
				StringBuilder sb = new StringBuilder();
				char[] caracteres = dato.toCharArray();
				for (char c : caracteres) {
					sb.append((int) c);
				}
				valor = Integer.parseInt(sb.toString());
			}
			else
			{
				valor =this.devuelveConstanteDirecta(dato);
				if (valor != -1)
				{
					valor = this.directas.get(valor).getIndiceCelda();
//					System.out.println("es constante directa");
				}
				else
					this.verifTipoDato = false;
			}
		}
		return valor;
	}



	public int makeValorConstanteDirecta() /// construye el valor de la constante directa en memoria
	{
		int indiceInicio = 0;
		if (directas.size() > 0)
			indiceInicio = directas.get(directas.size() - 1).getIndiceCelda()
					+ directas.get(directas.size() - 1).getValorDirecto().length();
		else
			indiceInicio = (this.getCantLineas())*3;
		
//		System.out.println("indice constante:"+indiceInicio);
		
		return indiceInicio;
	}

	public boolean lecturaConstante(String linea) { /// devuelve false si fue una correcta lectura
		// TODO Auto-generated method stub
		Constante nueva;
		boolean noCUMPLE = true;
		ConstanteDirecta nuevaDirecta;
		char escape = '\0';
		int indexRepetido = -1;
		String[] datos = linea.split("\\sEQU\\s");
//		System.out.println("ENTRO en constante");
		datos[0] = datos[0].trim();
		datos[1] = datos[1].trim();
//		System.out.println("constante" + datos[0]);
//		System.out.println("valor" + datos[1]);
		if (datos[1].matches("\".*\"")) {/// es constante directa
			datos[1] = datos[1].replaceAll("\"", "");
			indexRepetido = devuelveConstanteDirecta(datos[0]);
			if (indexRepetido == -1 && datos[0].length() >= 3 && datos[0].length() <= 10
					&& datos[0].matches("^[A-Z-a-z].*") && datos[0].matches("[(A-Z-a-z-0-9)]+")) {
				datos[1] = datos[1] + escape;
				nuevaDirecta = new ConstanteDirecta(datos[0], datos[1]);
				nuevaDirecta.setIndiceCelda(this.makeValorConstanteDirecta());
				directas.add(nuevaDirecta);
//				System.out.println("constante directa:" + nuevaDirecta.getNombre());
//				System.out.println(nuevaDirecta.getValorDirecto());
			} else
				noCUMPLE = false;

		} else { /// es constante de un solo valor
//			System.out.println("entro en constante inmediata");
			indexRepetido = devuelveConstanteInmediata(datos[0]);
			if (indexRepetido == -1) {
				nueva = new Constante(datos[0], tipoDato(datos[1]));
				constantes.add(nueva);
//				System.out.println(nueva.getNombre());
//				System.out.println("valor constante:"+nueva.getDato());
			} else
				noCUMPLE = false;

		}
		return noCUMPLE;
	}

	public int devuelveConstanteInmediata(String dato) {
		int i = 0;
		int indice = -1;
		while (i < constantes.size() && indice == -1) {
			if (constantes.get(i).getNombre().equalsIgnoreCase(dato))
				indice = i;
			i++;
		}
//		System.out.println("indice constante inmediata:"+indice);
		return indice;
	}

	public int devuelveConstanteDirecta(String dato) {
		int i = 0;
		int indice = -1;
		while (i < directas.size() && indice == -1) {
			if (directas.get(i).getNombre().equalsIgnoreCase(dato))
				indice = i;
			i++;
		}
//		System.out.println("constante directa:"+indice);
		return indice;
	}

	public int buscaMnemonico() {
		int i = 0x00;
		int indiceMnem = -1;
		while (i < mnemonicos.length && indiceMnem == -1) {
			if (mnemonicos[i] != null && this.linea.matches("^\\b"+mnemonicos[i]+"\\b\\s*.*")) {
				indiceMnem = i;
			}
			i++;
		}
//		System.out.println("indicemnmen:"+indiceMnem);
		return indiceMnem;
	}

	public void filtraMnemonico(int indiceMnem) {
		this.linea = this.linea.replace(mnemonicos[indiceMnem], "");
		this.linea = this.linea.trim();
//		System.out.println("linea con el mnemonico reemplazado " + this.linea);
	}

	public LineaInstruccion cortarDatos() /// consigo mnemonico,op1,op2 con tipos
	{
		Operando operando1 = null;
		Operando operando2 = null;
		String[] operandos = new String[2];
		String op1 = null; /// si los op quedan en null significa que algo anda mal
		String op2 = null;
		LineaInstruccion nueva = null;
		boolean repConstante = false;
		int indiceMnem = -1;
//		System.out.println("2da pasada" + this.linea);
		if (this.linea.matches(".*\\s\\bEQU\\b\\s.*")) { /// me fijo si es constante
			{
//				System.out.println("es const");
				repConstante = this.lecturaConstante(this.linea);
			}
		} else {
			this.linea = filtraRotulo(this.linea); /// quito rotulo si hay
			this.linea = this.linea.trim();
			indiceMnem = buscaMnemonico(); /// busco mnemonico asociado
//			System.out.println(indiceMnem);
//			System.out.println("sigo:" + this.linea);
			if (indiceMnem != -1) { //// si no hay mnemonico la linea sera invalida
				filtraMnemonico(indiceMnem); // quito mnemonico
//				System.out.println("linea despues mnemonico" + this.linea);
				if (this.linea.contains(",")) {
					operandos = this.linea.split(",");
					operandos[0] = operandos[0].trim();
					operandos[1] = operandos[1].trim(); /// el operando 1 no puede ser
					    op1 = operandos[0];
						op2 = operandos[1];
//						System.out.println("op1" + operandos[0]);
//						System.out.println("op2" + operandos[1]);

					}
			   else {
						op1 = this.linea.trim();
						if (!"".equalsIgnoreCase(op1)) {
//							System.out.println("operando0:" + operandos[0]);
//							System.out.println("operando1:" + operandos[1]);
						}
						else if (indiceMnem == 0x48 || indiceMnem == 0x8F) { // instrucciones que no admiten parametros //
								op1= "";									// parametro
								op2 = "";
						}
						//this.cantLineas++;

			   }		
		if (!repConstante && op1 != null) {			
			operando1 = obtenerCODOP(op1);
			operando2 = obtenerCODOP(op2);
			if (op1.matches(".*\\bCS\\b.*") && operando1.getTipo() == 3)			
                operando1=null;
			
			}
	}

		//System.out.println("STRING OP1:" + op1);
		nueva = this.generarLinea(indiceMnem, operando1, operando2);
	}
		return nueva;
}

	public void imprimirLinea(LineaInstruccion l)
	{
		System.out.print("["+String.format("%04x", (this.getCeldasActuales()>>16)));
		System.out.print(" "+String.format("%04x", this.getCeldasActuales())+"]: ");
		System.out.print(String.format("%04x", ((l.getCodInstruccion()>>16) & 0x0000FFFF)));
		System.out.print(" "+String.format("%04x",(l.getCodInstruccion() & 0x0000FFFF)));
		System.out.print(" "+String.format("%04x",(l.getOperando1()>>16 & 0x0000FFFF)));
		System.out.print(" "+String.format("%04x",(l.getOperando1() & 0x0000FFFF)));
		System.out.print(" "+String.format("%04x",(l.getOperando2()>>16 & 0x0000FFFF)));
		System.out.print(" "+String.format("%04x",(l.getOperando2() & 0x0000FFFF)));
		System.out.print(" "+this.getLineaActual()+":");
		System.out.println(this.getLineaOrg());
		
	}
	
	public void guardarEnMemoria(LineaInstruccion l)
	{
		int lugar = this.getCeldasActuales();
		this.memoria[lugar]= l.codInstruccion;
		this.memoria[++lugar] = l.operando1;
		this.memoria[++lugar] =l.operando2;
		this.setCeldasActuales(lugar+1);
	}

	public LineaInstruccion generarLinea(int indiceMnem, Operando operando1, Operando operando2) {
		int codInstruccion;
		LineaInstruccion nueva;
		if (operando1 == null || operando2 == null || indiceMnem == -1) {
			nueva = new LineaInstruccion(0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF);
			this.generaIMG = false; /// no generara imagen
		} else {
			codInstruccion = (indiceMnem << 16); 
			codInstruccion += (operando1.getTipo() << 8); 
			codInstruccion += operando2.getTipo();
			nueva = new LineaInstruccion(codInstruccion, operando1.getCODOP(), operando2.getCODOP());
		}
//		System.out.println(String.format("%08x", nueva.getCodInstruccion()));
//		System.out.println(String.format("%08x", nueva.getOperando1()));
//		System.out.println(String.format("%08x", nueva.getOperando2()));
		return nueva;
	}
	
	public void escribirEnMemoriaConstantesDirectas()
	{
	  Iterator<ConstanteDirecta> it = directas.iterator();
	  int tamConst=0;
	  ConstanteDirecta c;
	  int j=0;
	  while (it.hasNext())
	  {
		  c=it.next();
		  j=0;
		  while (j < c.getValorDirecto().length())
		  {
			  this.memoria[c.getIndiceCelda()+j] = (int)c.getValorDirecto().charAt(j);
//			  System.out.println("constante:"+String.format("%08X",this.memoria[c.getIndiceCelda()+j]));
		  		j++;
		  		
		  }
//		  System.out.println("cant letras"+j);
		  tamConst+=c.getValorDirecto().length();
	  }
	  this.espacioCONSTANTES=tamConst;
		
	}

	public int esRegistro(String dato) {
		int i = 0;
		while (i < registros.length && !dato.equals(registros[i].getNombre()))
			i++;
		if (i == registros.length)
			i = -1;

		return i;
	}

	public Operando obtenerCODOP(String op) {
		int CODOP = -1;
		int tipo = 0;
		int index;
		Operando operando = null;
//		System.out.println("ENTRO EN OBTENER CODOP");
		if (!("".equalsIgnoreCase(op)) && op != null) {
				if (op.startsWith("[")) {
//					System.out.println("ES INDIRECTO O INDIRECTO");
					operando = obtenerDIRECTOINDIRECTO(op);
				} else {
					if (esRotulo(op)) {
//						System.out.println("entro a rotulo");
						index = devRotulo(op);
						tipo = 0;
						if (index != -1)
							operando = new Operando(index, tipo);
						else
							System.out.println("El rotulo no existe");
					} else {
						index = esRegistro(op);
//						System.out.println("index registro:" + index);
						if (index != -1) { // es registro
							tipo = 1;
							operando = new Operando(index, tipo);
						} else {
							index = devuelveConstanteInmediata(op);
							if (index != -1) /// es constante
							{
//								System.out.println("Entro en constante inmediata");
								tipo = 0;
								operando = new Operando(constantes.get(index).getDato(), tipo);
						} 
//								else {
//								System.out.println("paso sector constante directa");
//								index = devuelveConstanteDirecta(op);
//								if (index != -1) /// es constante directa
//								{ 
//									System.out.println("Entro en constante directa");
//									tipo = 0;
//									operando = new Operando(directas.get(index).getIndiceCelda(), tipo);
//								} 
								else {
										tipo = 0;
//										System.out.println("paso sector tipo dato");
										CODOP = tipoDato(op);
											if (verifTipoDato)
											{
//											System.out.println("Entro en valor inmediato");
											operando = new Operando(CODOP, tipo);
											}
											else
											System.out.println("dato invalido");
									}
								}
							}
						}
					}
				else {
//				System.out.println("HOLA");
				operando = new Operando(0, 0); /// operando sin parametros
			}
		return operando;
	}

	public boolean debeCoincidir(int izq, int der) {
		boolean valor = true;
		if ((izq > 3 && izq != 5) || (der <= 5 || der == 9)) {
			valor = false;
		} else {
			if (der >= 10 && izq != 2 && izq != 3) {
				valor = false;
			} else if ((der == 6 || der == 7) && izq != 5) {
				valor = false;
			}
		}
		return valor;

	}

	public int buscaCoincidenteBase(int indiceReg) {
		int i = 1;
		int indexBASE = -1;
		while (i < registros.length && indexBASE == -1) {
			if (debeCoincidir(i, indiceReg))
				indexBASE = i;

			i++;
		}
		return indexBASE;
	}

	public Operando obtenerDIRECTOINDIRECTO(String op) /// SE DEBE CHEQUEAR EL REGEX
	{
		int CODOP = 0xFFFFFFFF;
		int registroIZQ = -2;
		int registroDER = -1;
		String stringDerIndirecto = null; /// el string a la derecha del registro si hay una operacion de suma o resta
		int offset = 0;
		int tipo = 0;
		int indexCeldaDirecta;
		int seSuma = 0; /// 0 ninguno 1 se suma, -1 se resta
		String registros[] = new String[2];
		int indiceConstante = -1;
		Operando operando = null;
		String[] indirectos = new String[2];
		op = op.replaceAll("\\[", "");
		op = op.replaceAll("\\]", "");
//		System.out.println("op con regex:" + op);
		if (op.indexOf(":") != -1) /// si no hay un registro definido
		{
			registros = op.split(":");
//			dato = op.substring(0, op.indexOf(":"));
			registroIZQ = esRegistro(registros[0]);
			op = registros[1];
//			System.out.println("en operando d/i registroIZQ:"+registroIZQ+ "op:"+op);
		} else
			registroIZQ = -2; /// aviso que no hay registro base definido

		if (op.contains("+") || op.contains("-")) /// operando indirecto con suma
		{
//			System.out.println("entro en + -");
			if (op.contains("+")) {
				indirectos = op.split("\\+");
				seSuma = 1;
			} else {
				indirectos = op.split("\\-");
				seSuma = -1;
			}
			indirectos[0] = indirectos[0].trim();
			indirectos[1] = indirectos[1].trim();

//			System.out.println("indirecto0:" + indirectos[0]);
//			System.out.println("indirecto1:" + indirectos[1]);
			stringDerIndirecto = indirectos[1];
			registroDER = this.esRegistro(indirectos[0]);
			/// if coincide con lo que debe ser con el operando 1 todo bien y lo sumo al
			/// CODOP sino -1
		} else
			registroDER = this.esRegistro(op);
		
		indexCeldaDirecta = this.devuelveConstanteDirecta(op);
		if (registroDER == -1) // si no hay registro en la derecha 	es DIRECTO													// DIRECTO
		{
//			System.out.println("esdirecto:");
			tipo = 2;
			if (stringDerIndirecto == null) /// verificamos que no haya una suma asociada
			{
				if (indexCeldaDirecta != -1) /// si es constante directa
				{
					registroDER = this.directas.get(indexCeldaDirecta).getIndiceCelda();
					if (registroIZQ == -2)
						registroIZQ = CS;

					offset = registroDER;
				}
				else
				{
				indiceConstante = this.devuelveConstanteInmediata(op); /// posee constante inmediata?
				if (indiceConstante != -1) ///es constante comun
					offset = constantes.get(indiceConstante).getDato();
				else {
					offset=this.tipoDato(op);
					if (!this.verifTipoDato)
						registroIZQ=-1;
				}
				if (registroIZQ == -2) /// se omitio la base
				
						registroIZQ = DS; /// por ser directo el DS es por defecto
//				System.out.println("escribe el directo:"+registroIZQ+" "+offset);
				}
			
		
		} 
			else
				registroIZQ=-1;
		}else { //// hay un registro a la derecha hablamos de operando indirecto
			tipo = 3;
//			if (indexCeldaDirecta != -1) /// si es constante directa
//			{
//				registroDER = indexCeldaDirecta;
//				if (registroIZQ == -2)
//					registroIZQ = CS;

//				offset = registroDER;
//			} else {
				if (registroIZQ == -2) /// se habia omitido la base en el acceso
				{
					registroIZQ = buscaCoincidenteBase(registroDER);
//					System.out.println("Es registro:" + registroIZQ);
				}
				if (indexCeldaDirecta == -1 && registroIZQ != -1 && debeCoincidir(registroIZQ, registroDER)) ///debe coincidir orden
				{
					if (stringDerIndirecto != null) /// hay operacion
					{
						if (stringDerIndirecto.matches("^[A-Z-a-z]+.*")) {
							indiceConstante = this.devuelveConstanteInmediata(stringDerIndirecto);
							if (indiceConstante != -1) {
								if (seSuma == 1)
									offset = (constantes.get(indiceConstante).getDato()) << 4;
								else {
									offset = (constantes.get(indiceConstante).getDato()) << 4;
									offset = offset * (-1);
								}
							} else /// existe un error
								registroIZQ = -1; /// anulo para que de error
						} else if (stringDerIndirecto.matches("^\\d+")) {

							if (seSuma == 1)
								offset = Integer.parseInt(stringDerIndirecto) << 4;
							else {
								offset = Integer.parseInt(stringDerIndirecto) << 4;
								offset = offset * (-1);
							}
						} else
							registroIZQ = -1; /// anulo para que de error

						offset += registroDER;
					} else {
						offset = registroDER; /// es solo el registro indirecto ultimos 4 bits
					}

				} else
					registroIZQ = -1;
			}
//		}
		if (registroIZQ != -1) {
//			System.out.println("registroizq" + registroIZQ);
			offset = offset & 0x0FFFFFFF;
			CODOP = registroIZQ << 28;
//			System.out.println("codop" + String.format("%08x", CODOP));
			CODOP = CODOP + offset;
			operando = new Operando(CODOP, tipo);
//			System.out.println("codop ind:" + String.format("%08x", operando.getCODOP()));
		}

		return operando;
	}



}
