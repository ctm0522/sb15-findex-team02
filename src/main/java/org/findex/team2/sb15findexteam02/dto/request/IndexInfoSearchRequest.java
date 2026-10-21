package org.findex.team2.sb15findexteam02.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexInfoSearchRequest {

    String indexClassification;
    String indexName;
    Boolean favorite;
    Long idAfter;
    String cursor;
    String sortField;
    String sortDirection;
    Integer size;
}
