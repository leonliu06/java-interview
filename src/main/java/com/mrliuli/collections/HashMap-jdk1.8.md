## HashMap源码（jdk1.8）阅读
- `HashMap`类主要由一个`Node`数组`Node<K,V>[] table`构成;

### 1. `put`方法，添加元素
```
    public V put(K key, V value) {
        // hash() 取 key 的哈希
        return putVal(hash(key), key, value, false, true);
    }    
    static final int hash(Object key) {
        int h;
        // 这里说明 key 可以为空，且为空时，hash 为 0
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
    
    /**
     * Implements Map.put and related methods.
     *
     * @param hash hash for key
     * @param key the key
     * @param value the value to put
     * @param onlyIfAbsent if true, don't change existing value
     * @param evict if false, the table is in creation mode.
     * @return previous value, or null if none
     */
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        if ((tab = table) == null || (n = tab.length) == 0)
            // 通过 resize 方法初始化
            n = (tab = resize()).length;
        if ((p = tab[i = (n - 1) & hash]) == null)  // 通过 (n-1) & hash 运算来定位索引
            // 该索引处元素为空，则直接插入新节点
            tab[i] = newNode(hash, key, value, null);
        else {
            Node<K,V> e; K k;
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                // 相同节点
                e = p;
            else if (p instanceof TreeNode)
                // 该索引处为树形链表，将该节点插入树形链表中
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                // 遍历单链表
                for (int binCount = 0; ; ++binCount) {
                    // p 是遍历链表时的当前节点，如果 p 的下一节点为空，则将新节点直接插入，作为 p 的后驱节点，
                    // 这里可以看出，新节点插入即所谓的“后插法”
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        // TREEIFY_THRESHOLD 即 单向链表转为树形链表时的节点个数的临界阈值，从源码声明处可知为 8
                        // 从表达式可知 binCount >= 7 为真时，因 binCount 从0开始，且此时，新节点已经插入，
                        // 即结果是当单身链表插入第9个节点时，开始执行 treefiyBin 方法，即将单向链表转为树形链表或扩容（Node数据table大小小于64时，进行扩容）
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            // 将单向链表转为树形链表或扩容（Node数据table大小小于64时，进行扩容）
                            treeifyBin(tab, hash);
                        break;
                    }
                    // 当前节点 p 的下一节点 e 与新节点相同，则什么都不做，直接跳出循环
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    // 上面 e 已赋值为 p 的下一节点，这里相当于 p = p.next;即继续遍历    
                    p = e;
                }
            }
            // e != null 说明 e 是索引处链表中与新节点想再的节点
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                // 根据 onlyIfAbsent 和 oldValue == null 来确定是否改变相同key的value，默认 onlyIfAbsent 为 false
                if (!onlyIfAbsent || oldValue == null)  // 即 默认 改变 value
                    e.value = value;
                // 用于 LinkedHashMap 实现按节点插入顺序有序化，HashMap 什么都不做    
                afterNodeAccess(e);
                return oldValue;
            }
        }
        // 记录 HashMap 中节点的修改（增加、删除、扩容等）次数
        // 该字段用于实现 HashMap 的迭代器的 fail-fast (快速失败)，HashMap 内部类 HashIterator 中有变量 expectedModCount，
        // 迭代器迭代过程中，当 expectedModCount != modCount 时，就 throw new ConcurrentModificationException(); 快速失败，
        // 以避免迭代器在遍历集合时，别的线程修改集合，使得遍历出现问题
        ++modCount;
        // 当集合中元素数量大于扩容临界数量时，进行扩容
        if (++size > threshold)
            resize();
        // 用于 LinkedHashMap 实现按节点插入顺序有序化，HashMap 什么都不做
        afterNodeInsertion(evict);
        return null;
    }
```

### 2. `resize`方法，初始化或扩容
```
    /**
     * Initializes or doubles table size.  If null, allocates in
     * accord with initial capacity target held in field threshold.
     * Otherwise, because we are using power-of-two expansion, the
     * elements from each bin must either stay at same index, or move
     * with a power of two offset in the new table.
     *
     * @return the table
     */
    // 初始化或2倍扩容。如 table 为 null，则按初始容量初始化。
    // 否则，2倍扩容，扩容后，原索引处链表上的元素在新表 table 上的位置，要么留在索引原位，要么移动 n 位，n 为原表 table 的长度 
    final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {   // 原表容量已经大于最大容量了，则不再扩容，返回自己
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            // newCap = oldCap << 1，即新容量扩大为原来的 2 倍
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold   // 新临界容量也扩大 2 倍
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults // 值为 0 ，表示开始初始化，使用默认值
            newCap = DEFAULT_INITIAL_CAPACITY;  // 默认初始容量 16
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY); // 0.75 * 16 = 12
        }
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];     // 新表
        table = newTab;
        if (oldTab != null) {
            // 遍历集合
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        // 该索引处的链表只有一个元素 e，则直接移动该元素
                        newTab[e.hash & (newCap - 1)] = e;
                    else if (e instanceof TreeNode)
                        // 树形链表，拆分节点或将树形链表转为单向链表
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    else { // preserve order
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        // 遍历该索引处的单身链表
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
```
