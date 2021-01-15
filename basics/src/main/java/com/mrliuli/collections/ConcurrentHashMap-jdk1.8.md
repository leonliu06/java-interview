## ConcurrentHashMap源码（jdk1.8）阅读
- `ConcurrentHashMap`由一个`Node`数组`table`构成
- `table`元素`Node`是一个链表结点，在`table`槽位上的链表元素大于等于`8`，并且`Node`数组`table`的长度大于`64`时，会转化为树形链表，扩容时，树元素个数小于等于6时，会转化为链表
- `ConcurrentHashMap`通过`transfer`方法扩容，扩容时，先从高位索引遍历数组`table`，然后再遍历索引处的链表或树，将链表（或树）上结点分成两个链表，一个保留在原来位置，一个向后移动`n`位
- `ConcurrentHashMap`利用`synchronized`和`Unsafe`类的`CAS`方法来控制并发
### 1. 部分字段
#### 1.1 `table`
```
    /**
     * The array of bins. Lazily initialized upon first insertion.
     * Size is always a power of two. Accessed directly by iterators.
     */
     // node 数组，大小是2的幂
    transient volatile Node<K,V>[] table;
```
#### 1.2 `sizeCtl`
```
    /**
     * Table initialization and resizing control.  When negative, the
     * table is being initialized or resized: -1 for initialization,
     * else -(1 + the number of active resizing threads).  Otherwise,
     * when table is null, holds the initial table size to use upon
     * creation, or 0 for default. After initialization, holds the
     * next element count value upon which to resize the table.
     */
     // 用来控制 Node 数组 table 初始化和扩容时的并发控制，当为负值时，表示数组 table 正在初始化或扩容。默认为0
     // -1 代表正在初始化
     // -N 表示有 N-1 个线程正在进行扩容操作
     // 如果table未初始化，表示table需要初始化的大小。
     // 如果table已初始化，表示table的容量，默认是table大小的0.75倍
    private transient volatile int sizeCtl;
```
#### 1.3 `MIN_TREEIFY_CAPACITY = 64`
```
    /**
     * The smallest table capacity for which bins may be treeified.
     * (Otherwise the table is resized if too many nodes in a bin.)
     * The value should be at least 4 * TREEIFY_THRESHOLD to avoid
     * conflicts between resizing and treeification thresholds.
     */
     // 允许 table 数组坑位上的链表出现红黑树的最小表大小，即如果 table 大小小于64，有某索引位置上的链表长度超过8时，
     // 不会首先转为红黑树，而是扩容node数组 table，重新分配该索引位置上的元素，以避免元素分配不均。
    static final int MIN_TREEIFY_CAPACITY = 64;
```
#### 1.4 `nextTable`
```
    /**
     * The next table to use; non-null only while resizing.
     */
     // table 扩容时，将要扩容后的表，仅正在扩容时非空。
    private transient volatile Node<K,V>[] nextTable;
```
#### 1.5 `transferIndex`
```
    /**
     * The next table index (plus one) to split while resizing.
     */
     // table扩容（元素迁移）时，当前table的长度
    private transient volatile int transferIndex;
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
            // Node 数组 table 为空，则先初始化
            if (tab == null || (n = tab.length) == 0)
                // 第一次插入元素时，初始化 node table 数组，参考下面 2.1
                tab = initTable();
            // Node 数组 table 不为空，根据要添加的元素的 hash 确定一下数组 table 所在索引处元素是否为空
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                // 如为空，则通过CAS方法控制并发，设置要添加的元素
                if (casTabAt(tab, i, null,
                             new Node<K,V>(hash, key, value, null)))
                    break;                   // no lock when adding to empty bin
            }
            else if ((fh = f.hash) == MOVED)    // 当前Map正在扩容，先协助扩容，再更新值
                tab = helpTransfer(tab, f);
            else {  // hash 冲突
                V oldVal = null;
                synchronized (f) {
                    if (tabAt(tab, i) == f) {   // 链表头节点
                        if (fh >= 0) {
                            binCount = 1;   // binCount 用于记录 node 数组 table 当前位置的链表有几个节点
                            for (Node<K,V> e = f;; ++binCount) {    // 遍历链表，每遍历一个节点，binCount 加 1
                                K ek;
                                if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                     (ek != null && key.equals(ek)))) { // 节点已经存在（key相同，== 或 equal），修改节点的值
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)
                                        e.val = value;
                                    break;
                                }
                                Node<K,V> pred = e;
                                if ((e = e.next) == null) { // 遍历到末尾节点为空，则将新节点插入到此处，即链表末尾
                                    pred.next = new Node<K,V>(hash, key,
                                                              value, null);
                                    break;
                                }
                            }
                        }
                        else if (f instanceof TreeBin) {    // 红黑树根节点
                            Node<K,V> p;
                            binCount = 2;
                            // 找到或添加一个节点，如找到，返回该节点，如添加，返回null
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
                    if (binCount >= TREEIFY_THRESHOLD)  // 链表节点数量超过8，转为红黑树，说明是先插入元素到链表，然后再判断要不要转为红黑树
                        treeifyBin(tab, i);
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        addCount(1L, binCount);     // 统计节点个数，检查是否需要resize
        return null;
    }
```

