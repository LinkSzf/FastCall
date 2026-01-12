package priv.szf.fastcall.api.model.vo;

import lombok.Data;
import priv.szf.fastcall.common.FcHeaderOperation;
import priv.szf.fastcall.common.FcHeaderType;

@Data
public class FcHeaderAssignVO {

    private String id;

    private String sysId;

    private String apiId;

    private String name;

    private String value;

    private FcHeaderOperation operation;

    private FcHeaderType type;

    private String path;
}
