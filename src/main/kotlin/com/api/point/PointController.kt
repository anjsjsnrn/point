package com.api.point


import com.api.point.mysql.RepositoryHistory
import com.api.point.mysql.RepositoryMember
import com.api.point.mysql.RepositoryMileage
import com.api.point.mysql.domain.History
import com.api.point.mysql.domain.Member
import com.api.point.mysql.domain.Mileage
import com.api.point.redis.RedisService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random



@RestController
class PointController(
    val repositoryMember: RepositoryMember, // 멤버쉽 테이블
    var repositoryMileage: RepositoryMileage, // 마일리지 테이블
    var repositoryHistory: RepositoryHistory, // 마일리지 사용내역 테이블
    val redisService: RedisService, // Redis
) {
    val marketList: List<String> = listOf("A","B","C") // 마켓종류

    /* 회원등록(바코드발급)
       Request : 사용자ID
       Response : 바코드
     */
    @PostMapping("/api/v1/membership/member")
    fun setMember(@RequestBody member: Member): ResponseEntity<*> {
        // 캐시에 등록된 ID가 있으면 바코드 리턴
        var cacheBarcode = redisService.find(member.id.toString())

        if( cacheBarcode != null){
            return ResponseEntity.ok(cacheBarcode)
        }

        // 멤버 DB에 저장 - 값이 이미 있어도 바코드는 리턴됨
        var random = Random(member.id?.times(10) ?: 0) // ID를 시드값으로 바코드 랜덤 생성
        member.barcode = (random.nextLong(1000000000, 9999999999)).toString()
        val res = repositoryMember.save(member)

        // 발급받은 바코드 캐싱
        if(res.barcode != null) {
            redisService.save(res.id.toString(), res.barcode.toString())
        }

        return ResponseEntity.ok(res.barcode.toString())
    }

    /* 포인트 적립 API
       Request : 상점 id(A, B, C), 상점명, 바코드, 적립금
       Response : 바코드
     */
    @PostMapping("/api/v1/membership/mileage")
    fun saveMileage(@RequestBody mileage: Mileage): ResponseEntity<*> {
        if(!marketList.contains(mileage.market)) return ResponseEntity.ok("임시에러 : 상점오류")
        if(!checkBarcode(mileage.barcode)) return ResponseEntity.ok("임시에러 : 멤버 미등록")

        var prekey = Mileage.PreKey(mileage.barcode, mileage.market)
        var old = repositoryMileage.findById(prekey)
        var thisMile = mileage.mileage

        // 이미 있는 값이면 마일리지 합산
        if(!old.isEmpty) {
            mileage.mileage = thisMile + old.get().mileage
        }

        val res = repositoryMileage.save(mileage)

        // 히스토리 테이블에도 추가
        var historyEntity : History = History()
        historyEntity.barcode = mileage.barcode
        historyEntity.flag = "적립"
        historyEntity.market = mileage.market
        historyEntity.marketName = mileage.marketName
        historyEntity.mileage = thisMile

        repositoryHistory.save(historyEntity)

        return ResponseEntity.ok(res.barcode)
    }

    /* 포인트 사용 API
       Request : 상점 id(A, B, C), 상점명, 바코드, 사용마일리지
       Response : 바코드
    */
    @PatchMapping("/api/v1/membership/mileage")
    fun useMileage(@RequestBody mileage: Mileage): ResponseEntity<*> {
        if(!marketList.contains(mileage.market)) return ResponseEntity.ok("임시에러 : 상점오류")
        if(!checkBarcode(mileage.barcode)) return ResponseEntity.ok("임시에러 : 멤버 미등록")

        var prekey = Mileage.PreKey(mileage.barcode, mileage.market)
        var old = repositoryMileage.findById(prekey)
        var thisMile = mileage.mileage

        // 마일리지 차감
        if(!old.isEmpty) mileage.mileage = old.get().mileage - thisMile

        if(mileage.mileage < 0)  return ResponseEntity.ok("임시에러 : 잔액없음")

        val res = repositoryMileage.save(mileage)

        // 히스토리 테이블에도 추가
        var historyEntity : History = History()
        historyEntity.barcode = mileage.barcode
        historyEntity.flag = "사용"
        historyEntity.market = mileage.market
        historyEntity.marketName = mileage.marketName
        historyEntity.mileage = thisMile

        repositoryHistory.save(historyEntity)

        return ResponseEntity.ok(res.barcode)
    }

    /* 사용내역조회
       Request : 사용자ID
       Response : 바코드
    */
    @GetMapping("/api/v1/membership/history")
    fun setMember(@RequestBody history: History): ResponseEntity<*> {
        if(!checkBarcode(history.barcode)) return ResponseEntity.ok("임시에러 : 멤버 미등록")
        var res = repositoryHistory.findByHistory(
              history.barcode
            , changeDateTime(history.fromDate)
            , changeDateTime(history.toDate)
        )

        return ResponseEntity.ok(res)
    }


    // 바코드 발급 여부
    fun checkBarcode(barcode: String?): Boolean {
        if(barcode != null) {
            // 멤버 디비에 있으면
            var memberData = repositoryMember.findById(barcode)
            if (!memberData.isEmpty) return true
        }

        return false
    }

    // dateType 변경
    fun changeDateTime(inputDateString: String?): ZonedDateTime {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val inputDateTime = LocalDateTime.parse(inputDateString, inputFormatter)
        val zoneId = ZoneId.systemDefault()
        val zonedDateTime = ZonedDateTime.of(inputDateTime, zoneId)

        return zonedDateTime
    }
}