#### 2.1 `Node<K,V>[] initTable()` 初始化node数组table方法
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
                // 所以 sizeCtl = -1 时，表示node数组正在进行初始化或正在扩容。Unsafe类参考：todo
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                try {
                    // 二次检验 node table 是否初始化
                    if ((tab = table) == null || tab.length == 0) {
                        // 初始table大小等于默认容量(DEFAULT_CAPACITY) 16 
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        @SuppressWarnings("unchecked")
                        Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n]; // 创建Node数组
                        table = tab = nt;
                        // 初始化完成，sc 等于 table 大小的 0.75 倍：n 是 table 大小16，n >>> 2 等价于 n/4
                        // n - (n >>> 2) 等价于 n - n/4，等价于 n*0.75
                        sc = n - (n >>> 2);
                    }
                } finally {
                    // 初始化完成后，设置 sizeCtl 等于 table 的容量（table 大小的 0.75 倍）
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }
```
#### 2.2 `void treeifyBin(Node<K,V>[] tab, int index)` 链表转红黑树方法
```
    /**
     * Replaces all linked nodes in bin at given index unless table is
     * too small, in which case resizes instead.
     */
     // 将索引 index 处的链表转化为红黑树，如果 node数组 table 长度小于 64，则不转化，而是扩容 
    private final void treeifyBin(Node<K,V>[] tab, int index) {
        Node<K,V> b; int n, sc;
        if (tab != null) {
            if ((n = tab.length) < MIN_TREEIFY_CAPACITY)    // 表长度小于64
                tryPresize(n << 1); // 进行扩容，调整某个链表中节点数量过多的问题
            else if ((b = tabAt(tab, index)) != null && b.hash >= 0) {  该索引处存在节点，且节点哈希值大于等于0
                synchronized (b) {  // 对链表第一个节点加锁
                    if (tabAt(tab, index) == b) {   // 双重校验，第一个节点没有变化
                        TreeNode<K,V> hd = null, tl = null;
                        for (Node<K,V> e = b; e != null; e = e.next) {
                            // 新生成一个treeNode节点
                            TreeNode<K,V> p =
                                new TreeNode<K,V>(e.hash, e.key, e.val,
                                                  null, null);
                            if ((p.prev = tl) == null)  // 该节点前驱为空，设p为头节点
                                hd = p;
                            else
                                tl.next = p;    // 尾节点的next域设为p
                            tl = p;     // 尾节点赋值为p
                        }
                        // 设置table表中下标为index的值为hd
                        setTabAt(tab, index, new TreeBin<K,V>(hd));
                    }
                }
            }
        }
    }
