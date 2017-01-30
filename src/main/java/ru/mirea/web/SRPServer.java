package ru.mirea.web;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.postgresql.util.PSQLException;

import java.io.*;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

/**
 * Created by Alex on 26.01.2017.
 */
public class SRPServer {

    private String filePath;

    private BigInteger u, b, A, v, B, N, g;
    private String U, s, K, M;

    public SRPServer() {
        filePath = "db.txt";

        N = new BigInteger("3785416927");
        g = new BigInteger("2");
        int t = 8;
        u = new BigInteger(t, new Random());
        do {
            b = new BigInteger(N.bitLength(), new Random());
        } while (b.compareTo(N) >= 0);

    }

    public void set_U(String U) {
        this.U = U;
    }

    public void set_A(BigInteger A) {
        this.A = A;
    }

    public void set_s(String s) {
        this.s = s;
    }

    public String get_s() {
        return s;
    }

    public BigInteger get_u() {
        return u;
    }

    public void set_v(BigInteger v) {
        this.v = v;
    }

    public BigInteger B() {
        BigInteger first = g.modPow(b, N);
        B = v.add(first);
        return B;
    }

    public void K() {
        BigInteger first = v.modPow(u, N);
        BigInteger second = A.multiply(first);
        BigInteger S = second.modPow(b, N);

        K = DigestUtils.sha256Hex(S.toString());
        //System.out.println(K);
    }

    public boolean register(String U, String s, String v) {
        Connection connection = null;
        //URL к базе состоит из протокола:подпротокола://[хоста]:[порта_СУБД]/[БД] и других_сведений
        String url = "jdbc:postgresql://localhost:5432/auth";
        //Имя пользователя БД
        String name = "server_owner";

        boolean isUnique = false;

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, name, "");

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT COUNT(*)\n" +
                    "  FROM public.t_user WHERE (\"U\" = ?);"
            );

            preparedStatement.setString(1, U);
            ResultSet result = preparedStatement.executeQuery();
            result.next();
            if (result.getLong("count") == 0) {                                    // пользователь уникален
                preparedStatement = connection.prepareStatement(
                        "INSERT INTO public.t_user(\n" +
                                "            \"U\", s, v)\n" +
                                "    VALUES (?, ?, ?);"
                );
                preparedStatement.setString(1, U);
                preparedStatement.setString(2, s);
                preparedStatement.setString(3, v);

                preparedStatement.executeUpdate();
                isUnique = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isUnique;

//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
//            String record = U + "→" + s + "→" + v;
//            bw.write(record);
//            bw.newLine();
//            bw.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public boolean readFromDB(String username) {
        Connection connection = null;
        //URL к базе состоит из протокола:подпротокола://[хоста]:[порта_СУБД]/[БД] и других_сведений
        String url = "jdbc:postgresql://localhost:5432/auth";
        //Имя пользователя БД
        String name = "server_owner";

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, name, "");

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT \"U\", s, v\n" +
                            "  FROM public.t_user WHERE (\"U\" = ?);"
            );

            preparedStatement.setString(1, username);
            ResultSet result = preparedStatement.executeQuery();
            result.next();
            try {
                set_U(result.getString("U"));
                set_s(result.getString("s"));
                set_v(new BigInteger(result.getString("v")));
            } catch (PSQLException e) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;

//        File db = new File(filePath);
//        boolean isFound = false;
//        try {
//            for(String record : FileUtils.readLines(db)) {
//                if (record.split("→")[0].equals(username)) {
//                    set_U(record.split("→")[0]);
//                    set_s(record.split("→")[1]);
//                    set_v(new BigInteger(record.split("→")[2]));
//                    isFound = true;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return isFound;
    }

    public boolean checkM(String M) {
        BigInteger H_N = new BigInteger(DigestUtils.sha256Hex(N.toString()), 16);
        BigInteger H_g = new BigInteger(DigestUtils.sha256Hex(g.toString()), 16);
        String H_U = DigestUtils.sha256Hex(U);
        this.M = DigestUtils.sha256Hex(H_N.xor(H_g).toString() + H_U + s + A.toString() + B.toString() + K);
        return this.M.equals(M);
    }

    public String AMK() {
        return DigestUtils.sha256Hex(A.toString() + M + K);
    }

}
