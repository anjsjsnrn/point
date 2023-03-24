package com.api.point.mysql.domain

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import java.time.ZonedDateTime


@Entity
@Table(name = "history")
class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "seq")
    var seq: Long? = null

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "barcode")
    var barcode: String? = null

    @Column(name = "flag")
    var flag: String? = null

    @Column(name = "market")
    var market: String? = null

    @Column(name = "market_name")
    var marketName: String? = null

    @Column(name = "mileage")
    var mileage: Long = 0

    @Column(name = "created_at")
    var createdAt: ZonedDateTime? = ZonedDateTime.now()

    @Column(name = "updated_at")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var updatedAt: ZonedDateTime? = ZonedDateTime.now()

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Transient
    val fromDate: String? = null

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Transient
    val toDate: String?? = null
}