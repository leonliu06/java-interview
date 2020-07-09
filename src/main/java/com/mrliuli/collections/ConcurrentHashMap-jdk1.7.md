## ConcurrentHashMap源码（jdk1.7）阅读
- `ConcurrentHashMap`类主要由一个`Segment`数组（`Segment<K,V>[] segments`）构成；
- `Segment`是一个`ReentrantLock`类，含有一个`HashEntry<K,V>[]`数组（`HashEntry<K,V>[] table`）;  
- `Segment`的数量`size`为并发级别`concurrencyLevel`的大小，默认为 `DEFAULT_CONCURRENCY_LEVEL = 16`；  
- 每个`Segment`表的容量为`ConcurrentHashMap`初始容量`initialCapacity`（默认为`DEFAULT_INITIAL_CAPACITY = 16`）除以Segment的数量`ssize`，最小容量为`2`（`MIN_SEGMENT_TABLE_CAPACITY = 2`）；  

### 1. 构造函数
```
    /**
     * Creates a new, empty map with a default initial capacity (16),
     * load factor (0.75) and concurrencyLevel (16).
     */
    public ConcurrentHashMap() {
        // 默认初始容量：static final int DEFAULT_INITIAL_CAPACITY = 16;
        // 默认负载因子：static final float DEFAULT_LOAD_FACTOR = 0.75f;
        // 默认并发级别：static final int DEFAULT_CONCURRENCY_LEVEL = 16;
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL);
    }
    
    public ConcurrentHashMap(int initialCapacity,
                             float loadFactor, int concurrencyLevel) {
        if (!(loadFactor > 0) || initialCapacity < 0 || concurrencyLevel <= 0)
            throw new IllegalArgumentException();
        if (concurrencyLevel > MAX_SEGMENTS)
            concurrencyLevel = MAX_SEGMENTS;
        // Find power-of-two sizes best matching arguments
        int sshift = 0;
        int ssize = 1;
        // ssize: segments数组大小，2的倍数，默认等于 concurrencyLevel
        while (ssize < concurrencyLevel) {
            ++sshift;
            ssize <<= 1;
        }
        this.segmentShift = 32 - sshift;
        this.segmentMask = ssize - 1;
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        int c = initialCapacity / ssize;
        if (c * ssize < initialCapacity)
            ++c;
        // cap：每个Segment内部的HashEntry数组大小，最小为`MIN_SEGMENT_TABLE_CAPACITY = 2`，
        // 实际大小为小于c的最小2的倍数，c为ConcurrentHashMap初始容量除以Segments数组大小，默认为16 / 16 = 1，
        // 则不小于1的最小2的倍数是2，即Segment内部的HashEntry数组大小 cap 默认是2。 
        int cap = MIN_SEGMENT_TABLE_CAPACITY;
        while (cap < c)
            cap <<= 1;
        // create segments and segments[0]
        // 创建 segments 数组中第0个元素，再创建 segments 数组，
        // 即 segments 数组中只初始化第0个元素，其它元素为 null
        Segment<K,V> s0 =
            new Segment<K,V>(loadFactor, (int)(cap * loadFactor),
                             (HashEntry<K,V>[])new HashEntry[cap]);
        Segment<K,V>[] ss = (Segment<K,V>[])new Segment[ssize];
        UNSAFE.putOrderedObject(ss, SBASE, s0); // ordered write of segments[0]
        this.segments = ss;
    }
```
### 2. `put(K key, V value)` 方法
```
    public V put(K key, V value) {
        Segment<K,V> s;
        if (value == null)
            // key 和 value 都不可以为 null
            throw new NullPointerException();
        int hash = hash(key);
        // 根据 key 的 hash 值确定 segments 数组中的某一个 segment 索引 j
        int j = (hash >>> segmentShift) & segmentMask;
        if ((s = (Segment<K,V>)UNSAFE.getObject          // nonvolatile; recheck
             (segments, (j << SSHIFT) + SBASE)) == null) //  in ensureSegment
            // 如果该索引 j 处的 segment 为 null，即没有初始化的 segment，
            // 则在该索引处初始化一个 segment 并将其返回
            s = ensureSegment(j);       // 该方法分析见下面 2.1
        // 执行 segment 中的 put 方法    
        return s.put(key, hash, value, false);  // 该方法分析见下面 2.2
    }

```
#### 2.1 `ensureSegment(int k)` 方法
&emsp;&emsp;`ensureSegment(int k)` 方法用来在 `segment` 数组 `ss` 索引 `k` 处创建一个`segment`。该方法利用了**自旋CAS**思想，
使得在不加锁的情况下保证线程安全。自旋CAS主要原理是利用`Unsafe`类中的方法来直接读写主存数据。

