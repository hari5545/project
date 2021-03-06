package models;

import java.util.concurrent.CompletionStage;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.persistence.*;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;

import models.dto.SurveyResultDto;
import models.entity.SurveyHeader;
import models.entity.SurveyResult;
import models.utils.AppException;

public class JPASurveyRepository<T> implements SurveyRepository{

	private final JPAApi jpaApi;
	private final DatabaseExecutionContext executionContext;

	@Inject
	public JPASurveyRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
		this.jpaApi = jpaApi;
		this.executionContext = executionContext;
	}

	@Override
	public CompletionStage<SurveyHeader> addSurvey(SurveyHeader surveyHeader) {
		return supplyAsync(() -> wrap(em -> insert(em, surveyHeader)), executionContext);
	}

	@Override
	public CompletionStage<Stream<SurveyHeader>> getSurveyDetailsBasedOnChannelId(String channelId) {
		return supplyAsync(() -> wrap(em ->getData(em, channelId)),executionContext);
	}


	@Override
	public CompletionStage<SurveyResult> saveSurveyResult(SurveyResult surveyResult) {
		return supplyAsync(() -> wrap(em -> insert(em, surveyResult)), executionContext);
	}

	@Override
	public CompletionStage<List<SurveyHeader>> getAllSurveyHeader() {
		return supplyAsync(() -> wrap(em ->getAllSurveyHeader(em)),executionContext);
	}
	
	@Override
	public CompletionStage<List<SurveyHeader>> getSurvey(String surveyName) {
		return supplyAsync(() -> wrap(em -> getSurvey(em,surveyName)),executionContext );
	}
	
	@Override
	public CompletionStage<SurveyHeader> getSurveyHeader(int id) {
		
		return supplyAsync(() -> wrap(em -> em.find(SurveyHeader.class,id)),executionContext);
	}

	@Override
	public CompletionStage<SurveyHeader> updateSurvey(SurveyHeader surveyHeader) {
		return supplyAsync(() -> wrap(em -> updateData(em, surveyHeader)), executionContext);
	}
	
	
	@Override
	public CompletionStage<SurveyHeader> deteteSurvey(SurveyHeader surveyHeader) {
		return supplyAsync(() -> wrap(em -> deleteData(em, surveyHeader)), executionContext);
	}
	
	@Override
	public CompletionStage<List<SurveyResultDto>> getSurveyResult(String channelId) {
		return supplyAsync(() -> wrap(em -> getSurveyResult(em,channelId)),executionContext );
	}

	private <T> T wrap(Function<EntityManager, T> function) {
		return jpaApi.withTransaction(function);
	}

	private <T>  T insert(EntityManager em,T t) {
		em.persist(t);
		return t;
	}
	
	private <T>  T updateData(EntityManager em,T t) {
		em.merge(t);
		return t;
	}
	
	private <T>  T deleteData(EntityManager em,T t) {
		em.remove(t);
		return t;
	}
			
	private Stream<SurveyHeader> getData(EntityManager em,String channelId) {
		List<SurveyHeader> surveyHeader = em.createQuery("select s from SurveyHeader s WHERE s.channelId='"+ channelId +"'",SurveyHeader.class).getResultList();
		return surveyHeader.stream();
	}

	private List<SurveyHeader> getAllSurveyHeader(EntityManager em){
		List<SurveyHeader> surveyHeaders = em.createQuery("SELECT  s FROM SurveyHeader s ORDER BY s.id", SurveyHeader.class).getResultList();
		return surveyHeaders;
	}
	
	private List<SurveyHeader> getSurvey(EntityManager em,String surveyName) {
		List<SurveyHeader> surveyHeader = em.createQuery("select s from SurveyHeader s WHERE s.surveyName='"+ surveyName +"'",SurveyHeader.class).getResultList();
		return surveyHeader;
	}
	
	private List<SurveyResultDto> getSurveyResult(EntityManager em,String channelId) {
		
		List<Object[]> resultList=null;
		List<SurveyResultDto> surveyResultDtos=null;
		TypedQuery<Tuple> query=null;
	
		//query=em.createQuery("SELECT s FROM SurveyResult s  JOIN SurveyDetail sd ON s.channelId='"+channelId+"'"+" AND sd.id=s.surveyQuestionId",Tuple.class);
		query=em.createQuery("SELECT distinct s.channelId as channelId,s.userId as userId,s.result as result,s.surveyQuestionId as questionId ,s.createdDate as date,sd.question as question,sd.type as type FROM SurveyResult s  JOIN  SurveyDetail sd ON s.channelId='"+channelId+"'"+" AND sd.id=s.surveyQuestionId",Tuple.class);
		
		
		
		List<Tuple> results = query.getResultList();
		surveyResultDtos=new ArrayList<SurveyResultDto>();
		
		for (Tuple result : results) {
			SurveyResultDto surveyResultDto=new SurveyResultDto(); 
			surveyResultDto.setChannelId(String.valueOf(result.get("channelId")));
			surveyResultDto.setUserId(String.valueOf(result.get("userId")));
			surveyResultDto.setQuestionId(String.valueOf(result.get("questionId")));
			surveyResultDto.setResult(String.valueOf(result.get("result")));
			surveyResultDto.setQuestion(String.valueOf(result.get("question")));
			surveyResultDto.setType(String.valueOf(result.get("type"))); 
			surveyResultDto.setResultDate(String.valueOf(result.get("date")));
			surveyResultDtos.add(surveyResultDto);
		}
	return surveyResultDtos;
	
	}
}