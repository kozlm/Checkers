package gamelogic;

public class Pair<K extends Comparable<K>, T extends Comparable<T>> implements Comparable<Pair<K,T>> {
    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    private K key;
    private T value;

    public Pair(K key, T value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key.toString() + " -> " + value.toString();
    }

    @Override
    public int compareTo(Pair<K,T> other) {
        int compareResult = this.key.compareTo(other.key);

        if (compareResult == 0) {
            compareResult = this.value.compareTo(other.value);
        }

        return compareResult;
    }



}
