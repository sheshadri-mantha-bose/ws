package com.bose.experiment.ws;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;

/**
 * Created by sm1030414 on 11/11/2016.
 */
public class Server extends AbstractVerticle {

    public void start() throws Exception {
        vertx.createHttpServer().websocketHandler(ws -> ws.handler(ws::writeBinaryMessage)).listen(8080);
    }

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        Runner.runExample(Server.class);
    }

}
