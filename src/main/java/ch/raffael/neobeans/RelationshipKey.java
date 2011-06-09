package ch.raffael.neobeans;


import java.io.Serializable;

import com.google.common.annotations.GwtCompatible;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import static ch.raffael.util.common.NotImplementedException.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@GwtCompatible
public class RelationshipKey implements Serializable {

    private NodeKey startKey;
    private NodeKey endKey;

    private RelationshipKey() {
    }

    private RelationshipKey(@NotNull NodeKey startKey, @NotNull NodeKey endKey) {
        this.startKey = startKey;
        this.endKey = endKey;
    }

    public static NodeKey arbitrary(@NotNull NodeKey startKey, @NotNull NodeKey endKey) {
        return notImplemented();
    }

    public static NodeKey from(@NotNull Node startNode, @NotNull Node endNode) {
        return notImplemented();
    }

    public static NodeKey from(@NotNull Relationship relationship) {
        return notImplemented();
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        RelationshipKey that = (RelationshipKey)o;
        if ( !endKey.equals(that.endKey) ) {
            return false;
        }
        if ( !startKey.equals(that.startKey) ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = startKey.hashCode();
        result = 31 * result + endKey.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RelationshipKey{" + startKey + "=>" + endKey + "}";
    }

    public NodeKey getStartKey() {
        return startKey;
    }

    public NodeKey getEndKey() {
        return endKey;
    }
}
