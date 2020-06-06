import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;

public class ESDatos implements constantesDePrograma {
	String nomArch;
	BufferedReader entrada;
	DataOutputStream salida;
	int cantLineas;
	Traductor t;

	public ESDatos(String nomArchEntrada, String nomArchSalida, String parametro) {
		// TODO Auto-generated constructor stub
		t = Traductor.getInstance();
		this.nomArch = nomArchEntrada;
		try {
			this.primerPasada();
			this.segundaPasada();
			if (t.isGeneraIMG() && parametro == null)
				generarIMG(nomArchSalida);
		} catch (Exception e) {
			System.out.println(e.getMessage() + "error en el archivo");
		}
	}

	private void abrirArchivo(String nomArchEntrada) throws IOException {
		this.entrada = new BufferedReader(new FileReader(nomArchEntrada));
	}

	public void primerPasada() throws IOException {
		String linea = null;
		this.abrirArchivo(this.nomArch);
		try {
			linea = entrada.readLine();
			while (linea != null) {
				linea=linea.trim();
				linea=linea.toUpperCase();
				if (t.verificarLineaContar(linea) && !linea.matches(".*\\sEQU\\s.*"))
				{
//					System.out.println("CUETNA LINEA");
					t.setCantLineas(t.getCantLineas() + 1);
				}
				t.guardaRotulos(linea);
				if (linea.contains("\\ASM"))
					t.leerDirectivas(linea);

				linea = entrada.readLine();
//				System.out.println(linea);

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
		int lineaAct = 0;
		this.abrirArchivo(this.nomArch);
		try {
			linea = this.entrada.readLine();
//			System.out.println("linea segunda pasada primera:" + linea);
			while (linea != null) {
				linea = linea.trim();
				if (!t.verificarLineaContar(linea)) {
					if (linea.startsWith("//")) /// SOLO COMENTARIO
					{
						linea = linea.replace("[//]+", "");		
						System.out.println(linea);
					}
				} else {
					t.setLineaOrg(linea);
					if (linea.contains("//")) {
						sep = linea.split("[//]+");
						linea = sep[0];
						linea = linea.trim();
					}
					linea = linea.toUpperCase();
					t.setLinea(linea);
//					System.out.println("linea contada :" + linea);
					l = t.cortarDatos();
					if (l != null) {
						lineaAct++;
						t.setLineaActual(lineaAct);
						t.imprimirLinea(l);
						t.guardarEnMemoria(l);
					}
				}

				linea = entrada.readLine();

			}
			this.entrada.close();
			t.escribirEnMemoriaConstantesDirectas(); ///debe estar antes de generarMemoria
			t.generarMemoria(); 
		   

		} catch (Exception e) {
			System.out.println("excepcion" + e.getMessage());
		}

	}

	public void abrirSalida(String nomArchSalida) throws FileNotFoundException {
		this.salida = new DataOutputStream(new FileOutputStream(nomArchSalida));
	}

	public void generarIMG(String nomArchSalida) throws FileNotFoundException {
		int[] memoria = t.getMemoria();
		Registro[] registros = t.getRegistros();
		this.abrirSalida(nomArchSalida);
		try {
			for (int i = 0; i < registros.length; i++)
			{
				this.salida.writeInt(registros[i].getValor());
			System.out.println(registros[i].getNombre()+" :"+registros[i].getValor());
			}
			for (int i = 0; i < registros[DS].getValor(); i++)
				this.salida.writeInt(memoria[i]);

			this.salida.close();
			//this.leeSalida(nomArchSalida);
			generarSalidaLittleEndian(nomArchSalida);
			
		} catch (IOException e) {
			System.out.println(e.getMessage() + "Error en salida");
			e.printStackTrace();
		}

	}

	public void leeSalida(String nomArchSalida) throws IOException {
		int[] array = new int[16];
		DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(nomArchSalida)));
		for (int i = 0; i < 16; i++) {
			array[i] = input.readInt();
		}
		for (int i = 0; i < 16; i++)
		///	System.out.println(array[i]);
		// String.format("%08X", array[i])
		input.close();
	
	}

	public void generarSalidaLittleEndian(String nomArchSalida) {
		try (FileChannel fc = (FileChannel) Files.newByteChannel(Paths.get(nomArchSalida), StandardOpenOption.READ)) {
			ByteBuffer byteBuffer = ByteBuffer.allocate((int) fc.size());
			byteBuffer.order(ByteOrder.BIG_ENDIAN);
			 fc.read(byteBuffer);
	            byteBuffer.flip();
			Buffer buffer = byteBuffer.asIntBuffer();
			int[] intArray = new int[(int) fc.size() / 4];
			((IntBuffer) buffer).get(intArray);
			byteBuffer.clear();
			byteBuffer = byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			IntBuffer intOutputBuffer = byteBuffer.asIntBuffer();
			intOutputBuffer.put(intArray);
			fc.close();
			@SuppressWarnings("resource")
			FileChannel out = new FileOutputStream("nomArchSalida").getChannel();
			out.write(byteBuffer);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
