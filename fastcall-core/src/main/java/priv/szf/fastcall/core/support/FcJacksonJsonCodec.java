package priv.szf.fastcall.core.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import lombok.Getter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.common.json.FcJsonCodec;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class FcJacksonJsonCodec implements FcJsonCodec {

    @Getter
    private final ObjectMapper objectMapper;

    public FcJacksonJsonCodec() {
        this(initObjectMapper());
    }

    public FcJacksonJsonCodec(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    private static ObjectMapper initObjectMapper() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .featuresToDisable(
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                        SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS
                )
                .build();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return objectMapper;
    }

    @Override
    public String write(Object value) {
        try {
            return this.objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new FcUnexpectedException(
                    e,
                    "Failed to serialize object of type[{}] to JSON",
                    Objects.isNull(value) ? "null" : value.getClass().getName()
            );
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T read(String json, Type type) {
        if (Objects.isNull(json)) {
            return null;
        }

        JavaType javaType = this.objectMapper.getTypeFactory().constructType(type);
        try {
            return this.objectMapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            throw new FcUnexpectedException(
                    e,
                    "Failed to deserialize JSON to type[{}], body length[{}]",
                    javaType.getTypeName(),
                    json.length()
            );
        }
    }

    @Override
    public Map<String, Object> toPropertyMap(Object bean) {
        if (Objects.isNull(bean)) {
            return null;
        }

        SerializationConfig config = this.objectMapper.getSerializationConfig();
        JavaType javaType = this.objectMapper.getTypeFactory().constructType(bean.getClass());
        BeanDescription description = config.introspect(javaType);
        if (Objects.nonNull(description.findJsonValueAccessor())) {
            // This type serializes to a scalar, so it cannot be expanded into multiple key-value pairs
            return null;
        }

        Set<String> ignoredNames = resolveIgnoredNames(config, description);
        Map<String, Object> properties = new LinkedHashMap<>();
        for (BeanPropertyDefinition property : description.findProperties()) {
            if (!property.couldSerialize() || isIgnored(property, ignoredNames)) {
                continue;
            }
            AnnotatedMember accessor = property.getAccessor();
            if (Objects.isNull(accessor)) {
                continue;
            }
            properties.put(property.getName(), readMemberValue(accessor, bean));
        }
        return properties;
    }

    @Override
    public Object toScalar(Object value) {
        if (Objects.isNull(value)) {
            return null;
        }
        return this.objectMapper.convertValue(value, Object.class);
    }

    /**
     * Reads the {@code @JsonIgnoreProperties} declared on the type.
     * Jackson introspection does not remove these properties, and serialization filters them separately in BeanSerializerFactory, so they must be handled explicitly here.
     */
    private Set<String> resolveIgnoredNames(SerializationConfig config, BeanDescription description) {
        JsonIgnoreProperties.Value ignoral = config.getAnnotationIntrospector()
                .findPropertyIgnoralByName(config, description.getClassInfo());
        if (Objects.isNull(ignoral)) {
            return Collections.emptySet();
        }
        return ignoral.findIgnoredForSerialization();
    }

    private boolean isIgnored(BeanPropertyDefinition property, Set<String> ignoredNames) {
        if (ignoredNames.isEmpty()) {
            return false;
        }
        return ignoredNames.contains("*")
                || ignoredNames.contains(property.getName())
                || ignoredNames.contains(property.getInternalName());
    }

    private Object readMemberValue(AnnotatedMember accessor, Object bean) {
        Member member = accessor.getMember();
        try {
            if (member instanceof Method) {
                Method method = (Method) member;
                makeAccessible(method);
                return method.invoke(bean);
            }
            if (member instanceof Field) {
                Field field = (Field) member;
                makeAccessible(field);
                return field.get(bean);
            }
        } catch (ReflectiveOperationException e) {
            throw new FcUnexpectedException(
                    e,
                    "Failed to read property[{}] of type[{}]",
                    accessor.getName(),
                    bean.getClass().getName()
            );
        }
        throw new FcUnexpectedException(
                "Unsupported property accessor[{}] of type[{}]",
                accessor.getName(),
                bean.getClass().getName()
        );
    }

    private void makeAccessible(Member member) {
        try {
            if (member instanceof Method) {
                ((Method) member).setAccessible(true);
            } else if (member instanceof Field) {
                ((Field) member).setAccessible(true);
            }
        } catch (RuntimeException ignore) {
            // Forcing access to open may not be allowed in a modular environment; public members can still be invoked normally
        }
    }
}