```
#### 2.3 `void addCount(long x, int check)` 增加节点数量方法
```
    /**
     * Adds to count, and if table is too small and not already
     * resizing, initiates transfer. If already resizing, helps
     * perform transfer if work is available.  Rechecks occupancy
     * after a transfer to see if another resize is already needed
     * because resizings are lagging additions.
     *
     * @param x the count to add
     * @param check if <0, don't check resize, if <= 1 only check if uncontended
     */
     // 新增节点后，将 baseCount + x，用 CAS 方法更新 baseCount 的值，并检测是否需要扩容
     // 如果节点数量baseCount >= table容量sieCtl时，如果没有其它线程在扩容，则进行扩容，如果有其它线程在进行扩容，则协助扩容。
    private final void addCount(long x, int check) {
        CounterCell[] as; long b, s;
        // 利用 Unsafe 类 CAS 更新 baseCount
        if ((as = counterCells) != null ||
            !U.compareAndSwapLong(this, BASECOUNT, b = baseCount, s = b + x)) { // counterCells 非空或比较交换失败
            CounterCell a; long v; int m;
            boolean uncontended = true;     // 无竞争
            if (as == null || (m = as.length - 1) < 0 ||
                (a = as[ThreadLocalRandom.getProbe() & m]) == null ||
                !(uncontended =
                  U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))) {
                fullAddCount(x, uncontended); // 当前线程更新失败，则执行 fullAddCount，把x的值插入到counterCell类中
                return;
            }
            if (check <= 1)
                return;
            s = sumCount();     // 保存节点数量
        }
        if (check >= 0) {
            Node<K,V>[] tab, nt; int n, sc;
            while (s >= (long)(sc = sizeCtl) && (tab = table) != null &&
                   (n = tab.length) < MAXIMUM_CAPACITY) {   // 节点数量大于等于 table 容量 sizeCtl，进行扩容操作
                int rs = resizeStamp(n);
                if (sc < 0) {   // sc = sizeCtl, 小于0表示有其它线程正在初始化或扩容
                    // sc 无符号右移
                    if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                        sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
                        transferIndex <= 0)     // 其它线程正在初始化
                        break;
                    if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) // 其它线程正在扩容，协助扩容
                        transfer(tab, nt);
                }
                else if (U.compareAndSwapInt(this, SIZECTL, sc,
                                             (rs << RESIZE_STAMP_SHIFT) + 2))
                    transfer(tab, null);    // 当前线程进行扩容
                s = sumCount();
            }
        }
    }
