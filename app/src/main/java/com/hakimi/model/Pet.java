package com.hakimi.model;

import java.io.Serializable;
import java.util.List;

/**
 * 宠物实体
 * 
 * @author hakimi
 */
public class Pet implements Serializable {
    private Long id;
    private Long userId;
    private String name;
    private String type;
    private String breed;
    private Integer gender;
    private Integer age;
    private String size;
    private String color;
    private Integer vaccinationStatus;
    private Integer sterilizationStatus;
    private Integer adoptionType;
    private String description;
    private String location;
    private String contactPhone;
    private Integer status;
    private Integer viewCount;
    private List<String> imageUrls;
    private PetHealth health;

    public Pet() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getVaccinationStatus() {
        return vaccinationStatus;
    }

    public void setVaccinationStatus(Integer vaccinationStatus) {
        this.vaccinationStatus = vaccinationStatus;
    }

    public Integer getSterilizationStatus() {
        return sterilizationStatus;
    }

    public void setSterilizationStatus(Integer sterilizationStatus) {
        this.sterilizationStatus = sterilizationStatus;
    }

    public Integer getAdoptionType() {
        return adoptionType;
    }

    public void setAdoptionType(Integer adoptionType) {
        this.adoptionType = adoptionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public PetHealth getHealth() {
        return health;
    }

    public void setHealth(PetHealth health) {
        this.health = health;
    }


    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", breed='" + breed + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                ", size='" + size + '\'' +
                ", color='" + color + '\'' +
                ", vaccinationStatus=" + vaccinationStatus +
                ", sterilizationStatus=" + sterilizationStatus +
                ", adoptionType=" + adoptionType +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", status=" + status +
                ", viewCount=" + viewCount +
                ", imageUrls=" + imageUrls +
                ", health=" + health +
                '}';
    }
}

