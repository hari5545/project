package models;

import javax.inject.Inject;

import play.mvc.*;
import play.libs.ws.*;
import java.util.concurrent.CompletionStage;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import java.util.Optional;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.temporal.ChronoUnit;
import java.time.Duration;
import models.mattermost.*;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

public class WSAdapter implements WSBodyReadables, WSBodyWritables {

  //WSClient ws;

  private String mm_login_token;

  WSRequest mmRequest;

  Gson gson;
  Gson customGson;

  public WSAdapter() {
    mm_login_token = "";
    gson = new Gson();
    customGson = gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
  }

  public void login(WSClient ws, String url, String username, String password) {
  	
  	try {
  		mmRequest = ws.url(url);

		WSRequest wsRequest = mmRequest
							        .addHeader("Content-Type", "application/json")
							        .setRequestTimeout(Duration.of(5000, ChronoUnit.MILLIS));

		JsonNode loginJson = Json.newObject().put("login_id", username).put("password", password);

		CompletionStage<WSResponse> responsePromise = wsRequest.post(loginJson);

		/*String bodyJson = responsePromise.getBody();
		MMUser mmUser = gson.fromJson(bodyJson, MMUser.class);*/

		/*CompletionStage<String> responseString = responsePromise.thenApply(
			response ->  {
				Optional<String> headers = response.getSingleHeader("Token");
				mm_login_token = headers.get();
				//response.getSingleHeader("Token");
			});*/
		WSResponse wsRes = responsePromise.toCompletableFuture().get();
		Optional<String> headers = wsRes.getSingleHeader("Token");
		mm_login_token = headers.get();

		System.out.println(mm_login_token);

		
  	}
  	catch(Exception exp) {
  		exp.printStackTrace();
  		mm_login_token = "";
  	}

  	//return mm_login_token;

  }

  public String createTeam(WSClient ws, String url, String teamName, String displayName, String teamType) {
	
	String teamId = "";

  	try {
  		mmRequest = ws.url(url);

		WSRequest wsRequest = mmRequest
							        .addHeader("Content-Type", "application/json")
							        .addHeader("Authorization", "Bearer " + mm_login_token)
							        .setRequestTimeout(Duration.of(5000, ChronoUnit.MILLIS));

		JsonNode createTeamJson = Json.newObject()
									.put("name", teamName)
									.put("display_name", displayName)
									.put("type", teamType);

		System.out.println(createTeamJson);
		
		CompletionStage<WSResponse> responsePromise = wsRequest.post(createTeamJson);

		
		WSResponse wsRes = responsePromise.toCompletableFuture().get();
		String bodyJson = wsRes.getBody();

		System.out.println(bodyJson);

		MMCreateTeamResponse createTeamResponse = customGson.fromJson(bodyJson, MMCreateTeamResponse.class);

		teamId = createTeamResponse.getId();

  	}
  	catch(Exception exp) {
  		exp.printStackTrace();
  		teamId = "";
  	}

  	return teamId;

  }

  public String deleteTeam(WSClient ws, String url) {
	
	String response = "";

  	try {
  		mmRequest = ws.url(url);

		WSRequest wsRequest = mmRequest
							        .addHeader("Content-Type", "application/json")
							        .addHeader("Authorization", "Bearer " + mm_login_token)
							        .setRequestTimeout(Duration.of(5000, ChronoUnit.MILLIS));

		CompletionStage<WSResponse> responsePromise = wsRequest.delete();
		
		WSResponse wsRes = responsePromise.toCompletableFuture().get();
		JsonNode responseJson = wsRes.asJson();
		response = responseJson.get("status").asText("");

  	}
  	catch(Exception exp) {
  		exp.printStackTrace();
  		response = "";
  	}

  	return response;

  }


}
