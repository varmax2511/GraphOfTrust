package edu.buffalo.cse.wot.neo4j;

/**
 * 
 * @author varunjai
 *
 * @param <K>
 * @param <V>
 */
public class Pair<K, V> {

  private final K key;
  private final V value;

  /**
   * 
   * @param key
   * @param value
   */
  public Pair(K key, V value) {
    this.key = key;
    this.value = value;
  }

  /**
   * 
   * @return
   */
  public K getKey() {
    return key;
  }

  /**
   * 
   * @return
   */
  public V getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Pair other = (Pair) obj;
    if (key == null) {
      if (other.key != null)
        return false;
    } else if (!key.equals(other.key))
      return false;
    return true;
  }

}
