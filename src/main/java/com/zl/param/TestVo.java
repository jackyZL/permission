package com.zl.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author jacky
 * @date 2017/10/26
 */
@Getter
@Setter
public class TestVo {

    @NotBlank
    private String msg; //不可以为空值

    @NotNull(message = "id不可以为空")
    @Max(value = 10, message = "id 不能大于10")
    @Min(value = 0, message = "id 至少大于等于0")
    private Integer id;

    //@NotEmpty
    private List<String> str;
}
