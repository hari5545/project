package models;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import javax.inject.Inject;
import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import models.dto.ReturnSurveyResultDto;
import models.dto.SurveyDto;
import models.dto.SurveyHeaderDto;
import models.dto.SurveyResultDto;
import models.entity.SurveyDetail;
import models.entity.SurveyHeader;
import models.entity.SurveyResult;


import com.typesafe.config.Config;

//import javafx.scene.chart.PieChart.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBServiceManager {

	private final Config config;
	private final SurveyRepository surveyRepository;

	private  LDAPServiceManager ldapServiceManager;
	Gson gson; 

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject
	public DBServiceManager(SurveyRepository surveyRepository,Config config) {
		this.surveyRepository = surveyRepository;
		gson=new Gson();
		this.config = config;
		ldapServiceManager = new LDAPServiceManager(this.config);
	}


	//create survey
	public SurveyHeader createSurvey(SurveyDto surveyDto ) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException{       
		SurveyHeader surveyHeaders=null;
		try {
			// get ChannelId from Ldap server
			String channelId=ldapServiceManager.searchTab(surveyDto.getTabName());
			if(channelId!=null) {
				logger.info("channelId",channelId);
				SurveyHeader surveyHeader=new SurveyHeader();
				surveyHeader.setOrganizationName(surveyDto.getOrgName());
				surveyHeader.setOrganizationUnitName(surveyDto.getOrgUnitName());
				surveyHeader.setChannel(surveyDto.getTabName());
				surveyHeader.setChannelId(channelId);
				surveyHeader.setStartDate(surveyDto.getStartDate());
				surveyHeader.setEndDate(surveyDto.getEndDate());
				surveyHeader.setSurveyName(surveyDto.getSurveyName() );
				surveyHeader.setSurveyDetails(surveyDto.getQuestion());
				CompletionStage<SurveyHeader> completionStageSurvey=surveyRepository.addSurvey(surveyHeader);
				surveyHeaders=completionStageSurvey.toCompletableFuture().get();
			}

		}catch(Exception e) {
			logger.error("Error Occurred!", e);
		}
		return surveyHeaders;       
	}	

	public List<SurveyHeaderDto> getSurveyList() throws InterruptedException, ExecutionException{
		List<SurveyHeaderDto> surveyHeaderDtoList=new ArrayList<SurveyHeaderDto>();
		try {
			CompletionStage<List<SurveyHeader>> headers=surveyRepository.getAllSurveyHeader();
			List<SurveyHeader> surveyHeaders=headers.toCompletableFuture().get();

			for(SurveyHeader surveyHeader:surveyHeaders) {

				SurveyHeaderDto  surveyDto=new  SurveyHeaderDto();
				surveyDto.setId(surveyHeader.getId());
				surveyDto.setOrganizationName(surveyHeader.getOrganizationName());
				surveyDto.setOrganizationUnitName(surveyHeader.getOrganizationUnitName());
				surveyDto.setChannel(surveyHeader.getChannel());
				surveyDto.setStartDate(surveyHeader.getStartDate());
				surveyDto.setEndDate(surveyHeader.getEndDate());
				surveyDto.setSurveyName(surveyHeader.getSurveyName());
				surveyDto.setChannelId(surveyHeader.getChannelId());   
				surveyHeaderDtoList.add(surveyDto);
			}	 
		}
		catch(Exception e) {
			logger.error("Error Occurred!", e);
		}
		logger.debug("SurveyDto List {} " + surveyHeaderDtoList) ;
		return surveyHeaderDtoList;
	}

	public List<SurveyHeader> getSurveyList(String surveyName) throws InterruptedException, ExecutionException{
		List<SurveyHeader> surveyHeaderList=new ArrayList<SurveyHeader>();
		try {
			CompletionStage<List<SurveyHeader>> headers=surveyRepository.getSurvey(surveyName); 
			List<SurveyHeader> surveyHeaders=headers.toCompletableFuture().get();
			surveyHeaderList=surveyHeaders.stream().map(s->s).collect(Collectors.toList()); 
		}
		catch(Exception e) {
			logger.error("Error Occurred!", e);
		}
		logger.debug("SurveyHeader List {} " + surveyHeaderList);
		return surveyHeaderList;
	}

	//update survey 
	public SurveyHeader updateSurvey(SurveyDto surveyDto) throws NamingException, NoSuchAlgorithmException, UnsupportedEncodingException{
		CompletionStage<SurveyHeader> completionStageSurvey=null;
		SurveyHeader respSurveyHeader=null;
		try {
			// get ChannelId from Ldapserver
			String channelId=ldapServiceManager.searchTab(surveyDto.getTabName());

			if(channelId!=null) {
				logger.info("channelId",channelId);
				CompletionStage<SurveyHeader> oldSurveyHeader=surveyRepository.getSurveyHeader(surveyDto.getId());
				SurveyHeader  surveyHeaders=oldSurveyHeader.toCompletableFuture().get();
				//surveyRepository.deteteSurvey(surveyHeaders);
	
				surveyHeaders.setOrganizationName(surveyDto.getOrgName());
				surveyHeaders.setOrganizationUnitName(surveyDto.getOrgUnitName());
				surveyHeaders.setChannel(surveyDto.getTabName());
				surveyHeaders.setSurveyName(surveyDto.getSurveyName());
				surveyHeaders.setChannelId(channelId);
				surveyHeaders.setStartDate(surveyDto.getStartDate());
				surveyHeaders.setEndDate(surveyDto.getEndDate());
				surveyHeaders.setSurveyDetails(surveyDto.getQuestion());
				completionStageSurvey=surveyRepository.updateSurvey(surveyHeaders);
				respSurveyHeader =completionStageSurvey.toCompletableFuture().get();
			}
		}catch(Exception e) { 
			logger.error("Error Occurred!", e);
		}
		return respSurveyHeader;
	}


	public Map<String, Map<List<String>,List<Long>>> getSurveyResultForRatingQuestions(String channelId){

		CompletionStage<List<SurveyResultDto>> results=null;
		List<SurveyResultDto> surveyHeaders =null;
		List<SurveyResultDto> surveyResultsList=null;
		Map<String, List<String>> uniqueMap=null;
		Map<String, Map<List<String>,List<Long>>> question=null;
		ReturnSurveyResultDto returnSurveyResultDto=null;
		
		try {
			results=surveyRepository.getSurveyResult(channelId); 
			surveyHeaders=results.toCompletableFuture().get();
			surveyResultsList=surveyHeaders.stream().filter(s->s.getType().contains("Rating")).collect(Collectors.toList()); 


			uniqueMap=new HashMap<String, List<String>>();
			question=new HashMap<String,Map<List<String>,List<Long>>>();

			for(SurveyResultDto surveyResultDto:surveyResultsList) {
				List<String> resultsString=findresults(surveyResultsList, surveyResultDto.getQuestion());
				uniqueMap.put(surveyResultDto.getQuestion(), resultsString);
			}
			for(Entry<String, List<String>> entry:uniqueMap.entrySet()) {
				Map<String, Long> mode = entry.getValue().stream()
						.collect(Collectors.groupingBy(value-> value,() -> new TreeMap<String, Long>(), Collectors.counting()));
				List<String> keyList=mode.entrySet().stream().map(m-> m.getKey()).collect(Collectors.toList());
				List<Long> valueList=mode.entrySet().stream().map(m->m.getValue()).collect(Collectors.toList());
				Map<List<String>,List<Long>> mapList=new HashMap<List<String>, List<Long>>();
				mapList.put(keyList, valueList);
				question.put(entry.getKey(),mapList); 
			};
		}
		catch(Exception e) {
			logger.error("Error Occurred!", e);
		}
		logger.debug("SurveyHeader List {} "+question);
		return question;
	}

	private List<String> findresults(List<SurveyResultDto>  sResultDto,String surveyQuestion) {
		return sResultDto.stream()
				.filter(s->s.getQuestion().contains(surveyQuestion)).map(s->s.getResult()).collect(Collectors.toList());

	}
    
	public List<ReturnSurveyResultDto> getSurveyResultsWithoutRatingQuestions(String channelId) {
		CompletionStage<List<SurveyResultDto>> results=null;
		List<SurveyResultDto> surveyHeaders =null;
		List<SurveyResultDto> surveyResultsList=null;
		List<ReturnSurveyResultDto> listResultDtos=null;
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		

		try {
			results=surveyRepository.getSurveyResult(channelId); 
			surveyHeaders=results.toCompletableFuture().get();
			surveyResultsList=surveyHeaders.stream().filter(s->!s.getType().equals("Rating")).collect(Collectors.toList()); 
			listResultDtos=new ArrayList<ReturnSurveyResultDto>();
			for(SurveyResultDto surveyResultDto:surveyResultsList) {
				ReturnSurveyResultDto resultDto=new ReturnSurveyResultDto();
				resultDto.setQuestion(surveyResultDto.getQuestion());
				resultDto.setAnswer(surveyResultDto.getResult());
				String userName=ldapServiceManager.searchUserName(surveyResultDto.getUserId());
				logger.info("usernmae"+userName);
				Date date = format.parse(surveyResultDto.getResultDate());
				resultDto.setUserName(userName);
				resultDto.setResultDate(date);
				listResultDtos.add(resultDto);
			}
		}
		catch(Exception e) {
			logger.error("Error Occurred!", e);
		}
		logger.debug("SurveyHeader List {} "+listResultDtos);
		return listResultDtos;

	}
}
