//package priv.szf.fastcall.data.entity;
//
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableName;
//import lombok.Data;
//import org.hibernate.annotations.GenericGenerator;
//import priv.szf.fastcall.common.FcHeaderOperation;
//import priv.szf.fastcall.common.FcHeaderType;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.EnumType;
//import javax.persistence.Enumerated;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.Table;
//
///**
// * Header assignment entity of the {@code fastcall_header_assign} table, modifying request or response headers.
// */
//@Entity
//@Table(name = FcEntityConsts.HEADER_ASSIGN_TABLE)
//@TableName(FcEntityConsts.HEADER_ASSIGN_TABLE)
//@Data
//public class FcHeaderAssign {
//
//    /** Primary key, generated as a snowflake id. */
//    @Id
//    @GeneratedValue(generator = "snowflake")
//    @GenericGenerator(name = "snowflake",
//            strategy = "priv.szf.fastcall.data.entity.SnowflakeIdGenerator")
//    @TableId(value = "id", type = IdType.ASSIGN_ID)
//    private Long id;
//
//    /** Id of the system whose requests or responses are modified; assignments are loaded per system. */
//    @Column(name = "sys_id")
//    @TableField("sys_id")
//    private Long sysId;
//
//    /** Id of the api the assignment is limited to; {@code null} applies it to every api of the system. */
//    @Column(name = "api_id")
//    @TableField("api_id")
//    private Long apiId;
//
//    /** Name of the header to modify, required and at most 50 characters. */
//    @Column(name = "name")
//    @TableField("name")
//    private String name;
//
//    /** Value set on or added to the header; unused by the {@code REMOVE} operation. */
//    @Column(name = "value")
//    @TableField("value")
//    private String value;
//
//    /** Operation applied to the header: {@code SET} replaces, {@code ADD} appends and {@code REMOVE} deletes. */
//    @Enumerated(EnumType.STRING)
//    @Column(name = "operation")
//    @TableField("operation")
//    private FcHeaderOperation operation;
//
//    /** Side the assignment applies to: {@code REQUEST}, {@code RESPONSE} or {@code REQUEST_RESPONSE}. */
//    @Enumerated(EnumType.STRING)
//    @Column(name = "type")
//    @TableField("type")
//    private FcHeaderType type;
//
//    /** Case-insensitive uri fragment the assignment is matched against; {@code null} matches every request. */
//    @Column(name = "path")
//    @TableField("path")
//    private String path;
//
//}
