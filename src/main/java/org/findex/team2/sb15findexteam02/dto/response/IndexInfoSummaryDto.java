package org.findex.team2.sb15findexteam02.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.findex.team2.sb15findexteam02.model.IndexInfo;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexInfoSummaryDto {

    Long id;
    String indexClassification;
    String indexName;

    public static IndexInfoSummaryDto from(IndexInfo indexInfo) {
        return new IndexInfoSummaryDto(
                indexInfo.getId(),
                indexInfo.getIndexCategory().getCategoryName(),
                indexInfo.getIndexName()
        );
    }
}