```
#### 2.4 `void transfer(Node<K,V>[] tab, Node<K,V>[] nextTab)` 扩容方法
```
    /**
     * Moves and/or copies the nodes in each bin to new table. See
     * above for explanation.
     */
    private final void transfer(Node<K,V>[] tab, Node<K,V>[] nextTab) {
        int n = tab.length, stride;
        // stride 在下面的 while 循环中会用到，这里设置最小值 16，等于table的最小长度
        if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
            stride = MIN_TRANSFER_STRIDE; // subdivide range
        if (nextTab == null) {            // initiating
            try {
                @SuppressWarnings("unchecked")
                Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n << 1];    // 构建新数组 nextTable，大小是原来的2倍
                nextTab = nt;
            } catch (Throwable ex) {      // try to cope with OOME
                sizeCtl = Integer.MAX_VALUE;
                return;
            }
            nextTable = nextTab;
            transferIndex = n;  // 设置 transferIndex 等于当前 table 大小
        }
        int nextn = nextTab.length;
        // ForwardingNode 继承自 Node，这里创建一个 ForwadingNode，把上面初始化好的nextTab作为参数
        // fwd.nextTable = nextTab; fwd.hash = MOVED
        // MOVED = -1，所以hash为-1的node是 ForwadingNode
        ForwardingNode<K,V> fwd = new ForwardingNode<K,V>(nextTab);
        boolean advance = true;
        boolean finishing = false; // to ensure sweep before committing nextTab
        // 自循环处理node数组每个槽位（数组每个索引位置）中的链表元素
        for (int i = 0, bound = 0;;) {
            Node<K,V> f; int fh;
            // 通过CAS方法设置属性transferIndex的值，并初始化i和bound，i指当前处理node数组的槽位，bound指需要处理的槽位边界，
            // 下面while代码第一次执行后，i值等于15，即先处理node数组索引15处的节点，bound值等于0
            while (advance) {
                int nextIndex, nextBound;
                if (--i >= bound || finishing)  // 扩容时，遍历元素是按索引i从大到小开始遍历的
                    advance = false;
                else if ((nextIndex = transferIndex) <= 0) {
                    i = -1;
                    advance = false;
                }
                else if (U.compareAndSwapInt
                         (this, TRANSFERINDEX, nextIndex,
                          nextBound = (nextIndex > stride ?
                                       nextIndex - stride : 0))) {  // CAS 方法设置 transferIndex
                    bound = nextBound;
                    i = nextIndex - 1;
                    advance = false;
                }
            }
            // 第一次走到这里时，i=15, bound=0, n=16, nextn=32
            if (i < 0 || i >= n || i + n >= nextn) {
                int sc;
                if (finishing) {
                    nextTable = null;
                    table = nextTab;
                    sizeCtl = (n << 1) - (n >>> 1);
                    return;
                }
                if (U.compareAndSwapInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
                    if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
                        return;
                    finishing = advance = true;
                    i = n; // recheck before commit
                }
            }
            else if ((f = tabAt(tab, i)) == null)
                advance = casTabAt(tab, i, null, fwd);  // 如果i索引位置节点f为空，则把 fwd 节点放进来
            else if ((fh = f.hash) == MOVED)    // 上面已经说过，fwd节点的hash为MOVED，所以如果i处节点hash为MOVED，则该节点是fwd节点，已遍历并处理过
                advance = true; // already processed
            else {
                // f 表示当前位置i索引处的节点
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        Node<K,V> ln, hn;
                        if (fh >= 0) {  // i处节点hash>=0，常规链表节点
                            // fh 是节点f的hash，n 是原数组的长度，数组长度是2的n次方，初始默认是16，即二进制 10000，
                            // 所以 fh & n 的结果 runBit（移动位）要么是 0，要么是 n（初始默认时，二进制 10000）
                            int runBit = fh & n;
                            Node<K,V> lastRun = f;  // lastRun 要移动的节点
                            // 遍历链表，p为f的后驱节点
                            for (Node<K,V> p = f.next; p != null; p = p.next) {
                                int b = p.hash & n;
                                if (b != runBit) {  // 当后驱节点的hash&n的结果b不等于bunBit时，lastRun 记录该节点
                                    runBit = b;     // 所以，p的后驱节点 p.hash&n 不变时，lastRun 就不会指向后面节点
                                    lastRun = p;    // 因为 lastRun 是要移动的节点，所以这样做，可以使得lastRun后面的节点不用移动
                                }
                            }
                            if (runBit == 0) {
                                ln = lastRun;
                                hn = null;
                            }
                            else {
                                hn = lastRun;
                                ln = null;
                            }
                            // 链表顺序遍历
                            for (Node<K,V> p = f; p != lastRun; p = p.next) {
                                int ph = p.hash; K pk = p.key; V pv = p.val;
                                if ((ph & n) == 0)  // 上面提到，hash & n的结果要么是0，要么是n，这里 ln保存是0的节点
                                    ln = new Node<K,V>(ph, pk, pv, ln);
                                else
                                    hn = new Node<K,V>(ph, pk, pv, hn); // hn是n保存是n的节点
                            }
                            // 以上 for 循环结束后，该 i 位置处的链表创建出来了两个新的链表，一个是 ln，为 p.hash & n 为 0 的节点构成的链表
                            // 一个是 hn，为 p.hash & n 为 1 的节点构成的链表，两个链表中的节点还是保留原来的先后顺序。
                            // 在 nextTabl i 位置插入链表 ln
                            setTabAt(nextTab, i, ln);   // hash & n 为 0 的节点保留原位置不动
                            // 在 nextTabl i+n 位置插入链表 hn
                            setTabAt(nextTab, i + n, hn);   // hash & n 为 1 的节点向后移动n位，新位置为 i + n
                            setTabAt(tab, i, fwd);
                            // 标记为true，回到上面的while循环，以执行--i操作，
                            advance = true;
                        }
                        // 树节点
                        else if (f instanceof TreeBin) {
                            TreeBin<K,V> t = (TreeBin<K,V>)f;
                            TreeNode<K,V> lo = null, loTail = null;
                            TreeNode<K,V> hi = null, hiTail = null;
                            int lc = 0, hc = 0;
                            for (Node<K,V> e = t.first; e != null; e = e.next) {
                                int h = e.hash;
                                TreeNode<K,V> p = new TreeNode<K,V>
                                    (h, e.key, e.val, null, null);
                                if ((h & n) == 0) {
                                    if ((p.prev = loTail) == null)
                                        lo = p;
                                    else
                                        loTail.next = p;
                                    loTail = p;
                                    ++lc;
                                }
                                else {
                                    if ((p.prev = hiTail) == null)
                                        hi = p;
                                    else
                                        hiTail.next = p;
                                    hiTail = p;
                                    ++hc;
                                }
                            }
                            // 如果树中的结点数量小于等于6，则将树转化为链表
                            ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) :
                                (hc != 0) ? new TreeBin<K,V>(lo) : t;
                            hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) :
                                (lc != 0) ? new TreeBin<K,V>(hi) : t;
                            setTabAt(nextTab, i, ln);
                            setTabAt(nextTab, i + n, hn);
                            setTabAt(tab, i, fwd);
                            advance = true;
                        }
                    }
                }
            }
        }
    }
```
