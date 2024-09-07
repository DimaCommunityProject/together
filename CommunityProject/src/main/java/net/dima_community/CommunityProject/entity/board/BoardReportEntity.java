package net.dima_community.CommunityProject.entity.board;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

<<<<<<< HEAD
=======

>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
<<<<<<< HEAD
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
=======
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.dima_community.CommunityProject.dto.board.BoardReportDTO;
import net.dima_community.CommunityProject.dto.board.check.ReportCategory;

@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
<<<<<<< HEAD
// @ToString
=======
@ToString
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
@Builder
@Entity
@Table(name = "board_report")
public class BoardReportEntity {
<<<<<<< HEAD
    @Id
    @Column(name = "report_id")
    private Long reportId;

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @ToString.Exclude
=======
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;
    
    // FK (1:1)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
    private BoardEntity boardEntity;

    @Column(name = "member_id", nullable = false)
    private String memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ReportCategory category;
<<<<<<< HEAD

=======
    
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
    private String reason;

    @Column(name = "report_date")
    @CreationTimestamp
    private LocalDateTime reportDate;

<<<<<<< HEAD
    public static BoardReportEntity toEntity(BoardReportDTO dto, BoardEntity boardEntity) {
        return BoardReportEntity.builder()
                .reportId(dto.getReportId())
                .boardEntity(boardEntity)
                .memberId(dto.getMemberId())
                .category(dto.getCategory())
                .reason(dto.getReason())
                .reportDate(dto.getReportDate())
                .build();
=======
    public static BoardReportEntity toEntity (BoardReportDTO dto, BoardEntity boardEntity){
        return BoardReportEntity.builder()
            .reportId(dto.getReportId())
            .boardEntity(boardEntity)
            .memberId(dto.getMemberId())
            .category(dto.getCategory())
            .reason(dto.getReason())
            .reportDate(dto.getReportDate())
            .build();
>>>>>>> a4224afd34ed1d8265e54278692dfd9085c161cc
    }
}
