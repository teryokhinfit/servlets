package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.JavaConfig;
import ru.netology.controller.PostController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
  private PostController controller;

  private static final String GET_METHOD = "GET";
  private static final String POST_METHOD = "POST";
  private static final String DELETE_METHOD = "DELETE";
  private static final String API_POST_PATH = "/api/posts";

  @Override
  public void init() {
    final var context = new AnnotationConfigApplicationContext(JavaConfig.class);
    controller = context.getBean(PostController.class);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    // если деплоились в root context, то достаточно этого
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      // primitive routing
      if (method.equals(GET_METHOD) && path.equals(API_POST_PATH)) {
        controller.all(resp);
        return;
      }
      if (method.equals(GET_METHOD) && path.matches(API_POST_PATH + "\\d+")) {
        // easy way
        final var id = parse(path);
        controller.getById(id, resp);
        return;
      }
      if (method.equals(POST_METHOD) && path.equals(API_POST_PATH)) {
        controller.save(req.getReader(), resp);
        return;
      }
      if (method.equals(DELETE_METHOD) && path.matches(API_POST_PATH + "\\d+")) {
        // easy way
        final var id = parse(path);
        controller.removeById(id, resp);
        return;
      }
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  public long parse(String path) {
    return Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
  }
}

