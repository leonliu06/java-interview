## ConcurrentHashMap源码（jdk1.8）阅读

### 1. 部分字段
#### 1.1 table
```
    /**
     * The array of bins. Lazily initialized upon first insertion.
     * Size is always a power of two. Accessed directly by iterators.
     */
     // node 数组，大小是2的幂
    transient volatile Node<K,V>[] table;
```
#### 1.2 sizeCtl
```
    /**
     * Table initialization and resizing control.  When negative, the
     * table is being initialized or resized: -1 for initialization,
     * else -(1 + the number of active resizing threads).  Otherwise,
     * when table is null, holds the initial table size to use upon
     * creation, or 0 for default. After initialization, holds the
     * next element count value upon which to resize the table.
     */
     // 用来控制 Node 数组 table 初始化和扩容时的并发控制，当为负值时，表示数组 table 下在初始化或扩容。
    private transient volatile int sizeCtl;
```

### 2. `put(K key, V value)` 方法
```
    public V put(K key, V value) {
        return putVal(key, value, false);
    }

    /** Implementation for put and putIfAbsent */
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        // key 或 value 都不能为 null
        if (key == null || value == null) throw new NullPointerException();
        int hash = spread(key.hashCode());
        int binCount = 0;
        for (Node<K,V>[] tab = table;;) {
            Node<K,V> f; int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                // 第一次插入元素时，初始化 node table 数组，参考下面 2.1
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                if (casTabAt(tab, i, null,
                             new Node<K,V>(hash, key, value, null)))
                    break;                   // no lock when adding to empty bin
            }
            else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            binCount = 1;
                            for (Node<K,V> e = f;; ++binCount) {
                                K ek;
                                if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                     (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)
                                        e.val = value;
                                    break;
                                }
                                Node<K,V> pred = e;
                                if ((e = e.next) == null) {
                                    pred.next = new Node<K,V>(hash, key,
                                                              value, null);
                                    break;
                                }
                            }
                        }
                        else if (f instanceof TreeBin) {
                            Node<K,V> p;
                            binCount = 2;
                            if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                           value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        addCount(1L, binCount);
        return null;
    }
```

### 2.1 `Node<K,V>[] initTable()` 方法
```
    /**
     * Initializes table, using the size recorded in sizeCtl.
     */
    private final Node<K,V>[] initTable() {
        Node<K,V>[] tab; int sc;
        while ((tab = table) == null || tab.length == 0) {
            if ((sc = sizeCtl) < 0)
                // sizeCtl < 0 说明有其它线程正在进行初始化，所以当前线程让步，等待其他线程操作完成
                Thread.yield(); // lost initialization race; just spin
                // 原子操作变量 sizeCtl，当且仅当 sizeCtl = sc 时，才将sizeCtl 设为 -1，下面代码是初始化node数组，
                // 所以 sizeCtl = -1 时，表示node数组正在进行初始化。Unsafe类参考：todo
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                try {
                    // 二次检验 node table 是否初始化
                    if ((tab = table) == null || tab.length == 0) {
                        // 初始table大小等于默认容量(DEFAULT_CAPACITY) 16 
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        @SuppressWarnings("unchecked")
                        Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n]; // 创建Node数组
                        table = tab = nt;
                        sc = n - (n >>> 2); // 初始时，n=16, sc=12
                    }
                } finally {
                    // 初始化完成后，总是设置 sizeCtl
                    sizeCtl = sc;   // sizeCtl = 12
                }
                break;
            }
        }
        return tab;
    }
```
