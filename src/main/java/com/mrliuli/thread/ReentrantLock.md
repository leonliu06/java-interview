## ReentrantLock(jdk1.8)源码阅读

### 1 构造函数
```
    // 默认构造函数是非公平锁
    public ReentrantLock() {
        sync = new NonfairSync();
    }
    
    // 通过构造参数 fair 选择创建非公平锁
    public ReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }
```
### 2 lock()方法
```
    public void lock() {
        // 以公平锁为例，则这里调用的是 FairSync 的 lock 方法
        sync.lock();
    }
```
#### 2.1 FairSync lock()方法

```
    /**
     * Sync object for fair locks
     */
    static final class FairSync extends Sync {
        private static final long serialVersionUID = -3000897897090466540L;

        final void lock() {
            // 调用同步队列 AbstractQueuedSynchronizer（即AQS） 的 acquire()方法
            acquire(1);
        }
    }
        
```
#### 2.2 同步队列 AbstractQueuedSynchronizer（即AQS） 的 acquire()方法
```
    public final void acquire(int arg) {
        // 先调用 FairSync 的 tryAcquire() 方法，如返回false，
        // 即获取锁失败，则调用 addWaiter(Node.EXCLUSIVE) 方法，
        // 将当前线程封装成队列节点入到队尾，并返回该节点。
        // 再调用acquireQueued()方法，阻塞该线程。
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
```
#### 2.3 FairSync 的 tryAcquire() 方法
```
    /**
     * Fair version of tryAcquire.  Don't grant access unless
     * recursive call or no waiters or is first.
     */
    protected final boolean tryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        // 获取同步状态 state，volatile int 类型
        int c = getState();
        // c == 0 指当前没有任何线程持有锁
        if (c == 0) {
            // hasQueuedPredecessors() 查询是否已有线程在队列中等待获取锁
            // 如没有，则调用 compareAndSetState(0, acquires) 方法
            if (!hasQueuedPredecessors() &&
                // 利用 Unsafe类原子性修改字段值，当且公当 state == 0 时，将state设置为 acquires
                compareAndSetState(0, acquires)) {
                // 设置当前线程为锁的持有者
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        // 有线程持有锁，如果当前线程是已持有锁的线程，说明是同一线程再次申请持有锁，
        // 则state + acquires，返回true，即可重入的原理
        else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            if (nextc < 0)
                throw new Error("Maximum lock count exceeded");
            // state + acquires，acquires=1，则state相当于同一线程持有锁的次数
            setState(nextc);
            return true;
        }
        return false;
    }

```
#### 2.4 AbstractQueuedSynchronized 的 addWaiter() 方法
当调用 tryAcquire() 尝试获取锁失败时，则调用 addWaiter() 方法，将当前线程入队，作为队尾节点。
AbstractQueuedSynchronized(AQS)是一种FIFO同步队列，由双向链表构成，链表节点是Node。
```
    /**
     * Creates and enqueues node for current thread and given mode.
     *
     * @param mode Node.EXCLUSIVE for exclusive, Node.SHARED for shared
     * @return the new node
     */
    // 将当前线程封闭成AQS队列节点node添加到队尾
    private Node addWaiter(Node mode) {
        // 将当前线程构造成AQS队列节点node
        Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
        // pred 指向队尾节点
        Node pred = tail;
        // 队尾非空
        if (pred != null) {
            // 把将要入队的该节点node的prev指针指向队尾节点
            node.prev = pred;
            // 利用Unsafe类CAS操作，将队尾节点指针tail指向新入队的节点node
            if (compareAndSetTail(pred, node)) {
                // 原队尾节点的next指针（表示下一节点）指向新入队的队尾节点node。
                pred.next = node;
                // 返回新入队的队尾节点
                return node;
            }
        }
        enq(node);
        // 返回当前线程的节点
        return node;
    }

```
#### 2.5 AbstractQueuedSynchronized 的 acquireQueued() 方法
```
    /**
     * Acquires in exclusive uninterruptible mode for thread already in
     * queue. Used by condition wait methods as well as acquire.
     *
     * @param node the node
     * @param arg the acquire argument
     * @return {@code true} if interrupted while waiting
     */
    final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                // 当前节点的姜前驱节点
                final Node p = node.predecessor();
                // 如当前节点的前驱节点是队头节点，则再尝试获取锁
                if (p == head && tryAcquire(arg)) {
                    // 如获取锁成功，则将当前节点设为队头节点
                    setHead(node);
                    p.next = null; // help GC
                    // 标记获取锁没有失败
                    failed = false;
                    // 跳出循环，返回false，即没有中断
                    return interrupted;
                }
                // 当前节点的前驱节点不是队头节点，或再尝试获取锁失败
                // 调用 AQS 的 shouldParkAfterFailedAcquire() 方法，检查当前线程获取锁失败后是否需要阻塞。
                // 如果需要阻塞，则执行 parkAndCheckInterrupt() 方法阻塞当前线程。
                if (shouldParkAfterFailedAcquire(p, node) &&
                    // 阻塞当前线程，返回当前线程是否中断
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
    
    private final boolean parkAndCheckInterrupt() {
        // 挂起当前线程，等待被唤醒
        LockSupport.park(this);
        return Thread.interrupted();
    }
```
### 3 unlock()方法
```
    public void unlock() {
        // 这里的 sync 以 FairSync 为例，没有重写父类的 release() 方法，
        // 直接调用 AbstractQueuedSynchronizer 的 release(int arg) 方法
        sync.release(1);
    }
```
#### 3.1 AbstractQueuedSynchronizer，即AQS，的 release(int arg) 方法
```
    // AQS 的 release 方法
    public final boolean release(int arg) {
        // 尝试释放持有的锁，如释放成功，则从同步等待队列的头节点开始唤醒等待线程
        if (tryRelease(arg)) {
            Node h = head;
            if (h != null && h.waitStatus != 0)
                // 唤醒同步等待队列的阻塞线程
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
```
#### 3.2 ReentrantLock 的 tryRelease(int releases) 方法
```
    // 尝试释放持有的锁
    protected final boolean tryRelease(int releases) {
        // state 减 1
        int c = getState() - releases;
        if (Thread.currentThread() != getExclusiveOwnerThread())
            throw new IllegalMonitorStateException();
        boolean free = false;
        if (c == 0) {
            // c == 0 表示当前线程释放锁成功
            free = true;
            setExclusiveOwnerThread(null);
        }
        setState(c);
        return free;
    }
```
#### 3.3 AbstractQueuedSynchronizer，即AQS，的 unparkSuccessor(Node node) 方法
```
    /**
     * Wakes up node's successor, if one exists.
     *
     * @param node the node
     */
    // 唤醒 node 后继节点的阻塞线程
    private void unparkSuccessor(Node node) {
        /*
         * If status is negative (i.e., possibly needing signal) try
         * to clear in anticipation of signalling.  It is OK if this
         * fails or if status is changed by waiting thread.
         */
        int ws = node.waitStatus;
        if (ws < 0)
            compareAndSetWaitStatus(node, ws, 0);

        /*
         * Thread to unpark is held in successor, which is normally
         * just the next node.  But if cancelled or apparently null,
         * traverse backwards from tail to find the actual
         * non-cancelled successor.
         */
        Node s = node.next;
        // 如果后继节点为空或waitSttus > 0即已被取消，则从队尾开始遍历，
        // 寻找第一个waitSttus <= 0即未被取消的节点
        if (s == null || s.waitStatus > 0) {
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0)
                    s = t;
        }
        if (s != null)
            // 唤醒等待队列中的线程，之前执行到 parkAndCheckInterrupt 方法
            // 的线程继续执行，再次尝试获取锁。
            LockSupport.unpark(s.thread);
    }
```
### 3 tryLock()方法
```
    // 尝试获取锁，成功返回true，如获取不到，则不会加入到AQS队尾，不阻塞当前线程，而是直接返回false。
    public boolean tryLock() {
        return sync.nonfairTryAcquire(1);
    }
    
    /**
     * Performs non-fair tryLock.  tryAcquire is implemented in
     * subclasses, but both need nonfair try for trylock method.
     */
    final boolean nonfairTryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        if (c == 0) {
            if (compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            if (nextc < 0) // overflow
                throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        // 获取不到，直接返回false
        return false;
    }

```
## 原理总结
&emsp;&emsp;设有T1和T2两个线程同时执行`lock()`方法，如T1先执行获取到锁，则`state`值加1，后续如T1又执行了`lock()`方法，
则T1会直接获取到锁，同时`state`值再加1，所以`state`值可以理解为线程T1执行`lock()`方法（获取到锁）的次数。  
&emsp;&emsp;当线程T2执行lock()方法时，由于锁已被T1持有，此时，线程T2会封装成`Node`节点，插入到同步等待队列`AbstractQueuedSynchronizer，即AQS`
的队尾，同时阻塞当前线程，等待被唤醒以再次尝试获取锁。  
&emsp;&emsp;线程T1执行`unlock()`方法以释放锁，每次执行时会将`state`值减1，当`state`值减到0时，锁才会被完全释放，
此时AQS会从同步阻塞队列的头节点开始唤醒阻塞的线程，使阻塞线程恢复执行，再次尝试获取锁。  
&emsp;&emsp;`ReentrantLock`公平锁的实现主要体现在`AQS(AbstractQueuedSynchronizer)`和`volatile state`上。