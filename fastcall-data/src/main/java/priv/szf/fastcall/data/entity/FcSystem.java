package priv.szf.fastcall.data.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import priv.szf.fastcall.data.FcEntityConsts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Entity
@Table(name = FcEntityConsts.SYSTEM_TABLE)
@TableName(FcEntityConsts.SYSTEM_TABLE)
@Data
public class FcSystem {

    @Id
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake",
            strategy = "priv.szf.fastcall.data.entity.SnowflakeIdGenerator")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Column(name = "name")
    @TableField("name")
    private String name;

    @Column(name = "code")
    @TableField("code")
    private String code;

    @Column(name = "enable")
    @TableField("enable")
    private Boolean enable;

    @Column(name = "scope")
    @TableField("scope")
    private String scope;

    @Column(name = "host")
    @TableField("host")
    private String host;

    @Column(name = "connect_timeout")
    @TableField("connect_timeout")
    private Integer connectTimeout;

    @Column(name = "read_timeout")
    @TableField("read_timeout")
    private Integer readTimeout;

    @Column(name = "write_timeout")
    @TableField("write_timeout")
    private Integer writeTimeout;

    @Column(name = "description")
    @TableField("description")
    private String description;

    @Column(name = "update_at")
    @TableField("update_at")
    private LocalDateTime updateAt;

    @TableField(exist = false)
    @Transient
    private FcAuth auth;

}
