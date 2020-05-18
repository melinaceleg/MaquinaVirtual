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
			System.out.println(e.getMessage() + "error en el archivo");
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
				linea=linea.trim();
				if (t.verificarLineaContar(linea) && !linea.matches(".*\\sEQU\\s.*"))
				{
					System.out.println("CUETNA LINEA");
					t.setCantLineas(t.getCantLineas() + 1);
				}
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
		int lineaAct = 0;
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
					t.setLineaOrg(linea);
					if (linea.contains("//")) {
						sep = linea.split("[//]+");
						linea = sep[0];
						linea = linea.trim();
						comentario = sep[1];
					}
					t.setLinea(linea);
					System.out.println("linea contada :" + linea);
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
			t.escribirEnMemoriaConstantesDirectas(); ///debe estar a continuacion de generarMemoria
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
				this.salida.writeInt(registros[i].getValor());
			for (int i = 0; i < registros[PS].getValor(); i++)
				this.salida.writeInt(memoria[i]);

			this.salida.close();
			generarSalidaLittleEndian();
			this.leeSalida();
		} catch (IOException e) {
			System.out.println(e.getMessage() + "Error en salida");
			e.printStackTrace();
		}

	}
//	public void generarIMG(String nomArchSalida) throws FileNotFoundException
//	{
//	
//
//		this.abrirSalida(nomArchSalida);
//		try {
//			this.salida.writeInt(24);
//			this.salida.close();
//			leeSalida();
//		}catch (IOException e) {
//			System.out.println(e.getMessage()+"Error en salida");
//			e.printStackTrace();
//		}
//	}

	public void leeSalida() throws IOException {
		int[] array = new int[16];
		DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream("salida.img")));
		for (int i = 0; i < 16; i++) {
			array[i] = input.readInt();
		}
		for (int i = 0; i < 16; i++)
			System.out.println(array[i]);
		// String.format("%08X", array[i])
		input.close();
	
	}

	public void generarSalidaLittleEndian() {
		try (FileChannel fc = (FileChannel) Files.newByteChannel(Paths.get("salida.img"), StandardOpenOption.READ)) {
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
			FileChannel out = new FileOutputStream("salida2.img").getChannel();
			out.write(byteBuffer);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
