package org.findex.team2.sb15findexteam02.service.basic;

import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataSyncRequest;
import org.findex.team2.sb15findexteam02.dto.request.SyncJobCreateRequest;
import org.findex.team2.sb15findexteam02.dto.response.*;
import org.findex.team2.sb15findexteam02.model.ResultType;
import org.findex.team2.sb15findexteam02.repository.IndexDataRepository;
import org.findex.team2.sb15findexteam02.repository.IndexInfoRepository;
import org.findex.team2.sb15findexteam02.repository.SyncJobRepository;
import org.findex.team2.sb15findexteam02.service.SyncJobService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BasicSyncJobService implements SyncJobService {
    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;
    private final SyncJobRepository syncJobRepository;

    @Override
    public SyncJobDto create(SyncJobCreateRequest request) {
        return null;
    }

    @Override
    public CursorPageResponseSyncJobDto findAll() {
        return null;
    }

    @Override
    public IndexDataClientDto convertToIndexDataClientDtoDto(IndexDataSyncRequest request) {
        return null;
    }

    @Override
    public SyncJobDto syncInfo(List<PublicDataInfoResponse> requests, String worker, ResultType status) {
        return null;
    }

    @Override
    public SyncJobDto syncData(List<PublicDataResponse> requests, String worker, ResultType status) {
        return null;
    }
}
