package priv.szf.fastcall.core.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    private final String CREATE_AT = "createAt";

    private final String UPDATE_AT = "updateAt";

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, CREATE_AT, LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, UPDATE_AT, LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, UPDATE_AT, LocalDateTime.class, LocalDateTime.now());
    }
}
