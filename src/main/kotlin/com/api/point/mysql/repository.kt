package com.api.point.mysql

import com.api.point.mysql.domain.History
import com.api.point.mysql.domain.Member
import com.api.point.mysql.domain.Mileage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query
import java.time.ZonedDateTime


@Repository
interface RepositoryMember : JpaRepository<Member, String>
interface RepositoryMileage : JpaRepository<Mileage, Mileage.PreKey>
interface RepositoryHistory : JpaRepository<History, String> {
    @Query("SELECT u " +
            "FROM History u " +
            "WHERE u.createdAt BETWEEN :fromDate AND :toDate AND u.barcode = :barcode")
    fun findByHistory(barcode: String?, fromDate: ZonedDateTime, toDate:ZonedDateTime): List<History>
}