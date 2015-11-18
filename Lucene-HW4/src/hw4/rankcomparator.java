package hw4;
import java.util.Comparator;
import java.util.Map;

class rankcomparator implements Comparator<String> {
 
    Map<String, Long> map;
 
    public rankcomparator(Map<String, Long> base) {
        this.map = base;
    }
 
    public int compare(String a, String b) {
        if (map.get(a) >= map.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys 
    }
}

