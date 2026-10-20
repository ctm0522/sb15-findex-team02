package org.findex.team2.sb15findexteam02.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IndexDataSearchRequest {

    Long indexInfoId;
    LocalDate startDate;
    LocalDate endDate;
    Long idAfter;
    String cursor;
    String sortField;
    String sortDirection;
    Integer size;
}
