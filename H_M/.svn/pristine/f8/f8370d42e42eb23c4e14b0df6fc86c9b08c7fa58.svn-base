package models;

import com.google.inject.ImplementedBy;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import models.dto.SurveyResultDto;
import models.entity.SurveyHeader;
import models.entity.SurveyResult;

@ImplementedBy(JPASurveyRepository.class)
public interface SurveyRepository {
	public CompletionStage<SurveyHeader> addSurvey(SurveyHeader surveyHeader);
	public CompletionStage<SurveyHeader> updateSurvey(SurveyHeader surveyHeader);
	public CompletionStage<Stream<SurveyHeader>>  getSurveyDetailsBasedOnChannelId(String channelId);
    public CompletionStage<SurveyResult> saveSurveyResult(SurveyResult surveyResult);
    
    public CompletionStage<List<SurveyHeader>> getAllSurveyHeader();
    
    public CompletionStage<List<SurveyHeader>> getSurvey(String surveyName);
    
    public CompletionStage<SurveyHeader> getSurveyHeader(int id);
    
    public CompletionStage<SurveyHeader> deteteSurvey(SurveyHeader surveyHeader);
     
    public CompletionStage<List<SurveyResultDto>> getSurveyResult(String channelId);
      
    
}




