package priv.szf.fastcall.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.szf.fastcall.api.model.dto.FcApiDTO;
import priv.szf.fastcall.api.model.dto.FcApiParamDTO;
import priv.szf.fastcall.api.model.dto.FcSystemDTO;
import priv.szf.fastcall.api.model.vo.FcApiParamVO;
import priv.szf.fastcall.api.model.vo.FcApiVO;
import priv.szf.fastcall.api.model.vo.FcSystemVO;
import priv.szf.fastcall.api.service.FcApiParamService;
import priv.szf.fastcall.api.service.FcApiService;
import priv.szf.fastcall.api.service.FcSystemService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/fc/system")
@RequiredArgsConstructor
public class FcController {

    private final FcSystemService systemService;

    private final FcApiService apiService;

    private final FcApiParamService apiParamService;

    @GetMapping("/all")
    public List<FcSystemVO> listAll(){
        return systemService.listAll();
    }

    @GetMapping("/{id}")
    public FcSystemVO findOne(@PathVariable("id") Long id){
        return systemService.getById(id);
    }

    @PostMapping("/save")
    public FcSystemVO saveOne(@Valid @RequestBody FcSystemDTO dto){
        return systemService.save(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") Long id){
        systemService.removeById(id);
    }

    @GetMapping("/{id}/api/all")
    public List<FcApiVO> listAllApiOfSystem(@PathVariable("id") Long id){
        return apiService.listAllBySystemId(id);
    }

    @PostMapping("/{id}/api/save")
    public List<FcApiVO> saveApiOfSystem(@PathVariable("id") Long id, @Valid @RequestBody List<FcApiDTO> apiList){
        return apiService.save(id, apiList);
    }

    @GetMapping("/api/{id}/param/all")
    public List<FcApiParamVO> listAllApiParamsOfApi(@PathVariable("id") Long id) {
        return apiParamService.listAllByApiId(id);
    }

    @PostMapping("/api/{id}/param/save")
    public List<FcApiParamVO> saveApiParamsOfApi(@PathVariable("id") Long id, @Valid @RequestBody List<FcApiParamDTO> apiParamList) {
        return apiParamService.save(id, apiParamList);
    }

}