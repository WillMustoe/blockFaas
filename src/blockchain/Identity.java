package blockchain;

public class Identity {
	private final String uuid;
	private final String publicKeySerialised;
	
	public Identity(String uuid, String publicKeySerialised) {
		this.uuid =uuid;
		this.publicKeySerialised = publicKeySerialised;
	}
	
	
	public String getUuid() {
		return uuid;
	}
	public String getPublicKeySerialised() {
		return publicKeySerialised;
	}
	
	

}