```
    /**
     * Returns the segment for the given index, creating it and
     * recording in segment table (via CAS) if not already present.
     *
     * @param k the index
     * @return the segment
     */
    @SuppressWarnings("unchecked")
    private Segment<K,V> ensureSegment(int k) {
        final Segment<K,V>[] ss = this.segments;
        // k 索引所在 segment 在 segments 数组中的偏移
        long u = (k << SSHIFT) + SBASE; // raw offset
        Segment<K,V> seg;
        // 取得 k 索引所在的 segment
        if ((seg = (Segment<K,V>)UNSAFE.getObjectVolatile(ss, u)) == null) {
            Segment<K,V> proto = ss[0]; // use segment 0 as prototype
            int cap = proto.table.length;
            float lf = proto.loadFactor;
            int threshold = (int)(cap * lf);
            // 以 segments[0] 这个 segment 为原型来重新创建一个 HashEntry[]
            HashEntry<K,V>[] tab = (HashEntry<K,V>[])new HashEntry[cap];
            // 再次检查 k 索引处是否有其它线程创建了一个 segment
            if ((seg = (Segment<K,V>)UNSAFE.getObjectVolatile(ss, u))
                == null) { // recheck
                Segment<K,V> s = new Segment<K,V>(lf, threshold, tab);
                while ((seg = (Segment<K,V>)UNSAFE.getObjectVolatile(ss, u))
                       == null) {
                    // 这里利用自旋的CAS操作对 segments 数组中 偏移量为 u 的位置设置 s，
                    // UNSAFE.compareAndSwapObject 具备原子性，从主存读取，线程可见，保证只有一个线程设置成功，
                    // 如当前线程设置成功，则 break 跳出循环，
                    // 如其它线程设置成功，则 while 条件为 false，亦会跳出循环。
                    if (UNSAFE.compareAndSwapObject(ss, u, null, seg = s))
                        break;
                }
            }
        }
        return seg;
    }
```

#### 2.2 `Segment` 类中的 `put` 方法
```
    final V put(K key, int hash, V value, boolean onlyIfAbsent) {
        // 先执行 `tryLock()` 尝试加锁（Segment继承自ReentrantLock），加锁成功，则 node = null,
        // 加锁失败，则执行 scanAndLockForPut() 方法自旋循环获取锁，
        // 该方法获取锁后，如 hash 对应的 HashEntry 没有，则新建一个返回，否则如有，则返回null
        HashEntry<K,V> node = tryLock() ? null :
            scanAndLockForPut(key, hash, value);    // 该方法分析见下面 2.3 
        V oldValue;
        // 获取锁成功后，执行下面代码，所以同一时间只有一个线程在操作下面这部分内容
        try {
            HashEntry<K,V>[] tab = table;
            int index = (tab.length - 1) & hash;
            // 获取需要put的<K,V>键值对在当前segment中对应的链表所在的表头节点
            HashEntry<K,V> first = entryAt(tab, index);
            // 开始遍历 first 为头节点的链表
            for (HashEntry<K,V> e = first;;) {
                if (e != null) {
                    K k;
                    // 这里的判断表示当前要 put 的 <K,V> 对应的 key 已经存在
                    if ((k = e.key) == key ||
                        (e.hash == hash && key.equals(k))) {
                        oldValue = e.value;
                        // 判断是否需要更新，是的话，则直接覆盖，然后 break 退出
                        if (!onlyIfAbsent) {
                            e.value = value;
                            ++modCount;
                        }
                        break;
                    }
                    // 遍历下一个节点
                    e = e.next;
                }
                // 走到这里，说明 e 为空，可能是链表第一个元素就为空，
                // 也可能是链表中元素没有找到 key ，遍历到链表尾部了
                else {
                    // node 不为空，如上所述，说明是通过scanAndLockForPut方法获取到锁后返回的新建的节点
                    if (node != null)
                        node.setNext(first);
                    // node 为空，如上说明第一次 tryLock() 时就获取到了锁，所以这里需要新建一个节点    
                    else
                        node = new HashEntry<K,V>(hash, key, value, first);    
                    int c = count + 1;
                    // 判断是否需要扩容
                    if (c > threshold && tab.length < MAXIMUM_CAPACITY)
                        rehash(node);   // 该方法分析见下面 2.4
                    else
                        // 数组无需扩容，直接将节点 node 插入到数组中指定的 index 位置
                        setEntryAt(tab, index, node);
                    ++modCount;
                    count = c;
                    oldValue = null;
                    break;
                }
            }
        } finally {
            unlock();
        }
        return oldValue;
    }
```
#### 2.3 `Segment` 类中的 `HashEntry<K,V> scanAndLockForPut(K key, int hash, V value)` 方法
&emsp;&emsp;获取segment中的 HashEntry 数组中 key 所在的 HashEntry，然后自旋获取当前 segment 锁，同时扫描（遍历）key 所在的 HashEntry 链表，
获取锁成功后，如未找到 HashEntry 则创建一个 HashEntry 并返回，如找到，则返回 null。
```
    private HashEntry<K,V> scanAndLockForPut(K key, int hash, V value) {
        // 获取当前 segment 中哈希值为 hash 的 HashEntry
        HashEntry<K,V> first = entryForHash(this, hash);
        HashEntry<K,V> e = first;
        HashEntry<K,V> node = null;
        int retries = -1; // negative while locating node
        // 自旋获取锁
        while (!tryLock()) {
            HashEntry<K,V> f; // to recheck first below
            // 获取锁失败，则必然进入第一个 if
            if (retries < 0) {
                // e == null 表示，HashEntry数组 table 中 hash 对应索引位置的 HashEntry 是空的，
                // 可能第一次遍历就是空的，也可能遍历链表到最后一个节点也是空的。
                if (e == null) {
                    // 再判一次null，避免重复赋值。
                    if (node == null) // speculatively create node
                        // 新建一个 HashEntry
                        node = new HashEntry<K,V>(hash, key, value, null);
                    retries = 0;
                }
                // 遍历过程中找到了key所在的索引位置（坑位）
                else if (key.equals(e.key))
                    retries = 0;
                // 遍历下一个节点    
                else
                    e = e.next;
            }
            else if (++retries > MAX_SCAN_RETRIES) {
                // 尝试获取锁的次数超过限制，则不再循环，直接进入阻塞。即有限制的自旋获取锁，防止无限制地重复自旋浪费资源。
                lock();
                break;
            }
            else if ((retries & 1) == 0 &&
                     (f = entryForHash(this, hash)) != first) {
                // 遍历过程中发现其它线程改变了正在遍历的链表，则重新遍历     
                e = first = f; // re-traverse if entry changed
                retries = -1;
            }
        }
        return node;
    }
```

