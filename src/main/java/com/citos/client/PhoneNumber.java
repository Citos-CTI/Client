/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client;

public class PhoneNumber {
    private boolean intern;
    private String phoneNumber;
    private String name;
    private int count;
    private int position;

    public PhoneNumber(boolean intern, String phoneNumber, String name, int count, int position) {
        this.intern = intern;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.count = count;
        this.position = position;
    }

    public boolean isIntern() {
        return intern;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setIntern(boolean intern) {
        this.intern = intern;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
