package priv.szf.fastcall.api.event;


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
        String code = resolveSystemCode(annotation, joinPoint.getArgs());
        Object result = joinPoint.proceed();

        FcSourceEventType type = annotation.type();
        IFcSourceEvent sourceEvent = new FcSourceEvent(code, type);
        sourcePublisher.publish(sourceEvent);
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
                    code = getSystemCodeByApiId((Long) arg);
                } else if (annotation.level() == IdLevel.SYSTEM) {
                    code = getSystemCodeById((Long) arg);
                }
            }
            break;
        }
        return code;
    }

    private String getSystemCodeById(Long id) {
        FcSystem system = systemDao.getOneById(id);
        return Optional.ofNullable(system)
                .map(FcSystem::getCode)
                .orElse(null);
    }

    private String getSystemCodeByApiId(Long id) {
        FcApi api = apiDao.getOneById(id);
        return Optional.ofNullable(api)
                .map(FcApi::getSysId)
                .map(this::getSystemCodeById)
                .orElse(null);
    }
}
