package ru.mirea.web;

import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by Alex on 27.01.2017.
 */
public class SRPClient {

    private BigInteger N, g, a, B, u, A;
    private String U, p, s, K, M;

    public SRPClient() {
        N = new BigInteger("3785416927");
        g = new BigInteger("2");
        do {
            a = new BigInteger(N.bitLength(), new Random());
        } while (a.compareTo(N) >= 0);
    }

    public void setUsername(String username) {
        U = username;
    }

    public void setPassword(String password) {
        p = password;
    }

    public String x(String s) {
        return DigestUtils.sha256Hex(p + s);
    }

    public BigInteger v(String s) {
        BigInteger x = new BigInteger(this.x(s), 16);
        return g.modPow(x, N);
    }

    public BigInteger A() {
        A = g.modPow(a, N);
        return A;
    }

    public String get_s() {
        return s;
    }

    public void set_s(String s) {
        this.s = s;
    }

    public void set_B(BigInteger B) {
        this.B = B;
    }

    public void set_u(BigInteger u) {
        this.u = u;
    }

    public String get_U() {
        return U;
    }

    public void K() {
        BigInteger x = new BigInteger(this.x(s), 16);
        BigInteger first = g.modPow(x, N);
        BigInteger second = B.subtract(first);
        BigInteger third = u.multiply(x);
        BigInteger fourth = a.add(third);
        BigInteger S = second.modPow(fourth, N);

        K = DigestUtils.sha256Hex(S.toString());
        //System.out.println(K);
    }

    public String M() {
        BigInteger H_N = new BigInteger(DigestUtils.sha256Hex(N.toString()), 16);
        BigInteger H_g = new BigInteger(DigestUtils.sha256Hex(g.toString()), 16);
        String H_U = DigestUtils.sha256Hex(U);
        M = DigestUtils.sha256Hex(H_N.xor(H_g).toString() + H_U + s + A.toString() + B.toString() + K);
        return M;
    }

    public boolean checkAMK(String AMK) {
        return DigestUtils.sha256Hex(A.toString() + M + K).equals(AMK);
    }


}
