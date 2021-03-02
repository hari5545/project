package models;

import java.util.Properties;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Date;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NameNotFoundException;
import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;

import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.directory.ModificationItem;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

import com.typesafe.config.Config;

import models.ldap.*;
import models.client.ClientUser;
import models.utils.HandlingConsumer;
import play.api.Play;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.apache.commons.lang3.StringUtils;

import play.mvc.*;
import play.libs.ws.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.File;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

import models.dto.TabDto;


/**
 * This class is responsible for managing the LDAP database of the application
 * This class is having following functionalities
 * 1. Creating an organization
 * 2. Creating an organization unit under an organization
 * 3. Creating an department under and organization unit
 * 4. Creating Roles
 * 5. Creating Tabs under departments/OUs
 * 6. Creating Users
 * 7. Mapping Roles to Tabs
 * 8. Mapping Roles to Users
 */

public class LDAPServiceManager {

	private final String organizationPrefix = "o=";
	private final String organizationUnitPrefix = "ou=";
	private final String commonNamePrefix = "cn=";
	private final String userNamePrefix = "uid=";
	private final String comma = ",";
	private final String ROLES = "Roles";
	private final String TABS = "Tabs";
	private final String USERS = "Users";
	private final String TABTEMPLATE = "TabTemplates";
	private final int TAB_TEMPLATE_BASE_VALUE = 100;
	private final int ROLE_BASE_VALUE = 500;
	private final int TAB_BASE_VALUE = 1000;
	private final int USER_BASE_VALUE = 5000;
	private final String ORGANIZATION_SEARCH_FILTER = "(objectClass=organization)";
	private final String ORGANIZATION_UNIT_SEARCH_FILTER = "(&(objectClass=organizationalUnit)(destinationIndicator=branch))";
	private final String DEPARTMENT_SEARCH_FILTER = "(&(objectClass=organizationalUnit)(destinationIndicator=department))";
	private final String ROLE_SEARCH_FILTER = "(objectClass=posixGroup)";
	private final String USER_SEARCH_FILTER = "(objectClass=inetOrgPerson)";
	private final String TAB_SEARCH_FILTER = "(objectClass=posixAccount)";
	private final String TAB_TEMPLATE_SEARCH_FILTER = "(objectClass=posixGroup)";
	private final String USER_SUB_SEARCH_FILTER = "(&(objectClass=inetOrgPerson)(|(sn=*%s*)(cn=*%s*)(uid=*%s*)))";
	private final String SELECTIVE_USER_SEARCH_FILTER = "(&(objectClass=inetOrgPerson)(cn=%s))";
	private final String SELECTIVE_TAB_SEARCH_FILTER = "(&(objectClass=posixAccount)(cn=%s))";
	private final String SELECTIVE_TOP_LEVEL_USER_SEARCH_FILTER = "(&(objectClass=organizationalRole))";
	private final Config config;
	Logger logger = LoggerFactory.getLogger(this.getClass());
	Properties connectionProperties;
	Gson gson;
	MattermostServiceManager mmServiceManager;
	String mmAuthToken;
	DirContext globalConnectionContext = null;
	private String serverURL;
	private String domainDN;
	private String adminUser;
	private String adminPassword;
	private String adminUserDN;
	private DirContext directoryServiceContext = null;

