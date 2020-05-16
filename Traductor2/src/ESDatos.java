import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

public class ESDatos {
	String nomArch;
	BufferedReader entrada;
	int cantLineas;
	Traductor t;

	public ESDatos(String nomArch) {
		// TODO Auto-generated constructor stub
		t = Traductor.getInstance();
		this.nomArch = nomArch;
		try {
			this.primerPasada();
			this.segundaPasada();
			if (t.isGeneraIMG()) 
				generarIMG();
		} catch (Exception e) {
			System.out.println(e.getMessage());
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
		int lineaAct=0;
		this.abrirArchivo();
		try {
			linea = this.entrada.readLine();
			System.out.println("linea segunda pasada primera:" + linea);
			while (linea != null) {
				linea = linea.trim();
				if (!t.verificarLineaContar(linea)) {
					if (linea.startsWith("////")) /// SOLO COMENTARIO
					{
						linea = linea.replace("//", "");
					}
				} else {
					t.setLineaOrg(linea);
					t.setLinea(linea);
					System.out.println("linea contada :" + linea);
					l = t.cortarDatos();
					if (l != null) {
						t.imprimirLinea(l);
						t.guardarEnMemoria(l);
					lineaAct++;
					t.setLineaActual(lineaAct);
					}
				}
				
				linea = entrada.readLine();

			}
			this.entrada.close();
			t.generarMemoria();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	
	public void generarIMG()
	{
		///falta generar archivo de salida
		
	}
}
