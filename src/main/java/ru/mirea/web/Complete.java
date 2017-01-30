package ru.mirea.web;

import com.google.common.io.Resources;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Alex on 28.01.2017.
 */
public class Complete extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession().getAttribute("isLoggedIn") != null) {

            try {
                File image = new File(Resources.getResource("sample_server.jpg").toURI());
                byte[] file = Files.readAllBytes(image.toPath());
                resp.setContentType("image/jpeg");
                resp.setHeader("status", "OK");
                resp.getOutputStream().write(file);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        } else {
            resp.setHeader("status", "ERROR");
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String result = req.getParameter("isLoggedIn");
        req.getSession().setAttribute("isLoggedIn", result);
    }
}
