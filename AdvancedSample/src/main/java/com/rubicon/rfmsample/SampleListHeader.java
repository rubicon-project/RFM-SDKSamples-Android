/*
 * Copyright (c) 2016. Rubicon Project. All rights reserved
 *
 */

package com.rubicon.rfmsample;

class SampleListHeader {
    private final String name;

    SampleListHeader(String name) {
        this.name = name;
    }

    String title() {
        return name;
    }

}