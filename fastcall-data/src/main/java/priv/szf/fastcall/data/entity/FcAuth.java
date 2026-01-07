package priv.szf.fastcall.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import priv.szf.fastcall.common.FcAuthType;
import priv.szf.fastcall.data.FcEntityConsts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = FcEntityConsts.AUTH_TABLE)
@TableName(FcEntityConsts.AUTH_TABLE)
@Data
public class FcAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Column(name = "sys_id")
    @TableField("sys_id")
    private Long sysId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @TableField("type")
    private FcAuthType type;

    @Column(name = "path")
    @TableField("path")
    private String path;

    @Column(name = "content", columnDefinition = "json")
    @TableField("content")
    private String content;

    @Column(name = "expiration")
    @TableField("expiration")
    private Long expiration;

    @Column(name = "last_access_time")
    @TableField("last_access_time")
    private LocalDateTime lastAccessTime;

    @Column(name = "particular_host")
    @TableField("particular_host")
    private String particularHost;

}
