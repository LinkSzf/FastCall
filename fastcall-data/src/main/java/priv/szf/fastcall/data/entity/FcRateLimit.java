package priv.szf.fastcall.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import priv.szf.fastcall.common.FcTimeSpan;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = FcEntityConsts.RATE_LIMIT_TABLE)
@TableName(FcEntityConsts.RATE_LIMIT_TABLE)
@Data
public class FcRateLimit {

    @Id
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake",
            strategy = "priv.szf.fastcall.data.entity.SnowflakeIdGenerator")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Column(name = "sys_id")
    @TableField("sys_id")
    private Long sysId;

    @Column(name = "enable")
    @TableField("enable")
    private Boolean enable;

    @Enumerated(EnumType.STRING)
    @Column(name = "span")
    @TableField("span")
    private FcTimeSpan span;

    @Column(name = "time")
    @TableField("time")
    private LocalDateTime time;

    @Column(name = "maximum")
    @TableField("maximum")
    private Long maximum;

    @Column(name = "current")
    @TableField("current")
    private Long current;

}
