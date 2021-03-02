package models;

import com.google.inject.ImplementedBy;

import java.util.List;
import java.util.concurrent.CompletionStage;

import models.entity.ReportLog;

@ImplementedBy(JPAReportLogRepository.class)
public interface ReportLogRepository {
    public CompletionStage<ReportLog> addReportLog(ReportLog reportLog);
    public CompletionStage<List<ReportLog>> getReportLogs();
}
