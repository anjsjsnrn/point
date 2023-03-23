package com.api.point.mysql.domain

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "member")
class Member {
    @Id
    @Column(name = "barcode")
    var barcode: String? = null

    @Column(name = "id")
    var id: Int? = null

    @Column(name = "created_at")
    var createdAt: ZonedDateTime? = ZonedDateTime.now()

    @Column(name = "updated_at")
    var updatedAt: ZonedDateTime? = ZonedDateTime.now()
}