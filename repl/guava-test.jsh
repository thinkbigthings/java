import com.google.common.base.Optional;
Integer invalidInput = null;
Optional<Integer> b =  Optional.of(new Integer(10));
b.get()

// java 8's is .ofNullable()
Optional<Integer> a = Optional.fromNullable(null);
a.or(5);

// https://google.github.io/guava/releases/19.0/api/docs/com/google/common/collect/Multimap.html
import com.google.common.collect.*;
ListMultimap<String,String> multiMap = ArrayListMultimap.create();
multiMap.put("a", "1")
multiMap.put("a", "2")
multiMap.put("a", "1")
multiMap.get("a")

