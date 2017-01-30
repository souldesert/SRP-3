package ru.mirea.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Alex on 28.01.2017.
 */
public class Verify extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        SRPServer host = (SRPServer) req.getSession().getAttribute("SRPServer");

        if(host.checkM(req.getParameter("M"))) {
            resp.addHeader("status", "OK");
            resp.addHeader("AMK", host.AMK());
        } else {
            resp.setHeader("status", "ERROR");
        }
    }
}
