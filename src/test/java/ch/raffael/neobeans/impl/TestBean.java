package ch.raffael.neobeans.impl;

import java.net.URL;

import ch.raffael.neobeans.NodeKey;


/**
* @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
*/
public class TestBean {

    private NodeKey key;
    private String name;
    private URL homepage;

    public NodeKey getKey() {
        return key;
    }

    public void setKey(NodeKey key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getHomepage() {
        return homepage;
    }

    public void setHomepage(URL homepage) {
        this.homepage = homepage;
    }

}
