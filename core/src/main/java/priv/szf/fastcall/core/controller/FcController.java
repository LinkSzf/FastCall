package priv.szf.fastcall.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import priv.szf.fastcall.core.FastCall;
import priv.szf.fastcall.core.call.FastCallResponse;
import priv.szf.fastcall.core.model.dto.FcApiDTO;
import priv.szf.fastcall.core.model.dto.FcSystemDTO;
import priv.szf.fastcall.core.model.vo.FcApiVO;
import priv.szf.fastcall.core.model.vo.FcSystemVO;
import priv.szf.fastcall.core.service.FcApiService;
import priv.szf.fastcall.core.service.FcSystemService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/fc/system")
@RequiredArgsConstructor
public class FcController {

    private final FcSystemService systemService;

    private final FcApiService apiService;

    private final FastCall fastCall;

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
        return systemService.saveOrUpdate(dto);
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
        return apiService.saveOrUpdate(id, apiList);
    }

    @GetMapping("/api/test")
    public FastCallResponse<?> ApiTest(@RequestParam String systemCode, @RequestParam String apiName) {
        return fastCall.getClient(systemCode)
                .newCall()
                .url(apiName)
                .call();
    }

}