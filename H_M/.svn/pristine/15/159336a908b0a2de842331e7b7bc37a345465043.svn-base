package models;

/*import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;
import java.io.IOException;
import com.google.cloud.translate.*;*/

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import play.libs.Json;

import models.translate.*;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;

import okhttp3.*;

import java.nio.file.Paths;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class TranslateText {

	private String API_KEY = "AIzaSyD5nrWYGanXQGMsAVsYuPuq5AXnumLYBms";
	final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static String readFile(String path, Charset encoding) throws IOException
	{
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}

	public TranslateText() {}

	public String simpleTranslate(ArrayList<String> keyList, ArrayList<String> valueList, String fromLang, String toLang) {
		String result = "";
		String translateResponse = "";
		Gson gson = new Gson();
		ArrayList<String> resultList = new ArrayList<String>();
		List<Translation> translatedList = new ArrayList<Translation>();
		HashMap<String, String> translateMap = new HashMap<String, String>();

		int i=0;

		ArrayList<String> tempKeyList = new ArrayList<String>();
		ArrayList<String> tempValueList = new ArrayList<String>();
		for(String key: keyList) {
			tempKeyList.add(key);
			tempValueList.add(valueList.get(i));
			i++;

			if(i%100 == 0) {
				try {
			    	translatedList = translate100Values(tempKeyList, tempValueList, fromLang, toLang);	
			    }
			    catch(Exception exp) {
			    	exp.printStackTrace();
			    }

		  		for (int j=0; j< tempKeyList.size(); j++) {
		  			System.out.println(tempKeyList.get(j));
		  			System.out.println(translatedList.get(j));
		  			translateMap.put(tempKeyList.get(j), translatedList.get(j).getTranslatedText());
		  		}

		  		tempKeyList = new ArrayList<String>();
				tempValueList = new ArrayList<String>();
			}
		}

		if(tempKeyList.size() > 0) {
			try {
		    	translatedList = translate100Values(tempKeyList, tempValueList, fromLang, toLang);	
		    }
		    catch(Exception exp) {
		    	exp.printStackTrace();
		    }

	  		for (int j=0; j< tempKeyList.size(); j++) {
	  			System.out.println(tempKeyList.get(j));
	  			System.out.println(translatedList.get(j));
	  			translateMap.put(tempKeyList.get(j), translatedList.get(j).getTranslatedText());
	  		}
		}
		
		translateResponse = gson.toJson(translateMap);					

		//System.out.println("Source Text: "+text2Translate);
		//System.out.println("Translated Text: "+result);

		return translateResponse;
	}

	public String translate(ArrayList<String> text2Translate, String fromLang, String toLang) throws IOException {
        // INSERT YOU URL HERE
        String urlStr = "https://translation.googleapis.com/language/translate/v2?key="+this.API_KEY +
                "?q=" + URLEncoder.encode(text2Translate.toString(), "UTF-8") +
                "&target=" + toLang +
                "&source=" + fromLang;
        URL url = new URL(urlStr);
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

	public String translateValues(ArrayList<String> keyList, ArrayList<String> text2Translate, String fromLang, String toLang) throws Exception {
		String translateResponse = "";
		OkHttpClient client = new OkHttpClient();

		Gson gson = new Gson();

		String urlStr = "https://translation.googleapis.com/language/translate/v2?key="+this.API_KEY;

		logger.debug(urlStr);

		TranslationRequest translateReq = new TranslationRequest(text2Translate, fromLang, toLang);
									
		String translateJson = gson.toJson(translateReq);

		logger.debug(translateJson);

		RequestBody body = RequestBody.create(
	      MediaType.parse("application/json; charset=utf-8"), translateJson);
	 
	    Request request = new Request.Builder()
	      .url(urlStr)
	      .addHeader("Content-Type", "application/json")//.addHeader("Authorization", "Bearer " + mm_login_token)
	      .post(body)
	      .build();

	    Call call = client.newCall(request);
	    Response response = call.execute();

	    if(response.code() == 200) {
	    	String bodyJson = response.body().string();
	    	logger.debug(bodyJson);

	  		TranslationResponse translateResp = gson.fromJson(bodyJson, TranslationResponse.class);
	  		System.out.println(translateResp);
	  		List<Translation> translatedList = translateResp.getData().getTranslations();

	  		HashMap<String, String> translateMap = new HashMap<String, String>(); 

	  		for (int i=0; i< keyList.size(); i++) {
	  			System.out.println(keyList.get(i));
	  			System.out.println(translatedList.get(i));
	  			translateMap.put(keyList.get(i), translatedList.get(i).getTranslatedText());
	  		}

	  		translateResponse = gson.toJson(translateMap);

	    } else {
	    	translateResponse = "";
	    	logger.debug(response.body().string());
	    }

		return translateResponse;
	}

	public List<Translation> translate100Values(ArrayList<String> keyList, ArrayList<String> text2Translate, String fromLang, String toLang) throws Exception {
		String translateResponse = "";
		OkHttpClient client = new OkHttpClient();

		Gson gson = new Gson();

		String urlStr = "https://translation.googleapis.com/language/translate/v2?key="+this.API_KEY;

		logger.debug(urlStr);

		TranslationRequest translateReq = new TranslationRequest(text2Translate, fromLang, toLang);
									
		String translateJson = gson.toJson(translateReq);

		logger.debug(translateJson);

		RequestBody body = RequestBody.create(
	      MediaType.parse("application/json; charset=utf-8"), translateJson);
	 
	    Request request = new Request.Builder()
	      .url(urlStr)
	      .addHeader("Content-Type", "application/json")//.addHeader("Authorization", "Bearer " + mm_login_token)
	      .post(body)
	      .build();

	    Call call = client.newCall(request);
	    Response response = call.execute();

	    if(response.code() == 200) {
	    	String bodyJson = response.body().string();
	    	logger.debug(bodyJson);

	  		TranslationResponse translateResp = gson.fromJson(bodyJson, TranslationResponse.class);
	  		System.out.println(translateResp);
	  		List<Translation> translatedList = translateResp.getData().getTranslations();

	  		return translatedList;

	  		/*HashMap<String, String> translateMap = new HashMap<String, String>(); 

	  		for (int i=0; i< keyList.size(); i++) {
	  			System.out.println(keyList.get(i));
	  			System.out.println(translatedList.get(i));
	  			translateMap.put(keyList.get(i), translatedList.get(i).getTranslatedText());
	  		}

	  		translateResponse = gson.toJson(translateMap);*/

	    }

		return new ArrayList<Translation>();
	}

	/*public void simpleTranslate() {
		System.setProperty("GOOGLE_API_KEY", "AIzaSyD5nrWYGanXQGMsAVsYuPuq5AXnumLYBms");

		Translate translate = TranslateOptions.getDefaultInstance().getService();

		com.google.cloud.translate.Translation translation = translate.translate("¡Hola Mundo!");
		System.out.printf("Translated Text:\n\t%s\n", translation.getTranslatedText());

		translation =
    		translate.translate(
		        "Hola Mundo!",
		        Translate.TranslateOption.sourceLanguage("es"),
		        Translate.TranslateOption.targetLanguage("ta"),
		        // Use "base" for standard edition, "nmt" for the premium model.
		        Translate.TranslateOption.model("base"));

		System.out.printf("TranslatedText:\nText: %s\n", translation.getTranslatedText());

	}

	public void simpleTranslate() {
		System.setProperty("GOOGLE_API_KEY", "AIzaSyD5nrWYGanXQGMsAVsYuPuq5AXnumLYBms");

		Translate translate = TranslateOptions.getDefaultInstance().getService();

		com.google.cloud.translate.Translation translation = translate.translate("¡Hola Mundo!");
		System.out.printf("Translated Text:\n\t%s\n", translation.getTranslatedText());

		translation =
    		translate.translate(
		        "Hola Mundo!",
		        Translate.TranslateOption.sourceLanguage("es"),
		        Translate.TranslateOption.targetLanguage("ta"),
		        // Use "base" for standard edition, "nmt" for the premium model.
		        Translate.TranslateOption.model("base"));

		System.out.printf("TranslatedText:\nText: %s\n", translation.getTranslatedText());

	}

	public void translate() {
		String projectId = "AIzaSyD5nrWYGanXQGMsAVsYuPuq5AXnumLYBms";
	    // Supported Languages: https://cloud.google.com/translate/docs/languages
	    String targetLanguage = "ta";
	    String text = "sakthi";
	    try {
	    	translateText(projectId, targetLanguage, text);	
	    }
	    catch(Exception exp) {
	    	exp.printStackTrace();
	    }
	    
	}

	public static void translateText(String projectId, String targetLanguage, String text) throws IOException {

		// Initialize client that will be used to send requests. This client only needs to be created
		// once, and can be reused for multiple requests. After completing all of your requests, call
		// the "close" method on the client to safely clean up any remaining background resources.
		try (TranslationServiceClient client = TranslationServiceClient.create()) {
			// Supported Locations: `global`, [glossary location], or [model location]
			// Glossaries must be hosted in `us-central1`
			// Custom Models must use the same location as your model. (us-central1)
			LocationName parent = LocationName.of(projectId, "global");

			// Supported Mime Types: https://cloud.google.com/translate/docs/supported-formats
			TranslateTextRequest request =
					TranslateTextRequest.newBuilder()
						.setParent(parent.toString())
						.setMimeType("text/plain")
						.setTargetLanguageCode(targetLanguage)
						.addContents(text)
						.build();

			TranslateTextResponse response = client.translateText(request);

			// Display the translation for each input text provided
			for (Translation translation : response.getTranslationsList()) {
				System.out.printf("Translated text: %s\n", translation.getTranslatedText());
			}
		}
	}*/
}