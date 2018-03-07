/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 *
 * @author Will
 */
public class Message {
    
    public static final int PEERS_QUERY_ALL = 0;
    public static final int CHAIN_QUERY_ALL = 1;
    public static final int CHAIN_QUERY_LATEST = 2;
    public static final int RESPONSE_PEERS = 3;
    public static final int RESPONSE_CHAIN_ALL = 4;
    public static final int RESPONSE_CHAIN_LATEST = 5;

    static Message fromJson(String json) throws JsonSyntaxException{
        Gson gson = new Gson();
        return gson.fromJson(json, Message.class);
    }
    
    private final int messageType;
    private final String messageData;
    private final int returnPort;
    
    public Message(int type, String messageData, int returnPort){
        this.messageType = type;
        this.messageData = messageData;
        this.returnPort = returnPort;
    }
    
    public int getType() {
        return messageType;
    }

    public int getReturnPort() {
        return returnPort;
    }

    public String getMessageData() {
        return messageData;
    }
    
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return "Message{" + "messageType=" + messageType + ", messageData=" + messageData + '}';
    }
   
    
}
