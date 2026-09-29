package priv.szf.fastcall.data.entity;

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

/**
 * Retry entity of the {@code fastcall_retry} table, holding the retry policy of one system.
 */
@Entity
@Table(name = FcEntityConsts.RETRY_TABLE)
@TableName(FcEntityConsts.RETRY_TABLE)
@Data
public class FcRetry {

    /** Primary key, generated as a snowflake id. */
    @Id
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake",
            strategy = "priv.szf.fastcall.data.entity.SnowflakeIdGenerator")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** Id of the system the retry policy belongs to; at most one policy per system. */
    @Column(name = "sys_id")
    @TableField("sys_id")
    private Long sysId;

    /** Maximum retries after the first call, where a value less than 1 disables retry; required. */
    @Column(name = "attempts")
    @TableField("attempts")
    private Integer attempts;

    /** Interval in milliseconds waited before each retry, where a value less than 1 retries at once; required. */
    @Column(name = "duration")
    @TableField("duration")
    private Long duration;

}
