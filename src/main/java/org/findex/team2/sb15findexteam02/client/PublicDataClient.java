package org.findex.team2.sb15findexteam02.client;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.findex.team2.sb15findexteam02.dto.request.PublicDataRequest;
import org.findex.team2.sb15findexteam02.dto.response.IndexDataClientDto;
import org.findex.team2.sb15findexteam02.dto.response.IndexTargetDto;
import org.findex.team2.sb15findexteam02.dto.response.PublicDataInfoResponse;
import org.findex.team2.sb15findexteam02.dto.response.PublicDataResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicDataClient {
  private final RestClient restClient;

  // 지수 정보 가져오기
  public List<PublicDataInfoResponse> fetchIndexInfo(IndexDataClientDto dto, PublicDataRequest request) {

    List<PublicDataInfoResponse> responses = new ArrayList<>();

    List<String> indexNames = dto.getInfoNames();
    List<String> categoryNames = dto.getCategoryNames();

    if (indexNames == null || categoryNames == null) {
      return responses;
    }
    int size = Math.min(indexNames.size(), categoryNames.size());

    for (int i = 0; i < size; i++) {
      String indexName = indexNames.get(i);
      String categoryName = categoryNames.get(i);
      try {
      PublicDataInfoResponse singleResponse = restClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/1160100/GetMarketIndexInfoService_V2/getStockMarketIndex_V2")
              .queryParam("idxNm", indexName)
              .queryParam("idxCsf", categoryName)
              .queryParam("resultType", request.getResultType())
              .queryParam("serviceKey", request.getServiceKey())
              .queryParam("beginBasDt", dto.getBaseDateFrom())
              .queryParam("endBasDt", dto.getBaseDateTo())
              .queryParam("numOfRows", 100)
              .build())
          .retrieve()
          .body(PublicDataInfoResponse.class);
          if (singleResponse != null)  {
            responses.add(singleResponse);
          }
      } catch (Exception e) {
        log.error("지수 정보 API 호출 실패 - 지수명: {}, 카테고리: {}", indexName, categoryName, e);
      };

    }
    return responses;
  }

  // 지수 데이터 가져오기
  // indexDataSync는 프론트엔드에서 오는 요청
  public List<PublicDataResponse> fetchIndexData(IndexDataClientDto dto, PublicDataRequest request) {
    List<PublicDataResponse> responses = new ArrayList<>();
    List<String> indexNames = dto.getInfoNames();

    // 지수를 선택적으로 지정
    if (indexNames == null || indexNames.isEmpty()) {
      try {
        PublicDataResponse response = restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/1160100/GetMarketIndexInfoService_V2/getStockMarketIndex_V2")
                .queryParam("resultType", request.getResultType())
                .queryParam("serviceKey", request.getServiceKey())
                .queryParam("beginBasDt", dto.getBaseDateFrom())
                .queryParam("endBasDt", dto.getBaseDateTo())
                .queryParam("numOfRows", 1000) // 전체 지수 조회이므로 넉넉하게 설정
                .build())
            .retrieve()
            .body(PublicDataResponse.class);

        if (response != null) {
          responses.add(response);
        }
      } catch (Exception e) {
        log.error("전체 지수 데이터 API 호출 실패 - 기간: {} ~ {}", dto.getBaseDateFrom(), dto.getBaseDateTo(), e);
      }
      return responses;
    }
    for (String indexName: indexNames) {
      try {
      PublicDataResponse singleResponse = restClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/1160100/GetMarketIndexInfoService_V2/getStockMarketIndex_V2")
              .queryParam("idxNm", indexName)
              .queryParam("resultType", request.getResultType())
              .queryParam("serviceKey", request.getServiceKey())
              .queryParam("beginBasDt", dto.getBaseDateFrom())
              .queryParam("endBasDt", dto.getBaseDateTo())
              .build())
          .retrieve()
          .body(PublicDataResponse.class);
        if (singleResponse != null) {
          responses.add(singleResponse);
        }
      } catch (Exception e) {
        log.error("지수 데이터 API 호출 실패 - 지수명: {}", indexName, e);
      }
    }
    return responses;
  }


}
