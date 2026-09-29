package priv.szf.fastcall.data.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import priv.szf.fastcall.common.FcAuthType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;

/**
 * System entity of the {@code fastcall_system} table, describing one third-party system and how it is reached.
 */
@Entity
@Table(name = FcEntityConsts.SYSTEM_TABLE)
@TableName(FcEntityConsts.SYSTEM_TABLE)
@Data
public class FcSystem {

    /** Primary key, generated as a snowflake id. */
    @Id
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake",
            strategy = "priv.szf.fastcall.data.entity.SnowflakeIdGenerator")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** Display name of the system; required and limited to 50 characters. */
    @Column(name = "name")
    @TableField("name")
    private String name;

    /** Unique system code used to obtain the client; required and unique across all systems. */
    @Column(name = "code")
    @TableField("code")
    private String code;

    /** Whether the system is callable, where a disabled system rejects client creation. */
    @Column(name = "enable")
    @TableField("enable")
    private Boolean enable;

    /** Base access address of the system, used as the host of calls that carry no host of their own. */
    @Column(name = "host")
    @TableField("host")
    private String host;

    /** Authentication type of the system, where {@code NONE} applies no credential. */
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type")
    @TableField("auth_type")
    private FcAuthType authType;

    /** Connect timeout in seconds overriding the global client setting; {@code null} keeps the global value. */
    @Column(name = "connect_timeout")
    @TableField("connect_timeout")
    private Integer connectTimeout;

    /** Read timeout in seconds overriding the global client setting; {@code null} keeps the global value. */
    @Column(name = "read_timeout")
    @TableField("read_timeout")
    private Integer readTimeout;

    /** Write timeout in seconds overriding the global client setting; {@code null} keeps the global value. */
    @Column(name = "write_timeout")
    @TableField("write_timeout")
    private Integer writeTimeout;

    /** Free-form description of the system, at most 200 characters. */
    @Column(name = "description")
    @TableField("description")
    private String description;

    /** Time the system record was last saved. */
    @Column(name = "update_at")
    @TableField("update_at")
    private LocalDateTime updateAt;

    /** Authentication record joined for the maintenance api; not a column of this table. */
    @TableField(exist = false)
    @Transient
    private FcAuth auth;

    /** Retry record joined for the maintenance api; not a column of this table. */
    @TableField(exist = false)
    @Transient
    private FcRetry retry;

}
