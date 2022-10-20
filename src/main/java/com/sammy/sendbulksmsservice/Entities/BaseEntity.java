package com.sammy.sendbulksmsservice.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/***
 *
 * @version 2.0
 */
@MappedSuperclass
@Getter
@Setter
public class BaseEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "created_on",updatable = false)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime createdOn;
    @Column(name = "softDelete", columnDefinition = "char(1) default 0")
    private boolean softDelete;

    @PrePersist
    public void addData() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        ZoneId zoneId = ZoneId.of("Africa/Nairobi");
        ZonedDateTime zambia_zone = zonedDateTime.withZoneSameInstant(zoneId);
        this.createdOn = zambia_zone.toLocalDateTime();
        this.softDelete = false;
    }
}