#### 2.4 `Segment` 类中的 `private void rehash(HashEntry<K,V> node)` 方法  
```
    /**
     * Doubles size of table and repacks entries, also adding the
     * given node to new table
     * 翻译：将HashEntry数组table两倍扩容，并且重新分配 <K,V> 元素实体，同时添加给定元素节点 node 到新 table
     */
    @SuppressWarnings("unchecked")
    private void rehash(HashEntry<K,V> node) {
        /*
         * Reclassify nodes in each list to new table.  Because we
         * are using power-of-two expansion, the elements from
         * each bin must either stay at same index, or move with a
         * power of two offset. We eliminate unnecessary node
         * creation by catching cases where old nodes can be
         * reused because their next fields won't change.
         * Statistically, at the default threshold, only about
         * one-sixth of them need cloning when a table
         * doubles. The nodes they replace will be garbage
         * collectable as soon as they are no longer referenced by
         * any reader thread that may be in the midst of
         * concurrently traversing table. Entry accesses use plain
         * array indexing because they are followed by volatile
         * table write.
         */
        HashEntry<K,V>[] oldTable = table;
        int oldCapacity = oldTable.length;
        int newCapacity = oldCapacity << 1; // 新容量扩大2倍
        threshold = (int)(newCapacity * loadFactor);
        HashEntry<K,V>[] newTable =
            (HashEntry<K,V>[]) new HashEntry[newCapacity];  // 创建一个2倍原容量的新HashEntry数组
        int sizeMask = newCapacity - 1;
        for (int i = 0; i < oldCapacity ; i++) {
            HashEntry<K,V> e = oldTable[i];
            if (e != null) {
                HashEntry<K,V> next = e.next;
                int idx = e.hash & sizeMask;
                if (next == null)   //  Single node on list
                    newTable[idx] = e;  // 链表中只有一个节点
                else { // Reuse consecutive sequence at same slot
                    HashEntry<K,V> lastRun = e;
                    int lastIdx = idx;
                    for (HashEntry<K,V> last = next;
                         last != null;
                         last = last.next) {
                        int k = last.hash & sizeMask;
                        if (k != lastIdx) {
                            lastIdx = k;
                            lastRun = last;
                        }
                    }   // 该for循环找到链表中最后一个index不等于第一个节点的节点
                    newTable[lastIdx] = lastRun;
                    // Clone remaining nodes
                    for (HashEntry<K,V> p = e; p != lastRun; p = p.next) {
                        V v = p.value;
                        int h = p.hash;
                        int k = h & sizeMask;
                        HashEntry<K,V> n = newTable[k];
                        newTable[k] = new HashEntry<K,V>(h, p.key, v, n);
                    }
                }
            }
        }
        int nodeIndex = node.hash & sizeMask; // add the new node
        node.setNext(newTable[nodeIndex]);      // newTable[nodeIndex]是null，所以这里是让 node.next -> null，指向null。
        newTable[nodeIndex] = node;             // 新节点 node 放到 newTable[nodeIndex] 位置
        table = newTable;
    }
```
