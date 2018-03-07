/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

/**
 *
 * @author Will
 */
public class PeerData {
    private final String host;
    private final int port;

    public PeerData(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object obj) {
            if (obj == null) {
        return false;
    }
    if (!PeerData.class.isAssignableFrom(obj.getClass())) {
        return false;
    }
    final PeerData other = (PeerData) obj;
    if ((this.getHost() == null) ? (other.getHost() != null) : !this.getHost().equals(other.getHost())) {
        return false;
    }
    if (this.port != other.port) {
        return false;
    }
    return true;
    }
    
    
    
}
