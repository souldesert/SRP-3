package ru.mirea.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by Alex on 27.01.2017.
 */
public class Key extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SRPServer host;
        if (req.getSession().getAttribute("SRPServer") == null) {
            host = new SRPServer();
        } else {
            host = (SRPServer) req.getSession().getAttribute("SRPServer");
        }
        host.set_A(new BigInteger(req.getParameter("A")));
        if (host.readFromDB(req.getParameter("U"))) {
            resp.addHeader("s", host.get_s());
            resp.addHeader("B", host.B().toString());
            resp.addHeader("u", host.get_u().toString());
            resp.addHeader("status", "FOUND");

            host.K();
        } else {
            resp.addHeader("status", "NOT_FOUND");
        }

        req.getSession().setAttribute("SRPServer", host);
    }
}
