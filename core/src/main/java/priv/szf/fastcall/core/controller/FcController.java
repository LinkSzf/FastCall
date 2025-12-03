package priv.szf.fastcall.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.szf.fastcall.core.model.dto.FcSystemDTO;
import priv.szf.fastcall.core.model.vo.FcSystemVO;
import priv.szf.fastcall.core.service.FcSystemService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/fc/system")
@RequiredArgsConstructor
public class FcController {

    private final FcSystemService systemService;

    @GetMapping("/all")
    public List<FcSystemVO> listAll(){
        return systemService.listAll();
    }

    @GetMapping("/{id}")
    public FcSystemVO findOne(@PathVariable("id") Long id){
        return systemService.getById(id);
    }

    @PostMapping("/save")
    public void saveOne(@Valid @RequestBody FcSystemDTO dto){
        systemService.saveOrUpdate(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable("id") Long id){
        systemService.removeById(id);
    }

}
