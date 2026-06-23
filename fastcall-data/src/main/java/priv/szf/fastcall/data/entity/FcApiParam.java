package priv.szf.fastcall.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import priv.szf.fastcall.common.FcParamPos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = FcEntityConsts.API_PARAM_TABLE)
@TableName(FcEntityConsts.API_PARAM_TABLE)
@Data
public class FcApiParam {

    @Id
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake",
            strategy = "priv.szf.fastcall.data.entity.SnowflakeIdGenerator")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Column(name = "api_id")
    @TableField("api_id")
    private Long apiId;

    @Column(name = "name")
    @TableField("name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    @TableField("position")
    private FcParamPos position;

    @Column(name = "json_obj")
    @TableField("json_obj")
    private Boolean jsonObj;

    @Column(name = "default_value")
    @TableField("default_value")
    private String defaultValue;

}
