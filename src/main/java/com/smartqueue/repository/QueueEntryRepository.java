package com.smartqueue.repository;

import com.smartqueue.entity.QueueEntry;
import com.smartqueue.entity.QueueStatus;
import com.smartqueue.entity.ServiceEntity;
import com.smartqueue.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {

    List<QueueEntry> findByQueueDateOrderByJoinTimeAsc(LocalDate queueDate);

    List<QueueEntry> findByServiceAndQueueDateOrderByJoinTimeAsc(ServiceEntity service, LocalDate queueDate);

    List<QueueEntry> findByUserOrderByJoinTimeDesc(User user);

    List<QueueEntry> findByUserAndQueueDateOrderByJoinTimeDesc(User user, LocalDate queueDate);

    Optional<QueueEntry> findFirstByUserAndQueueDateAndStatusIn(User user, LocalDate queueDate, Collection<QueueStatus> statuses);

    Optional<QueueEntry> findFirstByUserAndServiceAndQueueDateAndStatusIn(User user, ServiceEntity service, LocalDate queueDate, Collection<QueueStatus> statuses);

    long countByServiceAndQueueDate(ServiceEntity service, LocalDate queueDate);

    Optional<QueueEntry> findFirstByQueueDateAndStatusOrderByJoinTimeAsc(LocalDate queueDate, QueueStatus status);

    Optional<QueueEntry> findFirstByServiceAndQueueDateAndStatusOrderByJoinTimeAsc(ServiceEntity service, LocalDate queueDate, QueueStatus status);

    @Query("SELECT COUNT(q) FROM QueueEntry q WHERE q.service = :service AND q.queueDate = :queueDate AND q.status = 'WAITING' AND q.joinTime < :joinTime")
    long countPeopleAhead(@Param("service") ServiceEntity service, @Param("queueDate") LocalDate queueDate, @Param("joinTime") LocalDateTime joinTime);

    @Query("SELECT COUNT(q) FROM QueueEntry q WHERE q.queueDate = :queueDate AND q.status = 'WAITING' AND q.joinTime < :joinTime")
    long countGlobalPeopleAhead(@Param("queueDate") LocalDate queueDate, @Param("joinTime") LocalDateTime joinTime);

    long countByStatus(QueueStatus status);

    long countByQueueDateAndStatus(LocalDate queueDate, QueueStatus status);

    @Query("SELECT q FROM QueueEntry q WHERE q.queueDate = :queueDate AND q.status IN :statuses ORDER BY q.joinTime ASC")
    List<QueueEntry> findActiveEntriesForDate(@Param("queueDate") LocalDate queueDate, @Param("statuses") Collection<QueueStatus> statuses);

    @Query("SELECT q FROM QueueEntry q WHERE q.service = :service AND q.queueDate = :queueDate AND q.status IN :statuses ORDER BY q.joinTime ASC")
    List<QueueEntry> findActiveEntriesForServiceAndDate(@Param("service") ServiceEntity service, @Param("queueDate") LocalDate queueDate, @Param("statuses") Collection<QueueStatus> statuses);
}
