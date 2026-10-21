package org.findex.team2.sb15findexteam02.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AutoSyncConfigSearchRequest {
    Long indexInfoId;
    Boolean enabled;
    Long idAfter;
    String cursor;
    String sortField = "indexInfo.indexName";
    String sortDirection = "asc";
    Integer size = 10;
}
