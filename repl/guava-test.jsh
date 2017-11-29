
// https://google.github.io/guava/releases/19.0/api/docs/com/google/common/collect/Multimap.html
import com.google.common.collect.*;
ListMultimap<String,String> multiMap = ArrayListMultimap.create();
multiMap.put("a", "1")
multiMap.put("a", "2")
multiMap.put("a", "1")
multiMap.get("a")
multiMap.remove("a", "1")
multiMap.get("a")
