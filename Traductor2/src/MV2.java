
public class MV2 {

	public MV2() {
		// TODO Auto-generated constructor stub
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(args[0]);
		ESDatos t;
		if (args.length > 2)
		t = new ESDatos(args[0],args[1],args[3]);
		else
		t = new ESDatos(args[0],args[1],null);

	}

}
