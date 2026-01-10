package priv.szf.fastcall.api.model.mapping;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.common.FcFuncScope;
import priv.szf.fastcall.data.FcDataConsts;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FcSystemFuncScopeConverter {

    public Set<FcFuncScope> toEnumSet(String scope) {
        if (StrUtil.isBlank(scope)) {
            return Collections.emptySet();
        }
        return Arrays.stream(scope.split(FcDataConsts.SYSTEM_FUNC_SCOPE_DELIMITER))
                .map(String::trim)
                .map(FcFuncScope::valueOf)
                .collect(Collectors.toSet());
    }

    public String toString(Set<FcFuncScope> scopes) {
        if (CollectionUtil.isEmpty(scopes)) {
            return null;
        }
        return scopes.stream()
                .map(Enum::name)
                .collect(Collectors.joining(FcDataConsts.SYSTEM_FUNC_SCOPE_DELIMITER));
    }

}
