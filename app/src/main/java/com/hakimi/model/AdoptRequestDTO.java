package com.hakimi.model;

import java.io.Serializable;

/**
 * 领养申请DTO
 *
 * @author hakimi
 */

public class AdoptRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 宠物ID
     */

    private Long petId;

    /**
     * 申请理由
     */

    private String reason;

    /**
     * 联系方式
     */
    private String contactInfo;

    private int userId;

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

