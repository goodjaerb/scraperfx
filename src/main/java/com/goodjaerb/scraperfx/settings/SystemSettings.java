/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goodjaerb.scraperfx.settings;

import org.xmappr.annotation.Element;
import org.xmappr.annotation.RootElement;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * @author goodjaerb
 */
@RootElement(name = "system-settings")
public class SystemSettings {

    @Element(name = "system")
    public Collection<System> systems;

    public SystemSettings() {
        systems = new TreeSet<>();
    }

    public void renameSystem(String oldName, String newName) {
        System oldSys = get(oldName);
        if(oldSys != null) {
            systems.remove(oldSys);

            System newSystem = new System(oldSys);
            newSystem.name = newName;
            systems.add(newSystem);
        }
    }

    public System get(String systemName) {
        System temp = new System(systemName);
        if(systems.contains(temp)) {
            List<System> list = asList();
            return list.get(list.indexOf(temp));
        }
        return null;
    }

    private List<System> asList() {
        return Arrays.asList(systems.toArray(new System[0]));
    }
}
