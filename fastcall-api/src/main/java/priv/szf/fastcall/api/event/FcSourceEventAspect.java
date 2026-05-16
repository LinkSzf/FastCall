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
import priv.szf.fastcall.api.service.IFcSourceCodeResolver;
import priv.szf.fastcall.common.event.source.FcSourceEventType;
import priv.szf.fastcall.common.event.source.IFcSourceEvent;

import java.util.Objects;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class FcSourceEventAspect {

    private final FcSourceEventPublisher sourcePublisher;

    private final IFcSourceCodeResolver sourceCodeResolver;

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
        Class<?> entity = annotation.entity();
        String code = null;
        for (Object arg : args) {
            if (!entity.isInstance(arg)) {
                continue;
            }
            if (entity == FcSystemDTO.class) {
                code = ((FcSystemDTO) arg).getCode();
            } else if (entity == Long.class) {
                if (annotation.level() == IdLevel.API) {
                    code = sourceCodeResolver.getSystemCodeByApiId((Long) arg);
                } else if (annotation.level() == IdLevel.SYSTEM) {
                    code = sourceCodeResolver.getSystemCodeBySystemId((Long) arg);
                }
            }
            break;
        }
        return code;
    }

    private String resolveOldSystemCode(FcSourceEventCut annotation, Object[] args) {
        if (annotation.entity() != FcSystemDTO.class) {
            return resolveSystemCode(annotation, args);
        }

        for (Object arg : args) {
            if (!(arg instanceof FcSystemDTO)) {
                continue;
            }
            FcSystemDTO systemDTO = (FcSystemDTO) arg;
            Long systemId = systemDTO.getId();
            if (Objects.nonNull(systemId)) {
                return sourceCodeResolver.getSystemCodeBySystemId(systemId);
            }
            break;
        }
        return null;
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
}
