package priv.szf.fastcall.api.event;


import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
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

@Aspect
@RequiredArgsConstructor
@Component
public class FcSourceEventAspect {

    private final FcSourceEventPublisher sourcePublisher;

    private final FcSystemDao systemDao;

    private final FcApiDao apiDao;

    @AfterReturning(
            pointcut = "@annotation(FcSourceEventCut)",
            returning = "result"
    )
    public void publishEventAfterMethodExecution(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        FcSourceEventCut annotation = signature.getMethod().getAnnotation(FcSourceEventCut.class);
        Class<?> entity = annotation.entity();
        Object[] args = joinPoint.getArgs();

        String code = null;
        for (Object arg : args) {
            if (!entity.isInstance(arg)) {
                continue;
            }

            if (entity == FcSystemDTO.class) {
                code = ((FcSystemDTO) arg).getCode();
                break;
            }

            if (entity == Long.class) {
                if (annotation.level() == IdLevel.API) {
                    code = getSystemCodeByApiId((Long) arg);
                }
                if (annotation.level() == IdLevel.SYSTEM) {
                    code = getSystemCodeById((Long) arg);
                }
            }

            break;
        }

        FcSourceEventType type = annotation.type();

        IFcSourceEvent sourceEvent = new FcSourceEvent(code, type);

        sourcePublisher.publish(sourceEvent);
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
