package org.findex.team2.sb15findexteam02.client;

import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataCreateRequest;
import org.findex.team2.sb15findexteam02.dto.request.PublicDataRequest;
import org.findex.team2.sb15findexteam02.dto.response.IndexDataClientDto;
import org.findex.team2.sb15findexteam02.dto.response.PublicDataInfoResponse;
import org.findex.team2.sb15findexteam02.dto.response.PublicDataResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class PublicDataClient {
  private final RestClient restClient;

  // 지수 정보 가져오기
  public PublicDataInfoResponse fetchIndexInfo(PublicDataRequest request) {
    return restClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/1160100/GetMarketIndexInfoService_V2/getStockMarketIndex_V2")
            .queryParam("resultType", request.getResultType())
            .queryParam("serviceKey", request.getServiceKey())
            .queryParam("BasDt", request.getBaseDate())
            .queryParam("idxNm", request.getIdxNm())
            .build())
        .retrieve()
        .body(PublicDataInfoResponse.class);
  }

  // 지수 데이터 가져오기
  public PublicDataResponse fetchIndexData(PublicDataRequest request) {
    return restClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/1160100/GetMarketIndexInfoService_V2/getStockMarketIndex_V2")
            .queryParam("resultType", request.getResultType())
            .queryParam("serviceKey", request.getServiceKey())
            .queryParam("beginBasDt", request.getBeginBaseDate())
            .queryParam("endBasDt", request.getBeginBaseDate())
            .queryParam("idxNm", request.getIdxNm())
            .build())
        .retrieve()
        .body(PublicDataResponse.class);
  }








}
