package controllers;

import play.mvc.*;
import models.*;
import views.html.*;

import com.typesafe.config.Config;

import play.data.FormFactory;
import play.data.Form;
import play.data.DynamicForm;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import javax.inject.Inject;

import java.util.ArrayList;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class LoginController extends Controller {

    private final Config config;

    private final String titleMessage = "Homing Admin";

    private String flag = "";

    @Inject
    FormFactory formFactory;

    @Inject
    public LoginController(Config config) {
        this.config = config;
    }

    public Result login() {
        String loggedUser = ctx().session().get("loggedUser");
        
        if(loggedUser != null) {          
            return redirect(controllers.routes.HomeController.index());
        } else {
            loggedUser = "";
        }

        if(loggedUser.equals("")) {
            if(flag.equals("")) {
                return ok(views.html.login.render(titleMessage, loggedUser, flag));
            }
        }

        return badRequest(views.html.login.render(titleMessage, loggedUser, flag));
    }

    public Result authenticate() {
        
        LoginModel lm = new LoginModel();
        MattermostServiceManager mmsm = new MattermostServiceManager(config);

        //String flag = "";
        String loggedUser = "";
        String mmLoginToken = "";

        try {
            mmLoginToken = mmsm.doLogin();
        }
        catch(Exception exp) {
            exp.printStackTrace();
            mmLoginToken = "";
        }
        loggedUser = ctx().session().get("loggedUser");

        String remoteAddr = request().remoteAddress();
        java.util.Optional<java.lang.String> userAgentHeader = request().header("User-Agent");
        String userAgent = userAgentHeader.toString();
        
        /*String uname = formFactory.form().bindFromRequest().get("user-name");
        String pwd = formFactory.form().bindFromRequest().get("pass-word");

        Form<LoginController> loginform = formFactory.form(LoginController.class).bindFromRequest();*/

        DynamicForm requestData = formFactory.form().bindFromRequest();
        String uname = requestData.get("user-name");
        String pwd = requestData.get("pass-word");

        if(uname.equals("")) {
            flag = "err-usr";
            return redirect("/login");
        }
        else if(pwd.equals("")) {
            flag = "err-pwd";
            return redirect("/login");
        }
        else {
            try {
                String roleInfo = lm.authenticate(config, uname, pwd, remoteAddr, userAgent);
                if(!roleInfo.equals("") && !roleInfo.equals(":::")) {
                    String[] userInfo = roleInfo.split(":::");
                    session("loggedUser", uname);
                    session("loggedUserRole", userInfo[0]);
                    try {
                        session("loggedUserOrg", userInfo[1]);
                        session("loginFlag", userInfo[2]);
                        session("mmAuthToken", mmLoginToken);                        
                    }
                    catch(Exception exp) {
                        session("loggedUserOrg", "");
                        session("loginFlag", "Prod");
                        session("mmAuthToken", mmLoginToken);
                    }
                    //String uuid = UUID.randomUUID().toString();
                    //lm.setLastVisitedDate(uname);
                    if(loggedUser != null) {
                        return redirect(controllers.routes.HomeController.index());
                    }
                } else {
                    if(loggedUser == null) {
                        loggedUser = "";
                    }
                    flag = "unauth";
                    return redirect("/login");
                }
            }
            catch (Exception r) {
                r.printStackTrace();
            }
        }

        if(loggedUser != null) {          
            return redirect(controllers.routes.HomeController.index());
        }
        return login();
    }

    public Result logout() {
       
        String loggedUser = ctx().session().get("loggedUser");
        //session().remove("loggedUser");
        session().clear();
        //LoginModel lm = new LoginModel();
        //lm.updateUserSession(loggedUser);
        flag = "logout";
        return redirect("/"); 

    }

    /* --------------------------- admin login ------------------------------------------------*/

    public Result authenticateUser() {

        String client_username = "";
        String client_password = "";
        String client_role = "";
        String client_org = "";

        String statusCode = "";
        String statusMessage = "";
        String mmURL = "";
        String clientUserId = "";

        LoginModel lm = new LoginModel();

        String loggedUser = "";
        loggedUser = ctx().session().get("loggedUser");

        String remoteAddr = request().remoteAddress();
        java.util.Optional<java.lang.String> userAgentHeader = request().header("User-Agent");
        String userAgent = userAgentHeader.toString();
        
        DynamicForm requestData = formFactory.form().bindFromRequest();
        String uname = requestData.get("user_identity");
        String pwd = requestData.get("user_identity_password");
        client_password = pwd;

        if(uname.equals("")) {
            flag = "err-usr";
            return badRequest("Invalid Request. No Username Provided.");
        }
        else if(pwd.equals("")) {
            flag = "err-pwd";
            return badRequest("Invalid Request. No Password Provided.");
        }
        else {
            try {
                String roleInfo = lm.authenticate(config, uname, pwd, remoteAddr, userAgent);
                if(!roleInfo.equals("") && !roleInfo.equals(":::")) {
                    String[] userInfo = roleInfo.split(":::");
                    //session("loggedUser", uname);
                    //session("loggedUserRole", userInfo[0]);
                    client_username = uname;
                    client_role = userInfo[0];
                    try {
                        //session("loggedUserOrg", userInfo[1]);
                        client_org = userInfo[1];
                    }
                    catch(Exception exp) {
                        //session("loggedUserOrg", "");   
                        client_org = "";
                    }
                } else {
                    flag = "err-unauth";
                    return badRequest("Unauthorised Request.");
                }
            }
            catch (Exception r) {
                client_username = "";
                client_role = "";
                client_org = "";
                statusCode = "403";
                r.printStackTrace();
            }
        }

        flag = "err-success";

        MattermostServiceManager msm = new MattermostServiceManager(config);
        
        String mm_auth_token = msm.getAuthToken(client_username, client_password);


        LoginResponse loginResponse = new LoginResponse();

        loginResponse.setStatusCode("");
        loginResponse.setStatusMessage("");
        loginResponse.setUserName("");
        loginResponse.setUserRoles("");
        loginResponse.setUserOrganization("");
        loginResponse.setUserAuthToken("");
        loginResponse.setUserURL("");

        return ok("Login Success.");
    }


    /* --------------------------- client login -----------------------------------------------*/

    public Result authenticateClientUser() {

        String client_username = "";
        String client_password = "";
        String client_role = "";
        String client_role_types = "";
        String client_org = "";
        String client_user_emp_id = "";

        String client_first_name = "";
        String client_last_name = "";
        String client_email = "";
        String client_gender = "";
        String client_department = "";

        String statusCode = "";
        String statusMessage = "";
        String client_hisURL = "";
        String mmURL = "";
        String clientUserId = "";
        String mm_auth_token = "";
        String mmUserId = "";

        ArrayList<models.client.Channel> broadCastChannel = new ArrayList<models.client.Channel>();

        LoginModel lm = new LoginModel();
        MattermostServiceManager msm = new MattermostServiceManager(config);

        mmURL = msm.getMattermostURL();

        String loggedUser = "";
        loggedUser = ctx().session().get("loggedUser");

        String remoteAddr = request().remoteAddress();
        java.util.Optional<java.lang.String> userAgentHeader = request().header("User-Agent");
        String userAgent = userAgentHeader.toString();
        
        DynamicForm requestData = formFactory.form().bindFromRequest();
        String uname = "";
        String pwd = "";

        try {
            uname = requestData.get("user_identity");
            pwd = requestData.get("user_identity_key");
            //client_org = requestData.get("user_organization");
        } catch(Exception exception) {
            uname = "";
            pwd = "";
            client_org = "";
        }
        
        client_username = uname;//.toLowerCase();
        uname = uname.toLowerCase();
        client_password = pwd;

        if(uname.equals("")) {
            flag = "err";
            statusMessage = "Invalid Request. No Username Provided.";
            //return badRequest("Invalid Request. No Username Provided.");
        }
        else if(pwd.equals("")) {
            flag = "err";
            statusMessage = "Invalid Request. No Password Provided.";
            //return badRequest("Invalid Request. No Password Provided.");
        }
        else {
            try {
                String userInfo = lm.authenticateClientUser(config, uname, pwd, remoteAddr, userAgent);
                System.out.println("UserRoleInfo: " + userInfo);
                if(!userInfo.equals("") && !userInfo.equalsIgnoreCase("none:::none")) {
                    String[] userInfoArray = userInfo.split(":::");
                    flag = "success";
                    client_role = userInfoArray[0]; //Roles for the user
                    mmUserId = userInfoArray[1]; //Mattermost User ID
                    client_first_name = userInfoArray[2]; //Mattermost User ID
                    client_last_name = userInfoArray[3]; //Mattermost User ID
                    client_email = userInfoArray[4]; //Mattermost User ID
                    client_gender = userInfoArray[5]; //Mattermost User ID
                    client_department = userInfoArray[6]; //User's Department
                    client_role_types = userInfoArray[7]; //User's Role Types
                    client_hisURL = userInfoArray[8]; //User's Role Types
                    client_user_emp_id = userInfoArray[9];
                    client_org = userInfoArray[10]; //User's Organization
                    statusCode = "201";
                    statusMessage = "Authentication Successful.";
                    mm_auth_token = msm.getAuthToken(client_username, client_password);
                    lm.updateUserToken(config, client_org, uname, mm_auth_token);
                    String[] orgTokens = client_org.split("###");
                    if(orgTokens.length > 0) {
                        for(String orgToken: orgTokens) {
                            broadCastChannel.add(lm.getBroadcastChannel(config, orgToken));
                        }                        
                    } else {
                        broadCastChannel.add(lm.getBroadcastChannel(config, client_org));
                    }
                    //broadCastChannel = lm.getBroadcastChannel(config, client_org);
                } else {
                    flag = "err";
                    statusMessage = "Invalid Credentials.";
                    //return badRequest("Unauthorised Request.");
                }
            }
            catch (Exception r) {
                flag = "err";
                statusMessage = "Invalid Credentials.";
                r.printStackTrace();
            }
        }

        if(flag.equalsIgnoreCase("err")) {
            client_role = "";
            statusCode = "400";
            mmURL = "";
            mm_auth_token = "";
            client_username = "";
            client_org = "";
            client_first_name = "";
            client_last_name = "";
            client_email = "";
            client_gender = "";
            client_department = "";
            client_role_types = "";
            client_hisURL = "";
            client_user_emp_id = "";
        }

        LoginResponse loginResponse = new LoginResponse();

        loginResponse.setStatusCode(statusCode);
        loginResponse.setStatusMessage(statusMessage);
        loginResponse.setUserName(client_username);
        loginResponse.setUserRoles(client_role);
        loginResponse.setUserRoleTypes(client_role_types);
        loginResponse.setUserOrganization(client_org);
        loginResponse.setUserAuthToken(mm_auth_token);
        loginResponse.setUserURL(mmURL);
        loginResponse.setHisURL(client_hisURL);
        loginResponse.setUserId(mmUserId);
        loginResponse.setBroadCastChannel(broadCastChannel);
        loginResponse.setUserFirstname(client_first_name);
        loginResponse.setUserLastname(client_last_name);
        loginResponse.setUserEmail(client_email);
        loginResponse.setUserGender(client_gender);
        loginResponse.setUserDepartment(client_department);
        loginResponse.setEmployeeId(client_user_emp_id);

        Gson gson = new Gson();
        String responseString = gson.toJson(loginResponse);

        return ok(responseString);
    }

    /*public generateAuthToken(LoginResponse loginResponse) {
        
    }*/

    public Result home() {
         String loggedUser = ctx().session().get("loggedUser");
         
         if(loggedUser != null) {          
             return  redirect("/docs/swagger-ui/index.html?url=/assets/swagger.json"); 
         }
         return badRequest(views.html.login.render(titleMessage, loggedUser, flag));
    }


}
