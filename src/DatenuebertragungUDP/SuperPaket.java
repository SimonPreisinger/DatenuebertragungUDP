package DatenuebertragungUDP;

import java.net.InetAddress;

public class SuperPaket {
	
	public SuperPaket(byte[] _buf,int _offset, int _length, InetAddress _address, int _port, String _dataName){
		buf = _buf;
		offset = _offset;
		length = _length;
		address = _address;
		port = _port;
		dataName = _dataName;

		System.out.println("Created SuperPaket " +" buf " + buf + " offset " + offset + " lenght " + length + " InetAddress " + address + " port " + port + " dataName " + dataName );		
		
	}
	private byte[] buf;
	private int offset;
	private int length;
	private InetAddress address;
	private int port;
	private String dataName ;
	
}
