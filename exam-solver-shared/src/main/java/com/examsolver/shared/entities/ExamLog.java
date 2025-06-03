package com.examsolver.shared.entities;

import com.examsolver.shared.enums.ExamStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exam_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExamStatus status;

    @Column(name = "message")
    private String message;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
