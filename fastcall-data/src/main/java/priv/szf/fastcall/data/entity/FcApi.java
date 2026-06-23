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
import java.time.LocalDateTime;

@Entity
@Table(name = FcEntityConsts.API_TABLE)
@TableName(FcEntityConsts.API_TABLE)
@Data
public class FcApi {

    @Id
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake",
            strategy = "priv.szf.fastcall.data.entity.SnowflakeIdGenerator")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Column(name = "sys_id")
    @TableField("sys_id")
    private Long sysId;

    @Column(name = "name")
    @TableField("name")
    private String name;

    @Column(name = "path")
    @TableField("path")
    private String path;

    @Column(name = "method")
    @TableField("method")
    private String method;

    @Column(name = "connect_timeout")
    @TableField("connect_timeout")
    private Integer connectTimeout;

    @Column(name = "read_timeout")
    @TableField("read_timeout")
    private Integer readTimeout;

    @Column(name = "write_timeout")
    @TableField("write_timeout")
    private Integer writeTimeout;

    @Column(name = "last_access_time")
    @TableField("last_access_time")
    private LocalDateTime lastAccessTime;

    @Column(name = "particular_host")
    @TableField("particular_host")
    private String particularHost;

    @Column(name = "description")
    @TableField("description")
    private String description;
}
