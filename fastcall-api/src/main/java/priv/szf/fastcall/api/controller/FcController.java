package priv.szf.fastcall.api.controller;

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
import priv.szf.fastcall.api.event.FcSourceEventCut;
import priv.szf.fastcall.api.event.IdLevel;
import priv.szf.fastcall.api.model.dto.FcApiDTO;
import priv.szf.fastcall.api.model.dto.FcApiParamDTO;
import priv.szf.fastcall.api.model.dto.FcHeaderAssignDTO;
import priv.szf.fastcall.api.model.dto.FcSystemDTO;
import priv.szf.fastcall.api.model.mapping.FcApiMapping;
import priv.szf.fastcall.api.model.mapping.FcApiParamMapping;
import priv.szf.fastcall.api.model.mapping.FcHeaderAssignMapping;
import priv.szf.fastcall.api.model.mapping.FcSystemMapping;
import priv.szf.fastcall.api.model.vo.FcApiParamVO;
import priv.szf.fastcall.api.model.vo.FcApiVO;
import priv.szf.fastcall.api.model.vo.FcHeaderAssignVO;
import priv.szf.fastcall.api.model.vo.FcSystemVO;
import priv.szf.fastcall.common.FastCallConsts;
import priv.szf.fastcall.common.event.source.FcSourceEventType;
import priv.szf.fastcall.common.exception.FcDataNotFoundException;
import priv.szf.fastcall.data.entity.FcSystem;
import priv.szf.fastcall.data.manager.FcApiManager;
import priv.szf.fastcall.data.manager.FcApiParamManager;
import priv.szf.fastcall.data.manager.FcHeaderAssignManager;
import priv.szf.fastcall.data.manager.FcSystemManager;

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

    private final FcHeaderAssignManager headerAssignManager;

    private final FcSystemMapping systemMapping;

    private final FcApiMapping apiMapping;

    private final FcApiParamMapping apiParamMapping;

    private final FcHeaderAssignMapping headerAssignMapping;

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

    @GetMapping("/{systemId}/header_assign/all")
    public List<FcHeaderAssignVO> listAllHeaderAssignOfSystem(@PathVariable Long systemId) {
        log.trace("{}-Web request query header assign list of system[{}]", FastCallConsts.NAME, systemId);
        return Optional.of(systemId)
                .map(headerAssignManager::listAllHeaderAssignBySystemId)
                .map(headerAssignMapping::toVoList)
                .orElseThrow(()->new FcDataNotFoundException("The system[{}] no longer exists.", systemId));
    }

    @FcSourceEventCut(idIndex = 1)
    @PostMapping("/{systemId}/header_assign/save")
    public List<FcHeaderAssignVO> saveHeaderAssignOfSystem(@PathVariable Long systemId, @Valid @RequestBody List<FcHeaderAssignDTO> headerAssignList) {
        log.trace("{}-Web request save header assign list of system[{}]", FastCallConsts.NAME, systemId);
        return Optional.of(headerAssignList)
                .map(headerAssignMapping::toEntityList)
                .map(list -> headerAssignManager.save(systemId, list))
                .map(headerAssignMapping::toVoList)
                .orElseThrow(()->new FcDataNotFoundException("The system[{}] no longer exists.", systemId));
    }



}
