/*
 * Copyright (c) 2017. Johannes Engler
 */
package com.citos.client.panels.deployment;

public class Intern {
    private String name;
    private int extension;

    public Intern(String name, int extension) {
        this.name = name;
        this.extension = extension;
    }

    public String getName() {
        return name;
    }

    public int getExtension() {
        return extension;
    }
    
}
