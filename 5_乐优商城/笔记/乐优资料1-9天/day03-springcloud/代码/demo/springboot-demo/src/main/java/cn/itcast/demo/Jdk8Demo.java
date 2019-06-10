package cn.itcast.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @author: HuYi.Zhang
 * @create: 2018-06-14 18:27
 **/
public class Jdk8Demo {
    public static void main(String[] args) {
//        List<Integer> list = Arrays.asList(10, 5, 25, -15, 20);

        // JDK7
//        Collections.sort(list, new Comparator<Integer>() {
//            @Override
//            public int compare(Integer o1, Integer o2) {
//                return o1 - o2;
//            }
//        });

        // Collections.sort(list,(o1,o2) -> o1 - o2);

//        list.sort((o1, o2) -> o1 - o2);

        // JDK7
//        for (Integer i : list) {
//            System.out.println(i);
//        }

        // JDK8
//        list.forEach(i -> System.out.println(i));

//        list.sort((i1,i2) -> i1- i2);
//
//        new Thread(() -> {}).start();

        List<Integer> list = Arrays.asList(1000, 2000, 3000);

        // JDK7
        List<String> result = new ArrayList<>();
        for (Integer i : list) {
            result.add(Integer.toHexString(i));
        }
        System.out.println("result = " + result);

        // JDK7
        List<String> result2 = convert(list, new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) {
                return Integer.toHexString(integer);
            }
        });
        System.out.println("result2 = " + result2);

        // JDK8
        convert(list, Integer::toHexString);
    }

    public static <T, R> List<R> convert(List<T> list, Function<T, R> function) {
        List<R> result = new ArrayList<>();
        for (T t : list) {
            R r = function.apply(t);
            result.add(r);
        }
        return result;
    }
}
