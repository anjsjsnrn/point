1. 프로젝트 구성
   1) API - Kotlin, Spring boot
   2) DB - Mysql (AWS EC2)
   3) Cache - Redis (AWS EC2)
   
2. API 상세 명세
   1) 통합 바코드 발급 API (/api/v1/membership/member)
      ● Method : Post
      ● 요청 : json / { "id" : 123456789 (9자리 숫자값) }
      ● 정상응답 : 10자리 바코드 값
      ● 오류 : 이미 등록된 id의 경우 기존 멤버십 바코드를 반환
      ● 캐싱 : (Key : 멤버ID, Value : 바코드값) 캐싱하여 DB 접근전에 확인
   2) 포인트 적립 API (/api/v1/membership/mileage)
      ● Method : Post
      ● 요청 : json / { "barcode" : "7097280463" , "market" : "A", "marketName" : "세븐일레븐", "mileage" : 20000 }
      ● 정상응답 : 10자리 바코드 값
      ● 오류 : 허용되지 않은 마켓코드("A","B","C" 이외) 일 경우 오류메세지
              등록되지 않은 멤버의 경우 오류메세지 
   3) 포인트 사용 API
      ● Method : PATCH
      ● 요청 : json / { "barcode" : "7097280463", "market" : "A", "marketName" : "씨유", "mileage" : 20000 }
      ● 정상응답 : 10자리 바코드 값
      ● 오류 : 허용되지 않은 마켓코드("A","B","C" 이외) 일 경우 오류메세지 
              등록되지 않은 멤버의 경우 오류메세지
              포인트부족한 경우 오류메세지
   4) 내역 조회 API
      ● Method : GET
      ● 요청 : json / { "barcode" : "7097280463", "market" : "A", "marketName" : "씨유", "mileage" : 20000 }
      ● 정상응답 : 사용내역 리스트 반환    
        {
          "flag": "적립",
          "market": "A",
          "marketName": "세븐일레븐",
          "mileage": 20000,
          "createdAt": "2023-03-24T08:33:13.265371+09:00"
        } ...
      ● 오류 : 등록되지 않은 멤버의 경우 오류메세지

3. 테스트코드 
  -> 작성되지 않음 향후추가

4. Impression
  : 학습도 병행할 목적으로 Kotlin,Kafka 사용 하였는데 처음 써봐서 막히는 부분이 많았음.
    결과적으로 시간 부족으로 기능만 급하게 구현하고 마무리 하게 되었다. 
    코드 정리해야될 부분도 많이 남았고 DB Column 명도 크게 신경쓰지 못했다. 과제 제출후에 따로 개인적으로 완성해야할 프로젝트로 남겨둔다.