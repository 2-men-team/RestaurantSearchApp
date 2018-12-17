package ua.kpi.restaurants.network.server;

import org.jetbrains.annotations.NotNull;
import ua.kpi.restaurants.logic.common.exceptions.ProjectRuntimeException;
import ua.kpi.restaurants.logic.common.exceptions.UserCausedException;
import ua.kpi.restaurants.logic.common.utils.Serializer;
import ua.kpi.restaurants.logic.representation.Dish;
import ua.kpi.restaurants.logic.strategies.handling.HandlingStrategy;
import ua.kpi.restaurants.logic.strategies.handling.HandlingStrategy.Result;
import ua.kpi.restaurants.network.data.Request;
import ua.kpi.restaurants.network.data.Response;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

final class Worker implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(Worker.class.getName());

  private final Socket socket;
  private final HandlingStrategy<Dish> handler;

  Worker(@NotNull Socket socket, @NotNull HandlingStrategy<Dish> handler) {
    this.socket  = socket;
    this.handler = handler;
  }

  @Override
  public void run() {
    LOGGER.entering(Worker.class.getName(), "run");

    try (Socket s = socket) {
      Request request = Serializer.deserializeJson(s.getInputStream(), Request.class);
      LOGGER.info("Received request: " + request);

      Response response;
      try {
        List<Dish> data = handler.apply(request.getQuery())
            .stream()
            .sorted(Result.<Dish>comparingByRank().reversed())
            .limit(20)
            .map(Result::getData)
            .collect(Collectors.toList());

        response = Response.success(null, data);
      } catch (UserCausedException e) {
        LOGGER.info("Caught UserCausedException: " + e.getMessage());
        String template = "Failed while processing data for %s: %s";
        response = Response.failure(String.format(template, request.getQuery(), e.getMessage()));
      } catch (ProjectRuntimeException e) {
        LOGGER.warning("Caught ProjectRuntimeException: " + e.getMessage());
        response = Response.failure("Internal server error.");
      }

      LOGGER.fine("Response is " + response);
      Serializer.serializeJson(s.getOutputStream(), response);
    } catch (IOException e) {
      LOGGER.warning("Caught IOException: " + e.getMessage());
    }

    LOGGER.exiting(Worker.class.getName(), "run");
  }
}
