package models;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import models.entity.ReportLog;
import models.entity.SurveyHeader;
import play.db.jpa.JPAApi;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class JPAReportLogRepository<T> implements ReportLogRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;
    @Inject
    public JPAReportLogRepository(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }

    @Override
    public CompletionStage<ReportLog> addReportLog(ReportLog reportLog) {
        return supplyAsync(() -> wrap(em -> insert(em, reportLog)), executionContext);
    }

    @Override
    public CompletionStage<List<ReportLog>> getReportLogs() {
        return supplyAsync(() -> wrap(this::getAllReporLogs),executionContext);
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

    private List<ReportLog> getAllReporLogs(EntityManager em){
        return em.createQuery("select r from ReportLog r ORDER BY r.createdDateTime desc", ReportLog.class).getResultList();
    }
}
