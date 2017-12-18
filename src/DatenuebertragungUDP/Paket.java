package DatenuebertragungUDP;

public class Paket {
	
	public Paket(String _dataName, String _targetPCName){
		dataName = _dataName;
		targetPCName = _targetPCName;
		System.out.println("Created Paket " + dataName + " for PC " + targetPCName );		
		
	}
	private String dataName ;
	private String targetPCName;
	
}
