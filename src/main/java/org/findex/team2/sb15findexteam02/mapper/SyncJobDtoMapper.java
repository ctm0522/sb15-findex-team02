package org.findex.team2.sb15findexteam02.mapper;

import org.findex.team2.sb15findexteam02.dto.response.SyncJobDto;
import org.findex.team2.sb15findexteam02.model.SyncJob;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SyncJobDtoMapper {

    @Mapping(source = "indexInfo.id", target = "indexInfoId")
    @Mapping(source = "result", target = "result")
    SyncJobDto toDto(SyncJob syncJob, String result);
}
