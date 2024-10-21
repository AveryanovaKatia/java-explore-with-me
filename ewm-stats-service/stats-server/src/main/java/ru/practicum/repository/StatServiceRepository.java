package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.StatResponseDto;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatServiceRepository extends JpaRepository<Stat, Long> {

    @Query("SELECT new ru.practicum.StatResponseDto(s.ip, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stat s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.ip, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<StatResponseDto> findAllWithUniqueIp(@Param("start") final LocalDateTime start,
                                              @Param("end") final LocalDateTime end);

    @Query("SELECT new ru.practicum.StatResponseDto(s.ip, s.uri, COUNT(s.ip)) " +
            "FROM Stat s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.ip, s.uri " +
            "ORDER BY COUNT(s.ip) DESC ")
    List<StatResponseDto> findAllWithCount(@Param("start") final LocalDateTime start,
                                           @Param("end") final LocalDateTime end);

    @Query("SELECT new ru.practicum.StatResponseDto(s.ip, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stat s " +
            "WHERE s.timestamp BETWEEN :start AND :end AND s.uri IN :uris " +
            "GROUP BY s.ip, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC ")
    List<StatResponseDto> findAllWithUrisUniqueIp(@Param("start") final LocalDateTime start,
                                                  @Param("end") final LocalDateTime end,
                                                  @Param("uris") final List<String> uris);

    @Query("SELECT new ru.practicum.StatResponseDto(s.ip, s.uri, COUNT(s.ip)) " +
            "FROM Stat s " +
            "WHERE s.timestamp BETWEEN :start AND :end AND s.uri IN :uris " +
            "GROUP BY s.ip, s.uri " +
            "ORDER BY COUNT(s.ip) DESC ")
    List<StatResponseDto> findAllWithUris(@Param("start") final LocalDateTime start,
                                          @Param("end") final LocalDateTime end,
                                          @Param("uris")final List<String> uris);

}