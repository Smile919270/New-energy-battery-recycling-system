package cn.zyc.res.entity;

import cn.zyc.basics.baseClass.ZwzBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;

 
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "a_resource")
@TableName("a_resource")
@ApiModel(value = "电池品类")
public class Resource extends ZwzBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "电池名称")
    private String title;

    @ApiModelProperty(value = "电池分类")
    private String type;

    @ApiModelProperty(value = "型号规格")
    private String modal;

    @ApiModelProperty(value = "存放方式")
    private String storageMethod;

    @ApiModelProperty(value = "新旧程度")
    private String degree;

    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "计量单位")
    private String unit;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "累计交易量")
    private BigDecimal sellNumber;
}