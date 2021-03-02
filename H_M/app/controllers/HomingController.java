
package controllers;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;

import models.DBServiceManager;
import models.JPAReportLogRepository;
import models.LDAPServiceManager;
import models.ReportLogRepository;
import models.SurveyRepository;
import models.dto.TabDto;
import models.entity.SurveyHeader;
import models.entity.SurveyResult;
import models.ldap.ResponseUserDto;
import models.ldap.Tab;
import models.ldap.UserDto;
import models.ldap.UsersDtos;
import models.entity.ReportLog;
import models.entity.SurveyDetail;
import models.client.ClientUser;



import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;
import static play.libs.Json.toJson;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.data.FormFactory;
import play.mvc.Http;
import com.typesafe.config.Config;

import javax.inject.Inject;
import javax.naming.NamingException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;




/**
 *
 * This class is responsible for creating API's and expose to external world.
 *
 */

public class HomingController extends Controller {

	private final FormFactory formFactory;
	private final SurveyRepository surveyRepository;
	private final ReportLogRepository reportLogRepository;
	private final HttpExecutionContext ec;
	private final LDAPServiceManager ldapServiceManager;
	private final Config config;
	private final Gson gson;
	ObjectMapper objectMapper;


	@Inject
	public HomingController(SurveyRepository surveyRepository,ReportLogRepository reportLogRepository,FormFactory formFactory,HttpExecutionContext ec,Config config) {
		this.config = config;
		this.surveyRepository = surveyRepository;
		this.reportLogRepository=reportLogRepository;
		ldapServiceManager = new LDAPServiceManager(this.config);
		gson=new Gson();
		this.objectMapper= new ObjectMapper();
		this.ec = ec;
		this.formFactory=formFactory;
	}


	 /**
	    * Get SurveyDetails based on channel Id
	    *
	    * @param channelId channel Id.
	    * @return return list of survey Details. 
	 */
	public CompletionStage<Result> getSurveyDetailsBasedOnChannelId(String channelId) {
		try {
			return surveyRepository .getSurveyDetailsBasedOnChannelId(channelId)
					.thenApplyAsync(surveyHeader ->ok(toJson(surveyHeader.flatMap(s->s.getSurveyDetails().stream().filter(obj->obj.isActive()==true).sorted(( obj1,  obj2) ->(obj1.getId() - obj2.getId())))
							.collect(Collectors.toList()))),ec.current());
		}catch (Exception e) {
			e.printStackTrace();
			return supplyAsync(() -> badRequest("channelId not found"));
		}
	}

	 /**
	    * create survey Results in database 
	    * @return return success. 
	 */
	public CompletionStage<Result> createSurveyResults() {
		 Http.Request request = request();
		 JsonNode json = request.body().asJson();
		SurveyResult surveyResult = Json.fromJson(json, SurveyResult.class);
		return surveyRepository
				.saveSurveyResult(surveyResult)
				.thenApplyAsync(results -> ok("survey results stored sucessfully"), ec.current());
	}
	
	
	 /**
	    * create new channel in MM 
	    * @return return success. 
	 */
	//create channel in MM
	public CompletionStage<Result> createTab() throws NamingException,NoSuchAlgorithmException,UnsupportedEncodingException  {
		 Http.Request request = request();
		 JsonNode json = request.body().asJson();
		TabDto tab= Json.fromJson(json, TabDto.class);
		// String response="";
		try {
		ldapServiceManager.createTab(tab);
			//response = "Tab creation successful.";
			return supplyAsync(() ->ok("Tab creation successful."),ec.current());
		}
		catch(Exception exp) {
			exp.printStackTrace();
			//response = "Tab creation failed.";
			return supplyAsync(() ->badRequest("Tab creation failed."),ec.current());
		}
	}

	 /**
	    * create bulk users in MM and assign users to channel 
	    * @return return success. 
	 */
	public Result createUsers() throws NamingException,NoSuchAlgorithmException,UnsupportedEncodingException{
		try{
			 Http.Request request = request();
			JsonNode  json = request.body().asJson();
			String string = objectMapper.writeValueAsString(json);
			List<UsersDtos> userDto=gson.fromJson(string,new TypeToken<List<UsersDtos>>() {}.getType());
			List<ResponseUserDto> responseUserDto =ldapServiceManager.createUsersInJava8(userDto);
			return ok(toJson(responseUserDto));
		}
		catch (JsonParseException e) { 
			e.printStackTrace();
			return badRequest("Json parser failed.");
		}
		catch (JsonMappingException e) {
			e.printStackTrace();
			return badRequest("Json parser failed.");
		}
		catch (Exception e) {
			e.printStackTrace();
			return badRequest("users creation failed.");
		}
	}
	
	/*
	 * public CompletionStage<Result> deleteTeam(String orgName) throws
	 * NamingException,NoSuchAlgorithmException,UnsupportedEncodingException{ try{
	 * ldapServiceManager.deleteTeam(orgName); return
	 * supplyAsync(()->ok("deleted "),ec.current()); }
	 * 
	 * catch (Exception e) { e.printStackTrace(); return
	 * supplyAsync(()->badRequest(" failed."),ec.current()); } }
	 */


	/**
	* Check the user is exist in LDAP
	**/
	public Result resetPassword() throws NamingException,NoSuchAlgorithmException,UnsupportedEncodingException{
		try{
			 Http.Request request = request();
			JsonNode  json = request.body().asJson();
			String string = objectMapper.writeValueAsString(json);
			ClientUser clientUser=gson.fromJson(string,ClientUser.class);
			String response = ldapServiceManager.resetPassword(clientUser);
			return ok(response);
		}
		catch (JsonParseException e) { 
			e.printStackTrace();
			return badRequest("Json parser failed.");
		}
		catch (JsonMappingException e) {
			e.printStackTrace();
			return badRequest("Json parser failed.");
		}
		catch (Exception e) {
			e.printStackTrace();
			return badRequest("users creation failed.");
		}
	}


	
	 /**
	    * create error logs in database 
	    * @return return success. 
	 */
  	//Record Logs
	public CompletionStage<Result> reportError(final Http.Request request) throws Exception  {

		JsonNode json = request.body().asJson();
		java.util.Optional<java.lang.String> header = request.header("username");
		String reportedUser = "Unknown";
		try {
			reportedUser = header.get();
		}catch(Exception exp) {
			reportedUser = "Unknown";
		}

		java.util.Date currentDateTime = new java.util.Date();
		/*java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(currentDateTime);*/

		System.out.println("Reported User ::::: "+reportedUser);
		ReportLog reportLog = Json.fromJson(json, ReportLog.class);
		reportLog.setReportedUser(reportedUser);
		reportLog.setCreatedDateTime(new java.sql.Timestamp(currentDateTime.getTime()));
		return reportLogRepository
				.addReportLog(reportLog)
				.thenApplyAsync(results -> ok("Error Log Reported"), ec.current());
	}

	public Result isServerRunning() {
		return ok("OK");
	}
}