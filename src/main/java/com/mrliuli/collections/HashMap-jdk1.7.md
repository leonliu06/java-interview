## HashMap源码（jdk1.7）阅读
- `HashMap`类主要由一个`Entry`数组`Entry<K,V>[] table`构成;

### 1. `put`方法
```
    public V put(K key, V value) {
        // 如果table为空，则初始化
        if (table == EMPTY_TABLE) {
            inflateTable(threshold);
        }
        // 这里可以看出HashMap的key可以为空
        if (key == null)
            return putForNullKey(value);    
        int hash = hash(key);
        // 用 key 的 hash 通过 `h & (length-1)` 来计算要放入的索引位置 `i`
        int i = indexFor(hash, table.length);
        // 如果该位置处的元素非空，则开始遍历链表
        for (Entry<K,V> e = table[i]; e != null; e = e.next) {
            Object k;
            // 如果结点e的hash相等并且e的key相同（== || equals），则将新value覆盖e的旧value
            if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                // 相同key，返回原元素value
                return oldValue;
            }
        }

        modCount++;
        // 走到这里，说明当前索引i位置要么为空，要么链表中不存在与将要添加的元素相同的key的结点，这时就要将该元素添加在该索引i位置处
        // 具体添加方法看下述 1.1
        addEntry(hash, key, value, i);
        // 不同元素，返回null
        return null;
    }
```
#### 1.1 `addEntry(int hash, K key, V value, int bucketIndex)`方法
```
    void addEntry(int hash, K key, V value, int bucketIndex) {
        // 如果map中含有的元素个数size大于等于扩容临界数量threshold(默认是容量capicity的0.75倍，即16*0.75为12)
        // 并且，数组table的bucketIndex索引处非空，则先进行扩容操作
        if ((size >= threshold) && (null != table[bucketIndex])) {
            // 新容量是原来的2倍，扩容方法参看下述 1.2
            resize(2 * table.length);
            hash = (null != key) ? hash(key) : 0;
            // 扩容后，重新计算该元素要插入的索引位置
            bucketIndex = indexFor(hash, table.length);
        }
        
        // 数组索引bucketIndex处为空或扩容完成后，则在索引bucketIndex处插入新元素
        createEntry(hash, key, value, bucketIndex);
    }
    
    void createEntry(int hash, K key, V value, int bucketIndex) {
        Entry<K,V> e = table[bucketIndex];
        // 新元素next指针指向该索引处结点，然后再把该新元素放入该索引处，即新添加结点是从链表头节点插入
        // 并发问题1：当有两个线程t1和t2都拿到索引bucketIndex时，如t1先执行下面赋值操作，则t2再执行时，
        // 因为t2线程拿到的头结点还是t1未插入结点时的链表头结点，所以t2执行完成后，则table[bucketIndex]只保留了t2结点开始的链表，
        // 所以t1刚才插入的结点就访问不到了，从而体体现为t1刚才插入的结点数据的丢失
        table[bucketIndex] = new Entry<>(hash, key, value, e);
        size++;
    }
```
#### 1.2 `void resize(int newCapacity)`方法，扩容
```
    void resize(int newCapacity) {
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[newCapacity];
        // 具体扩容方法通过transfer方法进行
        transfer(newTable, initHashSeedAsNeeded(newCapacity));
        // table指向扩容后的新数组
        table = newTable;
        threshold = (int)Math.min(newCapacity * loadFactor, MAXIMUM_CAPACITY + 1);
    }
```
##### 1.2.1 `void transfer(Entry[] newTable, boolean rehash)`方法，移动元素
```
    /**
     * Transfers all entries from current table to newTable.
     */
    void transfer(Entry[] newTable, boolean rehash) {
        int newCapacity = newTable.length;
        // 遍历原数组table
        for (Entry<K,V> e : table) {
            // 遍历链表
            while(null != e) {
                Entry<K,V> next = e.next;
                // 标记：Tag1（后文引用）
                if (rehash) {
                    e.hash = null == e.key ? 0 : hash(e.key);
                }
                // 重新计算该结点在新数组中的索引位置
                int i = indexFor(e.hash, newCapacity);
                // 插入到头部
                e.next = newTable[i];
                newTable[i] = e;
                // 顺序遍历下一个结点
                // 标记：Tag2（后文引用）
                e = next;
            }
            // 通过上面链表的遍历可看出，是顺序遍历原链表，然后在新的链表位置从头部插入结点，所以扩容后，新链表结点与原来比较，结点顺序是反的
            // 举例如下：原链表 A --> B --> C --> null 在索引1处，按A、B、C顺序遍历，如把A插入到索引17处，则17处链表为：A --> null
            // 经计算 B 也要插入到17处，因是从头结点插入，所以，17处的链表为：B --> A --> null，即扩容后，新链表结点是反序的。
            // 并发问题2：假设上述过程有t1和t2两个线程同时执行到 Tag1 处，此时，t1挂起，即t1遍历原始链表刚开始到A结点时挂起了，t2线程顺畅执行完上述过程。
            // 之后t1线程继续进行，因t1持有的结点还是A，则结点同样会插入到索引17位置，形成 A --> B --> A 这个样子，
            // t1继续进行，因挂起的时候，next是B，所以执行到 Tag2 时，e 指向的是 B 结点，继续进行，开始下一轮循环，则 B 也会插入到 17 处，
            // 此时 17 处链表的样子是 B --> A --> B --> A，即形如 B <==> A 所谓的环形链。造成的问题是，当调用 get 方法，查找一个不存在的 key 时，
            // 如果 该 key 计算后的索引位置正好是 17 时，则在遍历环形链表时，就会形成死循环。
        }
    }
```
