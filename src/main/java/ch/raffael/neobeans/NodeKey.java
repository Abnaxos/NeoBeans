package ch.raffael.neobeans;

import java.io.Serializable;
import java.util.UUID;

import com.google.common.annotations.GwtCompatible;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.Node;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@GwtCompatible
public class NodeKey implements Serializable {

    private Long id;
    private String key;

    private NodeKey() {
    }

    private NodeKey(Long id, @NotNull String key) {
        this.id = id;
        this.key = key;
    }

    public static NodeKey arbitrary(long neoId, @NotNull String id) {
        return new NodeKey(neoId, id);
    }

    public static NodeKey from(Node node) {
        return new NodeKey(node.getId(), (String)node.getProperty(NeoBeanStore.PROPERTY_KEY));
    }

    public static NodeKey create() {
        return new NodeKey(null, UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        NodeKey that = (NodeKey)o;
        if ( !key.equals(that.key) ) {
            return false;
        }
        if ( id != null ? !id.equals(that.id) : that.id != null ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + key.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "NodeKey{" + id + "," + key + "}";
    }

    public static String newId() {
        return UUID.randomUUID().toString();
    }

    public Long getId() {
        return id;
    }

    @NotNull
    public String getKey() {
        return key;
    }

}
