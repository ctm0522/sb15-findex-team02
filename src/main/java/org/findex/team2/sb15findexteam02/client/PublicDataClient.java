package org.findex.team2.sb15findexteam02.client;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.findex.team2.sb15findexteam02.dto.response.*;
import org.findex.team2.sb15findexteam02.model.ResultType;
import org.findex.team2.sb15findexteam02.service.SyncJobService;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicDataClient {
  private final RestClient restClient;
  private final SyncJobService syncJobService;

  private String serviceKey= "eaf1f3738059cc847f490d7f082fe7bde63ea609907502c975d62f61203259aa";

  private static final String API_PATH = "/1160100/GetMarketIndexInfoService_V2/getStockMarketIndex_V2";
  private static final int PAGE_SIZE = 1000;
  private final ObjectMapper objectMapper;

  public List<SyncJobDto> fetchIndexInfo(String worker) {
    LocalDate today = LocalDate.now();
    List<PublicDataInfoResponse> items = fetchPages(today.minusDays(7), today, null,
        PublicDataInfoResponse.class);
    // The API returns daily rows; keep the newest information for each index.
    Map<List<String>, PublicDataInfoResponse> latest = new LinkedHashMap<>();
    int skipped = 0;
    for (PublicDataInfoResponse item : items) {
      require(item.getIndexName() != null && !item.getIndexName().isBlank()
          && item.getIndexClassification() != null && !item.getIndexClassification().isBlank());
      if (item.getBasDate() == null || item.getEmployedItemCount() == null
          || item.getBaseIndex() == null) {
        skipped++;
        continue;
      }
      latest.putIfAbsent(List.of(item.getIndexClassification(), item.getIndexName()), item);
    }
    if (skipped > 0) {
      log.warn("Skipped {} external index rows missing required base information", skipped);
      require(!latest.isEmpty());
    }
    return syncJobService.syncInfo(new ArrayList<>(latest.values()), worker, ResultType.SUCCESS);
  }

  public List<SyncJobDto> fetchIndexData(IndexDataClientDto dto) {
    if (dto.getBaseDateFrom() == null || dto.getBaseDateTo() == null
        || dto.getBaseDateFrom().isAfter(dto.getBaseDateTo())) {
      throw new IllegalArgumentException("Invalid synchronization date range");
    }
    List<PublicDataResponse> items = new ArrayList<>();
    if (dto.getInfoNames() == null || dto.getInfoNames().isEmpty()) {
      items.addAll(fetchPages(dto.getBaseDateFrom(), dto.getBaseDateTo(), null, PublicDataResponse.class));
    } else {
      require(dto.getCategoryNames() != null
          && dto.getCategoryNames().size() == dto.getInfoNames().size());
      for (int i = 0; i < dto.getInfoNames().size(); i++) {
        String name = dto.getInfoNames().get(i);
        String category = dto.getCategoryNames().get(i);
        require(name != null && category != null);
        for (PublicDataResponse item : fetchPages(dto.getBaseDateFrom(), dto.getBaseDateTo(),
            name, PublicDataResponse.class)) {
          if (name.equals(item.getInfoNames()) && category.equals(item.getCategoryNames())) {
            items.add(item);
          }
        }
      }
    }
    for (PublicDataResponse item : items) {
      require(item.getInfoNames() != null && !item.getInfoNames().isBlank()
          && item.getCategoryNames() != null && !item.getCategoryNames().isBlank()
          && item.getBaseDate() != null && item.getMarketPrice() != null
          && item.getDailyPrice() != null && item.getHighPrice() != null
          && item.getLowPrice() != null && item.getVs() != null
          && item.getFluctuationRate() != null && item.getTradingQuantity() != null
          && item.getTradingPrice() != null && item.getMarketTotalAmount() != null);
    }
    return syncJobService.syncData(items, dto.getWorker(), ResultType.SUCCESS);
  }

  private <T> List<T> fetchPages(LocalDate from, LocalDate to, String indexName, Class<T> type) {
    List<JsonNode> rows = new ArrayList<>();
    long received = 0;
    try {
      for (int page = 1; ; page++) {
        final int pageNo = page;
        JsonNode root = restClient.get()
            .uri(builder -> {
              builder.path(API_PATH).queryParam("resultType", "json")
                  .queryParam("serviceKey", serviceKey)
                  .queryParam("beginBasDt", from.format(DateTimeFormatter.BASIC_ISO_DATE))
                  // The upstream API excludes endBasDt; our request includes the end date.
                  .queryParam("endBasDt", to.plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE))
                  .queryParam("pageNo", pageNo).queryParam("numOfRows", PAGE_SIZE);
              if (indexName != null) builder.queryParam("idxNm", indexName);
              return builder.build();
            }).retrieve().body(JsonNode.class);
        require(root != null && "00".equals(root.at("/response/header/resultCode").asText()));
        var body = root.at("/response/body");
        require(body.path("totalCount").canConvertToLong());
        long total = body.path("totalCount").asLong();
        require(total >= 0);
        if (total == 0) break;
        var items = body.path("items").path("item");
        require(items.isArray() || items.isObject());
        int count = items.isArray() ? items.size() : 1;
        require(count > 0);
        if (items.isArray()) {
          for (var item : items) rows.add(item);
        } else {
          rows.add(items);
        }
        received += count;
        if (received >= total) break;
      }
      rows.sort(java.util.Comparator.comparing(
          (JsonNode row) -> row.path("basDt").asText()).reversed());
      List<T> results = new ArrayList<>();
      for (JsonNode row : rows) results.add(objectMapper.treeToValue(row, type));
      return results;
    } catch (ResponseStatusException exception) {
      throw exception;
    } catch (Exception exception) {
      // Do not include the request URI, which contains the service key.
      log.warn("Public API request or parsing failed: {}", exception.getClass().getSimpleName());
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY, "External API request or parsing failed");
    }
  }

  private static void require(boolean valid) {
    if (!valid) {
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY, "Invalid external API response");
    }
  }
}
