package io.github.linkszf.fastcall.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Api entity of the {@code fastcall_api} table, describing one callable interface of a system.
 */
@Entity
@Table(name = FcEntityConsts.API_TABLE)
@TableName(FcEntityConsts.API_TABLE)
@Data
public class FcApi {

    /** Primary key, generated as a snowflake id. */
    @Id
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake",
            strategy = "io.github.linkszf.fastcall.data.entity.SnowflakeIdGenerator")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** Id of the system that owns the api. */
    @Column(name = "sys_id")
    @TableField("sys_id")
    private Long sysId;

    /** Api name used by callers to select the api; required and unique within the system. */
    @Column(name = "name")
    @TableField("name")
    private String name;

    /** Request path of the api, required and combined with the host to build the request url. */
    @Column(name = "path")
    @TableField("path")
    private String path;

    /** HTTP method of the api, stored as the name of an {@code FcRequestMethod} value such as {@code GET}. */
    @Column(name = "method")
    @TableField("method")
    private String method;

    /** Connect timeout in seconds overriding the system setting; {@code null} keeps the outer value. */
    @Column(name = "connect_timeout")
    @TableField("connect_timeout")
    private Integer connectTimeout;

    /** Read timeout in seconds overriding the system setting; {@code null} keeps the outer value. */
    @Column(name = "read_timeout")
    @TableField("read_timeout")
    private Integer readTimeout;

    /** Write timeout in seconds overriding the system setting; {@code null} keeps the outer value. */
    @Column(name = "write_timeout")
    @TableField("write_timeout")
    private Integer writeTimeout;

    /** Time of the last successful api call, updated by the api request event listener. */
    @Column(name = "last_access_time")
    @TableField("last_access_time")
    private LocalDateTime lastAccessTime;

    /** Host used for this api instead of the system host; {@code null} keeps the system host. */
    @Column(name = "particular_host")
    @TableField("particular_host")
    private String particularHost;

    /** Free-form description of the api, at most 200 characters. */
    @Column(name = "description")
    @TableField("description")
    private String description;
}
