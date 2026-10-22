package org.findex.team2.sb15findexteam02.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexDataSyncRequest {
    List<Long> indexInfoIds;
    LocalDate baseDateFrom;
    LocalDate baseDateTo;
}
