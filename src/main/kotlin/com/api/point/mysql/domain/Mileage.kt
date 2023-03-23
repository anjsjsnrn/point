package com.api.point.mysql.domain

import jakarta.persistence.*
import java.io.Serializable
import java.time.ZonedDateTime

@Entity
@IdClass(Mileage.PreKey::class)
@Table(name = "mileage")
class Mileage{
    @Embeddable
    data class PreKey(
        var barcode: String = "",
        var market: String = ""
    ): Serializable

    @Id
    @Column(name = "barcode")
    lateinit var barcode: String

    @Id
    @Column(name = "market")
    lateinit var market: String

    @Column(name = "id")
    var id: Int? = null

    @Column(name = "mileage")
    var mileage: Long = 0

    @Column(name = "created_at")
    var createdAt: ZonedDateTime? = ZonedDateTime.now()

    @Column(name = "updated_at")
    var updatedAt: ZonedDateTime? = ZonedDateTime.now()

    @Transient
    val marketName: String? = null
}