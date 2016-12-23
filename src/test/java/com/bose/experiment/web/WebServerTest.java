package com.bose.experiment.web;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by sm1030414 on 12/23/2016.
 */
@RunWith(VertxUnitRunner.class)
public class WebServerTest {
    private Vertx vertx;
    private static final String reading = "{" +
            "\"x\": [1, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20],\n" +
            "\"y\": [2, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40]\n" +
            "}";
    private int port = 8080;

    @Before
    public void setup(TestContext ctx) {
        vertx = Vertx.vertx();
        DeploymentOptions opts = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port));
        vertx.deployVerticle(WebServer.class.getName(), opts, ctx.asyncAssertSuccess());
    }

    @After
    public void teardown(TestContext ctx) {
        vertx.close(ctx.asyncAssertSuccess());
    }

    @Test
    public void testPostReading(TestContext ctx) {
        final Async async = ctx.async();
        vertx.createHttpClient().post(port, "localhost", "/users/1234/readings")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", reading.length()+"")
                .handler(resp -> {
                    ctx.assertEquals(resp.statusCode(), 200);
                    ctx.assertTrue(resp.headers().get("content-type").contains("application/json"));
                    resp.bodyHandler(body -> {
                        JsonObject json = new JsonObject(body.toString());
                        ctx.assertTrue(json.containsKey("id"));
                        ctx.assertTrue(json.containsKey("x"));
                        ctx.assertTrue(json.containsKey("y"));
                        async.complete();
                    });
                })
                .write(reading)
                .end();
    }

    @Test
    public void testGetAllReadings(TestContext ctx) {
        final Async async = ctx.async();
        vertx.createHttpClient().getNow(port, "localhost", "/users/1234/readings", resp -> {
           resp.handler(body -> {
               JsonObject json = new JsonObject(body.toString());
               System.out.println(json.encodePrettily().toString());
               async.complete();
           });
        });
    }
}
