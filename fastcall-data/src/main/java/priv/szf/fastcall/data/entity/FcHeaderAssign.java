package priv.szf.fastcall.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import priv.szf.fastcall.common.FcHeaderOperation;
import priv.szf.fastcall.common.FcHeaderType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = FcEntityConsts.HEADER_ASSIGN_TABLE)
@TableName(FcEntityConsts.HEADER_ASSIGN_TABLE)
@Data
public class FcHeaderAssign {

    @Id
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake",
            strategy = "priv.szf.fastcall.data.entity.SnowflakeIdGenerator")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Column(name = "sys_id")
    @TableField("sys_id")
    private Long sysId;

    @Column(name = "api_id")
    @TableField("api_id")
    private Long apiId;

    @Column(name = "name")
    @TableField("name")
    private String name;

    @Column(name = "value")
    @TableField("value")
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation")
    @TableField("operation")
    private FcHeaderOperation operation;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @TableField("type")
    private FcHeaderType type;

    @Column(name = "path")
    @TableField("path")
    private String path;

}
