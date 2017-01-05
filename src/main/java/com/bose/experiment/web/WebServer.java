package com.bose.experiment.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by sm1030414 on 12/22/2016.
 */
public class WebServer extends AbstractVerticle {
    private static Map<Integer, String> errorCodes;
    private static Map<String, JsonObject> readings = new HashMap<>();

    static {
        errorCodes = new HashMap<Integer, String>();
        errorCodes.put(10001, "Invalid User Id");
        errorCodes.put(10002, "Invalid JSON Body");
    };

    @Override
    public void start(Future<Void> future) {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post("/users/:userId/readings")
                .produces("application/json")
                .handler(this::addReading);
        router.get("/users/:userId/readings")
                .produces("application/json")
                .handler(this::getReadings);
        router.route().handler(routingContext -> {
            HttpServerResponse resp = routingContext.response();
            resp.putHeader("content-type", "text/plain");
            resp.end("Hello world - I'm your maker!");
        });
        server.requestHandler(router::accept).listen(
                config().getInteger("http.port", 8080),
                result -> {
                    if (result.succeeded())
                        future.complete();
                    else
                        future.fail(result.cause());
                });
    }

    private void getReadings(RoutingContext ctx) {
        ctx.response().end(Json.encodePrettily(readings.values()));
    }

    private void addReading(RoutingContext ctx) {
        String userId = ctx.request().getParam("userId");
        JsonObject reading = ctx.getBodyAsJson();
        HttpServerResponse response = ctx.response();
        if (userId == null)
            sendError(404, 10001, response);
        if (reading == null)
            sendError(404, 10002, response);
        if (!reading.containsKey("id")) {
            UUID id = UUID.randomUUID();
            reading.put("id", id.toString());
            System.out.println("Server u:[" + userId + "] id:[" + id.toString() + "] r:\n" + reading.encodePrettily());
        }
        readings.put(reading.getString("id"), reading);
        response.putHeader("content-type", "application/json")
                .end(reading.encodePrettily());
    }

    private void sendError(int statusCode, int errorCode, HttpServerResponse response) {
        response.setStatusCode(statusCode);
        response.setStatusMessage(errorCodes.get(errorCode));
        response.end();
    }

}
