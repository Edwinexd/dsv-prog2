/*
* PROG2 VT2023, Inlämningsuppgift, del 2
* Grupp 069
* Erik Lind Gou-Said - erli1872
* Edwin Sundberg - edsu8469
*/

package com.gouswin;

public class ConnectionDialogResult {
    private final String name;
    private final int weight;

    public ConnectionDialogResult(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

}
