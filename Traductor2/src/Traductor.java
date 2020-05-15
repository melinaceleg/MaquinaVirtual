import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Traductor implements constantesDePrograma {
	private String linea;
	private int CODOP;
	private int cantConstantes;
	private int cantLineas = 0;
	private int espacioDATA;
	private int espacioSTACK;
	private int espacioCODE;
	private int espacioEXTRA;

	private static Traductor instance = null;

	ArrayList<Rotulo> rotulos = new ArrayList<Rotulo>();
	ArrayList<Integer> codeSegment = new ArrayList<Integer>();
	ArrayList<Constante> constantes = new ArrayList<Constante>();
	ArrayList<ConstanteDirecta> directas = new ArrayList<ConstanteDirecta>();

	private Registro[] registros = new Registro[16];
	private String[] mnemonicos = new String[144];

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
		registros[DS].setValor(500);
		registros[ES].setNombre("ES");
		registros[ES].setValor(500);
		registros[IP].setNombre("IP");
		registros[DS].setValor(0);
		registros[AC].setNombre("AC");
		registros[SS].setNombre("SS");
		registros[SS].setValor(500);
		registros[SP].setNombre("SP");
		registros[SP].setValor(0);
		registros[BP].setNombre("BP");
		registros[BP].setValor(0);
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
		subLinea = subLinea.replace("/^\\\\ASM/", "");
		subLinea = subLinea.trim();
		System.out.println("asmms" + subLinea);
		subLinea = subLinea.replaceAll(" ", "");
		if (subLinea.contains("DATA=")) {
			dato = subLinea.substring(subLinea.indexOf("DATA="));
			dato = dato.replace("DATA=", "");
			espacioDATA = (Integer) NumberFormat.getInstance().parse(dato).intValue();
			System.out.println("data" + espacioDATA);
		}
		if (subLinea.contains("EXTRA=")) {
			dato = subLinea.substring(subLinea.indexOf("EXTRA="));
			dato = dato.replace("EXTRA=", "");
			espacioEXTRA = (Integer) NumberFormat.getInstance().parse(dato).intValue();
			System.out.println("extra" + espacioEXTRA);
		}
		if (subLinea.contains("STACK=")) {
			dato = subLinea.substring(subLinea.indexOf("STACK="));
			dato = dato.replace("STACK=", "");
			espacioSTACK = (Integer) NumberFormat.getInstance().parse(dato).intValue();
			System.out.println("stack" + espacioSTACK);
		}
	}

	public boolean verificarLineaContar(String linea) // verifica si es una linea que se deba contar /CHEQUEADO/
	{
		boolean b = true;

		if ((linea == null) || linea.startsWith("\\n") || linea.startsWith("\\\\ASM") || linea.startsWith("//"))
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

	public String filtraRotulo(String linea) {
		long cant = linea.chars().filter(ch -> ch == ':').count();
		if (cant > 1 || (cant == 1 && !linea.contains("["))) {
			System.out.println("se filtro como rotulo");
			linea = linea.substring(linea.indexOf(":"));
		}
		return linea;
	}

	public void guardaRotulos(String linea) /// metodo que guarda los rotulos en el arrayList ///chequeado
	{
		String subLinea;
		Rotulo nuevo = null;
		long cant = linea.chars().filter(ch -> ch == ':').count();
		// System.out.println("esto es" + linea);
		if (cant > 1 || (cant == 1 && !linea.contains("["))) {

			subLinea = linea.substring(0, linea.indexOf(":"));
			subLinea = subLinea.trim();
			if (!siEsRepetido(subLinea)) {
				nuevo = new Rotulo(subLinea, this.cantLineas);
				rotulos.add(nuevo);
				System.out.println("rotulo:" + nuevo.getNombre());
				System.out.println("linea:" + nuevo.getnLinea());
			} else
				System.out.println("ESTA REPETIDO");

		}
	}

	public int tipoDato(String dato) {
		int valor = 0xFFFFFFFF;
		if (dato.startsWith("#") || Character.isDigit(dato.charAt(0))) {
			dato = dato.replace("#", "");
			valor = Integer.parseInt(dato);

		} else if (dato.startsWith("%")) {
			dato = dato.replace("%", "");
			valor = Integer.parseInt(dato, 16);
		} else if (dato.startsWith("@")) {
			dato = dato.replace("@", "");
			valor = Integer.parseInt(dato, 8);
		} else {
			if (dato.charAt(0) == 0x0027) {
				dato = dato.replace("'", "");
				valor = Integer.parseInt(dato);
			}
		}
		System.out.println(valor);
		return valor;
	}


	public void lecturaConstante(String linea) { 
		// TODO Auto-generated method stub
		Constante nueva;
		ConstanteDirecta nuevaDirecta;
		String[] datos = linea.split("\\sEQU\\s");
		System.out.println("ENTRO en constante");
		datos[0] = datos[0].trim();
		datos[1] = datos[1].trim();
		System.out.println("constante" + datos[0]);
		System.out.println("valor" + datos[1]);
		if (datos[1].matches("\".*\"")) 
		{/// es constante directa
			datos[1] = datos[1].replaceAll("\"", "");
			nuevaDirecta = new ConstanteDirecta(datos[0], datos[1]);
			directas.add(nuevaDirecta);
			System.out.println("constante directa:"+nuevaDirecta.getNombre());
			System.out.println(nuevaDirecta.getValorDirecto());
		}
		else { ///es constante de un solo valor
			System.out.println("aca entro");
			nueva = new Constante(datos[0], tipoDato(datos[1]));
			constantes.add(nueva);
			System.out.println(nueva.getNombre());
			System.out.println(nueva.getDato());
		}

	}

	public int devuelveConstante(String dato) {
		int i = 0;
		int indice = -1;
		while (i < constantes.size() && indice == -1) {
			if (constantes.get(i).getNombre().equalsIgnoreCase(dato))
				indice = i;
			i++;
		}
		return indice;
	}

	public int buscaMnemonico() {
		int i = 0x00;
		int indiceMnem = -1;
		while (i < mnemonicos.length && indiceMnem == -1) {
			if (mnemonicos[i] != null && this.linea.startsWith(mnemonicos[i] + " ")) {
				indiceMnem = i;
			}
			i++;
		}
		return indiceMnem;
	}

	public void filtraMnemonico(int indiceMnem) {
		this.linea = this.linea.replace(mnemonicos[indiceMnem], " ");
		this.linea = this.linea.trim();
		System.out.println("linea con el mnemonico reemplazado " + this.linea);
	}

	public void cortarDatos() /// consigo mnemonico,op1,op2 con tipos
	{
		Operando operando1 = null;
		Operando operando2 = null;
		String[] operandos = new String[2];
		String op1 = null; ///si los op quedan en null significa que algo anda mal
		String op2 = null;
		int indiceMnem = -1;
		System.out.println("2da pasada"+this.linea);
		if (this.linea.matches(".*\\s\\bEQU\\b\\s.*")) { /// me fijo si es constante
			{
				System.out.println("es const");
			this.lecturaConstante(this.linea);
			}
		} else {
			this.linea = filtraRotulo(this.linea); /// quito rotulo si hay
			this.linea = this.linea.trim();
			indiceMnem = buscaMnemonico(); /// busco mnemonico asociado
			System.out.println(indiceMnem);
			System.out.println("sigo:" + this.linea);
			if (indiceMnem != -1) { //// si no hay mnemonico la linea sera invalida
				filtraMnemonico(indiceMnem); // quito mnemonico
				System.out.println("linea despues mnemonico" + this.linea);
				if (this.linea.contains(",")) {
					operandos = this.linea.split(",");
					operandos[0] = operandos[0].trim();
					operandos[1] = operandos[1].trim();				
					if (operandos[0] != null && !operandos[0].matches(".*\\bCS\\b.*")) { ///el operando 1 no puede ser nulo ni contener a CS
						op1= operandos[0];
						op2= operandos[1];
						System.out.println("op1"+operandos[0]);
						System.out.println("op2"+operandos[1]);
						
					}
				} else {
					op1 = this.linea.trim();
					if (!op1.matches(".*\\bCS\\b.*"))
					{
					if (!"".equalsIgnoreCase(op1)) {
						System.out.println("operando0:" + operandos[0]);
						System.out.println("operando1:" + operandos[1]);
					} else {
						if (indiceMnem == 0x48 || indiceMnem == 0x8F)  // instrucciones que no admiten parametros 																	// parametro
							op2 = "";
					}
					this.cantLineas++;
					}
					
				}
			
			}
			operando1 = obtenerCODOP(op1);
			operando2 = obtenerCODOP(op2);
			System.out.println("STRING OP1:" + op1);
			System.out.println("CODOP OPERANDO 1 " + String.format("%08x",operando1.getCODOP()));
			System.out.println("CODOP OPERANDO 2 " + String.format("%08x",operando2.getCODOP()));
			System.out.println("mnemonico:" + mnemonicos[indiceMnem]);

		}

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
		int CODOP = 0xFFFFFFFF;
		int tipo = 0;
		int index;
		Operando operando = null;
		System.out.println("ENTRO EN OBTENER CODOP");
		if (op != null) {
			if (!("".equalsIgnoreCase(op))) {
				if (op.startsWith("[")) {
					System.out.println("ES INDIRECTO O INDIRECTO");
					operando = obtenerDIRECTOINDIRECTO(op);
				} else {
					if (esRotulo(op)) {
						System.out.println("entro a rotulo");
						tipo = 0;
						CODOP = devRotulo(op);
					} else {
						index = esRegistro(op);
						System.out.println("index registro:" + index);
						if (index != -1) { // es registro
							tipo = 1;
							CODOP = index<<4;
						} else {
							index = devuelveConstante(op);
							if (index != -1) /// es constante
							{
								tipo = 0;
								CODOP = constantes.get(index).getDato();
							}

							else { /// es inmediato
								tipo = 0;
								CODOP = tipoDato(op);
							}
						}
					}
					if (CODOP != -1)
						operando = new Operando(CODOP, tipo);
				}
			} else {
				System.out.println("HOLA");
				operando = new Operando(0, 0); /// operando sin parametros
			}
		}
		return operando;
}

	public boolean debeCoincidir(int izq, int der) {
		boolean valor = true;
		if ((izq > 3 && izq != 5) || (der <=5 || der == 9 )) {
			valor = false;
		}
		else
		{
			if (der >= 10 && izq != 2 && izq != 3) {
				valor = false;
		} 	else if ((der == 6 || der == 7) && izq != 5) {
				valor = false;
		} 
		}
		return valor;

	}

	
	
	public int buscaCoincidenteBase(int indiceReg)
	{
		int i =1;
		int indexBASE=-1;
		while (i < registros.length && indexBASE==-1)
		{
			if (debeCoincidir(i,indiceReg))
				indexBASE=i;
			
			i++;
		}
		return indexBASE;
	}

	public Operando obtenerDIRECTOINDIRECTO(String op) /// SE DEBE CHEQUEAR EL REGEX
	{
		int CODOP = 0xFFFFFFFF;
		int registroIZQ = -2;
		int registroDER=-1;
		String stringDerIndirecto=null; ///el string a la derecha del registro si hay una operacion de suma o resta
		int offset = 0;
		int tipo = 0;
		int seSuma=0; /// 0 ninguno 1 se suma, -1 se resta
		String registros[] = new String[2];
		int indiceConstante = -1;
		Operando operando = null;
		String[] indirectos = new String[2];
		op = op.replaceAll("\\[", "");
		op=op.replaceAll("\\]", "");
		System.out.println("op con regex:"+op);
		if (op.indexOf(":") != -1) /// si no hay un registro definido
		{
			registros = op.split(":");
//			dato = op.substring(0, op.indexOf(":"));
			registroIZQ = esRegistro(registros[0]);
			op = registros[1];
		}
		else
			registroIZQ=-2; ///aviso que no hay registro base definido

		if (op.contains("+") || op.contains("-")) /// operando indirecto con suma
		{
			System.out.println("entro en + -");
			if (op.contains("+"))
			{
				indirectos = op.split("\\+");
				seSuma=1;
			}
			else
			{
				indirectos = op.split("\\-");
				seSuma=-1;				
			}
			indirectos[0] = indirectos[0].trim();
			indirectos[1] = indirectos[1].trim();
			
			System.out.println("indirecto0:"+indirectos[0]);
			System.out.println("indirecto1:"+indirectos[1]);
			stringDerIndirecto=indirectos[1];
			registroDER = this.esRegistro(indirectos[0]);
			///if coincide con lo que debe ser con el operando 1 todo bien y lo sumo al CODOP sino -1
		}
		else
			registroDER=this.esRegistro(op);
			if (registroDER == -1 ) //si no hay registro en la derecha ES DIRECTO
			{
				System.out.println("directo:"+registroDER);
				tipo=2;
				if (stringDerIndirecto == null) ///verificamos que no haya una suma asociada
				{
					indiceConstante = this.devuelveConstante(op); ///posee constante
					offset=constantes.get(indiceConstante).getDato();
					if (registroIZQ == -2) ///se omitio la base
						registroIZQ=DS; ///por ser directo el DS es por defecto
				}
				else
					registroIZQ=-1; ///anulo para que de error
			}
			else
				{ ////hay un registro a la derecha hablamos de operando indirecto
					tipo=3;
					if (registroIZQ == -2) ///se habia omitido la base en el acceso
					{
						registroIZQ=buscaCoincidenteBase(registroDER);
						System.out.println("Es registro:"+registroIZQ);
					}	
				    if (registroIZQ != -1 && debeCoincidir(registroIZQ,registroDER)) ///deben coincidir el orden de uso de los registros
					{

						if (stringDerIndirecto != null) ///hay operacion
						{
							if (stringDerIndirecto.matches("^[A-Z-a-z]"))
							{
							indiceConstante = this.devuelveConstante(stringDerIndirecto);
							if (indiceConstante!= -1)
							{
								if (seSuma == 1)
								 offset =(constantes.get(indiceConstante).getDato())<<4;
								else
									offset =(~constantes.get(indiceConstante).getDato())<<4;
							}
							else ///existe un error
								registroIZQ=-1; ///anulo para que de error
							}
							else
								if (stringDerIndirecto.matches("\\d+"))
								{
									
								if (seSuma == 1)
										offset = Integer.parseInt(stringDerIndirecto)<<4;
									else
										offset = (~Integer.parseInt(stringDerIndirecto))<<4;						
								}
								else
									registroIZQ=-1; ///anulo para que de error
							
							offset +=registroDER;
					 }
					else
					{
						offset = registroDER; ///es solo el registro indirecto ultimos 4 bits
					}
						
				}
				    else
				    	registroIZQ=-1;
				}
				if (registroIZQ != -1)
				{
					System.out.println("registroizq"+registroIZQ);
					offset = offset & 0x0FFFFFFF;
					CODOP= registroIZQ <<28;
					System.out.println("codop"+String.format("%08x",CODOP));
					CODOP = CODOP + offset;
					operando=new Operando(CODOP,tipo);
					System.out.println("codop ind:"+String.format("%08x",operando.getCODOP()));
				}
			
		return operando;
}

	public void setLinea(String linea) {
		// TODO Auto-generated method stub
		this.linea = linea;

	}

}
