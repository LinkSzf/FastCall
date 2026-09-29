package io.github.linkszf.fastcall.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import io.github.linkszf.fastcall.common.FcTimeSpan;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Rate limit entity of the {@code fastcall_rate_limit} table, limiting the request rate of one system.
 */
@Entity
@Table(name = FcEntityConsts.RATE_LIMIT_TABLE)
@TableName(FcEntityConsts.RATE_LIMIT_TABLE)
@Data
public class FcRateLimit {

    /** Primary key, generated as a snowflake id. */
    @Id
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake",
            strategy = "io.github.linkszf.fastcall.data.entity.SnowflakeIdGenerator")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** Id of the system the limit belongs to; several windows may be limited for one system. */
    @Column(name = "sys_id")
    @TableField("sys_id")
    private Long sysId;

    /** Whether the limit is used by the rate limit filter, which only loads enabled records. */
    @Column(name = "enable")
    @TableField("enable")
    private Boolean enable;

    /** Time window the limit is counted over, from {@code SECONDS} up to {@code YEARS}. */
    @Enumerated(EnumType.STRING)
    @Column(name = "span")
    @TableField("span")
    private FcTimeSpan span;

    /** Start time of the current counting window; {@code null} means no window is open yet. */
    @Column(name = "last_time")
    @TableField("last_time")
    private LocalDateTime lastTime;

    /** Requests allowed within one {@code span} window; limits with a value less than 1 are ignored. */
    @Column(name = "maximum")
    @TableField("maximum")
    private Long maximum;

    /** Requests already counted in the current window, reset when a new window starts. */
    @Column(name = "current_count")
    @TableField("current_count")
    private Long currentCount;

}
