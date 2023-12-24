package alonso;

class Room {
    public static class EntryPoint {
        public final Room room;
        public final int doorN;
        public EntryPoint(Room room, int doorN) {
            this.room = room;
            this.doorN = doorN;
        }
    }

    private static class BST<T extends Comparable<T>, E> {
        private int size;
        private T key;
        private E data;
        private BST<T, E> left, right;

        public boolean isEmpty() { return data==null; }
        public int size() { return size; }
        public E get(T key) {
            if (isEmpty())  return null;
            if (key.compareTo(this.key)==0)  return data;
            if (key.compareTo(this.key) <0)  return (left==null)? null : left.get(key);
            if (key.compareTo(this.key) >0)  return (right==null)? null : right.get(key);
            return null;
        }
        public boolean put(T key, E element) {
            if (isEmpty()) { this.key = key; this.data = element; size++; return true; }
            if (key.compareTo(this.key)==0)  { this.data = element; return false; }
            if (key.compareTo(this.key) <0)  {
                if (left==null)  left = new BST<T, E>();
                boolean ch = left.put(key, element);
                if (ch)  size++;
                return ch;
            }
            if (key.compareTo(this.key) >0) {
                if (right==null)  right = new BST<T, E>();
                boolean ch = right.put(key, element);
                if (ch)  size++;
                return ch;
            }
            return false;
        }
    }

    public String name;
    private final int cap;
    private int total;


    private BST<Integer, Integer> pools = new BST<Integer, Integer>();

    public Room(int cap) { this(cap, Integer.toString(cap)); }
    public Room(int cap, String name) {
        this.cap = cap;
        this.name = name;
    }
    
    private synchronized int addPool(int id) {
        int poolN = pools.size();
        int poolSize = (cap-1)/(poolN+1);
        // System.out.printf("[R%s ] Adding pool (%d pools, size %d)\n", name, poolN+1, poolSize);
        if (poolN == 0) {
            pools.put(id, (cap-1));
            return cap;
        }
        
        int delta = poolSize-cap/poolN;
        for (int i=0; i<pools.size(); i++) {
            pools.put(i, pools.get(i)+delta);
        }
        pools.put(id, poolSize);
        return pools.get(id);
    }
    protected synchronized int getPool(int id) {
        Integer pool = pools.get(id);
        if (pool == null)  pool = addPool(id);
        return pool;
    }
    public int getTotal() { return total; }
    
    public synchronized boolean tryEnter(int poolID) {
        int pool = getPool(poolID);
        if (pool>0 && total < cap) {
            pools.put(poolID, --pool);
            total++;
            notifyAll();
            return true;
        }
        return false;
    }
    public synchronized void enter(int poolID) {
        int pool = getPool(poolID);
        while (pool<=0 || total > cap) {
            try { wait(); } catch (InterruptedException e) {}
        }
        pools.put(poolID, --pool);
        total++;
        notifyAll();
    }

    public synchronized void exit() { exit(0); }
    public synchronized void exit(int poolID) {
        if (pools.get(poolID)==null)  throw new IllegalArgumentException("Invalid pool ID " + poolID);
        pools.put(poolID, pools.get(poolID)+1);
        total--;
    }
}
