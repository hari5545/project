package models;

import okhttp3.*;
import okio.ByteString;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.libs.Json;

import java.util.Optional;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.temporal.ChronoUnit;
import java.time.Duration;

import models.ldap.Tab;
import models.mattermost.*;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkAdapter {

	private OkHttpClient client;
	private String mm_login_token;

	Gson gson;
	Gson customGson;

	final Logger logger = LoggerFactory.getLogger(this.getClass());
	//Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);

	public NetworkAdapter() {
		client = new OkHttpClient();
		mm_login_token = "";
		gson = new Gson();
		customGson = gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	}

	public NetworkAdapter(String authToken) {
		client = new OkHttpClient();
		mm_login_token = authToken;
		gson = new Gson();
		customGson = gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	}

	public void login(String url, String username, String password) throws Exception{

		logger.debug("mm_token created\t"+mm_login_token);

		if(mm_login_token.isEmpty()) {
			logger.debug("login url \t"+url);
			String loginJson = "{\"login_id\":\"" + username + "\",\"password\":\"" + password + "\"}";

			RequestBody body = RequestBody.create(
					MediaType.parse("application/json; charset=utf-8"), loginJson);

			Request request = new Request.Builder()
					.url(url)
					.post(body)
					.build();

			Call call = client.newCall(request);
			Response response = call.execute();

			if(response.code() == 200) {
				mm_login_token = response.header("Token");
				logger.debug("mm_token\t"+mm_login_token);
			} else {
				mm_login_token = "";
			}
		}

	}

	public String clientLogin(String url, String username, String password) throws Exception{

		String mm_client_login_token = "";

		String loginJson = "{\"login_id\":\"" + username + "\",\"password\":\"" + password + "\"}";

		RequestBody body = RequestBody.create(
				MediaType.parse("application/json; charset=utf-8"), loginJson);

		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();

		if(response.code() == 200) {
			mm_client_login_token = response.header("Token");
			logger.debug(mm_client_login_token);
		} else {
			mm_client_login_token = "";
		}

		return mm_client_login_token;
	}

	//Creating a Team
	public String createTeam(String url, String teamName, String displayName, String teamType) throws Exception {
		String teamId = "";

		logger.debug(url);

		JsonNode createTeamJson = Json.newObject()
				.put("name", teamName)
				.put("display_name", displayName)
				.put("type", teamType);

		logger.debug(createTeamJson.toString());

		RequestBody body = RequestBody.create(
				MediaType.parse("application/json; charset=utf-8"), createTeamJson.toString());

		Request request = new Request.Builder()
				.url(url)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.post(body)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();

		if(response.code() == 201) {
			String bodyJson = response.body().string();
			logger.debug(bodyJson);
			MMCreateTeamResponse createTeamResponse = customGson.fromJson(bodyJson, MMCreateTeamResponse.class);
			teamId = createTeamResponse.getId();
		} else {
			teamId = "";
			logger.debug(response.body().string());
		}

		return teamId;
	}

	//Delete a Team - Soft delete
	public String deleteTeam(String url) throws Exception {

		String responseString = "";

		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

		String urlBuilt = urlBuilder.build().toString();

		Request request = new Request.Builder()
				.delete()
				.url(urlBuilt)
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();

		if(response.code() == 200) {
			String bodyJson = response.body().string();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode responseObj = mapper.readTree(bodyJson);
			responseString = responseObj.get("status").asText("");
		} else {
			responseString = "";
		}

		return responseString;
	}



	//Create Channel
	public String createChannel(String url, String...channelDetails) throws Exception {
		String channelId = "";

		logger.debug(url);

		JsonNode createTeamJson = Json.newObject()
				.put("team_id", channelDetails[0])
				.put("name", channelDetails[1])
				.put("display_name", channelDetails[2])
				.put("purpose", channelDetails[3])
				.put("header", channelDetails[4])
				.put("type", channelDetails[5]);

		logger.debug(createTeamJson.toString());

		RequestBody body = RequestBody.create(
				MediaType.parse("application/json; charset=utf-8"), createTeamJson.toString());

		Request request = new Request.Builder()
				.url(url)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.post(body)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();

		if(response.code() == 201) {
			String bodyJson = response.body().string();
			logger.debug(bodyJson);
			Channel createChannelResponse = customGson.fromJson(bodyJson, Channel.class);
			channelId = createChannelResponse.getId();
		} else {
			channelId = "";
			logger.debug(response.body().string());
		}

		return channelId;
	}

	//Delete a Channel - Soft delete (Archive)
	public String deleteChannel(String url) throws Exception {

		String responseString = "";

		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

		String urlBuilt = urlBuilder.build().toString();

		Request request = new Request.Builder()
				.delete()
				.url(urlBuilt)
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();

		if(response.code() == 200) {
			String bodyJson = response.body().string();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode responseObj = mapper.readTree(bodyJson);
			responseString = responseObj.get("status").asText("");
		} else {
			responseString = "";
		}

		return responseString;
	}

	//Create User 
	public String createUser(String url, String...userDetails) throws Exception {
		String userId = "";

		logger.debug(url);

		JsonNode createTeamJson = Json.newObject()
				.put("email", userDetails[0])
				.put("username", userDetails[1])
				.put("first_name", userDetails[2])
				.put("last_name", userDetails[3])
				.put("password", userDetails[4]);

		logger.debug(createTeamJson.toString());

		RequestBody body = RequestBody.create(
				MediaType.parse("application/json; charset=utf-8"), createTeamJson.toString());

		Request request = new Request.Builder()
				.url(url)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.post(body)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();

		if(response.code() == 201) {
			String bodyJson = response.body().string();
			logger.debug(bodyJson);
			User createUserResponse = customGson.fromJson(bodyJson, User.class);
			userId = createUserResponse.getId();
		} else {
			userId = "";
			logger.debug(response.body().string());
		}

		return userId;
	}


	//Delete a User - Deactivating a User
	public String deleteUser(String url) throws Exception {

		String responseString = "";

		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

		String urlBuilt = urlBuilder.build().toString();

		Request request = new Request.Builder()
				.delete()
				.url(urlBuilt)
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();

		if(response.code() == 200) {
			String bodyJson = response.body().string();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode responseObj = mapper.readTree(bodyJson);
			responseString = responseObj.get("status").asText("");
		} else {
			responseString = "";
		}

		return responseString;
	}


	//Add user into a team
	public String addUser2Team(String url, String teamId, String userId) throws Exception {
		String responseStr = "";

		logger.debug(url);

		JsonNode addUser2TeamJson = Json.newObject()
				.put("team_id", teamId)
				.put("user_id", userId);

		logger.debug(addUser2TeamJson.toString());

		RequestBody body = RequestBody.create(
				MediaType.parse("application/json; charset=utf-8"), addUser2TeamJson.toString());

		Request request = new Request.Builder()
				.url(url)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.post(body)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();

		if(response.code() == 201) {
			String bodyJson = response.body().string();
			logger.debug(bodyJson);
			//Channel createChannelResponse = customGson.fromJson(bodyJson, Channel.class);
			//channelId = createChannelResponse.getId();
			responseStr = bodyJson;
		} else {
			responseStr = response.body().string();
			logger.debug(responseStr);
		}

		return responseStr;
	}


	//Remove user from a Team
	public String removeUserFromTeam(String url) throws Exception {

		String responseString = "";

		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

		String urlBuilt = urlBuilder.build().toString();

		Request request = new Request.Builder()
				.delete()
				.url(urlBuilt)
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();

		if(response.code() == 200) {
			String bodyJson = response.body().string();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode responseObj = mapper.readTree(bodyJson);
			responseString = responseObj.get("status").asText("");
		} else {
			responseString = "";
		}

		return responseString;
	}


	//Add user into a Channel
	public String addUser2Channel(String url, String channelId, String userId) throws Exception {
		String responseStr = "";
		logger.debug(url);

		JsonNode addUser2ChannelJson = Json.newObject()
				.put("channel_id", channelId)
				.put("user_id", userId);

		logger.debug(addUser2ChannelJson.toString());

		RequestBody body = RequestBody.create(
				MediaType.parse("application/json; charset=utf-8"), addUser2ChannelJson.toString());

		Request request = new Request.Builder()
				.url(url)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.post(body)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();

		if(response.code() == 201) {
			String bodyJson = response.body().string();
			logger.debug(bodyJson);
			//Channel createChannelResponse = customGson.fromJson(bodyJson, Channel.class);
			//channelId = createChannelResponse.getId();
			responseStr = bodyJson;
		} else {
			responseStr = response.body().string();
			logger.debug(responseStr);
		}

		return responseStr;
	}


	//Remove user from a Channel
	public String removeUserFromChannel(String url) throws Exception {

		String responseString = "";

		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

		String urlBuilt = urlBuilder.build().toString();

		Request request = new Request.Builder()
				.delete()
				.url(urlBuilt)
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();

		if(response.code() == 200) {
			String bodyJson = response.body().string();
			logger.debug(bodyJson);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode responseObj = mapper.readTree(bodyJson);
			responseString = responseObj.get("status").asText("");
		} else {
			responseString = "";
		}

		return responseString;
	}


	//Validate User Token
	public String validateUserToken(String userId, String userAuthToken, String url) throws Exception {

		String responseString = "false";

		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

		String urlBuilt = urlBuilder.build().toString();

		Request request = new Request.Builder()
				.url(urlBuilt)
				.addHeader("Authorization", "Bearer " + userAuthToken)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();
		String bodyJson = response.body().string();
		logger.debug(bodyJson);

		if(response.code() == 200) {	
			ObjectMapper mapper = new ObjectMapper();
			JsonNode responseObj = mapper.readTree(bodyJson);
			String usrId = responseObj.get("id").asText("");
			if(usrId.equalsIgnoreCase(userId)) {
				responseString = "true";
			}
		} else {
			responseString = "false";
		}

		return responseString;
	}

	//Get Channel Members
	public String getChannelMembers(String channelId, String userAuthToken, String url) throws Exception {

		String responseString = "false";

		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

		String urlBuilt = urlBuilder.build().toString();

		Request request = new Request.Builder()
				.url(urlBuilt)
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();
		String bodyJson = response.body().string();
		logger.debug(bodyJson);

		if(response.code() == 200) {	
			//Channel createChannelResponse = customGson.fromJson(bodyJson, Channel.class);
			//channelId = createChannelResponse.getId();
			responseString = bodyJson;
		} else {
			responseString = "false";
		}

		return responseString;
	}


	//  post news  into channel

	public String  postChannelMessage(String url, String channelId, String message) throws Exception {
		String responseStr = "";
		logger.debug(url);


		JsonNode postChannelMessage = Json.newObject()
				.put("channel_id", channelId)
				.put("message", message);

		logger.debug(postChannelMessage.toString());

		RequestBody body = RequestBody.create(
				MediaType.parse("application/json; charset=utf-8"), postChannelMessage.toString());

		Request request = new Request.Builder()
				.url(url)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.post(body)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();

		if(response.code() == 201) {
			String bodyJson = response.body().string();
			logger.debug(bodyJson);
			//Channel createChannelResponse = customGson.fromJson(bodyJson, Channel.class);
			//channelId = createChannelResponse.getId();
			responseStr = bodyJson;
		} else {
			responseStr = response.body().string();
			logger.debug(responseStr);
		}

		return responseStr;
	}


	//  post news  into channel

	public String  resetPassword(String url, String new_password) throws Exception {
		String responseStr = "";
		logger.debug(url);


		JsonNode resetPasswordData = Json.newObject()
				.put("new_password", new_password);

		logger.debug(resetPasswordData.toString());

		RequestBody body = RequestBody.create(
				MediaType.parse("application/json; charset=utf-8"), resetPasswordData.toString());

		Request request = new Request.Builder()
				.url(url)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.put(body)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();
		int respCode = response.code();

		if(respCode == 200) {
			String bodyJson = response.body().string();
			logger.debug(bodyJson);
			//Channel createChannelResponse = customGson.fromJson(bodyJson, Channel.class);
			//channelId = createChannelResponse.getId();
			//responseStr = bodyJson;
			responseStr = String.valueOf(respCode);

		} else {
			responseStr = String.valueOf(respCode);
			logger.debug(responseStr);
		}

		return responseStr;
	}



	//Statistics Related APIs

	//Team Statistics
	public String getDashboardStatistics(String url) throws Exception {

		String responseString = "false";

		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

		String urlBuilt = urlBuilder.build().toString();

		Request request = new Request.Builder()
				.url(urlBuilt)
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();
		String bodyJson = response.body().string();
		//logger.debug(bodyJson);

		if(response.code() == 200) {	
			responseString = bodyJson;
		} else {
			responseString = "false";
		}

		return responseString;
	}


	public String getBroadcastCount(String url) throws Exception {

		String responseString = "0";

		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

		String urlBuilt = urlBuilder.build().toString();

		Request request = new Request.Builder()
				.url(urlBuilt)
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();
		String bodyJson = response.body().string();
		//logger.debug(bodyJson);

		if(response.code() == 200) {

			ChannelPosts channelPosts = gson.fromJson(bodyJson, ChannelPosts.class);
			System.out.println("Posts: "+channelPosts.getPostCount());

			int postCount = 0;

			if(channelPosts.getPostCount() > 0) {
				Object postListObject = channelPosts.getPostBlock();
				//LinkedTreeMap<String, Object> postList = (LinkedTreeMap) postListObject;
				String postPart = gson.toJson(postListObject);
				Type mapType = new TypeToken<Map<String, Post>>() {
				}.getType();

				ArrayList<String> postOrder = channelPosts.getOrder();

				Map<String, Post> postListMap = gson.fromJson(postPart, mapType);

				for (String postId : postOrder) {
					Post post = postListMap.get(postId);
					try {
						String postType = post.getType();
						if (postType.isEmpty()) {
							postCount++;
						}
					} catch (Exception exp) {
						exp.printStackTrace();
					}
				}
			}

			responseString = Integer.toString(postCount);

		} else {
			responseString = "0";
		}

		return responseString;
	}

	//updating  User 
	public String updateUser(String...userDetails) throws Exception {
		String userId = "";
		String url = userDetails[0];
		logger.debug(url);

		JsonNode updateUserJson = Json.newObject()
				.put("first_name", userDetails[1])
				.put("last_name", userDetails[2]);

		logger.debug(updateUserJson.toString());

		RequestBody body = RequestBody.create(
				MediaType.parse("application/json; charset=utf-8"), updateUserJson.toString());

		Request request = new Request.Builder()
				.url(url)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.put(body)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();

		if(response.code() == 200) {
			String bodyJson = response.body().string();
			logger.debug(bodyJson);
			User createUserResponse = customGson.fromJson(bodyJson, User.class);
			userId = createUserResponse.getId();
		} else {
			userId = "";
			logger.debug(response.body().string());
		}

		return userId;
	}

	//updating  User 
	public String updateTab(String tabId,String...tabDetails) throws Exception {
		String channelId = "";
		String url = tabDetails[0];
		logger.debug(url);

		JsonNode updateTabJson = Json.newObject()
				.put("id", tabId)
				.put("name", tabDetails[1].toLowerCase())
				.put("display_name",tabDetails[2])
				.put("header",tabDetails[3])
				.put("purpose",tabDetails[4]);

		logger.debug(updateTabJson.toString());

		RequestBody body = RequestBody.create(
				MediaType.parse("application/json; charset=utf-8"), updateTabJson.toString());

		Request request = new Request.Builder()
				.url(url)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + mm_login_token)
				.put(body)
				.build();

		Call call = client.newCall(request);
		Response response = call.execute();

		if(response.code() == 200) {
			String bodyJson = response.body().string();
			logger.debug(bodyJson);
			Channel updateChannelResponse = customGson.fromJson(bodyJson, Channel.class);
			channelId = updateChannelResponse.getId();
		} else {
			channelId = "";
			logger.debug(response.body().string());
		}
		return channelId;
	}

}