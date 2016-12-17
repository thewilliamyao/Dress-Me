package com.server;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.auth0.jwt.JWTSigner;

public class LoginToken {
    private static final String issuer = "OOSE_GROUP_5";
    private static final String prefix = "OOSE_SECRET";
    public int id;
    public String token;
    
    public LoginToken(int id) throws Exception {
        this.id = id;
        this.token = generateToken(this.id);
    }
    
    public LoginToken(int id, String token) throws Exception {
        this.id = id;
        this.token = "";
    }

    public static String generateToken(int id) throws Exception {
        long iat = System.currentTimeMillis() / 1000L;
        long exp = iat + 36000L; // expires in 60 min
        String signature = prefix + Integer.toString(id);
        JWTSigner signer = new JWTSigner(signature);
        HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put("exp", exp);
        claims.put("iat", iat);
        return signer.sign(claims);
    }
    
    public static boolean verify(String tok, int id) throws Exception {
        try {
            String signature = prefix + Integer.toString(id);
            JWTVerifier verifier = new JWTVerifier(signature);
            verifier.verify(tok);
            return true;
        } catch (JWTVerifyException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    public void print() {
        System.out.printf("ID: [%d], TOKEN: [%s]\n", this.id, this.token);
    }

    public int getId() {
        return this.id;
    }

    public String getToken() {
        return this.token;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setToken(String token) {
        this.token = token;
    }

}

