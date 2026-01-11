package priv.szf.fastcall.common.model;

import lombok.Data;
import priv.szf.fastcall.common.FcHeaderOperation;
import priv.szf.fastcall.common.FcHeaderType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Data
public class FcHeaderAssignPak implements IEssentialCheck<FcHeaderAssignPak>, Serializable {

    private static final long serialVersionUID = 514535964634501136L;

    private String name;

    private String value;

    private FcHeaderOperation operation;

    private FcHeaderType type;

    private String path;

    @Override
    public List<Function<FcHeaderAssignPak, ?>> requireNonNull() {
        return Arrays.asList(
                FcHeaderAssignPak::getName,
                FcHeaderAssignPak::getOperation,
                FcHeaderAssignPak::getType
        );
    }
}
