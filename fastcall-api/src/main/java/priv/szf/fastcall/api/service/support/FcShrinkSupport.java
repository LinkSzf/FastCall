package priv.szf.fastcall.api.service.support;

import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class FcShrinkSupport {

    private FcShrinkSupport() {
    }

    public static <E, D> List<Long> resolveRemoveIds(
            List<E> existingList,
            Function<E, Long> existingIdExtractor,
            List<D> incomingList,
            Function<D, Long> incomingIdExtractor
    ) {
        if (CollectionUtils.isEmpty(existingList)) {
            return Collections.emptyList();
        }

        Set<Long> keepIds = CollectionUtils.isEmpty(incomingList)
                ? Collections.emptySet()
                : incomingList.stream()
                .filter(Objects::nonNull)
                .map(incomingIdExtractor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return existingList.stream()
                .filter(Objects::nonNull)
                .map(existingIdExtractor)
                .filter(Objects::nonNull)
                .filter(id -> !keepIds.contains(id))
                .collect(Collectors.toList());
    }

}
