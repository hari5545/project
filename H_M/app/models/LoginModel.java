package models;

import java.util.*;
import java.sql.*;
import java.util.Date;
import models.utils.*;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;


public class LoginModel {

    public String username;
    public String password;
    public int password_auth;

    public String authenticate(Config config, String username, String password, String remoteAddr, String userAgent) {

        LDAPServiceManager ldapServiceManager = new LDAPServiceManager(config);
        String userRoleandOU = ldapServiceManager.authenticateAdminUser(username, password);
        
        return userRoleandOU;
    }

    public String authenticateClientUser(Config config, String username, String password, String remoteAddr, String userAgent) {

        LDAPServiceManager ldapServiceManager = new LDAPServiceManager(config);
        String userRoles = ldapServiceManager.authenticateClientUser(username, password);
        return userRoles;
    }

    public void updateUserToken(Config config, String userOrgName, String username, String userToken) {
    	LDAPServiceManager ldapServiceManager = new LDAPServiceManager(config);
    	ldapServiceManager.modifyUser(userOrgName, username, userToken);
    	//return "";
    }

    public models.client.Channel getBroadcastChannel(Config config, String userOrgName) {

        LDAPServiceManager ldapServiceManager = new LDAPServiceManager(config);        
        return ldapServiceManager.getBroadcastChannel(userOrgName);
    }
}