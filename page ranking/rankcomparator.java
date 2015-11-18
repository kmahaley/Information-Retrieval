import java.util.Comparator;
import java.util.Map;

class rankcomparator implements Comparator<String> {
 
    Map<String, Double> map;
 
    public rankcomparator(Map<String, Double> base) {
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

