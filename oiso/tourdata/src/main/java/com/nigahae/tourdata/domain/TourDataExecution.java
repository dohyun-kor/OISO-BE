package com.nigahae.tourdata.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Getter
    @Column(name = "processed_count", nullable = false)
    private int processedCount;

    @Lob
    @Column(name = "failure_message")
    private String failureMessage;

    protected TourDataExecution() {
    }

    private TourDataExecution(LocalDateTime startedAt) {
        this.status = TourDataStatus.RUNNING;
        this.startedAt = startedAt;
        this.processedCount = 0;
    }

    public static TourDataExecution started(LocalDateTime startedAt) {
        return new TourDataExecution(startedAt);
    }

    public void succeed(int processedCount, LocalDateTime finishedAt) {
        this.status = TourDataStatus.SUCCESS;
        this.processedCount = processedCount;
        this.finishedAt = finishedAt;
    }

    public void fail(String failureMessage, LocalDateTime finishedAt) {
        this.status = TourDataStatus.FAILED;
        this.failureMessage = failureMessage;
        this.finishedAt = finishedAt;
    }

}
