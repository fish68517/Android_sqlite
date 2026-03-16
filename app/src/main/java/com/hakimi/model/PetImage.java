package com.hakimi.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 宠物图片实体类
 *
 * @author hakimi
 */

public class PetImage implements Serializable {



    /**
     * ID
     */

    private Long id;

    /**
     * 宠物ID
     */
    private Long petId;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 排序顺序
     */
    private Integer sortOrder;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

/*    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }*/
}

