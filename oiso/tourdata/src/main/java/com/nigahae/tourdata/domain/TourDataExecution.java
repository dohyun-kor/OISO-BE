package com.nigahae.tourdata.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "tour_ingest_execution")
public class TourDataExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TourDataStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false, length = 30)
    private TourDataJobType jobType;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Getter
    @Column(name = "processed_count", nullable = false)
    private int processedCount;

    @Column(name = "failed_count", nullable = false)
    private int failedCount;

    @Column(name = "failure_message", columnDefinition = "TEXT")
    private String failureMessage;

    protected TourDataExecution() {
    }

    private TourDataExecution(TourDataJobType jobType, LocalDateTime startedAt) {
        this.jobType = jobType;
        this.status = TourDataStatus.RUNNING;
        this.startedAt = startedAt;
        this.processedCount = 0;
        this.failedCount = 0;
    }

    public static TourDataExecution started(TourDataJobType jobType, LocalDateTime startedAt) {
        return new TourDataExecution(jobType, startedAt);
    }

    public void succeed(int processedCount, LocalDateTime finishedAt) {
        this.status = TourDataStatus.SUCCESS;
        this.processedCount = processedCount;
        this.failedCount = 0;
        this.finishedAt = finishedAt;
    }

    public void partialSuccess(int processedCount, int failedCount, String failureMessage, LocalDateTime finishedAt) {
        this.status = TourDataStatus.PARTIAL_SUCCESS;
        this.processedCount = processedCount;
        this.failedCount = failedCount;
        this.failureMessage = failureMessage;
        this.finishedAt = finishedAt;
    }

    public void fail(int failedCount, String failureMessage, LocalDateTime finishedAt) {
        this.status = TourDataStatus.FAILED;
        this.failedCount = failedCount;
        this.failureMessage = failureMessage;
        this.finishedAt = finishedAt;
    }

}
