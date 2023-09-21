package com.animal.dialogue.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 
 * @TableName zoo_session
 */
@TableName(value ="zoo_session")
@Data
public class ZooSession implements Serializable {
    /**
     * 用户id
     */
    private Integer user_id;

    /**
     * id
     */
    private String id;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 删除时间
     */
    private Timestamp deleteTime;

    /**
     * 用户自定义标题
     */
    private String title;

    /**
     * 是否删除 0否
     */
    private Integer is_deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}