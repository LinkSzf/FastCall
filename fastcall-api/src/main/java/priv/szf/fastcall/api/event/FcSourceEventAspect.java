package priv.szf.fastcall.api.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import priv.szf.fastcall.api.model.dto.FcSystemDTO;
import priv.szf.fastcall.api.service.port.IFcSourceCodeResolver;
import priv.szf.fastcall.common.event.source.FcSourceEventType;
import priv.szf.fastcall.common.event.source.IFcSourceEvent;

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
                    code = sourceCodeResolver.getSystemCodeByApiId((Long) arg);
                } else if (annotation.level() == IdLevel.SYSTEM) {
                    code = sourceCodeResolver.getSystemCodeBySystemId((Long) arg);
                }
            }
            break;
        }
        return code;
    }
}
