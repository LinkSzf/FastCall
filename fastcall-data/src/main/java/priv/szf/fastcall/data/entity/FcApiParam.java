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

/**
 * Api parameter entity of the {@code fastcall_api_param} table, declaring one header, query parameter or body.
 */
@Entity
@Table(name = FcEntityConsts.API_PARAM_TABLE)
@TableName(FcEntityConsts.API_PARAM_TABLE)
@Data
public class FcApiParam {

    /** Primary key, generated as a snowflake id. */
    @Id
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake",
            strategy = "priv.szf.fastcall.data.entity.SnowflakeIdGenerator")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** Id of the api this parameter belongs to. */
    @Column(name = "api_id")
    @TableField("api_id")
    private Long apiId;

    /** Parameter name, required and used as the header or query name; unused for the {@code BODY} position. */
    @Column(name = "name")
    @TableField("name")
    private String name;

    /** Position the parameter is applied to: {@code HEADER}, {@code QUERY} or {@code BODY}; required. */
    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    @TableField("position")
    private FcParamPos position;

    /** Whether the value of a {@code BODY} parameter is parsed into JSON instead of being sent as text. */
    @Column(name = "json_obj")
    @TableField("json_obj")
    private Boolean jsonObj;

    /** Default value sent for the parameter, or the whole request body for the {@code BODY} position. */
    @Column(name = "default_value")
    @TableField("default_value")
    private String defaultValue;

}
