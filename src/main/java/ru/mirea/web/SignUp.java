package ru.mirea.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Alex on 27.01.2017.
 */
public class SignUp extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String s = String.valueOf(new Date().getTime());
        HttpSession session = req.getSession();
        session.setAttribute("s", s);
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("salt", s);


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SRPServer host;
        if (req.getSession().getAttribute("SRPServer") == null) {
            host = new SRPServer();
        } else {
            host = (SRPServer) req.getSession().getAttribute("SRPServer");
        }
        if (host.register(req.getParameter("U"), (String) req.getSession().getAttribute("s"), req.getParameter("v"))) {
            resp.setHeader("status", "OK");
        } else {
            resp.setHeader("status", "ERROR");
        }

        req.getSession().setAttribute("SRPServer", host);
    }
}
