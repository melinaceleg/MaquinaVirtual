import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;


public class ESDatos {
	String nomArch;
	BufferedReader entrada;
	DataOutputStream salida;
	int cantLineas;
	Traductor t;

	public ESDatos(String nomArchEntrada, String nomArchSalida) {
		// TODO Auto-generated constructor stub
		t = Traductor.getInstance();
		this.nomArch = nomArchEntrada;
		try {
			this.primerPasada();
			this.segundaPasada();
			if (t.isGeneraIMG()) 
				generarIMG(nomArchSalida);
		} catch (Exception e) {
			System.out.println(e.getMessage()+"error en el archivo");
		}
	}

	private void abrirArchivo() throws IOException {
		this.entrada = new BufferedReader(new FileReader("arch.txt.txt"));
	}

	public void primerPasada() throws IOException {
		String linea = null;
		this.abrirArchivo();
		try {
			linea = entrada.readLine();
			while (linea != null) {
				linea.trim();
				if (t.verificarLineaContar(linea) && !linea.matches(".*\\sEQU\\s.*"))
					t.setCantLineas(t.getCantLineas() + 1);
				t.guardaRotulos(linea);
				if (linea.contains("\\ASM"))
					t.leerDirectivas(linea);

				linea = entrada.readLine();
				System.out.println(linea);

			}
			entrada.close();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} finally {
		}

	}

	public void segundaPasada() throws IOException {
		String linea;
		LineaInstruccion l = null;
		String sep[];
		String comentario = null;
		int lineaAct=0;
		this.abrirArchivo();
		try {
			linea = this.entrada.readLine();
			System.out.println("linea segunda pasada primera:" + linea);
			while (linea != null) {
				linea = linea.trim();
				if (!t.verificarLineaContar(linea)) {
					if (linea.startsWith("//")) /// SOLO COMENTARIO
					{
						linea = linea.replace("[//]+", "");
					}
				} else {
					if (linea.contains("//"))
					{
						sep = linea.split("[//]+");
						linea = sep[0];
						linea = linea.trim();
						comentario = sep[1];
					}
					t.setLineaOrg(linea);
					t.setLinea(linea);
					System.out.println("linea contada :" + linea);
					l = t.cortarDatos();
					if (l != null) {
						lineaAct++;
						t.imprimirLinea(l);
						t.guardarEnMemoria(l);			
					t.setLineaActual(lineaAct);
					}
				}
				
				linea = entrada.readLine();

			}
			this.entrada.close();
			t.generarMemoria();
			//t.escribirEnMemoriaConstantesDirectas();

		} catch (Exception e) {
			System.out.println("excepcion"+e.getMessage());
		}

	}
	
	public void abrirSalida(String nomArchSalida) throws FileNotFoundException
	{
		this.salida = new DataOutputStream(new FileOutputStream(nomArchSalida));
	}
	
	public void generarIMG(String nomArchSalida) throws FileNotFoundException
	{
		int[] memoria = t.getMemoria();
		Registro [] registros = t.getRegistros();
		this.abrirSalida(nomArchSalida);
		try {
			for (int i =0; i < registros.length; i++)
				this.salida.write(registros[i].getValor());
			for (int i = 0 ; i < memoria.length ; i++)
				this.salida.write(memoria[i]);;
				
			this.salida.close();
		} catch (IOException e) {
			System.out.println(e.getMessage()+"Error en salida");
			e.printStackTrace();
		}
		
	}
}
