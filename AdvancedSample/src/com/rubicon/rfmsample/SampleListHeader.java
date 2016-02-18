/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

public class SampleListHeader {
    private final String name;

    public SampleListHeader(String name) {
        this.name = name;
    }

    public String title() {
        return name;
    }

}
