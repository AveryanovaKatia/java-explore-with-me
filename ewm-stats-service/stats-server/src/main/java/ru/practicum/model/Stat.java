package ru.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "stat")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode()
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Stat {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "app")
    String app;

    @Column(name = "ip")
    String ip;

    @Column(name = "time_stamp")
    LocalDateTime timestamp;

    @Column(name = "uri")
    String uri;
}