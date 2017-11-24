package com.ubt.alpha1e.blockly;

import org.litepal.crud.DataSupport;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class BlocklyProjectMode extends DataSupport {

    private String name;
    private String xml;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    @Override
    public String toString() {
        return "BlocklyProjectMode{" +
                "name='" + name + '\'' +
                ", xml='" + xml + '\'' +
                '}';
    }
}
