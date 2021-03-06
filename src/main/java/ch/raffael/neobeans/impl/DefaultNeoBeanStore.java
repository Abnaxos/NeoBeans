package ch.raffael.neobeans.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;

import com.google.common.annotations.VisibleForTesting;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;

import ch.raffael.neobeans.NeoBeanStore;
import ch.raffael.neobeans.NodeKey;
import ch.raffael.neobeans.Update;

import static ch.raffael.neobeans.temp.NotImplementedException.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DefaultNeoBeanStore implements NeoBeanStore {

    private final GraphDatabaseService database;
    private final Map<Object, BeanMapping> mappings = new HashMap<Object, BeanMapping>();
    private final BeanMappingFactory beanMappingFactory;
    private final ThreadLocal<TxContext> txContext = new ThreadLocal<TxContext>();

    public DefaultNeoBeanStore(@NotNull GraphDatabaseService database) {
        this(database, new AnnotatedBeanMappingFactory());
    }

    public DefaultNeoBeanStore(@NotNull GraphDatabaseService database, @NotNull BeanMappingFactory beanMappingFactory) {
        this.database = database;
        this.beanMappingFactory = beanMappingFactory;
        database.registerTransactionEventHandler(new TransactionEventHandler<TxContext>() {
            @Override
            public TxContext beforeCommit(TransactionData data) throws Exception {
                return txContext();
            }
            @Override
            public void afterCommit(TransactionData data, TxContext state) {
                if ( state.newKeys != null ) {
                    for ( KeyEntry keyEntry : state.newKeys.values() ) {
                        NodeKey key = keyEntry.newKey;
                        for ( NewNodeEntry entry = keyEntry.firstNewNode; entry != null; entry = entry.next ) {
                            BeanMapping mapping = getMapping(entry.bean);
                            mapping.setKey(entry.bean, key);
                        }
                    }
                }
            }
            @Override
            public void afterRollback(TransactionData data, TxContext state) {
                // nothing to do
            }
        });
    }

    @Override
    @NotNull
    public GraphDatabaseService database() {
        // FIXME: consider returning a wrapper here that enforces a TxContext
        return database;
    }

    @Override
    @NotNull
    public Transaction beginTx() {
        TxContext txCtx = txContext.get();
        if ( txCtx == null ) {
            txCtx = new TxContext();
            txContext.set(txCtx);
        }
        else {
            txCtx.txDepth++;
        }
        final Transaction tx = database.beginTx();
        return new Transaction() {
            @Override
            public void failure() {
                tx.failure();
            }
            @Override
            public void success() {
                tx.success();
            }
            @Override
            public void finish() {
                try {
                    tx.finish();
                }
                finally {
                    TxContext ctx = txContext.get();
                    //assert ctx == updateCtx;
                    if ( ctx != null ) {
                        ctx.txDepth--;
                        if ( ctx.txDepth == 0 ) {
                            txContext.set(null);
                        }
                    }
                }
            }
        };
    }

    @Override
    @PreDestroy
    public void shutdown() {
        database.shutdown();
    }

    @Override
    public void performUpdate(@NotNull Update update) {
        Transaction tx = beginTx();
        try {
            for ( Update.Operation operation : update ) {
                switch ( operation.getType() ) {
                    case STORE:
                        store(operation.getNeoBean());
                        break;
                    case DELETE:
                        delete(operation.getNeoBean());
                        break;
                }
            }
            tx.success();
        }
        finally {
            tx.finish();
        }
    }

    @Override
    public <T> T retrieve(@NotNull T bean) {
        BeanMapping mapping = getMapping(bean);
        NodeKey key = mapping.getKey(bean);
        doRetrieve(mapping, key, bean);
        return bean;
    }

    @Override
    public <T> T retrieve(@NotNull NodeKey key, @NotNull T bean) {
        BeanMapping mapping = getMapping(bean);
        doRetrieve(mapping, key, bean);
        mapping.setKey(bean, key);
        return bean;
    }

    private void doRetrieve(BeanMapping mapping, NodeKey key, Object bean) {
        if ( key.getId() == null ) {
            throw new IllegalArgumentException("Cannot retrieve bean with ID null");
        }
        Node node = database.getNodeById(key.getId());
        if ( !node.getProperty(PROPERTY_KEY).equals(key.getKey()) ) {
            throw new NotFoundException("Node for key " + key + " not found");
        }
        if ( !mapping.matchType(bean, node) ) {
            throw new NotFoundException("Node for key " + key + " not found");
        }
        mapping.read(node, bean);
    }

    @Override
    public void store(@NotNull Object bean) {
        TxContext txctx = txContext();
        BeanMapping mapping = getMapping(bean);
        if ( mapping.isNode() ) {
            NodeKey key = mapping.getKey(bean);
            if ( key.getId() == null ) {
                NodeKey newKey = txctx.persistentKey(bean, key);
                if ( newKey == null ) {
                    mapping.beforeCreate(bean);
                    Node node = database.createNode();
                    newKey = NodeKey.arbitrary(node.getId(), key.getKey());
                    txctx.registerCreatingBean(bean, key, newKey);
                    key = newKey;
                    newKey = null;
                    node.setProperty(PROPERTY_KEY, key.getKey());
                    mapping.writeType(node);
                    mapping.created(bean, node);
                    mapping.beforeUpdate(bean, node, true);
                    mapping.store(bean, node);
                }
                else {
                    updateNode(bean, mapping, key);
                }
            }
            else {
                updateNode(bean, mapping, key);
            }
        }
        else {
            notImplemented();
        }
    }

    private void updateNode(Object bean, BeanMapping mapping, NodeKey key) {
        Node node = database.getNodeById(key.getId());
        if ( !mapping.matchType(bean, node) ) {
            throw new NotFoundException("Node with key " + key + " not found");
        }
        if ( !node.getProperty(PROPERTY_KEY).equals(key.getKey()) ) {
            throw new NotFoundException("Node with key " + key + " not found");
        }
        mapping.beforeUpdate(bean, node, false);
        mapping.store(bean, node);
    }

    private void notFound(NodeKey key, boolean ignoreMissing) {
        if ( ignoreMissing ) {
            return;
        }
        else {
        }
    }

    @Override
    public boolean delete(@NotNull Object bean) {
        TxContext txctx = txContext();
        BeanMapping mapping = getMapping(bean);
        NodeKey key = mapping.getKey(bean);
        if ( key.getId() == null ) {
            key = txctx.persistentKey(bean, key);
            if ( key == null ) {
            // won't delete bean that hasn't been stored
            return false;
            }
        }
        Node node = database.getNodeById(key.getId());
        if ( !mapping.matchType(bean, node) ) {
            return false;
            //throw new NotFoundException("Node with key " + key + " not found");
        }
        if ( !node.getProperty(PROPERTY_KEY).equals(key.getKey()) ) {
            return false;
            //throw new NotFoundException("Node with key " + key + " not found");
        }
        mapping.beforeDelete(bean, node);
        node.delete();
        return true;
    }

    @NotNull
    protected TxContext txContext() {
        TxContext ctx = txContext.get();
        if ( ctx == null ) {
            // the user MUST use NeoBeanStore.beginTx(): throw exception (forcing rollback)
            throw new IllegalStateException("No update context set for thread. Use NeoBeanStore.beginTx()!");
        }
        return ctx;
    }

    @NotNull
    protected BeanMapping getMapping(@NotNull Object bean) {
        synchronized ( mappings ) {
            final Object mappingKey = beanMappingFactory.mappingKey(bean);
            BeanMapping mapping = mappings.get(mappingKey);
            if ( mapping == null ) {
                mapping = beanMappingFactory.createBeanMapping(database, bean);
                mappings.put(mappingKey, mapping);
            }
            return mapping;
        }
    }

    private class TxContext {
        private int txDepth = 1;
        private Map<NodeKey, KeyEntry> newKeys = null;

        private boolean registerCreatingBean(@NotNull Object bean, @NotNull NodeKey current, @NotNull NodeKey newKey) {
            //KeyEntry entry = newKeys.get(current);
            if ( newKeys == null ) {
                newKeys = new HashMap<NodeKey, KeyEntry>();
                newKeys.put(current, new KeyEntry(bean, newKey));
                return true;
            }
            else {
                KeyEntry entry = newKeys.get(current);
                if ( entry == null ) {
                    newKeys.put(current, new KeyEntry(bean, newKey));
                    return true;
                }
                else {
                    assert newKey.equals(entry.newKey);
                    return entry.append(bean);
                }
            }
        }

        private NodeKey persistentKey(@NotNull Object bean, @NotNull NodeKey current) {
            if ( current.getId() != null ) {
                // nothing to do
                return current;
            }
            else if ( newKeys == null ) {
                return null;
            }
            else {
                KeyEntry entry = newKeys.get(current);
                if ( entry == null ) {
                    return null;
                }
                else {
                    return entry.newKey;
                }
            }
        }
    }

    @VisibleForTesting
    void setMapping(Object key, BeanMapping mapping) {
        mappings.put(key, mapping);
    }

    @VisibleForTesting
    void removeMapping(Object key) {
        mappings.remove(key);
    }

    private static class KeyEntry {
        private final NodeKey newKey;
        private final NewNodeEntry firstNewNode;
        private KeyEntry(@NotNull Object bean, @NotNull NodeKey newKey) {
            this.newKey = newKey;
            firstNewNode = new NewNodeEntry(bean);
        }
        private boolean append(@NotNull Object bean) {
            NewNodeEntry e = firstNewNode;
            while ( true ) {
                if ( e.bean == bean ) {
                    return false;
                }
                if ( e.next == null ) {
                    e.next = new NewNodeEntry(bean);
                    return true;
                }
                e = e.next;
            }
        }
    }

    private static class NewNodeEntry {
        private final Object bean;
        private NewNodeEntry next = null;
        public NewNodeEntry(Object bean) {
            this.bean = bean;
        }
    }

}
