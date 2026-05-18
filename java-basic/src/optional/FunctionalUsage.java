package optional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionalUsage {
    public static void main(String[] args) {
        List<Integer> arr = new ArrayList<>(Arrays.asList(1, 2, null, 3, null, 4, 5, 6));

        List<Integer> onlyNotNull = arr.stream()
                .filter(n -> n!=null)
//                .collect(Collectors.toList());
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        System.out.println(onlyNotNull); // [1, 2, 3, 4, 5, 6]
    }
}
