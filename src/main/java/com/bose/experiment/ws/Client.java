package com.bose.experiment.ws;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;

/**
 * Created by sm1030414 on 11/11/2016.
 */
public class Client extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        HttpClient client = vertx.createHttpClient();
        client.websocket(8080, "localhost", "/", ws -> {
            ws.handler(data -> {
                System.out.println("Received data: " + data.toString("ISO-8859-1"));
            });
            ws.writeBinaryMessage(Buffer.buffer("yo - are you listening"));
        });
    }

    public static void main(String[] args) {
        Runner.runExample(Client.class);
    }
}
