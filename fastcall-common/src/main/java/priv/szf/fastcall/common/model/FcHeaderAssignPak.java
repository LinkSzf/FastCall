package priv.szf.fastcall.common.model;

import lombok.Builder;
import lombok.Getter;
import priv.szf.fastcall.common.FcHeaderOperation;
import priv.szf.fastcall.common.FcHeaderType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Builder
@Getter
public class FcHeaderAssignPak implements IFcPak {

    private static final long serialVersionUID = 514535964634501136L;

    private final String name;

    private final String value;

    private final FcHeaderOperation operation;

    private final FcHeaderType type;

    private String path;

    @Override
    public List<Supplier<?>> requireNonNull() {
        return Arrays.asList(
                this::getName,
                this::getOperation,
                this::getType
        );
    }
}
