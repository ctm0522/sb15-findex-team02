package org.findex.team2.sb15findexteam02.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageResponseIndexDataDto {
    List<IndexDataDto> content;
    String nextCursor;
    Long nextIdAfter;
    Integer size;
    Long totalElements;
    Boolean hasNext;
}