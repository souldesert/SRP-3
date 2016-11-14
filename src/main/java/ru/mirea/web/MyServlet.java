package ru.mirea.web;

import org.eclipse.jetty.util.IO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by master on 14.11.2016.
 */
public class MyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        String servletPath = req.getServletPath();
        String resourceName;
        if ("/".equals(servletPath)) {
            resourceName = "/index.html";
        } else {
            resourceName = servletPath;
        }
        InputStream stream = getClass()
                .getResourceAsStream(resourceName);
        IO.copy(stream, resp.getOutputStream());
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        System.out.println(password);
        resp.getWriter().write("Hello, " + login + "!");
    }
}
