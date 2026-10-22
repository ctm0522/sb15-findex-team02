package org.findex.team2.sb15findexteam02.client;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.findex.team2.sb15findexteam02.dto.response.*;
import org.findex.team2.sb15findexteam02.model.ResultType;
import org.findex.team2.sb15findexteam02.service.SyncJobService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicDataClient {
  private final RestClient restClient;
  private final SyncJobService syncJobService;

  private String serviceKey= "eaf1f3738059cc847f490d7f082fe7bde63ea609907502c975d62f61203259aa";

  private static final String API_PATH = "/1160100/GetMarketIndexInfoService_V2/getStockMarketIndex_V2";
  private static final String RESULT_TYPE = "json";
  private static final int DEFAULT_PAGE_NO = 1;
//  private static final int INFO_NUM_OF_ROWS = 500;
 // private static final int DATA_NUM_OF_ROWS = 1000;

  // 지수 정보 가져오기
  public List<SyncJobDto> fetchIndexInfo(String worker) {

    List<PublicDataInfoResponse> responses = new ArrayList<>();


    // 날짜 하드코딩
    String endBasDt = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String beginBasDt = LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    ResultType status = ResultType.FAILED; // 기본값
    try{
      PublicDataInfoResponse singleResponse = restClient.get()
          .uri(uriBuilder -> uriBuilder
              .path(API_PATH)
              .queryParam("resultType", RESULT_TYPE)
              .queryParam("serviceKey", serviceKey)
              .queryParam("beginBasDt", beginBasDt)
              .queryParam("endBasDt", endBasDt)
              .build())
          .retrieve()
          .body(PublicDataInfoResponse.class);

      if (singleResponse != null) {
        responses.add(singleResponse);
        status = ResultType.SUCCESS;
      }

      responses.add(singleResponse);
    } catch (Exception e) {
      log.error("지수 정보 연동 실패", e);
    }

    return syncJobService.syncInfo(responses, worker, status);
  }

  // 지수 데이터 가져오기
  // indexDataSync는 프론트엔드에서 오는 요청
  public List<SyncJobDto> fetchIndexData(IndexDataClientDto dto) {
    List<PublicDataResponse> responses = new ArrayList<>();
    List<String> indexNames = dto.getInfoNames();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    String beginBasDt = dto.getBaseDateFrom().format(formatter);
    String endBasDt = dto.getBaseDateTo().format(formatter);


    // 1. 지수가 없을 때
    if (indexNames == null || indexNames.isEmpty()) {
      ResultType status = ResultType.FAILED; // 기본값
      try{
        PublicDataResponse response = restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(API_PATH)
                .queryParam("resultType", RESULT_TYPE)
                .queryParam("serviceKey", serviceKey)
                .queryParam("beginBasDt", beginBasDt)
                .queryParam("endBasDt", endBasDt)
                .build())
            .retrieve()
            .body(PublicDataResponse.class);

        if (response != null) {
          responses.add(response);
          status = ResultType.SUCCESS;
        }
        responses.add(response);
      } catch (Exception e) {
        log.error("지수 정보 연동 실패", e);
      }

      return syncJobService.syncData(responses, dto.getWorker(), status);
    }

    // 2. 지수가 여러개
    List<SyncJobDto> finalResult = new ArrayList<>();

    for (String indexName: indexNames) {
      ResultType status = ResultType.FAILED;
      List<PublicDataResponse> singleList = new ArrayList<>();

      try{
        PublicDataResponse singleResponse = restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(API_PATH)
                .queryParam("resultType", RESULT_TYPE)
                .queryParam("serviceKey", serviceKey)
                .queryParam("beginBasDt", beginBasDt)
                .queryParam("endBasDt", endBasDt)
                .build())
            .retrieve()
            .body(PublicDataResponse.class);
        // 지수가 여러개 일때 연동 성공한 리스트에 추가
        if (singleResponse != null) {
          singleList.add(singleResponse);
          status = ResultType.SUCCESS;
        }

      } catch (Exception e) {
        log.error("지수 정보 연동 실패 - 지수명: {}", indexName, e);
      }

      finalResult.addAll(syncJobService.syncData(singleList, dto.getWorker(), status));
    }
    return finalResult;
  }


}
