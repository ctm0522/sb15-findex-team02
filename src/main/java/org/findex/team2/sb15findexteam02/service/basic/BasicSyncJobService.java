package org.findex.team2.sb15findexteam02.service.basic;

import lombok.RequiredArgsConstructor;
import org.findex.team2.sb15findexteam02.client.PublicDataClient;
import org.findex.team2.sb15findexteam02.dto.request.IndexDataSyncRequest;
import org.findex.team2.sb15findexteam02.dto.request.IndexInfoCreateRequest;
import org.findex.team2.sb15findexteam02.dto.request.SyncJobCreateRequest;
import org.findex.team2.sb15findexteam02.dto.response.*;
import org.findex.team2.sb15findexteam02.mapper.SyncJobDtoMapper;
import org.findex.team2.sb15findexteam02.model.*;
import org.findex.team2.sb15findexteam02.repository.IndexDataRepository;
import org.findex.team2.sb15findexteam02.repository.IndexInfoRepository;
import org.findex.team2.sb15findexteam02.repository.SyncJobRepository;
import org.findex.team2.sb15findexteam02.service.IndexDataService;
import org.findex.team2.sb15findexteam02.service.IndexInfoService;
import org.findex.team2.sb15findexteam02.service.SyncJobService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicSyncJobService implements SyncJobService {
    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;
    private final SyncJobRepository syncJobRepository;
    private final SyncJobDtoMapper syncJobDtoMapper;

    private final IndexDataService indexDataService;
    private final IndexInfoService indexInfoService;
    private final PublicDataClient publicDataClient;

    @Override
    public SyncJobDto create(SyncJobCreateRequest request, String result) {
        SyncJob syncJob = new SyncJob(request.getStatus(), request.getWorker(), request.getJobType(),
                request.getTargetDate(),  request.getIndexInfo());
        SyncJob createSyncJob = syncJobRepository.save(syncJob);
        return syncJobDtoMapper.toDto(createSyncJob,result);
    }

    @Override
    public CursorPageResponseSyncJobDto findAll() {
        return null;
    }

    @Override
    public List<SyncJobDto> convertToIndexDataClientDtoDto(IndexDataSyncRequest request, String worker) {
        List<Long> ids = request.getIndexInfoIds();
        List<String> infoNames = new ArrayList<>();
        List<String> categoryNames = new ArrayList<>();

        ids.forEach(id -> {
            IndexInfo indexInfo = indexInfoRepository.findById(id).
                    orElseThrow(() -> new NoSuchElementException("지수 정보 찾을 수 없음"+id));
            infoNames.add(indexInfo.getIndexName());
            categoryNames.add(indexInfo.getIndexCategory().getCategoryName());
        });

         IndexDataClientDto indexDataClientDto = new IndexDataClientDto(infoNames,categoryNames,request.getBaseDateFrom(),request.getBaseDateTo(),worker);
         publicDataClient.fetchIndexData(indexDataClientDto);
         return null;
    }

    @Override
    public List<SyncJobDto> syncInfo(List<PublicDataInfoResponse> requests, String worker, ResultType status) {
        String result;
        IndexInfo indexInfo;
        SyncJobDto syncJobDto;
        List<SyncJobDto> dtos = new ArrayList<>();
        for(PublicDataInfoResponse entry : requests){
            if(indexInfoRepository.existsByIndexNameAndIndexCategoryCategoryName(entry.getIndexName(),entry.getIndexClassification())){
                indexInfo = indexInfoRepository.findByIndexNameAndIndexCategoryCategoryName(entry.getIndexName(),entry.getIndexClassification()).get();
                indexInfo.update(entry.getEmployedItemCount(),entry.getBasDate(),entry.getBaseIndex(),indexInfo.isFavorite(),SourceType.OPEN_API);
                result = "UPDATE";
            } else {
                IndexInfoCreateRequest indexInfoCreateRequest = new IndexInfoCreateRequest(entry.getIndexClassification(),entry.getIndexName(),
                        entry.getEmployedItemCount(),entry.getBasDate(),entry.getBaseIndex(),false);
                indexInfoService.create(indexInfoCreateRequest, SourceType.OPEN_API);
                indexInfo = indexInfoRepository.findByIndexNameAndIndexCategoryCategoryName(entry.getIndexName(),entry.getIndexClassification()).get();
                result = "NEW";
            }
            SyncJobCreateRequest syncJobCreateRequest = new SyncJobCreateRequest(status,worker, JobType.INDEX_INFO, null, indexInfo);
            syncJobDto = create(syncJobCreateRequest,result);
            dtos.add(syncJobDto);

        }
        //여기서 할일
        //1.exist로 있나 없나 확인
        //없으면 create 있으면 update 호출
        //싱크잡크리에이트 호출

        return null;
    }

    @Override
    public List<SyncJobDto> syncData(List<PublicDataResponse> requests, String worker, ResultType status) {
        return null;
    }
}
