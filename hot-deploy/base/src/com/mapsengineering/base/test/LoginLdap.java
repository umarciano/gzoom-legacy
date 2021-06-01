package com.mapsengineering.base.test;

import java.io.InputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class LoginLdap {

    public static final String CONFIG = "jndiLdap.properties";

    public static void main(String[] args) {
        try {
            if (args.length != 2) {
                System.err.println("args: username password");
                return;
            }

            String username = args[0];
            String password = args[1];

            Properties env = new Properties();
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG);
            try {
                env.load(is);
            } finally {
                if (is != null)
                    is.close();
            }

            String dn = username;
            String dnTemplate = (String)env.get("ldap.dn.template");
            if (dnTemplate != null) {
                dn = dnTemplate.replace("%u", username);
            }

            env.put(Context.SECURITY_PRINCIPAL, dn);
            env.put(Context.SECURITY_CREDENTIALS, password);

            env.store(System.out, null);

            DirContext ldapCtx = new InitialDirContext(env);
            ldapCtx.close();

            System.out.println("SUCCESS !!!");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
