package org.findex.team2.sb15findexteam02.client;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.dto.request.PublicDataRequest;
import org.findex.team2.sb15findexteam02.dto.response.*;
import org.findex.team2.sb15findexteam02.model.ResultType;
import org.findex.team2.sb15findexteam02.service.SyncJobService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class PublicDataClient {
  private final RestClient restClient;
  private final SyncJobService syncJobService;

  // 지수 정보 가져오기
  public List<SyncJobDto> fetchIndexInfo(String worker) {

    List<PublicDataInfoResponse> responses = new ArrayList<>();

    List<String> indexNames = dto.getInfoNames();
    List<String> categoryNames = dto.getCategoryNames();

    for (int i = 0; i < indexNames.size(); i++) {
      String indexName = indexNames.get(i);
      String categoryName = categoryNames.get(i);

      PublicDataInfoResponse singleResponse = restClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/1160100/GetMarketIndexInfoService_V2/getStockMarketIndex_V2")
              .queryParam("idxNm", indexName)
              .queryParam("idxCsf", categoryName)
              .queryParam("resultType", request.getResultType())
              .queryParam("serviceKey", request.getServiceKey())
              .queryParam("beginBasDt", dto.getBaseDateFrom())
              .queryParam("endBasDt", dto.getBaseDateTo())
              .build())
          .retrieve()
          .body(PublicDataInfoResponse.class);
      responses.add(singleResponse);
    }

    syncJobService.syncInfo( responses, worker, ResultType status);
    return responses;
  }

  // 지수 데이터 가져오기
  // indexDataSync는 프론트엔드에서 오는 요청
  public List<SyncJobDto> fetchIndexData(IndexDataClientDto dto) {
    List<String> indexNames = dto.getInfoNames();

    List<PublicDataResponse> responses = new ArrayList<>();

    for (String indexName: indexNames) {
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
      responses.add(singleResponse);
    }
    syncJobService.syncData(responses,dto.getWorker(),ResultType status);
    return responses;
  }


}
