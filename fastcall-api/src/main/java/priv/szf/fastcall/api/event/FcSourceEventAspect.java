package priv.szf.fastcall.api.event;


import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.api.model.dto.FcSystemDTO;
import priv.szf.fastcall.common.event.source.FcSourceEventType;
import priv.szf.fastcall.common.event.source.IFcSourceEvent;
import priv.szf.fastcall.data.entity.FcApi;
import priv.szf.fastcall.data.entity.FcSystem;
import priv.szf.fastcall.data.mapper.FcApiDao;
import priv.szf.fastcall.data.mapper.FcSystemDao;

import java.util.Optional;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class FcSourceEventAspect {

    private final FcSourceEventPublisher sourcePublisher;

    private final FcSystemDao systemDao;

    private final FcApiDao apiDao;

    @Around("@annotation(FcSourceEventCut)")
    public Object publishEventAfterMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        FcSourceEventCut annotation = signature.getMethod().getAnnotation(FcSourceEventCut.class);
        Object[] args = joinPoint.getArgs();
        String oldCode = resolveOldSystemCode(annotation, args);
        String newCode = resolveSystemCode(annotation, args);
        Object result = joinPoint.proceed();

        FcSourceEventType type = annotation.type();
        publishOne(type, newCode);
        if (shouldPublishOldCode(type, oldCode, newCode)) {
            publishOne(FcSourceEventType.DELETE, oldCode);
        }
        return result;
    }

    private String resolveSystemCode(FcSourceEventCut annotation, Object[] args) {
        int index = annotation.idIndex() - 1;
        Object idObj = args[index];
        String code = null;
        if (idObj instanceof FcSystemDTO) {
            code = ((FcSystemDTO) idObj).getCode();
        } else if (idObj instanceof Long) {
            if (annotation.level() == IdLevel.API) {
                code = getSystemCodeByApiId((Long) idObj);
            } else if (annotation.level() == IdLevel.SYSTEM) {
                code = getSystemCodeBySystemId((Long) idObj);
            }
        }
        return code;
    }

    private String resolveOldSystemCode(FcSourceEventCut annotation, Object[] args) {
        int index = annotation.idIndex() - 1;
        Object idObj = args[index];
        if (!(idObj instanceof FcSystemDTO)) {
            return resolveSystemCode(annotation, args);
        }
        FcSystemDTO systemDTO = (FcSystemDTO) idObj;
        return Optional.of(systemDTO)
                .map(FcSystemDTO::getId)
                .map(this::getSystemCodeBySystemId)
                .orElse(null);
    }

    private boolean shouldPublishOldCode(FcSourceEventType type, String oldCode, String newCode) {
        return type == FcSourceEventType.UPDATE
                && StrUtil.isNotBlank(oldCode)
                && !StrUtil.equals(oldCode, newCode);
    }

    private void publishOne(FcSourceEventType type, String code) {
        if (StrUtil.isBlank(code)) {
            return;
        }
        IFcSourceEvent sourceEvent = new FcSourceEvent(code, type);
        sourcePublisher.publish(sourceEvent);
    }

    private String getSystemCodeBySystemId(Long systemId) {
        return Optional.ofNullable(systemId)
                .map(systemDao::getOneById)
                .map(FcSystem::getCode)
                .orElse(null);
    }

    private String getSystemCodeByApiId(Long apiId) {
        return Optional.ofNullable(apiId)
                .map(apiDao::getOneById)
                .map(FcApi::getSysId)
                .map(this::getSystemCodeBySystemId)
                .orElse(null);
    }
}
