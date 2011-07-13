package ch.raffael.neobeans.impl;

import java.net.URL;

import ch.raffael.neobeans.NodeKey;
import ch.raffael.neobeans.annotations.Transient;
import ch.raffael.neobeans.annotations.UseConverter;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class AnnotatedBean {

    private NodeKey key;
    private String name;
    private URL url;
    private int irrelevant = 42;

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

    @UseConverter(UrlConverter.class)
    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    @Transient
    public int getIrrelevant() {
        return irrelevant;
    }

    public void setIrrelevant(int irrelevant) {
        this.irrelevant = irrelevant;
    }
}
