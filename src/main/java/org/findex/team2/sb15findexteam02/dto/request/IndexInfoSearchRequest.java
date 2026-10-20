package org.findex.team2.sb15findexteam02.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexInfoSearchRequest {

    private String indexClassification;
    private String indexName;
    private Boolean favorite;
    private Long idAfter;
    private String cursor;
    private String sortField;
    private String sortDirection;
    private Integer size;
}
