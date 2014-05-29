package com.cometproject.logger.routes;

import com.cometproject.logger.ResponseBuilder;

import static spark.Spark.get;
import static com.cometproject.logger.CometLogger.JSON_TRANSFORMER;
import static spark.Spark.post;

public class Routes {
    public static void init() {
        defaultRoutes();
        chatlogRoutes();
    }

    private static void defaultRoutes() {
        get("/",(request, response) -> {
            response.type("application/json");

            return ResponseBuilder.builder()
                    .add("status", false)
                    .add("message", "Unable to process request")
                    .get();
        }, JSON_TRANSFORMER);

        get("/v1/test",(request, response) -> {
            response.type("application/json");

            return ResponseBuilder.builder()
                    .add("status", true)
                    .get();
        }, JSON_TRANSFORMER);
    }

    private static void chatlogRoutes() {
        post("/v1/chatlogs/room/add", (request, response) -> {
            response.type("application/json");

            String userId = request.queryParams("user_id");
            String roomId = request.queryParams("room_id");
            String message = request.queryParams("message");

            System.out.println("Chatlog adding USER ID: " + userId + " , ROOM ID: " + roomId + " , MESSAGE: " + message);

            return ResponseBuilder.builder()
                    .add("status", true)
                    .add("message", "Chatlog added successfully")
                    .get();
        }, JSON_TRANSFORMER);
    }
}
