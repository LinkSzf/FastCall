package priv.szf.fastcall.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
import java.time.LocalDateTime;

/**
 * Authentication entity of the {@code fastcall_auth} table, holding the credential of one system and how to renew it.
 */
@Entity
@Table(name = FcEntityConsts.AUTH_TABLE)
@TableName(FcEntityConsts.AUTH_TABLE)
@Data
public class FcAuth {

    /** Primary key, generated as a snowflake id. */
    @Id
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake",
            strategy = "priv.szf.fastcall.data.entity.SnowflakeIdGenerator")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** Id of the system that owns this authentication record; at most one record per system. */
    @Column(name = "sys_id")
    @TableField("sys_id")
    private Long sysId;

    /** Authentication type; it selects the credential content class and the matching auth handler. */
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @TableField("type")
    private FcAuthType type;

    /** Request path of the authentication endpoint, relative to the host used for authentication. */
    @Column(name = "path")
    @TableField("path")
    private String path;

    /** Credential content as JSON, bound to the content class of the auth type when the pak is built. */
    @Column(name = "content", columnDefinition = "json")
    @TableField("content")
    private String content;

    /** Credential lifetime of the auth record; not consulted by the current credential providers. */
    @Column(name = "expiration")
    @TableField("expiration")
    private Long expiration;

    /** Time of the last successful authentication call, updated by the auth request event listener. */
    @Column(name = "last_access_time")
    @TableField("last_access_time")
    private LocalDateTime lastAccessTime;

    /** Host used for authentication requests instead of the system host; {@code null} keeps the system host. */
    @Column(name = "particular_host")
    @TableField("particular_host")
    private String particularHost;

    /** Response code marking the credential as invalid; {@code null} means the default code 401. */
    @Column(name = "unauthorized_code")
    @TableField("unauthorized_code")
    private Integer unauthorizedCode;

}
