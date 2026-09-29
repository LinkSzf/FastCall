package io.github.linkszf.fastcall.data.manager.support;

import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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

    public static <D> void assignParentId(
            List<D> incomingList,
            Long parentId,
            BiConsumer<D, Long> parentIdSetter
    ) {
        if (CollectionUtils.isEmpty(incomingList) || parentIdSetter == null) {
            return;
        }

        incomingList.stream()
                .filter(Objects::nonNull)
                .forEach(dto -> parentIdSetter.accept(dto, parentId));
    }

    public static <E, D> void shrinkToIncoming(
            List<E> existingList,
            Function<E, Long> existingIdExtractor,
            List<D> incomingList,
            Function<D, Long> incomingIdExtractor,
            Consumer<List<Long>> removeAction
    ) {
        if (removeAction == null) {
            return;
        }

        List<Long> removeIds = resolveRemoveIds(
                existingList,
                existingIdExtractor,
                incomingList,
                incomingIdExtractor
        );
        if (!CollectionUtils.isEmpty(removeIds)) {
            removeAction.accept(removeIds);
        }
    }

}
