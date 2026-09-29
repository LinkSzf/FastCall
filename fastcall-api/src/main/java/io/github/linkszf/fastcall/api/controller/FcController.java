package io.github.linkszf.fastcall.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.github.linkszf.fastcall.api.event.FcSourceEventCut;
import io.github.linkszf.fastcall.api.event.IdLevel;
import io.github.linkszf.fastcall.api.model.dto.FcApiDTO;
import io.github.linkszf.fastcall.api.model.dto.FcApiParamDTO;
import io.github.linkszf.fastcall.api.model.dto.FcRetryDTO;
import io.github.linkszf.fastcall.api.model.dto.FcSystemDTO;
import io.github.linkszf.fastcall.api.model.mapping.FcApiMapping;
import io.github.linkszf.fastcall.api.model.mapping.FcApiParamMapping;
import io.github.linkszf.fastcall.api.model.mapping.FcRetryMapping;
import io.github.linkszf.fastcall.api.model.mapping.FcSystemMapping;
import io.github.linkszf.fastcall.api.model.vo.FcApiParamVO;
import io.github.linkszf.fastcall.api.model.vo.FcApiVO;
import io.github.linkszf.fastcall.api.model.vo.FcRetryVO;
import io.github.linkszf.fastcall.api.model.vo.FcSystemVO;
import io.github.linkszf.fastcall.common.FastCallConsts;
import io.github.linkszf.fastcall.common.event.source.FcSourceEventType;
import io.github.linkszf.fastcall.common.exception.FcDataNotFoundException;
import io.github.linkszf.fastcall.data.entity.FcSystem;
import io.github.linkszf.fastcall.data.manager.FcApiManager;
import io.github.linkszf.fastcall.data.manager.FcApiParamManager;
import io.github.linkszf.fastcall.data.manager.FcRetryManager;
import io.github.linkszf.fastcall.data.manager.FcSystemManager;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping("/fc/system")
@RequiredArgsConstructor
public class FcController {

    private final FcSystemManager systemManager;

    private final FcApiManager apiManager;

    private final FcApiParamManager apiParamManager;

    private final FcRetryManager retryManager;

    private final FcSystemMapping systemMapping;

    private final FcApiMapping apiMapping;

    private final FcApiParamMapping apiParamMapping;

    private final FcRetryMapping retryMapping;

    @GetMapping("/all")
    public List<FcSystemVO> listAllSystem(){
        log.trace("{}-Web request query system list", FastCallConsts.NAME);
        List<FcSystem> systemList = systemManager.listAllWithAuth();
        return systemMapping.toVoList(systemList);
    }

    @GetMapping("/{systemId}")
    public FcSystemVO findOneSystem(@PathVariable Long systemId){
        log.trace("{}-Web request query system[{}]", FastCallConsts.NAME, systemId);
        return Optional.of(systemId)
                .map(systemManager::getByIdWithAuth)
                .map(systemMapping::toVo)
                .orElseThrow(() -> new FcDataNotFoundException("The system[{}] no longer exists.",  systemId));
    }

    @FcSourceEventCut(idIndex = 1)
    @PostMapping("/save")
    public FcSystemVO saveOneSystem(@Valid @RequestBody FcSystemDTO dto){
        log.trace("{}-Web request save system[{}]", FastCallConsts.NAME, dto.getCode());
        return Optional.of(dto)
                .map(systemMapping::toEntity)
                .map(systemManager::save)
                .map(systemMapping::toVo)
                .orElseThrow(() -> new FcDataNotFoundException("The system[{}] no longer exists.",  dto.getCode()));
    }

    @FcSourceEventCut(idIndex = 1, type = FcSourceEventType.DELETE)
    @DeleteMapping("/{systemId}")
    public void deleteOneSystem(@PathVariable Long systemId){
        log.trace("{}-Web request delete system[{}]", FastCallConsts.NAME, systemId);
        systemManager.removeById(systemId);
    }

    @GetMapping("/{systemId}/api/all")
    public List<FcApiVO> listAllApiOfSystem(@PathVariable Long systemId){
        log.trace("{}-Web request query api list of system[{}]", FastCallConsts.NAME, systemId);
        return Optional.of(systemId)
                .map(apiManager::listAllBySystemId)
                .map(apiMapping::toVoList)
                .orElseThrow(()->new FcDataNotFoundException("The system[{}] no longer exists.", systemId));
    }

    @FcSourceEventCut(idIndex = 1)
    @PostMapping("/{systemId}/api/save")
    public List<FcApiVO> saveApiOfSystem(@PathVariable Long systemId, @Valid @RequestBody List<FcApiDTO> apiList) {
        log.trace("{}-Web request save api of system[{}]", FastCallConsts.NAME, systemId);
        return Optional.of(apiList)
                .map(apiMapping::toEntityList)
                .map(apis -> apiManager.save(systemId, apis))
                .map(apiMapping::toVoList)
                .orElseThrow(()->new FcDataNotFoundException("The system[{}] no longer exists.", systemId));
    }

    @GetMapping("/api/{apiId}/param/all")
    public List<FcApiParamVO> listAllApiParamsOfApi(@PathVariable Long apiId) {
        log.trace("{}-Web request query api param list of api[{}]", FastCallConsts.NAME, apiId);
        return Optional.of(apiId)
                .map(apiParamManager::listAllByApiId)
                .map(apiParamMapping::toVoList)
                .orElseThrow(()->new FcDataNotFoundException("The api[{}] no longer exists.", apiId));
    }

    @FcSourceEventCut(idIndex = 1, level = IdLevel.API)
    @PostMapping("/api/{apiId}/param/save")
    public List<FcApiParamVO> saveApiParamsOfApi(@PathVariable Long apiId, @Valid @RequestBody List<FcApiParamDTO> apiParamList) {
        log.trace("{}-Web request save api param list of api[{}]", FastCallConsts.NAME, apiId);
        return Optional.of(apiParamList)
                .map(apiParamMapping::toEntityList)
                .map(params -> apiParamManager.save(apiId, params))
                .map(apiParamMapping::toVoList)
                .orElseThrow(()->new FcDataNotFoundException("The api[{}] no longer exists.", apiId));
    }

    @GetMapping("/{systemId}/retry")
    public FcRetryVO findRetryOfSystem(@PathVariable Long systemId) {
        log.trace("{}-Web request query retry config of system[{}]", FastCallConsts.NAME, systemId);
        return Optional.ofNullable(retryManager.getBySystemId(systemId))
                .map(retryMapping::toVo)
                .orElse(null);
    }

    @FcSourceEventCut(idIndex = 1)
    @PostMapping("/{systemId}/retry/save")
    public FcRetryVO saveRetryOfSystem(@PathVariable Long systemId, @Valid @RequestBody FcRetryDTO dto) {
        log.trace("{}-Web request save retry config of system[{}]", FastCallConsts.NAME, systemId);
        return Optional.of(dto)
                .map(retryMapping::toEntity)
                .map(retry -> retryManager.save(systemId, retry))
                .map(retryMapping::toVo)
                .orElse(null);
    }

    @FcSourceEventCut(idIndex = 1)
    @DeleteMapping("/{systemId}/retry")
    public void removeRetryOfSystem(@PathVariable Long systemId) {
        log.trace("{}-Web request remove retry config of system[{}]", FastCallConsts.NAME, systemId);
        retryManager.removeBySystemId(systemId);
    }



}
