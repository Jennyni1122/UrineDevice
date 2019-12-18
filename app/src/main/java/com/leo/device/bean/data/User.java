package com.leo.device.bean.data;

import com.blankj.utilcode.util.RomUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Leo
 * @date 2019-05-15
 */
public class User {
    private int id;
    private String name;
    private int age;
    private int threshold = 100;
    private int gender;
    private int height;
    private int weight;
    private float bmi;
    private String phoneImei;
    private String phoneNumber;
    private String capture;
    private String appToken;
    private Integer type;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return gender;
    }

    public void setSex(int sex) {
        this.gender = sex;
    }

    public int getThreshold() {
        return threshold;
    }

    public User setThreshold(int threshold) {
        this.threshold = threshold;
        return this;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPhoneImei() {
        return phoneImei;
    }

    public void setPhoneImei(String phoneImei) {
        this.phoneImei = phoneImei;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCapture() {
        return capture;
    }

    public void setCapture(String capture) {
        this.capture = capture;
    }

    public int getMaxThreshold() {
        return getMaxThreshold(getAge(), getGender());
    }

    public static int getMaxThreshold(int age, int gender) {
        if (age < 3) {
            return 50;
        } else if (age < 12) {
            return 150;
        } else if (age < 18) {
            return 300;
        } else {
            return gender == 0 ? 400 : 500;
        }
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public void setType() {
        if (RomUtils.isHuawei()) {
            type = 1;
        } else if (RomUtils.isXiaomi()) {
            type = 2;
        } else if (RomUtils.isOppo()) {
            type = 3;
        } else if (RomUtils.isVivo()) {
            type = 4;
        } else {
            type = 0;
        }
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi() {
        if (height == 0) {
            this.bmi = 0;
            return;
        }
        BigDecimal height = new BigDecimal(this.height);
        BigDecimal weight = new BigDecimal(this.weight);
        BigDecimal bmi = weight.multiply(new BigDecimal(10000)).divide(height.multiply(height), 2, RoundingMode.HALF_UP);
        this.bmi = bmi.floatValue();
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
