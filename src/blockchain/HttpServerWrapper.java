/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Will
 */
class HttpServerWrapper {

    private static BlockChain blockChain;
    HttpServer httpServer;

    HttpServerWrapper(BlockChain blockChain) {
        HttpServerWrapper.blockChain = blockChain;
        httpServer = initHttpServer();

    }

    private HttpServer initHttpServer(){
        try {
            int port = 9000;
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            System.out.println("server started at " + port);
            server.createContext("/blocks", new BlockHandler());
            server.setExecutor(null);
            server.start();
            return server;
        } catch (IOException ex) {
            Logger.getLogger(HttpServerWrapper.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private static class BlockHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Block>>() {}.getType();
            String response = gson.toJson(blockChain.getBlockChain(), type);
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}
