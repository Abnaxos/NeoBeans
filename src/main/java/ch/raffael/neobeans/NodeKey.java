package ch.raffael.neobeans;

import java.io.Serializable;
import java.util.UUID;

import com.google.common.annotations.GwtCompatible;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.Relationship;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@GwtCompatible
public class NodeKey implements Serializable {

    public static final String PROPERTY_ID = "neobeans:id";

    private Long neoId;
    private String id;

    private NodeKey() {
    }

    private NodeKey(Long neoId, @NotNull String id) {
        this.neoId = neoId;
        this.id = id;
    }

    public static NodeKey newKey(long neoId, @NotNull String id) {
        return new NodeKey(neoId, id);
    }

    public static NodeKey from(Relationship node) {
        return new NodeKey(node.getId(), (String)node.getProperty(PROPERTY_ID));
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
        if ( neoId != that.neoId ) {
            return false;
        }
        if ( !id.equals(that.id) ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = (int)(neoId ^ (neoId >>> 32));
        result = 31 * result + id.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "NodeKey{" + neoId + "," + id + "}";
    }

    public static String newId() {
        return UUID.randomUUID().toString();
    }

    public long getNeoId() {
        return neoId;
    }

    @NotNull
    public String getId() {
        return id;
    }

}
