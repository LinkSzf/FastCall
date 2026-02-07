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
import priv.szf.fastcall.api.model.vo.FcApiParamVO;
import priv.szf.fastcall.api.model.vo.FcApiVO;
import priv.szf.fastcall.api.model.vo.FcHeaderAssignVO;
import priv.szf.fastcall.api.model.vo.FcSystemVO;
import priv.szf.fastcall.api.service.FcApiParamService;
import priv.szf.fastcall.api.service.FcApiService;
import priv.szf.fastcall.api.service.FcHeaderAssignService;
import priv.szf.fastcall.api.service.FcSystemService;
import priv.szf.fastcall.common.FastCallConsts;
import priv.szf.fastcall.common.event.source.FcSourceEventType;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/fc/system")
@RequiredArgsConstructor
public class FcController {

    private final FcSystemService systemService;

    private final FcApiService apiService;

    private final FcApiParamService apiParamService;

    private final FcHeaderAssignService headerAssignService;

    @GetMapping("/all")
    public List<FcSystemVO> listAll(){
        log.trace("{}-Web request query system list", FastCallConsts.NAME);
        return systemService.listAll();
    }

    @GetMapping("/{id}")
    public FcSystemVO findOne(@PathVariable("id") Long id){
        log.trace("{}-Web request query system[{}]", FastCallConsts.NAME, id);
        return systemService.getById(id);
    }

    @FcSourceEventCut(entity = FcSystemDTO.class)
    @PostMapping("/save")
    public FcSystemVO saveOne(@Valid @RequestBody FcSystemDTO dto){
        log.trace("{}-Web request save system[{}]", FastCallConsts.NAME, dto.getCode());
        return systemService.save(dto);
    }

    @FcSourceEventCut(entity = Long.class, type = FcSourceEventType.DELETE)
    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") Long id){
        log.trace("{}-Web request delete system[{}]", FastCallConsts.NAME, id);
        systemService.removeById(id);
    }

    @GetMapping("/{id}/api/all")
    public List<FcApiVO> listAllApiOfSystem(@PathVariable("id") Long id){
        log.trace("{}-Web request query api list of system[{}]", FastCallConsts.NAME, id);
        return apiService.listAllBySystemId(id);
    }

    @FcSourceEventCut(entity = Long.class)
    @PostMapping("/{id}/api/save")
    public List<FcApiVO> saveApiOfSystem(@PathVariable("id") Long id, @Valid @RequestBody List<FcApiDTO> apiList){
        log.trace("{}-Web request save api of system[{}]", FastCallConsts.NAME, id);
        return apiService.save(id, apiList);
    }

    @GetMapping("/api/{id}/param/all")
    public List<FcApiParamVO> listAllApiParamsOfApi(@PathVariable("id") Long id) {
        log.trace("{}-Web request query api param list of api[{}]", FastCallConsts.NAME, id);
        return apiParamService.listAllByApiId(id);
    }

    @FcSourceEventCut(entity = Long.class, level = IdLevel.API)
    @PostMapping("/api/{id}/param/save")
    public List<FcApiParamVO> saveApiParamsOfApi(@PathVariable("id") Long id, @Valid @RequestBody List<FcApiParamDTO> apiParamList) {
        log.trace("{}-Web request save api param list of api[{}]", FastCallConsts.NAME, id);
        return apiParamService.save(id, apiParamList);
    }

    @GetMapping("/{id}/header_assign/all")
    public List<FcHeaderAssignVO> listAllHeaderAssignOfSystem(@PathVariable("id") Long id) {
        log.trace("{}-Web request query header assign list of system[{}]", FastCallConsts.NAME, id);
        return headerAssignService.listAllHeaderAssignBySystemId(id);
    }

    @FcSourceEventCut(entity = Long.class, level = IdLevel.SYSTEM)
    @PostMapping("/{id}/header_assign/save")
    public List<FcHeaderAssignVO> saveHeaderAssignOfSystem(@PathVariable("id") Long id, @Valid @RequestBody List<FcHeaderAssignDTO> headerAssignList) {
        log.trace("{}-Web request save header assign list of system[{}]", FastCallConsts.NAME, id);
        return headerAssignService.save(id, headerAssignList);
    }



}