	public LDAPServiceManager(Config config) {
		this.config = config;

		serverURL = config.getString("ldap.serverURL");
		domainDN = config.getString("ldap.domainDN");
		adminUser = config.getString("ldap.adminUser");
		adminPassword = config.getString("ldap.adminPassword");
		adminUserDN = commonNamePrefix + adminUser + comma + domainDN;

		/*logger.debug("serverURL: "+serverURL);
		logger.debug("domainDN: "+domainDN);
		logger.debug("adminUser: "+adminUser);
		logger.debug("adminUserDN: "+adminUserDN);*/

		//connectionContext = getConnectionContext();
		gson = new Gson();

		connectionProperties = new Properties();
		connectionProperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		connectionProperties.put(Context.PROVIDER_URL, serverURL);
		//parms.put(Context.SECURITY_PROTOCOL, "ssl");
		connectionProperties.put(Context.SECURITY_AUTHENTICATION, "simple");
		connectionProperties.put(Context.SECURITY_PRINCIPAL, adminUserDN);
		connectionProperties.put(Context.SECURITY_CREDENTIALS, adminPassword);

		//logger.debug("Result={}", result);
		mmServiceManager = new MattermostServiceManager(config);
		mmAuthToken = "";

		try {
			globalConnectionContext = new InitialDirContext(connectionProperties);
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + globalConnectionContext);
		} catch (Exception exp) {
			globalConnectionContext = null;
		}

	}

	public LDAPServiceManager(Config config, String authToken) {
		this.config = config;

		serverURL = config.getString("ldap.serverURL");
		domainDN = config.getString("ldap.domainDN");
		adminUser = config.getString("ldap.adminUser");
		adminPassword = config.getString("ldap.adminPassword");
		adminUserDN = commonNamePrefix + adminUser + comma + domainDN;

		/*logger.debug("serverURL: "+serverURL);
		logger.debug("domainDN: "+domainDN);
		logger.debug("adminUser: "+adminUser);
		logger.debug("adminUserDN: "+adminUserDN);*/

		//connectionContext = getConnectionContext();
		gson = new Gson();

		connectionProperties = new Properties();
		connectionProperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		connectionProperties.put(Context.PROVIDER_URL, serverURL);
		//parms.put(Context.SECURITY_PROTOCOL, "ssl");
		connectionProperties.put(Context.SECURITY_AUTHENTICATION, "simple");
		connectionProperties.put(Context.SECURITY_PRINCIPAL, adminUserDN);
		connectionProperties.put(Context.SECURITY_CREDENTIALS, adminPassword);

		//logger.debug("Result={}", result);
		mmAuthToken = authToken;
		mmServiceManager = new MattermostServiceManager(config, authToken);

		try {
			globalConnectionContext = new InitialDirContext(connectionProperties);
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + globalConnectionContext);
		} catch (Exception exp) {
			globalConnectionContext = null;
		}
	}




	/*
	 * This method is used to get the connection context with admin credentials
	 * This connection context is used for doing LDAP operations
	 */
	/*public DirContext getConnectionContext() {

		Properties parms = new Properties();
        parms.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        parms.put(Context.PROVIDER_URL, serverURL);
        //parms.put(Context.SECURITY_PROTOCOL, "ssl");
        parms.put(Context.SECURITY_AUTHENTICATION, "simple");
        parms.put(Context.SECURITY_PRINCIPAL, adminUserDN);
        parms.put(Context.SECURITY_CREDENTIALS, adminPassword);

        DirContext ctx = null;

        try {
            ctx = new InitialDirContext(parms);
            logger.info("LDAP Connection Established...");
            logger.info("LDAP Context {}"+ctx);
        }
         catch (AuthenticationNotSupportedException ex) {
            logger.error("The authentication is not supported by the server", ex);
        } catch (AuthenticationException ex) {
            logger.error("Invalid password or username", ex);
        }
        catch(NamingException ne) {
            logger.error("Authentication and Bind Failed!", ne);
        }
        catch(Exception e) {
        	logger.error("Error Occurred!", e);
        }
        finally {
            if (ctx != null) {
                try {
                    ctx.close();
                }
                catch (NamingException ne) {
                	logger.error("Error Occurred while closing the connection!", ne);
                }
            }
        }

        return ctx;
	}*/

	public void createOrganization(String... parameters) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		try {

			String mm_team_id = "";

			//Create a Team in Mattermost
			mm_team_id = mmServiceManager.createTeam(parameters[0]);

			//mmServiceManager.deleteTeam(ws, "adfgmd51ofrc9dddjmaokpdzoy");
			//mmServiceManager.deleteTeam(ws, "w7swkm9fw78etnfqr9try6wjmh");

			//Create the LDAP Entry with the Mattermost id

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			Attributes attributes = new BasicAttributes();
			Attribute objectClass = new BasicAttribute("objectClass");
			objectClass.add("organization");
			objectClass.add("top");
			attributes.put(objectClass);

			String orgName = parameters[0]; //Organization Name
			String orgSupportEmail = parameters[1]; //Support Email
			String orgSupportPhoneNumber = parameters[2]; // Phone Number
			String orgHisUrl = "";

			try {
				orgHisUrl = parameters[3]; // HIS URL
			} catch (Exception exp) {
				orgHisUrl = "";
			}

			String description = "";
			if (mm_team_id != null && !mm_team_id.isEmpty()) {
				Organization org = new Organization();
				org.setOrganizationName(orgName);
				org.setOrganizationSupportEmail(orgSupportEmail);
				org.setOrganizationSupportPhone(orgSupportPhoneNumber);
				org.setOrganizationId_MM(mm_team_id);
				org.setOrganizationHisUrl(orgHisUrl);

				description = gson.toJson(org);

				attributes.put(new BasicAttribute("o", orgName));
				attributes.put(new BasicAttribute("description", description));
				logger.info(organizationPrefix + orgName + comma + domainDN);

				connectionContext.createSubcontext(organizationPrefix + orgName + comma + domainDN, attributes);

				createOrganizationUnit(connectionContext, orgName, "Users", "users", "User Group");
				createOrganizationUnit(connectionContext, orgName, "Roles", "roles", "Role Group");
				createOrganizationUnit(connectionContext, orgName, "Tabs", "tabs", "Tab Group");
				createOrganizationUnit(connectionContext, orgName, "TabTemplates", "tabtemplates", "TabTemplate Group");

				createTabtemplate("Chat", orgName);
				createTabtemplate("News", orgName);
				createTabtemplate("Reference", orgName);
				createTabtemplate("Survey", orgName);
				createTabtemplate("ASP", orgName);
				createTabtemplate("ASPTask", orgName);
				createTabtemplate("Assessment", orgName);

				String orgAdminUsername = orgName.toLowerCase() + "_admin";
				createAdminUser4Org(connectionContext, orgAdminUsername, orgAdminUsername + "1234", orgName, "admin", orgName + " Admin");

				createBroadcastRole(orgName);
				createBroadcastTab(orgName);
				addUsers2BroadcastChannel(orgName);
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {

			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/
	}


	public ArrayList<String> getOrganizationList() throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<String> orgList = new ArrayList<String>();

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"o", "description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(domainDN, ORGANIZATION_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				orgList.add(attr.get("o").get(0).toString());
				//description=attr.get("description").get(0).toString();
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		return orgList;
	}

	public ArrayList<String> getTeams() throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<String> orgList = new ArrayList<String>();

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"o", "description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(domainDN, ORGANIZATION_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				String orgName = attr.get("o").get(0).toString();
				String orgObj = attr.get("description").get(0).toString();

				Organization org = gson.fromJson(orgObj, Organization.class);
				if (!org.getOrganizationId_MM().isEmpty()) {
					orgList.add(orgName + ":::" + org.getOrganizationId_MM());
				}
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		return orgList;
	}

	public ArrayList<String> getOrganizationList(String orgName) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		if (orgName.equals("")) {
			return getOrganizationList();
		} else {
			ArrayList<String> orgList = new ArrayList<String>();
			orgList.add(orgName);
			return orgList;
		}
	}

	public void createOrganizationUnit(String... parameters) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			Attributes attributes = new BasicAttributes();
			Attribute objectClass = new BasicAttribute("objectClass");
			objectClass.add("organizationalUnit");
			objectClass.add("top");
			attributes.put(objectClass);

			String orgUnitName = parameters[0]; //Organization Unit Name
			String orgName = parameters[1]; //Organization Name
			String orgUnitLocation = parameters[2]; //Organization Unit Location
			String orgUnitLatitude = parameters[3]; //Organization Unit Latitude
			String orgUnitLongitude = parameters[4]; //Organization Unit Logitude
			String orgUnitEmergencyPhoneNumber = parameters[5]; // Emergency Phone Number

			String description = "";

			OrganizationUnit orgUnit = new OrganizationUnit();
			orgUnit.setOrganizationUnitName(orgUnitName);
			orgUnit.setOrganizationName(orgName);
			orgUnit.setOrganizationUnitLocation(orgUnitLocation);
			orgUnit.setOrganizationUnitLatitude(orgUnitLatitude);
			orgUnit.setOrganizationUnitLongitude(orgUnitLongitude);
			orgUnit.setOrganizationUnitEmergencyNumber(orgUnitEmergencyPhoneNumber);

			description = gson.toJson(orgUnit);

			attributes.put(new BasicAttribute("ou", orgUnitName));
			attributes.put(new BasicAttribute("destinationIndicator", "branch"));
			attributes.put(new BasicAttribute("description", description));
			logger.info(organizationUnitPrefix + orgUnitName + comma + organizationPrefix + orgName + comma + domainDN);

			connectionContext.createSubcontext(organizationUnitPrefix + orgUnitName + comma + organizationPrefix + orgName + comma + domainDN, attributes);
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		} */
	}

	public void createOrganizationUnit(DirContext connectionContext, String orgName, String orgUnitName, String destinationIndicator, String description) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		//DirContext connectionContext = null;

		try {

			Attributes attributes = new BasicAttributes();
			Attribute objectClass = new BasicAttribute("objectClass");
			objectClass.add("organizationalUnit");
			objectClass.add("top");
			attributes.put(objectClass);

			attributes.put(new BasicAttribute("ou", orgUnitName));
			attributes.put(new BasicAttribute("destinationIndicator", destinationIndicator));
			attributes.put(new BasicAttribute("description", description));
			logger.info(organizationUnitPrefix + orgUnitName + comma + organizationPrefix + orgName + comma + domainDN);

			connectionContext.createSubcontext(organizationUnitPrefix + orgUnitName + comma + organizationPrefix + orgName + comma + domainDN, attributes);
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
	}

	public ArrayList<String> getOrganizationUnitList(String orgName) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<String> orgUnitList = new ArrayList<String>();

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"ou", "description", "destinationIndicator"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(organizationPrefix + orgName + comma + domainDN, ORGANIZATION_UNIT_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				orgUnitList.add(attr.get("ou").get(0).toString());
				//description=attr.get("description").get(0).toString();
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		logger.debug("Org Unit List {} " + orgUnitList);

		return orgUnitList;
	}

	public ArrayList<models.ldap.OrganizationUnit> getOrganizationUnits(String orgName) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<models.ldap.OrganizationUnit> orgUnitList = new ArrayList<models.ldap.OrganizationUnit>();
		String description = "";

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"ou", "description", "destinationIndicator"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(organizationPrefix + orgName + comma + domainDN, ORGANIZATION_UNIT_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				//orgUnitList.add(attr.get("ou").get(0).toString());
				description=attr.get("description").get(0).toString();
				models.ldap.OrganizationUnit ou = gson.fromJson(description, models.ldap.OrganizationUnit.class);
				orgUnitList.add(ou);
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
            if (connectionContext != null) {
                try {
                    connectionContext.close();
                }
                catch (NamingException ne) {
                    logger.error("Error Occurred while closing the connection!", ne);
                }
            }
        }*/

		logger.debug("Org Unit List {} " + orgUnitList);

		return orgUnitList;
	}


	public void createDepartment(String... parameters) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			Attributes attributes = new BasicAttributes();
			Attribute objectClass = new BasicAttribute("objectClass");
			objectClass.add("organizationalUnit");
			objectClass.add("top");
			attributes.put(objectClass);

			String departmentName = parameters[0]; //Department Name
			String orgUnitName = parameters[1]; //Organization Unit Name
			String orgName = parameters[2]; //Organization Name

			String description = "";

			Department department = new Department();
			department.setDepartmentName(departmentName);
			department.setOrganizationName(orgUnitName);
			department.setOrganizationUnitName(orgName);

			description = gson.toJson(department);

			attributes.put(new BasicAttribute("ou", departmentName));
			attributes.put(new BasicAttribute("destinationIndicator", "department"));
			attributes.put(new BasicAttribute("description", description));
			logger.info(organizationUnitPrefix + departmentName + comma + organizationUnitPrefix + orgUnitName + comma + organizationPrefix + orgName + comma + domainDN);

			connectionContext.createSubcontext(organizationUnitPrefix + departmentName + comma + organizationUnitPrefix + orgUnitName + comma + organizationPrefix + orgName + comma + domainDN, attributes);
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/
	}


	public ArrayList<String> getDepartmentList(String orgName, String orgUnitName) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<String> departmentList = new ArrayList<String>();

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"ou", "description", "destinationIndicator"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(organizationUnitPrefix + orgUnitName + comma + organizationPrefix + orgName + comma + domainDN, DEPARTMENT_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				departmentList.add(attr.get("ou").get(0).toString());
				//description=attr.get("description").get(0).toString();
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		logger.debug("Department List {} " + departmentList);

		return departmentList;
	}

	public void createRole(String... parameters) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			Attributes attributes = new BasicAttributes();
			Attribute objectClass = new BasicAttribute("objectClass");
			objectClass.add("posixGroup");
			objectClass.add("top");
			attributes.put(objectClass);

			String roleName = parameters[0]; //Role Name
			String roleType = parameters[1]; //Role Type
			String orgName = parameters[2]; //Organization Name
			String accessInfo = parameters[3]; //Has Access Across Units?
			String orgUnitName = parameters[4]; //Organization Unit Name
			String depName = parameters[5]; //Department Name
			String gidNumber = "";
			try {
				gidNumber = getGidNumber4Role(orgName);
			} catch (Exception exp) {
				logger.debug("Error: {}" + exp);
			}

			String description = "";

			boolean hasAccess = false;

			if (accessInfo.equals("access_no")) {
				hasAccess = true;
			}

			Role role = new Role();
			role.setRoleName(roleName);
			role.setRoleType(roleType);
			role.setOrganizationName(orgName);
			role.setAccessAcrossUnits(hasAccess);
			role.setOrganizationUnitName(orgUnitName);
			role.setDepartmentName(depName);
			role.setGidNumber(gidNumber);

			description = gson.toJson(role);

			attributes.put(new BasicAttribute("cn", roleName));
			attributes.put(new BasicAttribute("gidNumber", gidNumber));
			attributes.put(new BasicAttribute("description", description));
			logger.info(commonNamePrefix + roleName + comma + organizationUnitPrefix + ROLES + comma + organizationPrefix + orgName + comma + domainDN);

			connectionContext.createSubcontext(commonNamePrefix + roleName + comma + organizationUnitPrefix + ROLES + comma + organizationPrefix + orgName + comma + domainDN, attributes);
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}  */
	}

	public String getGidNumber4Role(String orgName) throws Exception {
		String gidNumber = "";
		ArrayList<String> gidList = getRoleList(orgName, "group");
		ArrayList<Integer> gidNumberList = new ArrayList<Integer>();
		if (gidList.size() > 0) {
			for (String gidNum : gidList) {
				try {
					gidNumberList.add(Integer.parseInt(gidNum));
				} catch (Exception exp) {
					logger.debug("Exception: {} " + exp);
				}
				Integer maxValue = Collections.max(gidNumberList);
				maxValue += 1;
				gidNumber = String.valueOf(maxValue);
			}
		} else {
			gidNumber = String.valueOf(ROLE_BASE_VALUE + 1);
		}
		return gidNumber;
	}

	public ArrayList<String> getRoleList(String orgName, String flag) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<String> roleList = new ArrayList<String>();
		ArrayList<String> gidNumberList = new ArrayList<String>();

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"cn", "description", "destinationIndicator", "gidNumber"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(organizationUnitPrefix + ROLES + comma + organizationPrefix + orgName + comma + domainDN, ROLE_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				String cn = attr.get("cn").get(0).toString();
				if (!cn.equalsIgnoreCase("broadcast")) {
					roleList.add(cn);
				}
				gidNumberList.add(attr.get("gidNumber").get(0).toString());
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		logger.debug("Role List {} " + roleList);
		if (flag.equals("group")) {
			return gidNumberList;
		} else {
			return roleList;
		}
	}

	public ArrayList<Role> getRoles(String orgName) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<Role> roleList = new ArrayList<Role>();

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(organizationUnitPrefix + ROLES + comma + organizationPrefix + orgName + comma + domainDN, ROLE_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				String roleDefinition = attr.get("description").get(0).toString();
				Role role = gson.fromJson(roleDefinition, Role.class);
				if (!role.getRoleName().equalsIgnoreCase("broadcast")) {
					roleList.add(role);
				}
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		logger.debug("Role List size {} " + roleList.size());
		return roleList;
	}

	public ArrayList<Role> getRoles4Broadcast(String orgName) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<Role> roleList = new ArrayList<Role>();

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(organizationUnitPrefix + ROLES + comma + organizationPrefix + orgName + comma + domainDN, ROLE_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				String roleDefinition = attr.get("description").get(0).toString();
				Role role = gson.fromJson(roleDefinition, Role.class);
				roleList.add(role);
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		logger.debug("Role List size {} " + roleList.size());
		return roleList;
	}


	public void createUser(String... parameters) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			ArrayList<String> roleList = new ArrayList<String>();


			Attributes attributes = new BasicAttributes();
			Attribute objectClass = new BasicAttribute("objectClass");
			objectClass.add("inetOrgPerson");
			objectClass.add("posixAccount");
			objectClass.add("top");
			attributes.put(objectClass);

			String user_first_name = parameters[0]; //First Name
			String user_last_name = parameters[1]; //Last Name
			String user_name = parameters[2]; //UserName
			String user_password = parameters[3]; //Password Plain
			String user_email = parameters[4]; //Email
			String user_qualification = parameters[5]; //Qualification
			String user_specialization = parameters[6]; //Specialization
			String user_employee_id = parameters[7]; //Employee ID
			String user_aadhaar = parameters[8]; //Aadhaar
			String user_pan = parameters[9]; //PAN
			String user_passport = parameters[10]; //Passport
			String user_phone = parameters[11]; //Phone
			String orgName = parameters[12]; //Organization
			String user_org_unit = parameters[13]; //Organization Unit Name
			String user_role = parameters[14]; //Role Info
			String user_department = parameters[15]; //Department Name
			String genderInfo = parameters[16]; //Gender
			String accessInfo = parameters[17]; //Has Access Across Units?
			String universalAccessInfo = parameters[18]; //Has Access Across Organizations?

			String gidNumber = "";

			try {
				gidNumber = getGidNumber4User(user_role, orgName);
			} catch (Exception exp) {
				logger.debug("Error: {}" + exp);
			}

			String description = "";

			boolean hasOUAccess = false;
			boolean hasUniversalAccess = false;

			if (accessInfo.equals("access_ou_yes")) {
				hasOUAccess = true;
			}
			if (universalAccessInfo.equals("access_universal_yes")) {
				hasUniversalAccess = true;
			}

			String userId = getUidNumber4User(orgName);
			String passwordHash = digestPassword("MD5", user_password);

			roleList.add(user_role);

			String[] roles = roleList.toArray(new String[roleList.size()]);

			String mm_user_id = mmServiceManager.createUser(user_email, user_name, user_first_name, user_last_name, user_password);
			logger.info("Created User ID: " + mm_user_id);

			String teamId = searchOrganization(orgName);

			mmServiceManager.addUser2Team(teamId, mm_user_id);

			addUsers2Channel(orgName, roles, mm_user_id);
			User user = null;

			if(mm_user_id!=null && !mm_user_id.isEmpty()) {
				user = new User();
				user.setUserId(userId);
				user.setFirstName(user_first_name);
				user.setLastName(user_last_name);
				user.setUsername(user_name);
				user.setPasswordHash(passwordHash);
				user.setEmail(user_email);
				user.setQualification(user_qualification);
				user.setSpecialization(user_specialization);
				user.setEmployeeId(user_employee_id);
				user.setAadhaarNumber(user_aadhaar);
				user.setPanNumber(user_pan);
				user.setPassportNumber(user_passport);
				user.setPhoneNumber(user_phone);
				user.setOrganization(orgName);
				user.setOrganizationUnit(user_org_unit);
				user.setDepartment(user_department);
				user.setRoleList(roleList);
				user.setGender(genderInfo);
				user.setOuAccess(hasOUAccess);
				user.setUniversalAccess(hasUniversalAccess);
				user.setUserId_MM(mm_user_id);
				user.setActiveStatus(true);
				user.setDateOfDeactivation("");

				addUsers2BroadcastChannel(orgName, user);

				description = gson.toJson(user);

				attributes.put(new BasicAttribute("cn", user_name)); //Common name
				attributes.put(new BasicAttribute("givenName", user_first_name)); //First name
				attributes.put(new BasicAttribute("sn", user_last_name)); //Last name
				attributes.put(new BasicAttribute("gidNumber", getGidNumber4User(user_role, orgName))); //Role ID
				attributes.put(new BasicAttribute("uid", user_name)); //username
				attributes.put(new BasicAttribute("uidNumber", userId)); //User ID
				attributes.put(new BasicAttribute("userPassword", passwordHash)); //User Password
				attributes.put(new BasicAttribute("homeDirectory", "/home/users/" + user_name)); //common name
				attributes.put(new BasicAttribute("description", description)); //All Info

				logger.info(commonNamePrefix + user_name + comma + organizationUnitPrefix + USERS + comma + organizationPrefix + orgName + comma + domainDN);

				connectionContext.createSubcontext(commonNamePrefix + user_name + comma + organizationUnitPrefix + USERS + comma + organizationPrefix + orgName + comma + domainDN, attributes);
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/
	}


	public String processBulkUpload(String fileExt, File inputFile, String hisURL) {
		String result = "true";

		if (fileExt.equalsIgnoreCase("csv")) {
			processCSVFile(inputFile, hisURL);
		} else if (fileExt.equalsIgnoreCase("xls")) {
			processXLSFile(inputFile, hisURL);
		} else if (fileExt.equalsIgnoreCase("xlsx")) {
			processXLSXFile(inputFile, hisURL);
		}

		return result;
	}

	public void processCSVFile(File inputFile, String hisURL) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
			String line = "";
			//bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {

				logger.info("line: " + line);
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	public void processXLSFile(File inputFile, String hisURL) {
		List<UsersDtos> users = new ArrayList<UsersDtos>();
		try {
			//obtaining input bytes from a file
			FileInputStream fis = new FileInputStream(inputFile);
			//creating workbook instance that refers to .xls file
			HSSFWorkbook wb = new HSSFWorkbook(fis);
			//creating a Sheet object to retrieve the object
			HSSFSheet sheet = wb.getSheetAt(0);
			//evaluating cell type
			FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();

			int columnIndex = 0;
			UsersDtos user;
			int rowIndex = 0;
			for (Row row : sheet)     //iteration over row using for each loop
			{
				columnIndex = 0;
				user = new UsersDtos();
				user.setHisURL(hisURL);
				for (Cell cell : row)    //iteration over cell using for each loop
				{
					//System.out.print(cell.getStringCellValue() + "\t");
					switch (columnIndex) {
					case 0:
						user.setOrganizationName(cell.getStringCellValue());
						break;
					case 1:
						user.setOrganizationUnitName(cell.getStringCellValue());
						break;
					case 2:
						user.setDepartment(cell.getStringCellValue());
						break;  
					case 3:
						user.setRole(cell.getStringCellValue());
						break;
					case 4:
						user.setFirstName(cell.getStringCellValue());
						break;  
					case 5:
						user.setLastName(cell.getStringCellValue());
						break;  
					case 6:
						user.setGender(cell.getStringCellValue());
						break;  
					case 7:
						user.setUserName(cell.getStringCellValue());
						break;  
					case 8:
						user.setPassword(cell.getStringCellValue());
						break;  
					case 9:
						user.setEmployeeId(cell.getStringCellValue());
						break;
					case 10:
						user.setEmail(cell.getStringCellValue());
						break;
					case 11:
						user.setChannelName(cell.getStringCellValue());
						break;  
					case 12:
						user.setChannelDisplayName(cell.getStringCellValue());
						break;
					case 13:
						user.setChannelPurpose(cell.getStringCellValue());
						break;
					}
					columnIndex++;
				}
				System.out.println();
				if (rowIndex > 0) {
					users.add(user);
				}
				rowIndex++;
			}

			System.out.println("User List Size: " + users.size());
			createUsersInJava8(users);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	public void processXLSXFile(File inputFile, String hisURL) {

		List<UsersDtos> users = new ArrayList<UsersDtos>();

		try {
			FileInputStream fis = new FileInputStream(inputFile);   //obtaining bytes from the file
			//creating Workbook instance that refers to .xlsx file
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object
			Iterator<Row> itr = sheet.iterator();    //iterating over excel file
			int columnIndex = 0;
			UsersDtos user;

			int rowIndex = 0;

			while (itr.hasNext()) {
				Row row = itr.next();
				Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column
				columnIndex = 0;
				user = new UsersDtos();
				user.setHisURL(hisURL);
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					//System.out.print(cell.getStringCellValue() + "\t");
					switch (columnIndex) {
					case 0:
						user.setOrganizationName(cell.getStringCellValue());
						break;
					case 1:
						user.setOrganizationUnitName(cell.getStringCellValue());
						break;
					case 2:
						user.setDepartment(cell.getStringCellValue());
						break;  
					case 3:
						user.setRole(cell.getStringCellValue());
						break;
					case 4:
						user.setFirstName(cell.getStringCellValue());
						break;  
					case 5:
						user.setLastName(cell.getStringCellValue());
						break;  
					case 6:
						user.setGender(cell.getStringCellValue());
						break;  
					case 7:
						user.setUserName(cell.getStringCellValue());
						break;  
					case 8:
						user.setPassword(cell.getStringCellValue());
						break;  
					case 9:
						user.setEmployeeId(cell.getStringCellValue());
						break;
					case 10:
						user.setEmail(cell.getStringCellValue());
						break;
					case 11:
						user.setChannelName(cell.getStringCellValue());
						break;  
					case 12:
						user.setChannelDisplayName(cell.getStringCellValue());
						break;
					case 13:
						user.setChannelPurpose(cell.getStringCellValue());
						break;

					}
					columnIndex++;
				}
				System.out.println("");
				if (rowIndex > 0) {
					users.add(user);
				}
				rowIndex++;
			}

			System.out.println("User List Size: " + users.size());

			System.out.println("User List (0): " + users.get(0).getFirstName());
			createUsersInJava8(users);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}


	//create bulk users

	public void createUserOld(List<UsersDtos> users) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException{

		DirContext connectionContext = null;        

		try {
			connectionContext = new InitialDirContext(connectionProperties);
			logger.info("LDAP Connection context created...");


			Attributes attributes =new BasicAttributes();
			Attribute objectClass =new BasicAttribute("objectClass");
			objectClass.add("inetOrgPerson");
			objectClass.add("posixAccount");
			objectClass.add("top");
			attributes.put(objectClass);

			String roleType="";
			String orgUnitLocation ="";
			String orgUnitLatitude = "";
			String orgUnitLongitude = ""; 
			String templateName="Chat";

			boolean hasOUAccess = false;
			boolean hasUniversalAccess = false; 
			boolean acessAcrossUnits=false;
			boolean acessAcrossDept=false;

			users.stream().forEach(HandlingConsumer.handlingConsumerBuilder(user ->{
				List<Role> roleList=new ArrayList<Role>();

				String orgName=user.getOrganizationName();
				String teamId = searchOrganization(orgName);
				logger.info("orgName : "+orgName+"and teamId :"+teamId);
				//check weather team is present or not
				if(teamId!=null && !teamId.isEmpty()) {
					roleList=getRoles(orgName);
					logger.info("roles"+roleList+"roleList size"+roleList.size());
					if(roleList.size() > 0) {
						roleList.stream().forEach(HandlingConsumer.handlingConsumerBuilder(role ->{
							// check role is present or not
							if(role.getRoleName().contains(user.getRole())) {
								List<User> userList=getUsers(orgName);
								logger.info("users"+userList);
								if(userList.size() > 0) {
									userList.stream().forEach(HandlingConsumer.handlingConsumerBuilder(usr ->{
										List<String> roles = usr.getRoleList();
										logger.info("user having role"+roles);
										// check weather user present or not
										if(usr.getUsername().contains(user.getUserName())) {
											// check weather user having role or not
											logger.info("username "+usr.getUsername());
											if(roles.contains(user.getRole())) {
												List<Tab> tabList=getTabs(orgName);
												tabList.stream().forEach(HandlingConsumer.handlingConsumerBuilder(tab ->{
													//check weather channel is present or not
													if(tab.getTabName().contains(user.getChannelName())) {
														logger.info("channelName "+user.getChannelName());
														// If channel present for  user
														String response=mmServiceManager.addUser2Channel(tab.getChannelId_MM(), usr.getUserId_MM());                                        
														logger.info("alreaday user is assigned "+response);
													}else {

														/*
														 * createTab(user.getChannelName(),user.getChannelPurpose(),user
														 * .getChannelPurpose(),templateName,user.getOrganizationName(),
														 * String.valueOf(acessAcrossUnits),
														 * user.getOrganizationUnitName(),user.getDepartment(),user.
														 * getRole(),String.valueOf(acessAcrossDept));
														 * logger.info("Tab created sucessully"+user.getChannelName());
														 */
														//creating new channel and assign user
														TabDto tabDto = new TabDto();
														tabDto.setChannelName(user.getChannelName());
														tabDto.setHeader(user.getChannelPurpose());
														tabDto.setPurpose(user.getChannelPurpose());
														tabDto.setOrganizationUnitName(user.getOrganizationUnitName());
														tabDto.setTemplateName(templateName);
														tabDto.setDepartmentName(user.getDepartment());
														tabDto.setRole(user.getRole());
														tabDto.setAccessAcrossUnits(false);
														tabDto.setAccessAcrossDepartments(false);
														try {
															tabDto.setChannelDisplayName(user.getChannelDisplayName());
														} catch (Exception exp) {
															tabDto.setChannelDisplayName(user.getChannelName());
														}
														createTab(tabDto);
														logger.info("Tab created sucessully" + user.getChannelName());
													}
												}));
											}else {
												// assign role to user 
												addRoles4User(orgName,user.getUserName(),user.getRole());
												logger.info("role is assign to User"+user.getRole());
											}
										}else {

											// create new user
											createUser(user.getFirstName(),user.getLastName(),user.getUserName(),user.getPassword(),user.getEmail()
													,user.getQualification(),user.getSpecialization(),user.getEmployeeId(),user.getAadhaarNumber()
													,user.getPanNumber(),user.getPassportNumber(),user.getPhoneNumber(),orgName,user.getOrganizationUnitName(),
													user.getRole(),user.getDepartment(),user.getGender(),String.valueOf(hasOUAccess),String.valueOf(hasOUAccess));
											logger.info("User created sucessfully"+user.getUserName());
										}
									}
											)
											);
								}
							}else {
								// create new role and Tab

								createRole(user.getRole(),role.getRoleType(),orgName,String.valueOf(hasOUAccess),user.getOrganizationUnitName(),user.getDepartment()); 
								logger.info("role created sucessfully"+user.getRole());
								//creating new channel and assign user
								TabDto tabDto = new TabDto();
								tabDto.setChannelName(user.getChannelName());
								tabDto.setHeader(user.getChannelPurpose());
								tabDto.setPurpose(user.getChannelPurpose());
								tabDto.setOrganizationUnitName(user.getOrganizationUnitName());
								tabDto.setTemplateName(templateName);
								tabDto.setDepartmentName(user.getDepartment());
								tabDto.setRole(user.getRole());
								tabDto.setAccessAcrossUnits(false);
								tabDto.setAccessAcrossDepartments(false);
								try {
									tabDto.setChannelDisplayName(user.getChannelDisplayName());
								} catch (Exception exp) {
									tabDto.setChannelDisplayName(user.getChannelName());
								}
								createTab(tabDto);
								logger.info("Tab created sucessully" + user.getChannelName());

								createUser(user.getFirstName(),user.getLastName(),user.getUserName(),user.getPassword(),user.getEmail()
										,user.getQualification(),user.getSpecialization(),user.getEmployeeId(),user.getAadhaarNumber()
										,user.getPanNumber(),user.getPassportNumber(),user.getPhoneNumber(),orgName,user.getOrganizationUnitName(),
										user.getRole(),user.getDepartment(),user.getGender(),String.valueOf(hasOUAccess),String.valueOf(hasOUAccess));
								logger.info("User created"+user.getUserName());
							}
						}));
					}
				}else {
					String hisURL = "";
					try {
						hisURL = user.getHisURL();
					} catch (Exception exp) {
						hisURL = "";
					}
					// create new team,orgUnit,role and Tab
					createOrganization(user.getOrganizationName(),user.getEmail(),user.getPhoneNumber(), hisURL);
					logger.info("team created"+user.getOrganizationName());
					createOrganizationUnit(user.getOrganizationUnitName(),user.getOrganizationName(),orgUnitLocation,orgUnitLatitude,orgUnitLongitude,user.getPhoneNumber());
					logger.info("Ou created "+user.getOrganizationUnitName());
					createDepartment(user.getDepartment(),user.getOrganizationUnitName(),user.getOrganizationName());
					logger.info("department created"+user.getDepartment());
					createRole(user.getRole(),roleType,user.getOrganizationName(),String.valueOf(hasOUAccess),user.getOrganizationUnitName(),user.getDepartment()); 
					logger.info("role created"+user.getRole());
					//creating new channel and assign user
					TabDto tabDto = new TabDto();
					tabDto.setChannelName(user.getChannelName());
					tabDto.setHeader(user.getChannelPurpose());
					tabDto.setPurpose(user.getChannelPurpose());
					tabDto.setOrganizationUnitName(user.getOrganizationUnitName());
					tabDto.setTemplateName(templateName);
					tabDto.setDepartmentName(user.getDepartment());
					tabDto.setRole(user.getRole());
					tabDto.setAccessAcrossUnits(false);
					tabDto.setAccessAcrossDepartments(false);
					try {
						tabDto.setChannelDisplayName(user.getChannelDisplayName());
					} catch (Exception exp) {
						tabDto.setChannelDisplayName(user.getChannelName());
					}
					createTab(tabDto);
					logger.info("Tab created sucessully" + user.getChannelName());

					createUser(user.getFirstName(),user.getLastName(),user.getUserName(),user.getPassword(),user.getEmail()
							,user.getQualification(),user.getSpecialization(),user.getEmployeeId(),user.getAadhaarNumber()
							,user.getPanNumber(),user.getPassportNumber(),user.getPhoneNumber(),user.getOrganizationName(),
							user.getOrganizationUnitName(),user.getRole(),user.getDepartment(),user.getGender(),
							String.valueOf(hasOUAccess),String.valueOf(hasOUAccess));
					logger.info("User created"+user.getUserName());
				}
			}));

		}catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		}
		catch(NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		}
		catch(Exception e) {
			logger.error("Error Occurred!", e);
		}
		finally {
			if (connectionContext != null) {
				try {   
					connectionContext.close();
				}
				catch (NamingException ne) { 
					logger.error("Error Occurred while closing the connection!", ne); 
				}
			}
		}
	}

	public List<ResponseUserDto> createUser(List<UsersDtos> users) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;
		List<ResponseUserDto> respUserDto = new ArrayList<ResponseUserDto>();
		String status = null;
		String userId = null;
		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			Attributes attributes = new BasicAttributes();
			Attribute objectClass = new BasicAttribute("objectClass");
			objectClass.add("inetOrgPerson");
			objectClass.add("posixAccount");
			objectClass.add("top");
			attributes.put(objectClass);

			String roleType = "";
			String orgUnitLocation = "";
			String orgUnitLatitude = "";
			String orgUnitLongitude = "";
			String templateName = "";

			boolean hasOUAccess = false;
			boolean hasUniversalAccess = false;

			for (UsersDtos userDto : users) {
				String description = "";
				String orgName = userDto.getOrganizationName();
				String teamId = "";
				List<Role> roleList = new ArrayList<Role>();
				List<User> userList = new ArrayList<User>();
				List<String> roles = new ArrayList<String>();
				List<Tab> tabList = new ArrayList<Tab>();

				teamId = searchOrganization(orgName);
				logger.info("orgName : " + orgName + "and teamId :" + teamId);

				//check weather team is present or not
				if (teamId != null && !teamId.isEmpty()) {
					roleList = getRoles(orgName);
					logger.info("roles" + roleList + "roleList size" + roleList.size());
					if (roleList.size() > 0) {
						for (Role role : roleList) {
							// check role is present or not
							if (role.getRoleName().contains(userDto.getRole())) {
								userList = getUsers(orgName);
								logger.info("users" + userList);
								for (User usr : userList) {
									roles = usr.getRoleList();
									logger.info("user having role" + roles);
									// check weather user present or not
									if (usr.getUsername().contains(userDto.getUserName())) {
										// check weather user having role or not
										logger.info("username " + usr.getUsername());
										if (roles.contains(userDto.getRole())) {
											tabList = getTabs(orgName);
											for (Tab tab : tabList) {
												//check weather channel is present or not
												if (tab.getTabName().contains(userDto.getChannelName())) {
													// If channel present for  user
													String response = mmServiceManager.addUser2Channel(tab.getChannelId_MM(), usr.getUserId_MM());
													logger.info("user is assign to channel" + response);

												} else {

													//creating new channel and assign user
													TabDto tabDto = new TabDto();
													tabDto.setChannelName(userDto.getChannelName());
													tabDto.setHeader(userDto.getChannelPurpose());
													tabDto.setPurpose(userDto.getChannelPurpose());
													tabDto.setOrganizationUnitName(userDto.getOrganizationUnitName());
													tabDto.setTemplateName(templateName);
													tabDto.setDepartmentName(userDto.getDepartment());
													tabDto.setRole(userDto.getRole());
													tabDto.setAccessAcrossUnits(false);
													tabDto.setAccessAcrossDepartments(false);
													try {
														tabDto.setChannelDisplayName(userDto.getChannelDisplayName());
													} catch (Exception exp) {
														tabDto.setChannelDisplayName(userDto.getChannelName());
													}
													createTab(tabDto);
													logger.info("Tab created sucessully" + userDto.getChannelName());
												}
											}
										} else {
											// assign role to user
											addRoles4User(orgName, userDto.getUserName(), userDto.getRole());
											logger.info("role is assign to User" + userDto.getRole());
										}
									} else {

										// create new user
										createUser(userDto.getFirstName(), userDto.getLastName(), userDto.getUserName(), userDto.getPassword(), userDto.getEmail()
												, userDto.getQualification(), userDto.getSpecialization(), userDto.getEmployeeId(), userDto.getAadhaarNumber()
												, userDto.getPanNumber(), userDto.getPassportNumber(), userDto.getPhoneNumber(), orgName, userDto.getOrganizationUnitName(),
												userDto.getRole(), userDto.getDepartment(), userDto.getGender(), String.valueOf(hasOUAccess), String.valueOf(hasOUAccess));
										logger.info("User created sucessfully" + userDto.getUserName());

										userId = searchUser(userDto.getUserName());
										ResponseUserDto response = new ResponseUserDto();
										response.setTeamId(teamId);
										response.setUserId(userId);
										response.setUserName(userDto.getUserName());
										if (userId != null) {
											status = "created";
										} else {
											status = "failed";
										}
										response.setStatus(status);
										respUserDto.add(response);

									}
								}
							} else {
								// create new role and Tab

								createRole(userDto.getRole(), role.getRoleType(), orgName, String.valueOf(hasOUAccess), userDto.getOrganizationUnitName(), userDto.getDepartment());
								logger.info("role created sucessfully" + userDto.getRole());
								TabDto tabDto = new TabDto();
								tabDto.setChannelName(userDto.getChannelName());
								tabDto.setHeader(userDto.getChannelPurpose());
								tabDto.setPurpose(userDto.getChannelPurpose());
								tabDto.setTemplateName(templateName);
								tabDto.setOrganizationUnitName(userDto.getOrganizationUnitName());
								tabDto.setDepartmentName(userDto.getDepartment());
								tabDto.setRole(userDto.getRole());
								tabDto.setAccessAcrossUnits(false);
								tabDto.setAccessAcrossDepartments(false);
								try {
									tabDto.setChannelDisplayName(userDto.getChannelDisplayName());
								} catch (Exception exp) {
									tabDto.setChannelDisplayName(userDto.getChannelName());
								}
								createTab(tabDto);
								logger.info("Tab created " + userDto.getChannelName());
								createUser(userDto.getFirstName(), userDto.getLastName(), userDto.getUserName(), userDto.getPassword(), userDto.getEmail()
										, userDto.getQualification(), userDto.getSpecialization(), userDto.getEmployeeId(), userDto.getAadhaarNumber()
										, userDto.getPanNumber(), userDto.getPassportNumber(), userDto.getPhoneNumber(), orgName, userDto.getOrganizationUnitName(),
										userDto.getRole(), userDto.getDepartment(), userDto.getGender(), String.valueOf(hasOUAccess), String.valueOf(hasOUAccess));
								logger.info("User created" + userDto.getUserName());

								userId = searchUser(userDto.getUserName());
								ResponseUserDto response = new ResponseUserDto();
								response.setTeamId(teamId);
								response.setUserId(userId);
								response.setUserName(userDto.getUserName());
								if (userId != null) {
									status = "created";
								} else {
									status = "failed";
								}
								response.setStatus(status);
								respUserDto.add(response);

							}
						}
					}
				} else {
					String hisURL = "";
					try {
						hisURL = userDto.getHisURL();
					} catch (Exception exp) {
						hisURL = "";
					}
					// create new team,orgUnit,role and Tab
					createOrganization(orgName, userDto.getEmail(), userDto.getPhoneNumber(), hisURL);
					logger.info("team created" + orgName);
					createOrganizationUnit(userDto.getOrganizationUnitName(), orgName, orgUnitLocation, orgUnitLatitude, orgUnitLongitude, userDto.getPhoneNumber());
					logger.info("Ou created " + userDto.getOrganizationUnitName());
					createDepartment(userDto.getDepartment(), userDto.getOrganizationUnitName(), orgName);
					logger.info("department created" + userDto.getDepartment());
					createRole(userDto.getRole(), roleType, orgName, String.valueOf(hasOUAccess), userDto.getOrganizationUnitName(), userDto.getDepartment());
					logger.info("role created" + userDto.getRole());
					TabDto tabDto = new TabDto();
					tabDto.setChannelName(userDto.getChannelName());
					tabDto.setHeader(userDto.getChannelPurpose());
					tabDto.setPurpose(userDto.getChannelPurpose());
					tabDto.setOrganizationUnitName(userDto.getOrganizationUnitName());
					tabDto.setTemplateName(templateName);
					tabDto.setDepartmentName(userDto.getDepartment());
					tabDto.setRole(userDto.getRole());
					tabDto.setAccessAcrossUnits(false);
					tabDto.setAccessAcrossDepartments(false);
					try {
						tabDto.setChannelDisplayName(userDto.getChannelDisplayName());
					} catch (Exception exp) {
						tabDto.setChannelDisplayName(userDto.getChannelName());
					}
					createTab(tabDto);
					logger.info("Tab created" + userDto.getChannelName());
					createUser(userDto.getFirstName(), userDto.getLastName(), userDto.getUserName(), userDto.getPassword(), userDto.getEmail()
							, userDto.getQualification(), userDto.getSpecialization(), userDto.getEmployeeId(), userDto.getAadhaarNumber()
							, userDto.getPanNumber(), userDto.getPassportNumber(), userDto.getPhoneNumber(), orgName, userDto.getOrganizationUnitName(),
							userDto.getRole(), userDto.getDepartment(), userDto.getGender(), String.valueOf(hasOUAccess), String.valueOf(hasOUAccess));
					logger.info("User created" + userDto.getUserName());

					String respTeamId = searchOrganization(orgName);
					userId = searchUser(userDto.getUserName());
					ResponseUserDto response = new ResponseUserDto();
					response.setTeamId(respTeamId);
					response.setUserId(userId);
					response.setUserName(userDto.getUserName());
					if (userId != null) {
						status = "created";
					} else {
						status = "failed";
					}
					response.setStatus(status);
					respUserDto.add(response);

				}
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
		if (connectionContext != null) {
			try {
				connectionContext.close();
			}
			catch (NamingException ne) {
				logger.error("Error Occurred while closing the connection!", ne);
			}
		}
	}*/
		return respUserDto;
	}

	public void createUserInJava8(List<UsersDtos> users) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException{       

		DirContext connectionContext = null; 
		try {
			connectionContext = new InitialDirContext(connectionProperties);
			logger.info("LDAP Connection context created...");


			Attributes attributes =new BasicAttributes();
			Attribute objectClass =new BasicAttribute("objectClass");
			objectClass.add("inetOrgPerson");
			objectClass.add("posixAccount");
			objectClass.add("top");
			attributes.put(objectClass);

			String orgUnitLocation ="";
			String orgUnitLatitude = "";
			String orgUnitLongitude = ""; 
			String templateName="Chat";

			boolean hasOUAccess = false;
			boolean hasUniversalAccess = false; 
			boolean acessAcrossUnits=false;
			boolean acessAcrossDept=false;

			users.stream().forEach(HandlingConsumer.handlingConsumerBuilder(user ->{
				logger.info("user object : "+user);
				//List<Role> roleList=new ArrayList<Role>();

				String firstName=user.getFirstName();
				String lastName=user.getLastName();
				String userName=user.getUserName();
				String password=user.getPassword();
				String email=user.getEmail();
				String qualification=user.getQualification();
				String specialization=user.getSpecialization();
				String employeeId=user.getEmployeeId();
				String aadhaarNumber=user.getAadhaarNumber();
				String panNumber=user.getPanNumber();
				String passportNumber=user.getPassportNumber();
				String phoneNumber=user.getPhoneNumber();
				String orgName=user.getOrganizationName();
				String orgunitName=user.getOrganizationUnitName();
				String department=user.getDepartment();
				String userRole=user.getRole();
				String gender=user.getGender();
				String channelName=user.getChannelName();
				String channelPurpose=user.getChannelPurpose();
				String channelDisplayName=user.getChannelDisplayName();
				String hasOUAccessString= String.valueOf(hasOUAccess);
				String hasUniversalAccessString=String.valueOf(hasUniversalAccess);
				String url=user.getHisURL();
				String roleType=user.getRole();
				String teamId = searchOrganization(orgName);
				logger.info("orgName : "+orgName+"and teamId :"+teamId);
				List<String> orgUnitList=null;

				//check weather team is present or not
				if(teamId!=null && !teamId.isEmpty()) {
					orgUnitList=getOrganizationUnitList(orgName);
					logger.info(" org unit list"+orgUnitList);
					if(orgUnitList.contains(orgunitName)) {
						logger.info("org unit"+orgunitName);
						List<String> deptList=getDepartmentList(orgName, orgunitName);
						logger.info("department list"+deptList);
						if(deptList.contains(department)) {
							logger.info("department name "+department);
							List<Role> roles=getRoles(orgName);
							if(roles.stream().filter(r-> r.getRoleName().equals(userRole)) != null){
								logger.info("role name "+userRole);
								List<User> userList = getUsers(orgName);
								logger.info(" list of users" +userList);
								if(userList.stream().filter(u-> u.getUsername().equals(userName)) != null){
									String userRoles= getUserRoleList(orgName, userName, "page");
									logger.info("user having role" + roles);
									if(userRoles.equals(userRole)) {
										List<Tab> tabList = getTabs(orgName);
										logger.info("list of tab" +tabList);
										if(tabList.stream().filter(t-> t.getTabName().equals(channelName)) != null){
											// If channel present for  user
											String channel_mm_id=searchTab(channelName);
											String mm_user_id=searchUserGetMMUserId(userName);
											String resp=mmServiceManager.addUser2Channel(channel_mm_id,mm_user_id);
											logger.info("assign user to particular channel \t " + channelName);

										}else {
											//creating new channel and assign user

											TabDto tabDto = new TabDto();
											tabDto.setChannelName(channelName);
											tabDto.setHeader(channelPurpose);
											tabDto.setPurpose(channelPurpose);
											tabDto.setOrganizationUnitName(orgunitName);
											tabDto.setOrganizationName(orgName);
											tabDto.setTemplateName(templateName);
											tabDto.setDepartmentName(department);
											tabDto.setRole(userRole);
											tabDto.setAccessAcrossUnits(acessAcrossUnits);
											tabDto.setAccessAcrossDepartments(
													acessAcrossDept); 
											try {
												tabDto.setChannelDisplayName(channelDisplayName); 
											} catch (Exception exp){
												tabDto.setChannelDisplayName(channelName);
											}
											createTab(tabDto);
											logger.info(" Tab created sucessully \t" + channelName);
										}
									}else {
										// assign role to user 
										addRoles4User(orgName,userName,userRole);
										logger.info("role is assign to User \t" + userRole);
									}
								}else {
									// create new user
									createUser(firstName,lastName,userName,password,email,qualification,specialization,employeeId
											,aadhaarNumber,panNumber,passportNumber,phoneNumber,orgName,orgunitName,userRole,department,gender,hasOUAccessString,hasUniversalAccessString);
									logger.info(" User created sucessfully \t"+userName);

								}
							}else {
								// create new role and Tab
								createRole(userRole,roleType,orgName,hasOUAccessString,orgunitName,department); 
								logger.info(" role created sucessfully \t"+userRole);
								//creating new channel and assign user

								TabDto tabDto = new TabDto();
								tabDto.setChannelName(channelName);
								tabDto.setHeader(channelPurpose);
								tabDto.setPurpose(channelPurpose);
								tabDto.setOrganizationUnitName(orgunitName);
								tabDto.setOrganizationName(orgName);
								tabDto.setTemplateName(templateName);
								tabDto.setDepartmentName(department); 
								tabDto.setRole(userRole);
								tabDto.setAccessAcrossUnits(acessAcrossUnits);
								tabDto.setAccessAcrossDepartments(acessAcrossDept);
								try {
									tabDto.setChannelDisplayName(channelDisplayName);
								}catch (Exception exp) { 
									tabDto.setChannelDisplayName(channelName);
								}
								createTab(tabDto);
								logger.info("new Tab created sucessully \t" + channelName);
								// create new user
								createUser(firstName,lastName,userName,password,email,qualification,specialization,employeeId
										,aadhaarNumber,panNumber,passportNumber,phoneNumber,orgName,orgunitName,userRole,department,gender,hasOUAccessString,hasUniversalAccessString);
								logger.info("new User created sucessfully \t "+userName);   
							}
						}else {
							//create department
							createDepartment(department,orgunitName,orgName);
							logger.info(" department created \t "+department);
							// create new role and Tab
							createRole(userRole,roleType,orgName,hasOUAccessString,orgunitName,department); 
							logger.info("role created sucessfully \t"+userRole);
							//creating new channel and assign user

							TabDto tabDto = new TabDto(); 
							tabDto.setChannelName(channelName);
							tabDto.setHeader(channelPurpose); 
							tabDto.setPurpose(channelPurpose);
							tabDto.setOrganizationUnitName(orgunitName);
							tabDto.setOrganizationName(orgName); 
							tabDto.setTemplateName(templateName);
							tabDto.setDepartmentName(department); 
							tabDto.setRole(userRole);
							tabDto.setAccessAcrossUnits(acessAcrossUnits);
							tabDto.setAccessAcrossDepartments(acessAcrossDept); 
							try {
								tabDto.setChannelDisplayName(channelDisplayName);
							} catch (Exception exp) {
								tabDto.setChannelDisplayName(channelName);
							} 
							createTab(tabDto);
							logger.info("Tab created sucessully \t" + channelName);
							// create new user
							createUser(firstName,lastName,userName,password,email,qualification,specialization,employeeId
									,aadhaarNumber,panNumber,passportNumber,phoneNumber,orgName,orgunitName,userRole,department,gender,hasOUAccessString,hasUniversalAccessString);
							logger.info("User created sucessfully \t"+userName);
						}
					}else {
						// creating org unit
						createOrganizationUnit(orgunitName,orgName,orgUnitLocation,orgUnitLatitude,orgUnitLongitude,phoneNumber);
						logger.info("new Ou created "+orgunitName);
						// creating department
						createDepartment(department,orgunitName,orgName);
						logger.info("new department created"+department);
						// creating a role
						createRole(userRole,roleType,orgName,hasOUAccessString,orgunitName,department);
						logger.info("role created sucessfully"+userRole);
						//creating new channel and assign user

						TabDto tabDto = new TabDto(); 
						tabDto.setChannelName(channelName);
						tabDto.setHeader(channelPurpose); 
						tabDto.setPurpose(channelPurpose);
						tabDto.setOrganizationUnitName(orgunitName);
						tabDto.setOrganizationName(orgName); 
						tabDto.setTemplateName(templateName);
						tabDto.setDepartmentName(department); 
						tabDto.setRole(userRole);
						tabDto.setAccessAcrossUnits(acessAcrossUnits);
						tabDto.setAccessAcrossDepartments(acessAcrossDept); 
						try {
							tabDto.setChannelDisplayName(channelDisplayName);
						} catch (Exception exp) {
							tabDto.setChannelDisplayName(channelName);
						} 
						createTab(tabDto);
						logger.info("Tab created sucessully \t" + channelName);
						// create new user
						createUser(firstName,lastName,userName,password,email,qualification,specialization,employeeId
								,aadhaarNumber,panNumber,passportNumber,phoneNumber,orgName,orgunitName,userRole,department,gender,hasOUAccessString,hasUniversalAccessString);
						logger.info("User created sucessfully \t"+userName);
					}

				}else {
					String hisURL = "";
					try {
						hisURL = url;
					} catch (Exception exp) {
						hisURL = "";
					}
					// create new team
					createOrganization(orgName,email,phoneNumber,hisURL);
					logger.info(" team created \t"+orgName);
					createOrganizationUnit(orgunitName,orgName,orgUnitLocation,orgUnitLatitude,orgUnitLongitude,phoneNumber);
					logger.info(" Ou created \t"+orgunitName);
					createDepartment(department,orgunitName,orgName);
					logger.info(" department created \t"+department);
					// create new role and Tab
					createRole(userRole,roleType,orgName,hasOUAccessString,orgunitName,department); 
					logger.info(" role created sucessfully \t"+userRole);
					//creating new channel and assign user

					TabDto tabDto = new TabDto();
					tabDto.setChannelName(channelName);
					tabDto.setHeader(channelPurpose);
					tabDto.setPurpose(channelPurpose);
					tabDto.setOrganizationUnitName(orgunitName);
					tabDto.setOrganizationName(orgName); 
					tabDto.setTemplateName(templateName);
					tabDto.setDepartmentName(department);
					tabDto.setRole(userRole);
					tabDto.setAccessAcrossUnits(acessAcrossUnits);
					tabDto.setAccessAcrossDepartments(acessAcrossDept);
					try {
						tabDto.setChannelDisplayName(channelDisplayName);
					} catch (Exception exp) {
						tabDto.setChannelDisplayName(channelName); 
					} 
					createTab(tabDto);

					logger.info(" Tab created sucessully \t" + channelName);

					// create new user
					createUser(firstName,lastName,userName,password,email,qualification,specialization,employeeId
							,aadhaarNumber,panNumber,passportNumber,phoneNumber,orgName,orgunitName,userRole,department,gender,hasOUAccessString,hasUniversalAccessString);
					logger.info(" User created sucessfully \t"+userName);
				}
			}));

		}catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		}
		catch(NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		}
		catch(Exception e) {
			logger.error("Error Occurred!", e);
		}
		finally {
			if (connectionContext != null) {
				try {   
					connectionContext.close();
				}
				catch (NamingException ne) { 
					logger.error("Error Occurred while closing the connection!", ne); 
				}
			}
		}
	}


	public List<ResponseUserDto> createUsersInJava8(List<UsersDtos> users) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException{       

		DirContext connectionContext = null; 
		List<ResponseUserDto> respUserDto = new ArrayList<ResponseUserDto>();       

		try {
			connectionContext = new InitialDirContext(connectionProperties);
			logger.info("LDAP Connection context created...");


			Attributes attributes =new BasicAttributes();
			Attribute objectClass =new BasicAttribute("objectClass");
			objectClass.add("inetOrgPerson");
			objectClass.add("posixAccount");
			objectClass.add("top");
			attributes.put(objectClass);

			String orgUnitLocation ="";
			String orgUnitLatitude = "";
			String orgUnitLongitude = ""; 
			String templateName="Chat";

			boolean hasOUAccess = false;
			boolean hasUniversalAccess = false; 
			boolean acessAcrossUnits=false;
			boolean acessAcrossDept=false;

			users.stream().forEach(HandlingConsumer.handlingConsumerBuilder(user ->{
				logger.info("user object : "+user);
				//List<Role> roleList=new ArrayList<Role>();

				String firstName=user.getFirstName();
				String lastName=user.getLastName();
				String userName=user.getUserName();
				String password=user.getPassword();
				String email=user.getEmail();
				String qualification=user.getQualification();
				String specialization=user.getSpecialization();
				String employeeId=user.getEmployeeId();
				String aadhaarNumber=user.getAadhaarNumber();
				String panNumber=user.getPanNumber();
				String passportNumber=user.getPassportNumber();
				String phoneNumber=user.getPhoneNumber();
				String orgName=user.getOrganizationName();
				String orgunitName=user.getOrganizationUnitName();
				String department=user.getDepartment();
				String userRole=user.getRole();
				String gender=user.getGender();
				String channelName=user.getChannelName();
				String channelPurpose=user.getChannelPurpose();
				String channelDisplayName=user.getChannelDisplayName();
				String hasOUAccessString= String.valueOf(hasOUAccess);
				String hasUniversalAccessString=String.valueOf(hasUniversalAccess);
				String url=user.getHisURL();
				String roleType=user.getRole();
				String teamId = searchOrganization(orgName);
				logger.info("orgName : "+orgName+"and teamId :"+teamId);
				List<String> orgUnitList=null;

				//check weather team is present or not
				if(teamId!=null && !teamId.isEmpty()) {
					orgUnitList=getOrganizationUnitList(orgName);
					logger.info(" org unit list"+orgUnitList);
					if(orgUnitList.contains(orgunitName)) {
						logger.info("org unit"+orgunitName);
						List<String> deptList=getDepartmentList(orgName, orgunitName);
						logger.info("department list"+deptList);
						if(deptList.contains(department)) {
							logger.info("department name "+department);
							List<Role> roles=getRoles(orgName);
							if(roles.stream().filter(r-> r.getRoleName().equals(userRole)) != null){
								logger.info("role name "+userRole);
								List<User> userList = getUsers(orgName);
								logger.info(" list of users" +userList);
								if(userList.stream().filter(u-> u.getUsername().equals(userName)) != null){
									String userRoles= getUserRoleList(orgName, userName, "page");
									logger.info("user having role" + roles);
									if(userRoles.equals(userRole)) {
										List<Tab> tabList = getTabs(orgName);
										logger.info("list of tab" +tabList);
										if(tabList.stream().filter(t-> t.getTabName().equals(channelName)) != null){
											// If channel present for  user
											String channel_mm_id=searchTab(channelName);
											String mm_user_id=searchUserGetMMUserId(userName);
											String resp=mmServiceManager.addUser2Channel(channel_mm_id,mm_user_id);
											logger.info("assign user to particular channel \t " + channelName);

											String userId = searchUser(userName);
											ResponseUserDto response = new ResponseUserDto();
											response.setTeamId(teamId);
											response.setUserId(userId);
											response.setUserName(userName);
											String status=null;
											if (userId != null) { 
												status ="user already exists";
											}else { 
												status ="user creation failed";
											}
											response.setStatus(status);
											respUserDto.add(response);

										}else {
											//creating new channel and assign user

											TabDto tabDto = new TabDto();
											tabDto.setChannelName(channelName);
											tabDto.setHeader(channelPurpose);
											tabDto.setPurpose(channelPurpose);
											tabDto.setOrganizationUnitName(orgunitName);
											tabDto.setOrganizationName(orgName);
											tabDto.setTemplateName(templateName);
											tabDto.setDepartmentName(department);
											tabDto.setRole(userRole);
											tabDto.setAccessAcrossUnits(acessAcrossUnits);
											tabDto.setAccessAcrossDepartments(
													acessAcrossDept); 
											try {
												tabDto.setChannelDisplayName(channelDisplayName); 
											} catch (Exception exp){
												tabDto.setChannelDisplayName(channelName);
											}
											createTab(tabDto);
											logger.info(" Tab created sucessully \t" + channelName);

											String userId = searchUser(userName);
											ResponseUserDto response = new ResponseUserDto();
											response.setTeamId(teamId);
											response.setUserId(userId);
											response.setUserName(userName);
											String
											status=null; 
											if (userId != null) {
												status ="user already exists";
											} else { 
												status =" user creation failed";
											}
											response.setStatus(status);
											respUserDto.add(response);
										}
									}else {
										// assign role to user 
										addRoles4User(orgName,userName,userRole);
										logger.info("role is assign to User \t" + userRole);

										String userId = searchUser(userName);
										ResponseUserDto response = new ResponseUserDto();
										response.setTeamId(teamId);
										response.setUserId(userId);
										response.setUserName(userName);
										String status=null;
										if (userId != null) {
											status = "user already exists";
										} else {
											status = "user creation failed";
										}
										response.setStatus(status);
										respUserDto.add(response);

									}
								}else {
									// create new user
									createUser(firstName,lastName,userName,password,email,qualification,specialization,employeeId
											,aadhaarNumber,panNumber,passportNumber,phoneNumber,orgName,orgunitName,userRole,department,gender,hasOUAccessString,hasUniversalAccessString);
									logger.info(" User created sucessfully \t"+userName);

									String userId = searchUser(userName); 
									ResponseUserDto response = new ResponseUserDto();
									response.setTeamId(teamId); 
									response.setUserId(userId);
									response.setUserName(userName); 
									String status=null; 
									if(userId != null) {
										status = "user created succesfully"; 
									} else { 
										status = "user creation failed";
									}
									response.setStatus(status);
									respUserDto.add(response);
								}
							}else {
								// create new role and Tab
								createRole(userRole,roleType,orgName,hasOUAccessString,orgunitName,department); 
								logger.info(" role created sucessfully \t"+userRole);
								//creating new channel and assign user

								TabDto tabDto = new TabDto();
								tabDto.setChannelName(channelName);
								tabDto.setHeader(channelPurpose);
								tabDto.setPurpose(channelPurpose);
								tabDto.setOrganizationUnitName(orgunitName);
								tabDto.setOrganizationName(orgName);
								tabDto.setTemplateName(templateName);
								tabDto.setDepartmentName(department); 
								tabDto.setRole(userRole);
								tabDto.setAccessAcrossUnits(acessAcrossUnits);
								tabDto.setAccessAcrossDepartments(acessAcrossDept);
								try {
									tabDto.setChannelDisplayName(channelDisplayName);
								}catch (Exception exp) { 
									tabDto.setChannelDisplayName(channelName);
								}
								createTab(tabDto);
								logger.info("new Tab created sucessully \t" + channelName);
								// create new user
								createUser(firstName,lastName,userName,password,email,qualification,specialization,employeeId
										,aadhaarNumber,panNumber,passportNumber,phoneNumber,orgName,orgunitName,userRole,department,gender,hasOUAccessString,hasUniversalAccessString);
								logger.info("new User created sucessfully \t "+userName);   
								String userId = searchUser(userName); 
								ResponseUserDto response = new ResponseUserDto();
								response.setTeamId(teamId); 
								response.setUserId(userId);
								response.setUserName(userName); 
								String status=null; 
								if(userId != null) {
									status = "user created succesfully"; 
								} else { 
									status = "user creation failed";
								}
								response.setStatus(status);
								respUserDto.add(response);
							}
						}else {
							//create department
							createDepartment(department,orgunitName,orgName);
							logger.info(" department created \t "+department);
							// create new role and Tab
							createRole(userRole,roleType,orgName,hasOUAccessString,orgunitName,department); 
							logger.info("role created sucessfully \t"+userRole);
							//creating new channel and assign user

							TabDto tabDto = new TabDto(); 
							tabDto.setChannelName(channelName);
							tabDto.setHeader(channelPurpose); 
							tabDto.setPurpose(channelPurpose);
							tabDto.setOrganizationUnitName(orgunitName);
							tabDto.setOrganizationName(orgName); 
							tabDto.setTemplateName(templateName);
							tabDto.setDepartmentName(department); 
							tabDto.setRole(userRole);
							tabDto.setAccessAcrossUnits(acessAcrossUnits);
							tabDto.setAccessAcrossDepartments(acessAcrossDept); 
							try {
								tabDto.setChannelDisplayName(channelDisplayName);
							} catch (Exception exp) {
								tabDto.setChannelDisplayName(channelName);
							} 
							createTab(tabDto);
							logger.info("Tab created sucessully \t" + channelName);
							// create new user
							createUser(firstName,lastName,userName,password,email,qualification,specialization,employeeId
									,aadhaarNumber,panNumber,passportNumber,phoneNumber,orgName,orgunitName,userRole,department,gender,hasOUAccessString,hasUniversalAccessString);
							logger.info("User created sucessfully \t"+userName);
							String userId = searchUser(userName); 
							ResponseUserDto response = new ResponseUserDto();
							response.setTeamId(teamId); 
							response.setUserId(userId);
							response.setUserName(userName); 
							String status=null; 
							if(userId != null) {
								status = "user created succesfully"; 
							} else { 
								status = "user creation failed";
							}
							response.setStatus(status);
							respUserDto.add(response);
						}
					}else {
						// creating org unit
						createOrganizationUnit(orgunitName,orgName,orgUnitLocation,orgUnitLatitude,orgUnitLongitude,phoneNumber);
						logger.info("new Ou created "+orgunitName);
						// creating department
						createDepartment(department,orgunitName,orgName);
						logger.info("new department created"+department);
						// creating a role
						createRole(userRole,roleType,orgName,hasOUAccessString,orgunitName,department);
						logger.info("role created sucessfully"+userRole);
						//creating new channel and assign user

						TabDto tabDto = new TabDto(); 
						tabDto.setChannelName(channelName);
						tabDto.setHeader(channelPurpose); 
						tabDto.setPurpose(channelPurpose);
						tabDto.setOrganizationUnitName(orgunitName);
						tabDto.setOrganizationName(orgName); 
						tabDto.setTemplateName(templateName);
						tabDto.setDepartmentName(department); 
						tabDto.setRole(userRole);
						tabDto.setAccessAcrossUnits(acessAcrossUnits);
						tabDto.setAccessAcrossDepartments(acessAcrossDept); 
						try {
							tabDto.setChannelDisplayName(channelDisplayName);
						} catch (Exception exp) {
							tabDto.setChannelDisplayName(channelName);
						} 
						createTab(tabDto);
						logger.info("Tab created sucessully \t" + channelName);
						// create new user
						createUser(firstName,lastName,userName,password,email,qualification,specialization,employeeId
								,aadhaarNumber,panNumber,passportNumber,phoneNumber,orgName,orgunitName,userRole,department,gender,hasOUAccessString,hasUniversalAccessString);
						logger.info("User created sucessfully \t"+userName);
						String userId = searchUser(userName); 
						ResponseUserDto response = new ResponseUserDto();
						response.setTeamId(teamId); 
						response.setUserId(userId);
						response.setUserName(userName); 
						String status=null; 
						if(userId != null) {
							status = "user created succesfully"; 
						} else { 
							status = "user creation failed";
						}
						response.setStatus(status);
						respUserDto.add(response);
					}

				}else {
					String hisURL = "";
					try {
						hisURL = url;
					} catch (Exception exp) {
						hisURL = "";
					}
					// create new team
					createOrganization(orgName,email,phoneNumber,hisURL);
					logger.info(" team created \t"+orgName);
					createOrganizationUnit(orgunitName,orgName,orgUnitLocation,orgUnitLatitude,orgUnitLongitude,phoneNumber);
					logger.info(" Ou created \t"+orgunitName);
					createDepartment(department,orgunitName,orgName);
					logger.info(" department created \t"+department);
					// create new role and Tab
					createRole(userRole,roleType,orgName,hasOUAccessString,orgunitName,department); 
					logger.info(" role created sucessfully \t"+userRole);
					//creating new channel and assign user

					TabDto tabDto = new TabDto();
					tabDto.setChannelName(channelName);
					tabDto.setHeader(channelPurpose);
					tabDto.setPurpose(channelPurpose);
					tabDto.setOrganizationUnitName(orgunitName);
					tabDto.setOrganizationName(orgName); 
					tabDto.setTemplateName(templateName);
					tabDto.setDepartmentName(department);
					tabDto.setRole(userRole);
					tabDto.setAccessAcrossUnits(acessAcrossUnits);
					tabDto.setAccessAcrossDepartments(acessAcrossDept);
					try {
						tabDto.setChannelDisplayName(channelDisplayName);
					} catch (Exception exp) {
						tabDto.setChannelDisplayName(channelName); 
					} 
					createTab(tabDto);

					logger.info(" Tab created sucessully \t" + channelName);

					// create new user
					createUser(firstName,lastName,userName,password,email,qualification,specialization,employeeId
							,aadhaarNumber,panNumber,passportNumber,phoneNumber,orgName,orgunitName,userRole,department,gender,hasOUAccessString,hasUniversalAccessString);
					logger.info(" User created sucessfully \t"+userName);
					String respTeamId=searchOrganization(orgName);
					String userId = searchUser(userName); 
					ResponseUserDto response = new ResponseUserDto();
					response.setTeamId(respTeamId);
					response.setUserId(userId);
					response.setUserName(userName); 
					String status=null; 
					if(userId != null) {
						status = "user created succesfully"; 
					} else { 
						status = "user creation failed";
					}
					response.setStatus(status);
					respUserDto.add(response);
				}
			}));

		}catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		}
		catch(NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		}
		catch(Exception e) {
			logger.error("Error Occurred!", e);
		}
		finally {
			if (connectionContext != null) {
				try {   
					connectionContext.close();
				}
				catch (NamingException ne) { 
					logger.error("Error Occurred while closing the connection!", ne); 
				}
			}
		}
		return respUserDto;
	}

	public String searchUserGetMMUserId(String userName) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		String description = "";
		String user = "";
		String mmUserId = "";

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"cn", "description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(domainDN, USER_SEARCH_FILTER, controls);
			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				user = attr.get("cn").get(0).toString();
				description = attr.get("description").get(0).toString();
				if (user.equals(userName)) {
					User userData = gson.fromJson(description, User.class);
					mmUserId = userData.getUserId_MM();

				}
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
            if (connectionContext != null) {
                try {
                    connectionContext.close();
                }
                catch (NamingException ne) {
                    logger.error("Error Occurred while closing the connection!", ne);
                }
            }
        }*/
		return mmUserId;
	}


	private String digestPassword(String algorithm, String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String encryptedPassword = null;
		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(password.getBytes());
		encryptedPassword = Base64.getEncoder().encodeToString(md.digest());
		encryptedPassword = "{" + algorithm + "}" + encryptedPassword;
		System.out.println(encryptedPassword);
		return encryptedPassword;
	}

	public String getGidNumber4User(String userRole, String orgName) throws Exception {
		//logger.info("userRole: " +userRole);
		//logger.info("orgName: " +orgName);
		String gidNumber = "";
		ArrayList<Role> roleList = getRoles4Broadcast(orgName);
		if (roleList.size() > 0) {
			for (Role role : roleList) {
				try {
					//logger.info("role(insideBroadcast): " +role.getRoleName());
					if (role.getRoleName().equalsIgnoreCase(userRole)) {
						gidNumber = role.getGidNumber();
						//logger.info("gidNumber1: " +gidNumber);
					}
				} catch (Exception exp) {
					//logger.info("gidNumber2: " +gidNumber);
					logger.debug("Exception: {} " + exp);
				}
			}
		}
		//logger.info("gidNumber2: " +gidNumber);
		return gidNumber;
	}

	public String getUidNumber4User(String orgName) throws Exception {
		String uidNumber = "";
		ArrayList<User> userList = getUsers(orgName);
		ArrayList<Integer> uidNumberList = new ArrayList<Integer>();
		if (userList.size() > 0) {
			for (User user : userList) {
				try {
					uidNumberList.add(Integer.parseInt(user.getUserId()));
				} catch (Exception exp) {
					logger.debug("Exception: {} " + exp);
				}
				if (uidNumberList.size() > 0) {
					Integer maxValue = Collections.max(uidNumberList);
					maxValue += 1;
					uidNumber = String.valueOf(maxValue);
				}
			}
		} else {
			uidNumber = String.valueOf(USER_BASE_VALUE + 1);
		}
		logger.debug("uidNumber: {} " + uidNumber);
		return uidNumber;
	}

	public ArrayList<User> getUsers(String orgName) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<User> userList = new ArrayList<User>();

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(organizationUnitPrefix + USERS + comma + organizationPrefix + orgName + comma + domainDN, USER_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				String userDefinition = attr.get("description").get(0).toString();
				try {
					User user = gson.fromJson(userDefinition, User.class);

					userList.add(user);
				} catch (Exception exp) {
					logger.debug("Exception: {} " + exp);
				}
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		logger.debug("User List size {} " + userList.size());
		return userList;
	}


	public void createTabtemplate(String... parameters) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			Attributes attributes = new BasicAttributes();
			Attribute objectClass = new BasicAttribute("objectClass");
			objectClass.add("posixGroup");
			objectClass.add("top");
			attributes.put(objectClass);

			String tabTemplateName = parameters[0]; //Tab template Name
			String orgName = parameters[1]; //Organization Name

			String gidNumber = "";
			try {
				gidNumber = getGidNumber4TabTemplate(orgName);
			} catch (Exception exp) {
				logger.debug("Error: {}" + exp);
			}

			String description = "";

			TabTemplate tabTemplate = new TabTemplate();
			tabTemplate.setTemplateName(tabTemplateName);
			tabTemplate.setGidNumber(gidNumber);

			description = gson.toJson(tabTemplate);

			attributes.put(new BasicAttribute("cn", tabTemplateName));
			attributes.put(new BasicAttribute("gidNumber", gidNumber));
			attributes.put(new BasicAttribute("description", description));

			logger.info(commonNamePrefix + tabTemplateName + comma + organizationUnitPrefix + TABTEMPLATE + comma + organizationPrefix + orgName + comma + domainDN);

			connectionContext.createSubcontext(commonNamePrefix + tabTemplateName + comma + organizationUnitPrefix + TABTEMPLATE + comma + organizationPrefix + orgName + comma + domainDN, attributes);
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/
	}


	public String getGidNumber4TabTemplate(String orgName) throws Exception {
		String gidNumber = "";
		ArrayList<TabTemplate> tabTemplateList = getTabTemplates(orgName);
		ArrayList<Integer> gidNumberList = new ArrayList<Integer>();
		if (tabTemplateList.size() > 0) {
			for (TabTemplate tabTemplate : tabTemplateList) {
				try {
					gidNumberList.add(Integer.parseInt(tabTemplate.getGidNumber()));
				} catch (Exception exp) {
					logger.debug("Exception: {} " + exp);
				}
				if (gidNumberList.size() > 0) {
					Integer maxValue = Collections.max(gidNumberList);
					maxValue += 1;
					gidNumber = String.valueOf(maxValue);
				}
			}
		} else {
			gidNumber = String.valueOf(TAB_TEMPLATE_BASE_VALUE + 1);
		}
		logger.debug("gidNumber: {} " + gidNumber);
		return gidNumber;
	}

	public ArrayList<TabTemplate> getTabTemplates(String orgName) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<TabTemplate> templateList = new ArrayList<TabTemplate>();

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			logger.debug(organizationUnitPrefix + TABTEMPLATE + comma + organizationPrefix + orgName + comma + domainDN);
			NamingEnumeration searchResults = connectionContext.search(organizationUnitPrefix + TABTEMPLATE + comma + organizationPrefix + orgName + comma + domainDN, TAB_TEMPLATE_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				String templateDefinition = attr.get("description").get(0).toString();
				TabTemplate tabtemplate = gson.fromJson(templateDefinition, TabTemplate.class);
				templateList.add(tabtemplate);
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		logger.debug("TabTemplate List size {} " + templateList.size());
		return templateList;
	}

	//TODO: Complete the logic
	public void createTab(String... parameters) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			Attributes attributes = new BasicAttributes();
			Attribute objectClass = new BasicAttribute("objectClass");
			objectClass.add("inetOrgPerson");
			objectClass.add("posixAccount");
			objectClass.add("top");
			attributes.put(objectClass);


			String tab_name = parameters[0];
			String tab_header = parameters[1];
			String tab_purpose = parameters[2];
			String tab_template = parameters[3];
			String tab_org = parameters[4];
			String tab_access_ou = parameters[5];
			String tab_org_unit = parameters[6];
			String tab_department = parameters[7];
			String tab_role = parameters[8];
			String tab_access_dep = parameters[9];
			String displayName = "";

			String gidNumberRole = "";
			String gidNumberTemplate = "";

			try {
				displayName = parameters[10];
			} catch (Exception exp) {
				displayName = tab_name;
			}

			/* try {
                gidNumberRole = getGidNumber4Tab(tab_role, orgName);
            }catch(Exception exp) {
                logger.debug("Error: {}"+ exp);
            }

            try {
                gidNumberTemplate = getGidNumber4Template(tab_template, orgName);
            }catch(Exception exp) {
                logger.debug("Error: {}"+ exp);
            }*/

			String description = "";

			boolean hasOUAccess = false;
			boolean hasDEPAccess = false;

			if (tab_access_ou.equals("ou_specific_no")) {
				hasOUAccess = true;
			}

			if (tab_access_dep.equals("dep_specific_no")) {
				hasDEPAccess = true;
			}

			String tabId = getUidNumber4Tab(tab_org);

			ArrayList<String> roleList = new ArrayList<String>();
			roleList.add(tab_role);

			String teamID = searchOrganization(tab_org);
			logger.info("Team ID: " + teamID);
			//Create a Team in Mattermost
			String mm_channel_id = mmServiceManager.createChannel(teamID, tab_name.toLowerCase(), displayName, tab_purpose, tab_header, "P");
			logger.info("Created Channel ID: " + mm_channel_id);

			if (mm_channel_id.equalsIgnoreCase("")) {

			}

			ArrayList<Tab> tabList = new ArrayList<Tab>();

			Tab tab = new Tab();
			tab.setTabId(tabId);
			tab.setTabName(tab_name);
			tab.setTabHeader(tab_header);
			tab.setTabPurpose(tab_purpose);
			tab.setTemplateName(tab_template);
			tab.setOrganizationName(tab_org);
			tab.setOrganizationUnitName(tab_org_unit);
			tab.setDepartmentName(tab_department);
			tab.setRoleList(roleList);
			tab.setAccessAcrossUnits(hasOUAccess);
			tab.setAccessAcrossDepartments(hasDEPAccess);
			tab.setChannelId_MM(mm_channel_id);
			tab.setTabDisplayName(displayName);

			description = gson.toJson(tab);

			tabList.add(tab);

			attributes.put(new BasicAttribute("cn", tab_name)); //Common name
			attributes.put(new BasicAttribute("sn", tab_header)); //Last name
			attributes.put(new BasicAttribute("gidNumber", getGidNumber4User(tab_role, tab_org))); //Role Id
			attributes.put(new BasicAttribute("uid", tab_name)); //User Name
			attributes.put(new BasicAttribute("uidNumber", tabId)); //User Id
			attributes.put(new BasicAttribute("homeDirectory", "/home/users/" + tab_name)); //common name
			attributes.put(new BasicAttribute("description", description)); //All Info

			logger.info(commonNamePrefix + tab_name + comma + organizationUnitPrefix + TABS + comma + organizationPrefix + tab_org + comma + domainDN);

			connectionContext.createSubcontext(commonNamePrefix + tab_name + comma + organizationUnitPrefix + TABS + comma + organizationPrefix + tab_org + comma + domainDN, attributes);

			addUsers2Channel(tab_org, tab_role, tabList);
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/
	}

	//creating tab for post request

	public void createTab(TabDto tabDto) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;
		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			Attributes attributes = new BasicAttributes();
			Attribute objectClass = new BasicAttribute("objectClass");
			objectClass.add("inetOrgPerson");
			objectClass.add("posixAccount");
			objectClass.add("top");
			attributes.put(objectClass);

			String description = "";
			String orgName="";
			String displayName = "";
			boolean hasOUAccess = false;
			boolean hasDEPAccess = false;
			String templateName = tabDto.getTemplateName();

			if (templateName == "" && templateName.isEmpty()) {
				templateName = "Chat";
			}

			try {
				displayName = tabDto.getChannelDisplayName();
			} catch (Exception exp) {
				displayName = tabDto.getChannelName();
			}

			logger.info("templateName : " + templateName);
			if(tabDto.getOrganizationName()!=null &&  !tabDto.getOrganizationName().isEmpty()) {
				orgName=tabDto.getOrganizationName();
			}else {
				orgName = serachOranizationUint(tabDto.getOrganizationUnitName());
			}
			logger.info("orgName : " + orgName);

			String teamID = searchOrganization(orgName);
			logger.info("Team ID: " + teamID);

			String tabId = getUidNumber4Tab(orgName);

			ArrayList<String> roleList = new ArrayList<String>();
			roleList.add(tabDto.getRole());
			//Create a Team in Mattermost
			String mm_channel_id = mmServiceManager.createChannel(teamID, tabDto.getChannelName().toLowerCase(), displayName, tabDto.getPurpose(), tabDto.getHeader(), "P");
			logger.info("Created Channel ID: " + mm_channel_id);


			ArrayList<Tab> tabList = new ArrayList<Tab>();
			if (mm_channel_id != null && !mm_channel_id.isEmpty()) {
				Tab tab = new Tab();
				tab.setTabId(tabId);
				tab.setTabName(tabDto.getChannelName());
				tab.setTabHeader(tabDto.getHeader());
				tab.setTabPurpose(tabDto.getPurpose());
				tab.setTemplateName(templateName);
				tab.setOrganizationName(orgName);
				tab.setOrganizationUnitName(tabDto.getOrganizationUnitName());
				tab.setDepartmentName(tabDto.getDepartmentName());
				tab.setRoleList(roleList);
				tab.setAccessAcrossUnits(hasOUAccess);
				tab.setAccessAcrossDepartments(hasDEPAccess);
				tab.setChannelId_MM(mm_channel_id);
				tab.setTabDisplayName(displayName);

				description = gson.toJson(tab);

				tabList.add(tab);

				attributes.put(new BasicAttribute("cn", tabDto.getChannelName())); //Common name
				attributes.put(new BasicAttribute("sn", tabDto.getHeader())); //Last name
				attributes.put(new BasicAttribute("gidNumber", getGidNumber4User(tabDto.getRole(), orgName))); //Role Id
				attributes.put(new BasicAttribute("uid", tabDto.getChannelName())); //User Name
				attributes.put(new BasicAttribute("uidNumber", tabId)); //User Id
				attributes.put(new BasicAttribute("homeDirectory", "/home/users/" + tabDto.getChannelName())); //common name
				attributes.put(new BasicAttribute("description", description)); //All Info

				logger.info(commonNamePrefix + tabDto.getChannelName() + comma + organizationUnitPrefix + TABS + comma + organizationPrefix + orgName + comma + domainDN);

				connectionContext.createSubcontext(commonNamePrefix + tabDto.getChannelName() + comma + organizationUnitPrefix + TABS + comma + organizationPrefix + orgName + comma + domainDN, attributes);
			}
			addUsers2Channel(orgName, tabDto.getRole(), tabList);
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

	}


	public String getUidNumber4Tab(String orgName) throws Exception {
		String uidNumber = "";
		ArrayList<Tab> tabList = getTabs(orgName);
		ArrayList<Integer> uidNumberList = new ArrayList<Integer>();
		if (tabList.size() > 0) {
			for (Tab tab : tabList) {
				try {
					uidNumberList.add(Integer.parseInt(tab.getTabId()));
				} catch (Exception exp) {
					logger.debug("Exception: {} " + exp);
				}
				if (uidNumberList.size() > 0) {
					Integer maxValue = Collections.max(uidNumberList);
					maxValue += 1;
					uidNumber = String.valueOf(maxValue);
				}
			}
		} else {
			uidNumber = String.valueOf(TAB_BASE_VALUE + 1);
		}
		logger.debug("uidNumber: {} " + uidNumber);
		return uidNumber;
	}


	public ArrayList<Tab> getTabs(String orgName) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<Tab> tabList = new ArrayList<Tab>();

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(organizationUnitPrefix + TABS + comma + organizationPrefix + orgName + comma + domainDN, TAB_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				String tabDefinition = attr.get("description").get(0).toString();
				try {
					Tab tab = gson.fromJson(tabDefinition, Tab.class);
					tabList.add(tab);
				} catch (Exception exp) {
					logger.debug("Exception: {} " + exp);
				}
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		logger.debug("Tab List size {} " + tabList.size());
		return tabList;
	}

	public String searchUsers(String orgName, String searchTerm, String listSize) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<User> userList = new ArrayList<User>();
		ArrayList<String> userNameList = new ArrayList<String>();

		int listSiz = 5;

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);

			String CUSTOM_SEARCH_FILTER = String.format(USER_SUB_SEARCH_FILTER, searchTerm, searchTerm, searchTerm);
			NamingEnumeration searchResults = connectionContext.search(organizationUnitPrefix + USERS + comma + organizationPrefix + orgName + comma + domainDN, CUSTOM_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				String userDefinition = attr.get("description").get(0).toString();
				try {
					User user = gson.fromJson(userDefinition, User.class);
					userList.add(user);
				} catch (Exception exp) {
					logger.debug("Exception: {} " + exp);
				}
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/
		logger.debug("User List size {} " + userList.size());

		try {
			listSiz = Integer.parseInt(listSize);
		} catch (Exception exp) {
			listSiz = 5;
		}

		if (userList.size() > 0) {
			int cnt = 0;
			for (User user : userList) {
				cnt++;
				userNameList.add(user.getUsername());
				if (cnt >= listSiz) {
					break;
				}
			}
		}
		String userNameListString = StringUtils.join(userNameList, "###");
		return userNameListString;
	}

	public String getUserRoleList(String orgName, String userName, String diaplayFlag) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<User> userList = new ArrayList<User>();
		String roleString = "";

		int listSiz = 5;

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);

			String CUSTOM_SEARCH_FILTER = String.format(SELECTIVE_USER_SEARCH_FILTER, userName);
			NamingEnumeration searchResults = connectionContext.search(organizationUnitPrefix + USERS + comma + organizationPrefix + orgName + comma + domainDN, CUSTOM_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				String userDefinition = attr.get("description").get(0).toString();
				try {
					User user = gson.fromJson(userDefinition, User.class);
					userList.add(user);
				} catch (Exception exp) {
					logger.debug("Exception: {} " + exp);
				}
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/
		logger.debug("User List size {} " + userList.size());

		if (userList.size() > 0) {
			int cnt = 0;
			for (User user : userList) {
				roleString = user.getRoles();

			}
		}

		return roleString;
	}

	public String addRoles4User(String orgName, String userName, String roles) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<User> userList = new ArrayList<User>();
		String result = "";
		String userId_MM = "";

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);

			String CUSTOM_SEARCH_FILTER = String.format(SELECTIVE_USER_SEARCH_FILTER, userName);
			NamingEnumeration searchResults = connectionContext.search(organizationUnitPrefix + USERS + comma + organizationPrefix + orgName + comma + domainDN, CUSTOM_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				String userDefinition = attr.get("description").get(0).toString();
				try {
					User user = gson.fromJson(userDefinition, User.class);
					userId_MM = user.getUserId_MM();
					userList.add(user);
				} catch (Exception exp) {
					logger.debug("Exception: {} " + exp);
				}
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/
		logger.debug("User List size {} " + userList.size());

		String[] rolesArray = roles.split(",");

		if (userList.size() > 0) {
			int cnt = 0;
			for (User user : userList) {
				ArrayList<String> roleList = user.getRoleList();
				for (String role : rolesArray) {
					roleList.add(role);
				}
				user.setRoleList(roleList);
				modifyUser(orgName, userName, user);
			}
		}

		addUsers2Channel(orgName, rolesArray, userId_MM);

		return result;
	}

	public String removeRoles4User(String orgName, String userName, String roles) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<User> userList = new ArrayList<User>();
		String result = "";
		String userId_MM = "";

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);

			String CUSTOM_SEARCH_FILTER = String.format(SELECTIVE_USER_SEARCH_FILTER, userName);
			NamingEnumeration searchResults = connectionContext.search(organizationUnitPrefix + USERS + comma + organizationPrefix + orgName + comma + domainDN, CUSTOM_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				String userDefinition = attr.get("description").get(0).toString();
				try {
					User user = gson.fromJson(userDefinition, User.class);
					userId_MM = user.getUserId_MM();
					userList.add(user);
				} catch (Exception exp) {
					logger.debug("Exception: {} " + exp);
				}
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/
		logger.debug("User List size {} " + userList.size());

		String[] rolesArray = roles.split(",");

		if (userList.size() > 0) {
			int cnt = 0;
			for (User user : userList) {
				ArrayList<String> roleList = user.getRoleList();
				for (String role : rolesArray) {
					roleList.remove(role);
				}
				user.setRoleList(roleList);
				modifyUser(orgName, userName, user);
			}
		}

		removeUsersFromChannel(orgName, rolesArray, userId_MM);

		return result;
	}


	public void modifyUser(String orgName, String userName, User userObj) {

		DirContext connectionContext = null;
		String result = "";

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			ModificationItem[] modificationItems = new ModificationItem[1];

			Attribute description = new BasicAttribute("description", gson.toJson(userObj));

			modificationItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, description);

			logger.debug(commonNamePrefix + userName + comma + organizationUnitPrefix + USERS + comma + organizationPrefix + orgName + comma + domainDN);

			connectionContext.modifyAttributes(commonNamePrefix + userName + comma + organizationUnitPrefix + USERS + comma + organizationPrefix + orgName + comma + domainDN, modificationItems);
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

	}

	//Adding the loginToken for the user
	public void modifyUser(String orgName, String userName, String userToken) {

		DirContext connectionContext = null;
		String result = "";

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);


			String distinguishedName = commonNamePrefix + userName + comma + organizationUnitPrefix + USERS + comma + organizationPrefix + orgName + comma + domainDN;

			logger.info("LDAP Context {} " + distinguishedName);
			//Read and update the existing data
			Attributes attrs = connectionContext.getAttributes(distinguishedName, new String[]{"description"});
			String userDefinition = attrs.get("description").get().toString();
			logger.info("Description {} " + userDefinition);

			User userObj = gson.fromJson(userDefinition, User.class);
			userObj.setUserAccessToken(userToken);

			//Update the modified data into LDAP
			ModificationItem[] modificationItems = new ModificationItem[1];

			Attribute description = new BasicAttribute("description", gson.toJson(userObj));

			modificationItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, description);

			logger.debug(distinguishedName);

			connectionContext.modifyAttributes(distinguishedName, modificationItems);
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

	}


	public String deleteUser(String orgName, String userName) {

		DirContext connectionContext = null;
		String result = "User Deleted successfully.";

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String distinguishedName = commonNamePrefix + userName + comma + organizationUnitPrefix + USERS + comma + organizationPrefix + orgName + comma + domainDN;
			logger.debug(distinguishedName);

			Attributes attrs = connectionContext.getAttributes(distinguishedName, new String[]{"description"});
			String userDefinition = attrs.get("description").get().toString();
			logger.info("Description {} " + userDefinition);

			User user = gson.fromJson(userDefinition, User.class);
			String mmUserId = user.getUserId_MM();

			//Delete the Mattermost User - Deactivating
			mmServiceManager.deleteUser(mmUserId);


			//Set the user Inactive
			user.setActiveStatus(false);
			user.setDateOfDeactivation(String.valueOf(new Date().getTime()));

			ModificationItem[] modificationItems = new ModificationItem[1];

			Attribute descriptionAttr = new BasicAttribute("description", gson.toJson(user));

			modificationItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, descriptionAttr);

			connectionContext.modifyAttributes(distinguishedName, modificationItems);

			//Delete the LDAP Entry
			//connectionContext.unbind(distinguishedName);

			/*Object obj = null;
            try {
                obj = connectionContext.lookup(distinguishedName);
            } catch (NameNotFoundException ne) {
                logger.debug("User Deletion Successful");
            }*/
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		return result;

	}

	public String deleteUser(String userName) {

		DirContext connectionContext = null;
		String result = "User Deleted successfully.";

		try {
			String userDefinition = "";

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);

			String CUSTOM_SEARCH_FILTER = String.format(SELECTIVE_USER_SEARCH_FILTER, userName);
			NamingEnumeration searchResults = connectionContext.search(domainDN, CUSTOM_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				userDefinition = attr.get("description").get(0).toString();
			}

			logger.debug(userDefinition);

			User user = gson.fromJson(userDefinition, User.class);

			String distinguishedName = commonNamePrefix + userName + comma + organizationUnitPrefix + USERS + comma + organizationPrefix + user.getOrganization() + comma + domainDN;
			logger.debug(distinguishedName);

			String mmUserId = user.getUserId_MM();

			logger.debug(mmUserId);
			//logger.debug("");

			//Delete the Mattermost User - Deactivating
			mmServiceManager.deleteUser(mmUserId);

			//Delete the LDAP Entry
			connectionContext.unbind(distinguishedName);

			Object obj = null;
			try {
				obj = connectionContext.lookup(distinguishedName);
				logger.debug("User Still Present");
			} catch (NameNotFoundException ne) {
				logger.debug("User Deletion Successful");
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "Invalid User / User does not exist";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "Invalid User / User does not exist";
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		return result;

	}

	public ArrayList<Tab> getAssignedTabs(String orgName, String orgUnit, String includeDepFlag, String depName, String roleName) throws Exception {

		ArrayList<Tab> tabList = getTabs(orgName);
		ArrayList<Tab> validList = new ArrayList<Tab>();

		for (Tab tab : tabList) {
			String ou = tab.getOrganizationUnitName();
			String dep = "";
			if (includeDepFlag.equals("true")) {
				dep = tab.getDepartmentName();
			} else {
				dep = "";
			}

			ArrayList<String> roleList = tab.getRoleList();

			logger.debug("orgName: " + orgName + " orgUnit: " + orgUnit + " includeDepFlag: " + includeDepFlag + " depName: " + depName + " roleName: " + roleName);
			logger.debug("-------------------------------------------");
			logger.debug("includeDepFlag: " + includeDepFlag);
			logger.debug("orgName: " + orgName);
			logger.debug("ou: " + ou);
			logger.debug("dep: " + dep);
			logger.debug("roleList: " + roleList);

			if (ou.equals(orgUnit)) {
				if (!dep.equals("")) {
					if (dep.equals(depName)) {
						if (roleList.contains(roleName)) {
							validList.add(tab);
						}
					}
				} else {
					if (roleList.contains(roleName)) {
						validList.add(tab);
					}
				}
			} else {
				if (tab.hasAccessAcrossUnits()) {
					if (roleList.contains(roleName)) {
						validList.add(tab);
					}
				}
			}
		}

		return validList;
	}

	public ArrayList<Tab> getAvailableTabs(String orgName, String orgUnit, String includeDepFlag, String depName, String roleName, String has_all_ou_access, String has_all_dep_access) throws Exception {

		ArrayList<Tab> tabList = getTabs(orgName);
		ArrayList<Tab> validList = new ArrayList<Tab>();

		for (Tab tab : tabList) {
			String ou = tab.getOrganizationUnitName();
			String dep = "";
			if (includeDepFlag.equals("true")) {
				dep = tab.getDepartmentName();
			} else {
				dep = "";
			}

			ArrayList<String> roleList = tab.getRoleList();

			/*logger.debug("orgName: "+orgName+" orgUnit: "+orgUnit+" includeDepFlag: "+includeDepFlag+" depName: "+depName+" roleName: "+roleName);
            logger.debug("-------------------------------------------");
            logger.debug("includeDepFlag: "+includeDepFlag);
            logger.debug("orgName: "+orgName);
            logger.debug("ou: "+ou);
            logger.debug("dep: "+dep);
            logger.debug("roleList: "+roleList);*/

			if (ou.equals(orgUnit)) {
				if (!dep.equals("")) {
					if (dep.equals(depName)) {
						if (!roleList.contains(roleName)) {
							if (has_all_ou_access.equals("ou_specific_no")) {
								if (tab.hasAccessAcrossUnits()) {
									validList.add(tab);
								}
							} else {
								if (!tab.hasAccessAcrossUnits()) {
									if (has_all_dep_access.equals("dep_specific_no")) {
										if (tab.hasAccessAcrossDepartments()) {
											validList.add(tab);
										}
									} else {
										if (!tab.hasAccessAcrossDepartments()) {
											validList.add(tab);
										}
									}
								}
							}

						}
					} else {
						if (has_all_ou_access.equals("ou_specific_no")) {
							if (tab.hasAccessAcrossUnits()) {
								validList.add(tab);
							}
						}
					}
				} else {
					if (!roleList.contains(roleName)) {
						if (has_all_ou_access.equals("ou_specific_no")) {
							if (tab.hasAccessAcrossUnits()) {
								validList.add(tab);
							}
						} else {
							if (!tab.hasAccessAcrossUnits()) {
								validList.add(tab);
							}
						}

					}
				}
			}
		}

		return validList;
	}

	//get availbleTabsBasedOnOU

	public ArrayList<String> getAvailableTabsBasedOnOU(String orgName, String orgUnit, String has_all_ou_access, String tabType) throws Exception {

		ArrayList<Tab> tabList = getTabs(orgName);
		ArrayList<String> validList = new ArrayList<String>();

		for (Tab tab : tabList) {
			String ou = tab.getOrganizationUnitName();

			if (ou.equals(orgUnit)) {
				if (has_all_ou_access.equals("ou_specific_no")) {
					if (tab.getTemplateName().equalsIgnoreCase(tabType)) {
						String newsTabName = tab.getTabName();
						validList.add(newsTabName);
					}
				}
			}
		}
		return validList;
	}


	public String addRoles4Tab(String orgName, String roleName, String tabs) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<Tab> tabList = new ArrayList<Tab>();
		ArrayList<String> tabInputList = new ArrayList<String>(Arrays.asList(tabs.split(",")));

		String result = "";

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);

			for (String tabName : tabInputList) {

				String CUSTOM_SEARCH_FILTER = String.format(SELECTIVE_TAB_SEARCH_FILTER, tabName);
				NamingEnumeration searchResults = connectionContext.search(organizationUnitPrefix + TABS + comma + organizationPrefix + orgName + comma + domainDN, CUSTOM_SEARCH_FILTER, controls);

				SearchResult searchResult = null;

				while (searchResults.hasMore()) {
					searchResult = (SearchResult) searchResults.next();
					Attributes attr = searchResult.getAttributes();
					String tabDefinition = attr.get("description").get(0).toString();
					try {
						Tab tab = gson.fromJson(tabDefinition, Tab.class);

						ArrayList<String> roleList = tab.getRoleList();
						roleList.add(roleName);
						tab.setRoleList(roleList);
						modifyTab(orgName, tabName, tab);

						tabList.add(tab);
					} catch (Exception exp) {
						logger.debug("Exception: {} " + exp);
					}
				}

			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/
		logger.debug("Tab List size {} " + tabList.size());

		addUsers2Channel(orgName, roleName, tabList);

		return result;
	}

	public String removeRoles4Tab(String orgName, String roleName, String tabs) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<Tab> tabList = new ArrayList<Tab>();
		ArrayList<String> tabInputList = new ArrayList<String>(Arrays.asList(tabs.split(",")));
		String result = "";

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);

			for (String tabName : tabInputList) {

				String CUSTOM_SEARCH_FILTER = String.format(SELECTIVE_TAB_SEARCH_FILTER, tabName);
				NamingEnumeration searchResults = connectionContext.search(organizationUnitPrefix + TABS + comma + organizationPrefix + orgName + comma + domainDN, CUSTOM_SEARCH_FILTER, controls);

				SearchResult searchResult = null;

				while (searchResults.hasMore()) {
					searchResult = (SearchResult) searchResults.next();
					Attributes attr = searchResult.getAttributes();
					String tabDefinition = attr.get("description").get(0).toString();
					try {
						Tab tab = gson.fromJson(tabDefinition, Tab.class);

						ArrayList<String> roleList = tab.getRoleList();
						roleList.remove(roleName);
						tab.setRoleList(roleList);
						modifyTab(orgName, tabName, tab);

						tabList.add(tab);
					} catch (Exception exp) {
						logger.debug("Exception: {} " + exp);
					}
				}
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/
		logger.debug("Tab List size {} " + tabList.size());

		removeUsersFromChannel(orgName, roleName, tabList);

		return result;
	}


	public void modifyTab(String orgName, String tabName, Tab tabObj) {

		DirContext connectionContext = null;
		String result = "";

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			ModificationItem[] modificationItems = new ModificationItem[1];

			Attribute description = new BasicAttribute("description", gson.toJson(tabObj));

			modificationItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, description);

			logger.debug(commonNamePrefix + tabName + comma + organizationUnitPrefix + TABS + comma + organizationPrefix + orgName + comma + domainDN);

			connectionContext.modifyAttributes(commonNamePrefix + tabName + comma + organizationUnitPrefix + TABS + comma + organizationPrefix + orgName + comma + domainDN, modificationItems);
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

	}


	public String deleteTab(String orgName, String tabName) {

		DirContext connectionContext = null;
		String result = "Tab Deleted successfully.";

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String distinguishedName = commonNamePrefix + tabName + comma + organizationUnitPrefix + TABS + comma + organizationPrefix + orgName + comma + domainDN;
			logger.debug(distinguishedName);

			Attributes attrs = connectionContext.getAttributes(distinguishedName, new String[]{"description"});
			String tabDefinition = attrs.get("description").get().toString();
			logger.info("Description {} " + tabDefinition);

			Tab tab = gson.fromJson(tabDefinition, Tab.class);
			String mmChannelId = tab.getChannelId_MM();

			//Delete the Mattermost Channel
			mmServiceManager.deleteChannel(mmChannelId);

			//Delete the LDAP Entry
			connectionContext.unbind(distinguishedName);

			Object obj = null;
			try {
				obj = connectionContext.lookup(distinguishedName);
				logger.debug("Tab Deletion Failed");
			} catch (NameNotFoundException ne) {
				logger.debug("Tab Deletion Successful");
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		return result;

	}


	public void createAdminUser4Org(DirContext connectionContext, String commonName, String passwordString, String orgName, String destinationIndicator, String description) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		//DirContext connectionContext = null;

		try {

			Attributes attributes = new BasicAttributes();
			Attribute objectClass = new BasicAttribute("objectClass");
			objectClass.add("simpleSecurityObject");
			objectClass.add("organizationalRole");
			attributes.put(objectClass);

			attributes.put(new BasicAttribute("cn", commonName));
			attributes.put(new BasicAttribute("ou", orgName));
			attributes.put(new BasicAttribute("destinationIndicator", destinationIndicator));
			attributes.put(new BasicAttribute("description", description));
			attributes.put(new BasicAttribute("userPassword", digestPassword("MD5", passwordString)));
			logger.info(commonNamePrefix + commonName + comma + organizationPrefix + orgName + comma + domainDN);

			connectionContext.createSubcontext(commonNamePrefix + commonName + comma + organizationPrefix + orgName + comma + domainDN, attributes);
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
	}


	public String authenticateAdminUser(String adminUsername, String adminPassword) {

		String orgName = "";
		String productionFlag = "";
		String userBindDN = "";

		ArrayList<TopLevelUser> userList = null;
		TopLevelUser adminUserObj = null;

		try {
			userList = getTopLevelUsers();
		} catch (Exception exp) {
			userList = new ArrayList<TopLevelUser>();
		}

		for (TopLevelUser tlu : userList) {
			//logger.debug("Inside the loop");
			if (tlu.getUsername().equals(adminUsername)) {
				adminUserObj = tlu;
			}
		}

		if (adminUserObj != null) {
			try {
				LdapName ldapDN = new LdapName(adminUserObj.getUserId());

				for (Rdn rdn : ldapDN.getRdns()) {
					if (rdn.getType().equalsIgnoreCase("o")) {
						orgName = rdn.getValue().toString();
						break;
					}
				}
			} catch (Exception exp) {
				logger.debug("Exception occured while parsing the DN");
			}
		}

		if (orgName.equals("")) {
			userBindDN = commonNamePrefix + adminUsername + comma + domainDN;
		} else {
			userBindDN = commonNamePrefix + adminUsername + comma + organizationPrefix + orgName + comma + domainDN;
		}

		logger.debug("userBindDN: " + userBindDN);

		Properties conProperties = new Properties();
		conProperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		conProperties.put(Context.PROVIDER_URL, serverURL);
		//parms.put(Context.SECURITY_PROTOCOL, "ssl");
		conProperties.put(Context.SECURITY_AUTHENTICATION, "simple");
		conProperties.put(Context.SECURITY_PRINCIPAL, userBindDN);
		conProperties.put(Context.SECURITY_CREDENTIALS, adminPassword);

		DirContext conContext = null;

		String roleIndicator = "";

		try {
			conContext = new InitialDirContext(conProperties);
			logger.debug("Admin User Authentication Successful!");
			//logger.debug(ctx.getEnvironment());
			Attributes userAttrs = conContext.getAttributes(userBindDN);

			try {
				for (NamingEnumeration ae = userAttrs.getAll(); ae.hasMore(); ) {
					Attribute attr = (Attribute) ae.next();
					String attrID = attr.getID();
					//logger.debug("\n" + attrID);
					for (NamingEnumeration e = attr.getAll(); e.hasMore(); ) {
						String attrVal = String.valueOf(e.next());
						if (attrID.equals("destinationIndicator")) {
							//logger.debug(" : " + attrVal);
							roleIndicator = attrVal;
						}
						if (attrID.equals("description")) {
							productionFlag = attrVal;
						}
					}
				}
			} catch (NamingException e) {
				e.printStackTrace();
			}

		} catch (AuthenticationNotSupportedException ex) {
			logger.error("User Authentication Failed!");
			//ex.printStackTrace(System.err);
			roleIndicator = "";
		} catch (AuthenticationException aex) {
			logger.error("Invalid credentials");
			//aex.printStackTrace(System.err);
			roleIndicator = "";
		} catch (NamingException ne) {
			logger.error("User Authentication Failed!");
			//ne.printStackTrace(System.err);
			roleIndicator = "";
		} catch (Exception e) {
			logger.error("Error Occurred!");
			//e.printStackTrace(System.err);
			roleIndicator = "";
		} finally {
			if (conContext != null) {
				try {
					conContext.close();
				} catch (NamingException ne) {
				}
			}
		}

		return roleIndicator + ":::" + orgName + ":::" + productionFlag;
	}

	public ArrayList<TopLevelUser> getTopLevelUsers() throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		ArrayList<TopLevelUser> userList = new ArrayList<TopLevelUser>();

		try {
			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"*", "+"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(domainDN, SELECTIVE_TOP_LEVEL_USER_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				String cn = attr.get("cn").get(0).toString();
				String roleIndicator = attr.get("destinationIndicator").get(0).toString();
				String userPswd = attr.get("userPassword").get(0).toString();
				String dn = attr.get("entryDN").get(0).toString();

				TopLevelUser topLevelUser = new TopLevelUser();

				topLevelUser.setUserId(dn);
				topLevelUser.setPasswordHash(userPswd);
				topLevelUser.setUsername(cn);
				topLevelUser.addRole(roleIndicator);

				userList.add(topLevelUser);

			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		logger.debug("User List size {} " + userList.size());
		return userList;
	}


	//Search Organization - To get organization ID
	//For creating the Mattermost channels
	public String searchOrganization(String orgName) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		//ArrayList<String> orgList = new ArrayList<String>();

		String description = "";
		String org = "";
		String teamId = "";

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"o", "description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(domainDN, ORGANIZATION_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				org = attr.get("o").get(0).toString();
				description = attr.get("description").get(0).toString();
				if (org.equals(orgName)) {
					Organization organizationData = gson.fromJson(description, Organization.class);
					teamId = organizationData.getOrganizationId_MM();
				}
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/
		return teamId;
	}

	//search tab - To get channel ID

	public String searchTab(String tabName) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		//ArrayList<String> orgList = new ArrayList<String>();

		String description = "";
		String tab = "";
		String channelId = "";

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"cn", "description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(domainDN, TAB_SEARCH_FILTER, controls);
			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				tab = attr.get("cn").get(0).toString();
				description = attr.get("description").get(0).toString();
				if (tab.equals(tabName)) {
					Tab tabData = gson.fromJson(description, Tab.class);
					channelId = tabData.getChannelId_MM();
				}
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/
		return channelId;
	}

	public void resetPassword4User(User userObj, String newPassword) {

		DirContext connectionContext = null;
		String result = "";

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String passwordHash = digestPassword("MD5", newPassword);

			userObj.setPasswordHash(passwordHash);

			ModificationItem[] modificationItems = new ModificationItem[2];

			Attribute description = new BasicAttribute("description", gson.toJson(userObj));

			//modificationItems[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("userPassword", oldUnicodePassword));
			modificationItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", passwordHash));
			modificationItems[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, description);

			String dn = commonNamePrefix + userObj.getUsername() + comma + organizationUnitPrefix + USERS + comma + organizationPrefix + userObj.getOrganization() + comma + domainDN;
			logger.debug(dn);

			connectionContext.modifyAttributes(dn, modificationItems);

		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
		}
		/*finally {
            if (connectionContext != null) {
                try {
                    connectionContext.close();
                }
                catch (NamingException ne) {
                    logger.error("Error Occurred while closing the connection!", ne);
                }
            }
        }*/

	}

	public String resetPassword(ClientUser userDetails) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		String response = "";

		try {
			String user = searchUser4Client(userDetails.getUsername());
			logger.info("User {}" + user);

			User userData;

			try {
				userData = gson.fromJson(user, User.class);
				//LDAP reset
				resetPassword4User(userData, userDetails.getPassword());
				//Mattermost reset
				String status = mmServiceManager.resetPassword(userData.getUserId_MM(), userDetails.getPassword());
				logger.info("Reset Password Status {}" + status);
				//Set response
				if(status.equalsIgnoreCase("200"))
					response = "Password updated successfully";
				else
					response = "Error Occurred, Please try again";
			}
			catch(Exception exp) {
				response = "User does not exist";
			}

		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			response = "User does not exist";
		}
		/*finally {
            if (connectionContext != null) {
                try {
                    connectionContext.close();
                }
                catch (NamingException ne) {
                    logger.error("Error Occurred while closing the connection!", ne);
                }
            }
        }*/
		return response;
	}

	public String searchUser4Client(String userName) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		String description = "";
		String user = "";
		String userId = "";
		String responseString = "";

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"cn", "description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(domainDN, USER_SEARCH_FILTER, controls);
			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				user = attr.get("cn").get(0).toString();
				description = attr.get("description").get(0).toString();
				if (user.equals(userName)) {
					responseString = description;
				}
			}
		} catch (AuthenticationNotSupportedException ex) {
			responseString = "";
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			responseString = "";
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			responseString = "";
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			responseString = "";
			logger.error("Error Occurred!", e);
		}
		/*finally {
            if (connectionContext != null) {
                try {
                    connectionContext.close();
                }
                catch (NamingException ne) {
                    logger.error("Error Occurred while closing the connection!", ne);
                }
            }
        }*/
		return responseString;
	}


	public String searchUser(String userName) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		String description = "";
		String user = "";
		String userId = "";

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"cn", "description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(domainDN, USER_SEARCH_FILTER, controls);
			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				user = attr.get("cn").get(0).toString();
				description = attr.get("description").get(0).toString();
				if (user.equals(userName)) {
					User userData = gson.fromJson(description, User.class);
					userId = userData.getUserId();
				}
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/
		return userId;
	}

	public String searchUserName(String userId) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException{       
		DirContext connectionContext = null;  

		String description = "";
		String user = "";
		String userName = "";

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {} "+connectionContext);

			String[] requiredAttributes={"cn", "description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(domainDN, USER_SEARCH_FILTER, controls);
			SearchResult searchResult=null;

			while(searchResults.hasMore()) {
				searchResult=(SearchResult) searchResults.next();
				Attributes attr=searchResult.getAttributes();
				user = attr.get("cn").get(0).toString();
				description=attr.get("description").get(0).toString();
				User userData = gson.fromJson(description, User.class);
				if(userId.equals(userData.getUserId_MM())) { 
					userName =userData.getUsername();
				}

			}
		}
		catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		}
		catch(NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		}
		catch(Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
                if (connectionContext != null) {
                    try {   
                        connectionContext.close();
                    }
                    catch (NamingException ne) { 
                        logger.error("Error Occurred while closing the connection!", ne); 
                    }
                }
            }*/
		return userName;
	}


	// search orgunit -To get Org name
	public String serachOranizationUint(String oranizationUnitName) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {

		DirContext connectionContext = null;

		String description = "";
		String orgUnitName = "";
		String orgName = "";

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String[] requiredAttributes = {"ou", "description"};

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(requiredAttributes);
			NamingEnumeration searchResults = connectionContext.search(domainDN, ORGANIZATION_UNIT_SEARCH_FILTER, controls);

			SearchResult searchResult = null;

			while (searchResults.hasMore()) {
				searchResult = (SearchResult) searchResults.next();
				Attributes attr = searchResult.getAttributes();
				orgUnitName = attr.get("ou").get(0).toString();
				description = attr.get("description").get(0).toString();
				if (orgUnitName.equals(oranizationUnitName)) {
					OrganizationUnit organizationUnitData = gson.fromJson(description, OrganizationUnit.class);
					orgName = organizationUnitData.getOrganizationName();
				}
			}
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/
		return orgName;

	}

	//This will be called when a role is assigned for a user.
	public String addUsers2Channel(String orgName, String[] rolesArray, String userId_MM) {
		String responseStr = "";

		ArrayList<Tab> tabList = new ArrayList<Tab>();
		try {
			tabList = getTabs(orgName);
		} catch (Exception exp) {
			tabList = new ArrayList<Tab>();
			logger.error("Error while reading tabs: ", exp);
		}

		ArrayList<String> roles = new ArrayList<String>();
		if (tabList.size() > 0) {
			for (Tab tab : tabList) {
				roles = tab.getRoleList();
				for (String role : rolesArray) {
					if (roles.contains(role)) {
						//logger.info(role);
						//logger.info(tab.getChannelId_MM());
						//logger.info(userId_MM);
						mmServiceManager.addUser2Channel(tab.getChannelId_MM(), userId_MM);
					}
				}
			}
		}

		return responseStr;
	}

	//This method will be called when role is assigned to tabs
	public String addUsers2Channel(String orgName, String role, ArrayList<Tab> tabList) {
		String responseStr = "";

		//ArrayList<Tab> tabList = new ArrayList<Tab>();
		//ArrayList<Tab> selectedTabList = new ArrayList<Tab>();
		ArrayList<User> userList = new ArrayList<User>();
		try {
			//tabList = getTabs(orgName);
			userList = getUsers(orgName);
		} catch (Exception exp) {
			//tabList = new ArrayList<Tab>();
			userList = new ArrayList<User>();
			logger.error("Error while reading users: ", exp);
		}

		/*//Push only selected Tabs
        if(tabList.size() > 0) {
            for(Tab tab : tabList) {
                for(String tabname: tabsArray) {
                    if(tab.getTabName().equals(tabname)) {
                        selectedTabList.add(tab);
                    }
                }
            }
        }*/

		ArrayList<String> roles = new ArrayList<String>();
		if (userList.size() > 0) {
			for (User usr : userList) {
				roles = usr.getRoleList();
				if (roles.contains(role)) {
					for (Tab tab : tabList) {
						logger.info(role);
						logger.info(tab.getChannelId_MM());
						logger.info(usr.getUserId_MM());
						mmServiceManager.addUser2Channel(tab.getChannelId_MM(), usr.getUserId_MM());
					}
				}
			}
		}

		return responseStr;
	}


	//This will be called when a role is removed for a user.
	public String removeUsersFromChannel(String orgName, String[] rolesArray, String userId_MM) {
		String responseStr = "";

		ArrayList<Tab> tabList = new ArrayList<Tab>();
		try {
			tabList = getTabs(orgName);
		} catch (Exception exp) {
			tabList = new ArrayList<Tab>();
			logger.error("Error while reading tabs: ", exp);
		}

		ArrayList<String> roles = new ArrayList<String>();
		if (tabList.size() > 0) {
			for (Tab tab : tabList) {
				roles = tab.getRoleList();
				for (String role : rolesArray) {
					if (roles.contains(role)) {
						logger.info(role);
						logger.info(tab.getChannelId_MM());
						logger.info(userId_MM);
						mmServiceManager.removeUserFromChannel(tab.getChannelId_MM(), userId_MM);
					}
				}
			}
		}

		return responseStr;
	}

	//This method will be called when role is removed from tabs
	public String removeUsersFromChannel(String orgName, String role, ArrayList<Tab> tabList) {
		String responseStr = "";

		//ArrayList<Tab> tabList = new ArrayList<Tab>();
		//ArrayList<Tab> selectedTabList = new ArrayList<Tab>();
		ArrayList<User> userList = new ArrayList<User>();
		try {
			//tabList = getTabs(orgName);
			userList = getUsers(orgName);
		} catch (Exception exp) {
			//tabList = new ArrayList<Tab>();
			userList = new ArrayList<User>();
			logger.error("Error while reading users: ", exp);
		}

		ArrayList<String> roles = new ArrayList<String>();
		if (userList.size() > 0) {
			for (User usr : userList) {
				roles = usr.getRoleList();
				if (roles.contains(role)) {
					for (Tab tab : tabList) {
						logger.info(role);
						logger.info(tab.getChannelId_MM());
						logger.info(usr.getUserId_MM());
						mmServiceManager.removeUserFromChannel(tab.getChannelId_MM(), usr.getUserId_MM());
					}
				}
			}
		}

		return responseStr;
	}

	public String authenticateClientUser(String username, String userPassword) {

		ArrayList<String> orgList = new ArrayList<String>();
		String roleIndicator = "none:::none";

		try {
			orgList = getOrganizationList();
		} catch (Exception exp) {
			exp.printStackTrace();
			return roleIndicator;
		}

		ArrayList<String> lst = new ArrayList<String>();

		for (String orgName : orgList) {
			roleIndicator = authenticateClientUser(orgName, username, userPassword);
			if (!roleIndicator.equals("") && !roleIndicator.equalsIgnoreCase("none:::none")) {
				lst.add(roleIndicator + ":::" + orgName);
			}

		}

		if(lst.size() > 0) {
			ArrayList<String> roleLst = new ArrayList<String>();
			ArrayList<String> departmentLst = new ArrayList<String>();
			ArrayList<String> roleTypeLst = new ArrayList<String>();
			ArrayList<String> orgLst = new ArrayList<String>();
			ArrayList<String> orgHisUrlLst = new ArrayList<String>();
			ArrayList<String> employeeIdLst = new ArrayList<String>();

			String latestRecord = "";
			for(String token : lst) {
				String[] tokens = token.split(":::");
				roleLst.add(tokens[0]);
				departmentLst.add(tokens[6]);
				roleTypeLst.add(tokens[7]);
				orgHisUrlLst.add(tokens[8]);
				orgLst.add(tokens[10]);
				latestRecord = token;
				employeeIdLst.add(tokens[9]);
			}

			String[] tokens = latestRecord.split(":::");

			String roles = StringUtils.join(roleLst, "###");
			String departments = StringUtils.join(departmentLst, "###");
			String roleTypes = StringUtils.join(roleTypeLst, "###");
			String hisUrls = StringUtils.join(orgHisUrlLst, "###");
			String orgs = StringUtils.join(orgLst, "###");
			String empIds = StringUtils.join(employeeIdLst, "###");

			roleIndicator = roles + ":::" + tokens[1] + ":::" + tokens[2] + ":::" +
					tokens[3] + ":::" + tokens[4] + ":::" + tokens[5] + ":::" +
					departments + ":::" + roleTypes + ":::" + hisUrls + ":::" + empIds + ":::" + tokens[10] + ":::" + orgs;
		} else {
			roleIndicator = "none:::none";
		}

		System.out.println(roleIndicator);


		/*for (String orgName : orgList) {
            roleIndicator = authenticateClientUser(orgName, username, userPassword);
            if (!roleIndicator.equals("") && !roleIndicator.equalsIgnoreCase("none:::none")) {
                return roleIndicator + ":::" + orgName;
            }
        }*/



		return roleIndicator;
	}

	public String authenticateClientUser(String orgName, String username, String userPassword) {

		//String orgName = "";
		String userBindDN = "";

		//Converting username into lowercase
		username = username.toLowerCase();

		ArrayList<User> userList = new ArrayList<User>();
		User userObj = null;

		try {
			userList = getUsers(orgName);
		} catch (Exception exp) {
			userList = new ArrayList<User>();
		}

		for (User tlu : userList) {
			//logger.debug("Inside the loop");
			if (tlu.getUsername().equals(username)) {
				userObj = tlu;
			}
		}

		userBindDN = commonNamePrefix + username + comma + organizationUnitPrefix + USERS + comma + organizationPrefix + orgName + comma + domainDN;

		logger.debug("userBindDN: " + userBindDN);

		Properties conProperties = new Properties();
		conProperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		conProperties.put(Context.PROVIDER_URL, serverURL);
		//parms.put(Context.SECURITY_PROTOCOL, "ssl");
		conProperties.put(Context.SECURITY_AUTHENTICATION, "simple");
		conProperties.put(Context.SECURITY_PRINCIPAL, userBindDN);
		conProperties.put(Context.SECURITY_CREDENTIALS, userPassword);

		DirContext conContext = null;

		Organization org = getOrganization(orgName);

		String statusIndicator = "";
		String roleIndicator = "";
		String roleTypeIndicator = "";
		String userIdValue = "";
		String firstName = "";
		String lastName = "";
		String email = "";
		String gender = "";
		String department = "";
		String hisURL = "";
		String user_employee_id = "";
		String userActiveStatus = "";

		try {
			conContext = new InitialDirContext(conProperties);
			logger.debug("Admin User Authentication Successful!");

			Attributes userAttrs = conContext.getAttributes(userBindDN);

			try {
				/*for (NamingEnumeration ae = userAttrs.getAll(); ae.hasMore();) {
                    Attribute attr = (Attribute) ae.next();
                    String attrID = attr.getID();
                    //logger.debug("\n" + attrID);
                    for (NamingEnumeration e = attr.getAll(); e.hasMore();) {
                        String attrVal = String.valueOf(e.next());
                        if(attrID.equals("description"))  {
                            //logger.debug(" : " + attrVal);
                            roleIndicator = attrVal;
                        }
                    }
                }*/
				roleIndicator = userObj.getRoles();
				userIdValue = userObj.getUserId_MM();
				firstName = userObj.getFirstName();
				lastName = userObj.getLastName();
				email = userObj.getEmail();
				gender = userObj.getGender();
				department = userObj.getDepartment();
				hisURL = org.getOrganizationHisUrl();
				user_employee_id = userObj.getEmployeeId();
				userActiveStatus = String.valueOf(userObj.isActiveStatus());

				ArrayList<Role> roleList = getRoles(orgName);

				String[] rolez = roleIndicator.split(",");
				ArrayList<String> rolezList = new ArrayList<String>();
				ArrayList<String> roleTypes = new ArrayList<String>();

				for (Role role : roleList) {
					for (String userRole : rolez) {
						if (role.getRoleName().equals(userRole)) {
							rolezList.add(role.getRoleName());
							roleTypes.add(role.getRoleType());
						}
					}
				}
				roleIndicator = StringUtils.join(rolezList, ",");
				roleTypeIndicator = StringUtils.join(roleTypes, ",");

			} catch (Exception e) {
				roleIndicator = "none";
				e.printStackTrace();
			}

		} catch (AuthenticationNotSupportedException ex) {
			logger.error("User Authentication Failed!");
			//ex.printStackTrace(System.err);
			roleIndicator = "";
		} catch (AuthenticationException aex) {
			logger.error("Invalid credentials");
			//aex.printStackTrace(System.err);
			roleIndicator = "";
		} catch (NamingException ne) {
			logger.error("User Authentication Failed!");
			//ne.printStackTrace(System.err);
			roleIndicator = "";
		} catch (Exception e) {
			logger.error("Error Occurred!");
			//e.printStackTrace(System.err);
			roleIndicator = "";
		}
		/*finally {
			if (conContext != null) {
				try {
					conContext.close();
				}
				catch (NamingException ne) { }
			}
		}*/

		if (roleIndicator.equalsIgnoreCase("")) {
			statusIndicator = "none:::none";
		} else if (userIdValue.equalsIgnoreCase("")) {
			statusIndicator = "none:::none";
		} else {
			statusIndicator = roleIndicator + ":::" + userIdValue + ":::" + firstName + ":::" + lastName + ":::" + email + ":::" + gender + ":::" + department + ":::" + roleTypeIndicator + ":::" + hisURL + ":::" + user_employee_id + ":::" + userActiveStatus;
		}

		return statusIndicator;
	}

	//Get Organization Details
	public String updateOrganization(Organization org) {

		DirContext connectionContext = null;
		Organization orgObj;

		String result = "";
		String resultCode = "400";
		boolean isValidUserId = false;
		boolean isValidAuthToken = false;

		try {

			String data = gson.toJson(org);

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String distinguishedName = organizationPrefix + org.getOrganizationName() + comma + domainDN;

			ModificationItem[] modificationItems = new ModificationItem[1];

			Attribute description = new BasicAttribute("description", data);

			modificationItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, description);

			logger.debug(distinguishedName);

			connectionContext.modifyAttributes(distinguishedName, modificationItems);
			result = "HIS URL Updated.";
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "Update Failed";
			orgObj = new Organization();
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "Update Failed";
			orgObj = new Organization();
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "Update Failed";
			orgObj = new Organization();
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "Update Failed";
			orgObj = new Organization();
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		return result;

	}

	//Get Organization Details
	public Organization getOrganization(String orgName) {

		DirContext connectionContext = null;
		Organization orgObj;

		String result = "";
		String resultCode = "400";
		boolean isValidUserId = false;
		boolean isValidAuthToken = false;

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String distinguishedName = organizationPrefix + orgName + comma + domainDN;

			//Read and update the existing data
			Attributes attrs = connectionContext.getAttributes(distinguishedName, new String[]{"description"});
			String orgDefinition = attrs.get("description").get().toString();
			logger.info("Description {} " + orgDefinition);

			orgObj = gson.fromJson(orgDefinition, Organization.class);

		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
			orgObj = new Organization();
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
			orgObj = new Organization();
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
			orgObj = new Organization();
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
			orgObj = new Organization();
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		return orgObj;

	}

	//Validating the userAuthToken against the user
	public String validateUser(String orgName, String userName, String userId, String userAuthToken) {

		DirContext connectionContext = null;
		String result = "";
		String resultCode = "400";
		boolean isValidUserId = false;
		boolean isValidAuthToken = false;

		try {

			String userAuthTokenValidity = mmServiceManager.validateUserToken(userId, userAuthToken);

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String distinguishedName = commonNamePrefix + userName + comma + organizationUnitPrefix + USERS + comma + organizationPrefix + orgName + comma + domainDN;

			//Read and update the existing data
			Attributes attrs = connectionContext.getAttributes(distinguishedName, new String[]{"description"});
			String userDefinition = attrs.get("description").get().toString();
			logger.info("Description {} " + userDefinition);

			User userObj = gson.fromJson(userDefinition, User.class);

			if (userObj.getUserId_MM().equalsIgnoreCase(userId)) {
				isValidUserId = true;
			} else {
				isValidUserId = false;
			}

			if (userAuthTokenValidity.equalsIgnoreCase("true")) {
				isValidAuthToken = true;
			} else {
				isValidAuthToken = false;
			}

			if (isValidAuthToken && isValidUserId) {
				resultCode = "201";
				result = getAuthorizedTabs4User(orgName, userObj);
			}

		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
			resultCode = "400";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
			resultCode = "400";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
			resultCode = "400";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
			resultCode = "400";
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		return resultCode + ":::" + result;

	}

	public String getAuthorizedTabs4User(String orgName, User userObj) {
		ArrayList<Tab> tabList = new ArrayList<Tab>();
		ArrayList<Tab> authorizedTabListDup = new ArrayList<Tab>();
		ArrayList<Tab> authorizedTabList = new ArrayList<Tab>();
		Organization orgObj = getOrganization(orgName);

		ArrayList<String> organizationUnitsMaster = new ArrayList<>();

		try {
			tabList = getTabs(orgName);
			organizationUnitsMaster = getOrganizationUnitList(orgName);
		} catch (Exception exp) {
			tabList = new ArrayList<Tab>(); 
			logger.error("Error while reading tabs: ", exp);
		}

		ArrayList<String> rolesArray = userObj.getRoleList();
		ArrayList<String> roles = new ArrayList<String>();

		models.client.AuthorizedTabs authorizedTabs = new models.client.AuthorizedTabs();
		ArrayList<models.client.OrganizationUnit> organizationUnits = new ArrayList<>();
		ArrayList<models.client.OrganizationUnit> organizationUnitsTemp = new ArrayList<>();
		ArrayList<models.client.Department> departments = new ArrayList<>();
		ArrayList<models.client.Channel> globalChannels = new ArrayList<>();
		ArrayList<models.client.Channel> ouChannels = new ArrayList<>();
		ArrayList<models.client.Channel> depChannels = new ArrayList<>();

		ArrayList<String> orgUnitNameList = new ArrayList<String>();
		ArrayList<String> orgUnitNameListEmpty = new ArrayList<String>(organizationUnitsMaster);

		authorizedTabs.setOrganization(orgName);
		authorizedTabs.setOrganizationId(orgObj.getOrganizationId_MM());

		if (tabList.size() > 0) {
			for (Tab tab : tabList) {
				roles = tab.getRoleList();
				for (String role : rolesArray) {
					if (roles.contains(role)) {
						authorizedTabListDup.add(tab);
					} //If role matches
				} //Role parsing
			} //Tab parsing
		} //tablist size

		for (Tab tabb : authorizedTabListDup) {
			boolean isFound = false;
			// check if the event name exists in noRepeat
			for (Tab e : authorizedTabList) {
				if (e.getChannelId_MM().equals(tabb.getChannelId_MM()) || (e.equals(tabb))) {
					isFound = true;
					break;
				}
			}
			if (!isFound) authorizedTabList.add(tabb);
		}

		if (authorizedTabList.size() > 0) {

			for (Tab tab : authorizedTabList) {
				models.client.OrganizationUnit orgUnit = new models.client.OrganizationUnit();
				orgUnit.setOrganizationUnitName(tab.getOrganizationUnitName());
				orgUnitNameList.add(tab.getOrganizationUnitName());

				//OU Non-Specific Tabs
				if (tab.hasAccessAcrossUnits()) {
					models.client.Channel gChannel = new models.client.Channel();
					String tabDispName = "";
					try {
						tabDispName = tab.getTabDisplayName();
					}catch(Exception exp) { tabDispName = tab.getTabName(); }
					gChannel.setChannelId(tab.getChannelId_MM());
					gChannel.setChannelname(tab.getTabName());
					gChannel.setChannelHeader(tab.getTabHeader());
					gChannel.setChannelPurpose(tab.getTabPurpose());
					gChannel.setAccessAcrossUnits(String.valueOf(tab.hasAccessAcrossUnits()));
					gChannel.setAccessAcrossDepartments(String.valueOf(tab.hasAccessAcrossDepartments()));
					gChannel.setChannelType(tab.getTemplateName());
					gChannel.setChannelDisplayName(tabDispName);
					globalChannels.add(gChannel);
				}
				//OU Specific Tabs
				else {

					models.client.Channel ouChannel = new models.client.Channel();
					String tabDispName = "";
					try {
						tabDispName = tab.getTabDisplayName();
					}catch(Exception exp) { tabDispName = tab.getTabName(); }
					ouChannels = new ArrayList<>();
					ouChannel.setChannelId(tab.getChannelId_MM());
					ouChannel.setChannelname(tab.getTabName());
					ouChannel.setChannelHeader(tab.getTabHeader());
					ouChannel.setChannelPurpose(tab.getTabPurpose());
					ouChannel.setAccessAcrossUnits(String.valueOf(tab.hasAccessAcrossUnits()));
					ouChannel.setAccessAcrossDepartments(String.valueOf(tab.hasAccessAcrossDepartments()));
					ouChannel.setChannelType(tab.getTemplateName());
					ouChannel.setChannelDisplayName(tabDispName);
					ouChannels.add(ouChannel);

					orgUnit.setChannels(ouChannels);
				}

				organizationUnits.add(orgUnit);
			}

		}

		Set<String> uniqueOUs = new HashSet<String>(orgUnitNameList);

		orgUnitNameListEmpty.removeAll(new HashSet<>(orgUnitNameList));

		System.out.println(orgUnitNameListEmpty);

		for (String uniqueOU : uniqueOUs) {
			models.client.OrganizationUnit orgUnit = new models.client.OrganizationUnit();
			orgUnit.setOrganizationUnitName(uniqueOU);
			ouChannels = new ArrayList<>();
			for (models.client.OrganizationUnit orgUnitTemp : organizationUnits) {
				if (uniqueOU.equalsIgnoreCase(orgUnitTemp.getOrganizationUnitName())) {
					List<models.client.Channel> tempChannelList = orgUnitTemp.getChannels();
					if (tempChannelList != null && tempChannelList.size() > 0) {
						for (models.client.Channel ouChannel : tempChannelList) {
							ouChannels.add(ouChannel);
						}
					}
				}
			}
			orgUnit.setChannels(ouChannels);
			organizationUnitsTemp.add(orgUnit);
		}

		for (String emptyOU : orgUnitNameListEmpty) {
			models.client.OrganizationUnit orgUnit = new models.client.OrganizationUnit();
			orgUnit.setOrganizationUnitName(emptyOU);
			ouChannels = new ArrayList<>();

			orgUnit.setChannels(ouChannels);
			organizationUnitsTemp.add(orgUnit);
		}


		authorizedTabs.setChannels(globalChannels);
		authorizedTabs.setOrganizationUnit(organizationUnitsTemp);

		String authTabString = gson.toJson(authorizedTabs);

		logger.info("Parsed Tree {}" + authTabString);
		return authTabString;
	}

	public String createBroadcastTab(String orgName) {
		String result = "";

		try {
			createTab("Broadcast", "Broadcast", "Broadcast", "Chat", orgName,
					"ou_specific_no", "", "", "broadcast", "dep_specific_no");
		} catch (Exception exp) {
			exp.printStackTrace();
		}

		return result;
	}

	public String createBroadcastRole(String orgName) {
		String result = "";

		try {
			createRole("broadcast", "Doctor", orgName, "access_no", "", "");
		} catch (Exception exp) {
			exp.printStackTrace();
		}

		return result;
	}

	//This method will be called when broadcast channel is created
	//addUsers2BroadcastChannel(orgName);
	public String addUsers2BroadcastChannel(String orgName) {//, User userObj) {
		String responseStr = "";

		ArrayList<Tab> tabList = new ArrayList<Tab>();
		ArrayList<Tab> selectedTabList = new ArrayList<Tab>();
		ArrayList<User> userList = new ArrayList<User>();
		try {
			tabList = getTabs(orgName);
			userList = getUsers(orgName);
		} catch (Exception exp) {
			tabList = new ArrayList<Tab>();
			userList = new ArrayList<User>();
			logger.error("Error while reading users: ", exp);
		}

		//Push only selected Tabs
		if (tabList.size() > 0) {
			for (Tab tab : tabList) {
				if (tab.getTabName().equals("Broadcast")) {
					selectedTabList.add(tab);
				}
			}
		}

		if (userList.size() > 0) {
			for (User usr : userList) {
				for (Tab tab : selectedTabList) {
					logger.info(tab.getChannelId_MM());
					logger.info(usr.getUserId_MM());
					mmServiceManager.addUser2Channel(tab.getChannelId_MM(), usr.getUserId_MM());
				}
			}
		} else {

		}

		return responseStr;
	}

	public String addUsers2BroadcastChannel(String orgName, User userObj) {
		String responseStr = "";

		ArrayList<Tab> tabList = new ArrayList<Tab>();
		ArrayList<Tab> selectedTabList = new ArrayList<Tab>();
		try {
			tabList = getTabs(orgName);
		} catch (Exception exp) {
			tabList = new ArrayList<Tab>();
			logger.error("Error while reading tabs: ", exp);
		}

		//Push only selected Tabs
		if (tabList.size() > 0) {
			for (Tab tab : tabList) {
				if (tab.getTabName().equals("Broadcast")) {
					selectedTabList.add(tab);
				}
			}
		}

		for (Tab tab : selectedTabList) {
			logger.info(tab.getChannelId_MM());
			logger.info(userObj.getUserId_MM());
			mmServiceManager.addUser2Channel(tab.getChannelId_MM(), userObj.getUserId_MM());
		}

		return responseStr;
	}

	/*  public models.client.Channel getBroadcastChannel(String orgName) {
        String responseStr = "";
        models.client.Channel broadCastChannel = new models.client.Channel();
        ArrayList<Tab> tabList = new ArrayList<Tab>();
        ArrayList<Tab> selectedTabList = new ArrayList<Tab>();
        try {
            tabList = getTabs(orgName);
        }
        catch(Exception exp) {
            tabList = new ArrayList<Tab>();
            logger.error("Error while reading tabs: ", exp);
        }

        //Push only selected Tabs
        if(tabList.size() > 0) {
            for(Tab tab : tabList) {
                if(tab.getTabName().equalsIgnoreCase("Broadcast")) {
                    selectedTabList.add(tab);
                    broadCastChannel.setChannelId(tab.getChannelId_MM());
                    broadCastChannel.setChannelname(tab.getTabName());
                    broadCastChannel.setChannelHeader(tab.getTabHeader());
                    broadCastChannel.setChannelPurpose(tab.getTabPurpose());
                    broadCastChannel.setAccessAcrossUnits(String.valueOf(tab.hasAccessAcrossUnits()));
                    broadCastChannel.setAccessAcrossDepartments(String.valueOf(tab.hasAccessAcrossDepartments()));
                    broadCastChannel.setChannelType(tab.getTemplateName());
                }
            }
        }
        return broadCastChannel;
    }*/


	public models.client.Channel getBroadcastChannel(String orgName) {

		DirContext connectionContext = null;
		String result = "";
		models.client.Channel broadCastChannel = new models.client.Channel();

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String distinguishedName = commonNamePrefix + "Broadcast" + comma + organizationUnitPrefix + TABS + comma + organizationPrefix + orgName + comma + domainDN;
			logger.debug(distinguishedName);

			Attributes attrs = connectionContext.getAttributes(distinguishedName, new String[]{"description"});
			String tabDefinition = attrs.get("description").get().toString();
			logger.info("Description {} " + tabDefinition);

			Tab tab = gson.fromJson(tabDefinition, Tab.class);
			//String mmChannelId = tab.getChannelId_MM();
			String tabDispName = "";
			try {
				tabDispName = tab.getTabDisplayName();
			}catch(Exception exp) { tabDispName = tab.getTabName(); }
			broadCastChannel.setChannelId(tab.getChannelId_MM());
			broadCastChannel.setChannelname(tab.getTabName());
			broadCastChannel.setChannelHeader(tab.getTabHeader());
			broadCastChannel.setChannelPurpose(tab.getTabPurpose());
			broadCastChannel.setAccessAcrossUnits(String.valueOf(tab.hasAccessAcrossUnits()));
			broadCastChannel.setAccessAcrossDepartments(String.valueOf(tab.hasAccessAcrossDepartments()));
			broadCastChannel.setChannelType(tab.getTemplateName());
			broadCastChannel.setChannelDisplayName(tabDispName);

		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		return broadCastChannel;

	}

	public boolean validateOrgName(String orgName) {
		boolean isValid = true;
		try {
			ArrayList<String> orgList = getOrganizationList();
			for (String org : orgList) {
				if (orgName.equals(org)) {
					isValid = false;
					break;
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return isValid;
	}

	public boolean validateOrgUnitName(String orgName, String orgUnitName) {
		boolean isValid = true;
		try {
			ArrayList<String> orgUnitList = getOrganizationUnitList(orgName);
			for (String orgUnit : orgUnitList) {
				if (orgUnitName.equals(orgUnit)) {
					isValid = false;
					break;
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return isValid;
	}

	public boolean validateDepartmentName(String orgName, String orgUnitName, String departmentName) {
		boolean isValid = true;
		try {
			ArrayList<String> departmentList = getDepartmentList(orgName, orgUnitName);
			for (String department : departmentList) {
				if (departmentName.equals(department)) {
					isValid = false;
					break;
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return isValid;
	}

	public boolean validateRoleName(String orgName, String roleName) {
		boolean isValid = true;
		try {
			ArrayList<String> roleList = getRoleList(orgName, "list");
			for (String role : roleList) {
				if (roleName.equals(role)) {
					isValid = false;
					break;
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return isValid;
	}

	public boolean validateTabName(String orgName, String tabName) {
		boolean isValid = true;
		try {
			ArrayList<Tab> tabList = getTabs(orgName);
			for (Tab tab : tabList) {
				if (tab.getTabName().equals(tabName)) {
					isValid = false;
					break;
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return isValid;
	}

	public boolean validateUserName(String orgName, String userName) {
		boolean isValid = true;
		try {
			ArrayList<User> userList = getUsers(orgName);
			for (User user : userList) {
				if (user.getUsername().equals(userName)) {
					isValid = false;
					break;
				}
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return isValid;
	}


	//Get Members of a channel
	public String getMembers(String orgName, String userName, String userId, String userAuthToken, String channelId) {

		DirContext connectionContext = null;
		String result = "";
		String resultCode = "400";
		boolean isValidUserId = false;
		boolean isValidAuthToken = false;

		try {

			String userAuthTokenValidity = mmServiceManager.validateUserToken(userId, userAuthToken);

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String distinguishedName = commonNamePrefix + userName + comma + organizationUnitPrefix + USERS + comma + organizationPrefix + orgName + comma + domainDN;

			//Read and update the existing data
			Attributes attrs = connectionContext.getAttributes(distinguishedName, new String[]{"description"});
			String userDefinition = attrs.get("description").get().toString();
			logger.info("Description {} " + userDefinition);

			User userObj = gson.fromJson(userDefinition, User.class);

			if (userObj.getUserId_MM().equalsIgnoreCase(userId)) {
				isValidUserId = true;
			} else {
				isValidUserId = false;
			}

			if (userAuthTokenValidity.equalsIgnoreCase("true")) {
				isValidAuthToken = true;
			} else {
				isValidAuthToken = false;
			}

			if (isValidAuthToken && isValidUserId) {
				resultCode = "201";
				result = getChannelMembers(channelId, userAuthToken, orgName);
			}

		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
			resultCode = "400";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
			resultCode = "400";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
			resultCode = "400";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
			resultCode = "400";
		}
		/*finally {
			if (connectionContext != null) {
				try {
					connectionContext.close();
				}
				catch (NamingException ne) {
					logger.error("Error Occurred while closing the connection!", ne);
				}
			}
		}*/

		return resultCode + ":::" + result;

	}


	public String getChannelMembers(String channelId, String tokenId, String orgName) throws Exception {
		String responseString = "";

		ArrayList<User> userList = getUsers(orgName);
		ArrayList<Role> roleList = getRoles(orgName);

		String memberJson = mmServiceManager.getChannelMembers(channelId, tokenId);
		//logger.info("Member Json {} " + memberJson);

		ArrayList<models.client.ChannelMember> channelMemberList = gson.fromJson(memberJson, models.client.ChannelMembers.class);
		ArrayList<models.client.ChannelMember> channelMemberListFinal = new ArrayList<>();
		for (models.client.ChannelMember channelMember : channelMemberList) {
			logger.info("Member Json {} " + channelMember.getUserId());

			for (User user : userList) {
				if (user.getUserId_MM().equalsIgnoreCase(channelMember.getUserId())) {
					String roleIndicator = user.getRoles();
					String roleTypeIndicator = "";

					String[] rolez = roleIndicator.split(",");
					ArrayList<String> rolezList = new ArrayList<String>();
					ArrayList<String> roleTypes = new ArrayList<String>();

					for (Role role : roleList) {
						for (String userRole : rolez) {
							if (role.getRoleName().equals(userRole)) {
								rolezList.add(role.getRoleName());
								roleTypes.add(role.getRoleType());
							}
						}
					}
					roleIndicator = StringUtils.join(rolezList, ",");
					roleTypeIndicator = StringUtils.join(roleTypes, ",");

					//Set User Details
					channelMember.setUserRoles(roleIndicator);
					channelMember.setUserRoleTypes(roleTypeIndicator);
					channelMember.setUserFirstname(user.getFirstName());
					channelMember.setUserLastname(user.getLastName());
					channelMember.setUserEmail(user.getEmail());
					channelMember.setUserGender(user.getGender());
					channelMember.setUserDepartment(user.getDepartment());
					break;
				}
			}

			channelMemberListFinal.add(channelMember);
		}

		responseString = gson.toJson(channelMemberListFinal);
		return responseString;
	}


	// create news
	public String createNews(String... parameters) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException {
		String response = "";
		String content = "!!!news_feed!!!###";
		try {

			String message = content.concat(parameters[0]); // content
			String orgName = parameters[1]; //Organization Name
			String orgUnitName = parameters[2]; //Organization unit name
			String tabName = parameters[3]; // tabName

			String channelId = searchTab(tabName);
			logger.info(channelId);

			response = mmServiceManager.postChannelMessage(channelId, message);
			logger.info(response);
			/*
			 * String description = ""; News news =new News(); news.setContent(content);
			 * news.setOrganizationName(orgName); news.setOrganizationUnitName(orgUnitName);
			 * news.setTabName(tabName);
			 *
			 * description = gson.toJson(news);
			 */
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
		}
		return response;
	}

	/*
	 * // delete Team public void deleteTeam(String orgName) throws
	 * NamingException,NoSuchAlgorithmException,UnsupportedEncodingException{ String
	 * teamId=searchOrganization(orgName); mmServiceManager.deleteTeam(teamId); }
	 */


	public String getStats4LDAP(String teamName) {

		try {
			ArrayList<Tab> tabs = getTabs(teamName);
			ArrayList<String> counts = new ArrayList<String>();

			int chatCnt = 0;
			int surveyCnt = 0;
			int newsCnt = 0;
			int referenceCnt = 0;

			for (Tab tab : tabs) {
				if (tab.getTemplateName().equalsIgnoreCase("Chat")) {
					chatCnt++;
				}
				if (tab.getTemplateName().equalsIgnoreCase("Survey")) {
					surveyCnt++;
				}
				if (tab.getTemplateName().equalsIgnoreCase("News")) {
					newsCnt++;
				}
				if (tab.getTemplateName().equalsIgnoreCase("Reference")) {
					referenceCnt++;
				}
			}

			counts.add(Integer.toString(chatCnt));
			counts.add(Integer.toString(surveyCnt));
			counts.add(Integer.toString(newsCnt));
			counts.add(Integer.toString(referenceCnt));

			return StringUtils.join(counts, ":::");

		} catch (Exception exp) {
			return "0:::0:::0:::0";
		}

	}

	//get user

	public models.dto.UserDto getUser(String userName,String orgName) {

		DirContext connectionContext = null;
		String result = "";
		models.dto.UserDto userDto=null;

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String distinguishedName = commonNamePrefix + userName + comma + organizationUnitPrefix + USERS + comma + organizationPrefix + orgName + comma + domainDN;
			logger.debug(distinguishedName);

			Attributes attrs = connectionContext.getAttributes(distinguishedName, new String[]{"description"});
			String userDefinition = attrs.get("description").get().toString();
			logger.info("Description {} " + userDefinition);

			User user = gson.fromJson(userDefinition, User.class);

			if(user!=null) {
				userDto=new models.dto.UserDto();
				userDto.setAadhaarNumber(user.getAadhaarNumber());
				userDto.setAccessInfo(String.valueOf(user.hasOuAccess()));
				userDto.setDepartmentName(user.getDepartment());
				userDto.setEmail(user.getEmail());
				userDto.setEmployeeId(user.getEmployeeId());
				userDto.setFirstName(user.getFirstName());
				userDto.setGender(user.getGender());
				userDto.setLastName(user.getLastName());
				userDto.setOrganizationName(user.getOrganization());
				userDto.setOrganizationUnitName(user.getOrganizationUnit());
				userDto.setPanNumber(user.getPanNumber());
				userDto.setPassportNumber(user.getPassportNumber());
				userDto.setPasswordHash(user.getPasswordHash());
				userDto.setPhoneNumber(user.getPhoneNumber());
				userDto.setQualification(user.getQualification());
				userDto.setRoleName(user.getRoles());
				userDto.setSpecialization(user.getSpecialization());
				userDto.setUniversalAccessInfo(String.valueOf(user.hasUniversalAccess()));
				userDto.setUsername(user.getUsername());
			}            
		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
		}
		/*finally {
            if (connectionContext != null) {
                try {
                    connectionContext.close();
                }
                catch (NamingException ne) {
                    logger.error("Error Occurred while closing the connection!", ne);
                }
            }
        }*/
		return userDto;
	}


	// updating the user 
	public void updateUser(models.dto.UserDto userDto) {

		DirContext connectionContext = null;
		String result = "";

		try {

			String orgName = userDto.getOrganizationName();
			String userName = userDto.getUsername();

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String distinguishedName = commonNamePrefix + userName + comma + organizationUnitPrefix + USERS + comma + organizationPrefix + orgName + comma + domainDN;

			//Read Data
			Attributes attrs = connectionContext.getAttributes(distinguishedName, new String[]{"description"});
			String userDefinition = attrs.get("description").get().toString();
			logger.info("Description {} " + userDefinition);
			User userObj = gson.fromJson(userDefinition, User.class);

			//Update Data
			userObj.setFirstName(userDto.getFirstName());
			userObj.setLastName(userDto.getLastName());
			userObj.setQualification(userDto.getQualification());
			userObj.setSpecialization(userDto.getSpecialization());
			userObj.setEmployeeId(userDto.getEmployeeId());
			userObj.setAadhaarNumber(userDto.getAadhaarNumber());
			userObj.setPanNumber(userDto.getPanNumber());
			userObj.setPassportNumber(userDto.getPassportNumber());
			userObj.setPhoneNumber(userDto.getPhoneNumber());

			//Update LDAP Entry
			ModificationItem[] modificationItems = new ModificationItem[1];
			Attribute description = new BasicAttribute("description", gson.toJson(userObj));
			modificationItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, description);
			connectionContext.modifyAttributes(distinguishedName, modificationItems);      

			//Update Mattermost Entry
			String userId = searchUserGetMMUserId(userDto.getUsername());
			mmServiceManager.updateUser(userId, userDto.getFirstName(), userDto.getLastName());            

		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
		}
		/*finally {
            if (connectionContext != null) {
                try {
                    connectionContext.close();
                }
                catch (NamingException ne) {
                    logger.error("Error Occurred while closing the connection!", ne);
                }
            }
        }*/
	}

	public TabDto getTab(String tabName,String orgName) {
		DirContext connectionContext = null;
		String result = "";
		models.dto.TabDto tabDto=null;

		try {

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String distinguishedName = commonNamePrefix + tabName + comma + organizationUnitPrefix + TABS + comma + organizationPrefix + orgName + comma + domainDN;

			logger.debug(distinguishedName);

			Attributes attrs = connectionContext.getAttributes(distinguishedName, new String[]{"description"});
			String tabDefinition = attrs.get("description").get().toString();
			logger.info("Description {} " + tabDefinition);

			Tab tab = gson.fromJson(tabDefinition, Tab.class);
			if(tab!=null) {
				tabDto=new TabDto();
				tabDto.setChannelName(tab.getTabName());
				tabDto.setHeader(tab.getTabHeader());
				tabDto.setChannelDisplayName(tab.getTabDisplayName());
				tabDto.setTemplateName(tab.getTemplateName());
				tabDto.setPurpose(tab.getTabPurpose());
				tabDto.setOrganizationName(tab.getOrganizationName());
				tabDto.setOrganizationUnitName(tab.getOrganizationUnitName());
				tabDto.setDepartmentName(tab.getDepartmentName());
				tabDto.setRole(tab.getRoles());
				tabDto.setAccessAcrossDepartments(tab.hasAccessAcrossDepartments());
				tabDto.setAccessAcrossUnits(tab.hasAccessAcrossUnits());
			}

		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
		}
		/*finally {
             if (connectionContext != null) {
                 try {
                     connectionContext.close();
                 }
                 catch (NamingException ne) {
                     logger.error("Error Occurred while closing the connection!", ne);
                 }
             }
         }*/
		return tabDto;

	}

	// updating the user 

	public void updateTab(models.dto.TabDto tabDto) {

		DirContext connectionContext = null;
		String result = "";

		try {

			String orgName = tabDto.getOrganizationName();
			String tabName = tabDto.getChannelName();

			if (globalConnectionContext != null) {
				connectionContext = globalConnectionContext;
			} else {
				connectionContext = new InitialDirContext(connectionProperties);
			}
			logger.info("LDAP Connection context created...");
			logger.info("LDAP Context {}" + connectionContext);

			String distinguishedName = commonNamePrefix + tabName + comma + organizationUnitPrefix + TABS + comma + organizationPrefix + orgName + comma + domainDN;

			System.out.println(distinguishedName);
			//Read Data
			Attributes attrs = connectionContext.getAttributes(distinguishedName, new String[]{"description"});
			System.out.println(attrs);
			String tabDefinition = attrs.get("description").get().toString();
			logger.info("Description {} " + tabDefinition);
			Tab tabObj = gson.fromJson(tabDefinition, Tab.class);

			System.out.println("tab"+tabObj.toString());

			//Update Data
			// tabObj.setTabName(tabDto.getChannelName());
			tabObj.setTabPurpose(tabDto.getPurpose());
			tabObj.setTabHeader(tabDto.getHeader());
			// tabObj.setOrganizationName(tabDto.getOrganizationName());
			// tabObj.setOrganizationUnitName(tabDto.getOrganizationUnitName());
			// tabObj.setTemplateName(tabDto.getTemplateName());
			// tabObj.setDepartmentName(tabDto.getDepartmentName());
			// tabObj.setRoleList(Arrays.asList(tabDto.getRole()));
		    // tabObj.setAccessAcrossUnits(tabDto.isAccessAcrossUnits());
			// tabObj.setAccessAcrossDepartments(tabDto.isAccessAcrossDepartments());
			tabObj.setTabDisplayName(tabDto.getChannelDisplayName());


			//Update LDAP Entry

			ModificationItem[] modificationItems = new ModificationItem[1];
			Attribute description = new BasicAttribute("description", gson.toJson(tabObj));
			modificationItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, description);
			connectionContext.modifyAttributes(distinguishedName, modificationItems);  

			//Update Mattermost Entry
			String channelId = searchTab(tabDto.getChannelName());
			mmServiceManager.updateTab(channelId, tabDto.getChannelName(),tabDto.getChannelDisplayName(),tabDto.getHeader(),tabDto.getPurpose());          

		} catch (AuthenticationNotSupportedException ex) {
			logger.error("The authentication is not supported by the server", ex);
			result = "An error occured while reading the data";
		} catch (AuthenticationException ex) {
			logger.error("Invalid password or username", ex);
			result = "An error occured while reading the data";
		} catch (NamingException ne) {
			logger.error("Authentication and Bind Failed!", ne);
			result = "An error occured while reading the data";
		} catch (Exception e) {
			logger.error("Error Occurred!", e);
			result = "An error occured while reading the data";
		}
		/*finally {
            if (connectionContext != null) {
                try {
                    connectionContext.close();
                }
                catch (NamingException ne) {
                    logger.error("Error Occurred while closing the connection!", ne);
                }
            }
        }*/
	}

}