package org.findex.team2.sb15findexteam02.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AutoSyncConfigDto {
    Long id;
    Long indexInfoId;
    String indexClassification;
    String indexName;
    Boolean enabled;
}