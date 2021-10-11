/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.awssecretsmanagerexample;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

/**
 * https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-usagenotes-connect-drivermanager.html
 * @author cduran
 */
public class Start {

    public static void main (String [] args) {
//        connectOne();
        //connectTwo();
        connectThree();
    }

    /**
     * Connects to mysql after having db logins read securely from 
     */
    private static void connectFive() {
        
    }
    /**
     * Connects to myql after having db logins read securely from Microsoft Azure key vault.
     * https://azure.microsoft.com/en-us/services/key-vault/
     */
    private static void connectFour() {
        
    }
    /**
     * Connects to mysql after having db logins read securely from AWS Secrets Manager.
     */
    private static void connectThree() {
        String secretName = "ExampleSecret";
        String region = "us-east-1";
        AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard().withRegion(region).build();
        String secret = null;
        String decodedBinarySecret;
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = null;

        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (Exception ex) {
            System.out.println(ex);
            return;
        }
        
        if (getSecretValueResult.getSecretString() != null) {
            secret = getSecretValueResult.getSecretString();
        } else {
            decodedBinarySecret = new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
        }
        System.out.println("secret: " + secret);
        JSONObject jobject = new JSONObject(secret);
        System.out.println("user: " + jobject.getString("username") + " password: " + jobject.getString("password"));
        // if type wrong key, you'll get a JSONException, ie if use 'user' instead of 'username' will get the exception.
    }
    /**
     * Connects to mysql reading from .properties file.
     * 
     * Still not good because the connection login/password are in plain text.
     */
    private static void connectTwo() {
        FileInputStream in = null;
        try {
            Properties properties = new Properties();
            String propFile = "src/main/resources/app.properties";
            in = new FileInputStream(propFile);
            properties.load(in);

            String user = properties.getProperty("username");
            String password = properties.getProperty("password");
            System.out.println("User: " + user + " Password: " + password);
            in.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    /**
     * Connects to mysql from connection string.
     * 
     * Not good.  Because the connection string is in plain text.
     */
    private static void connectOne() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn =
               DriverManager.getConnection("jdbc:mysql://localhost/comicotaku?" +
                                           "user=root&password=password");

            // Do something with the Connection
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM checkout");
            while (rs.next()) {
                System.out.println("Column 1 value: " + rs.getString(1));
            }
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.out.println(ex);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    System.out.println(ex);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex);
                }
            }
        }

    }
}
