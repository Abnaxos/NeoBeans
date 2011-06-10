package ch.raffael.neobeans;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Update implements Iterable<Update.Operation> {

    private final List<Operation> operations = new LinkedList<Operation>();

    public Update() {
    }

    @NotNull
    public static Update update() {
        return new Update();
    }

    @NotNull
    public Update store(@NotNull Object neoBean) {
        return store(neoBean, false);
    }

    @NotNull
    public Update store(@NotNull Object neoBean, boolean ignoreMissing) {
        operations.add(new Operation(OperationType.STORE, neoBean, ignoreMissing));
        return this;
    }

    @NotNull
    public Update delete(@NotNull Object neoBean) {
        operations.add(new Operation(OperationType.DELETE, neoBean, true));
        return this;
    }

    public void perform(@NotNull NeoBeanStore store) {
        store.performUpdate(this);
    }

    @Override
    public Iterator<Operation> iterator() {
        return operations.iterator();
    }

    public static final class Operation {
        private final OperationType type;
        private final Object neoBean;
        private final boolean ignoreMissing;
        private Operation(@NotNull OperationType type, @NotNull Object neoBean, boolean ignoreMissing) {
            this.type = type;
            this.neoBean = neoBean;
            this.ignoreMissing = ignoreMissing;
        }
        public OperationType getType() {
            return type;
        }
        public Object getNeoBean() {
            return neoBean;
        }
        public boolean isIgnoreMissing() {
            return ignoreMissing;
        }
    }

    public static enum OperationType {
        STORE, DELETE
    }

}
