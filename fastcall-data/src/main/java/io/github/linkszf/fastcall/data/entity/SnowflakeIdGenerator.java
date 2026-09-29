package io.github.linkszf.fastcall.data.entity;


import cn.hutool.core.util.IdUtil;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Assigned;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Objects;
import java.util.Properties;

/**
 * Hibernate identifier generator that keeps an explicitly assigned id and otherwise generates a snowflake id.
 * Declared per entity via {@code @GenericGenerator}; uniqueness relies on Hutool's thread-safe snowflake.
 */
public class SnowflakeIdGenerator extends Assigned implements IdentifierGenerator {

    private String entityName;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        final Serializable manualId = session.getEntityPersister( entityName, obj ).getIdentifier( obj, session );
        return (Objects.nonNull(manualId)) ? manualId : IdUtil.getSnowflakeNextId();
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        entityName = params.getProperty( ENTITY_NAME );
        if ( entityName == null ) {
            throw new MappingException("no entity name");
        }
    }
}