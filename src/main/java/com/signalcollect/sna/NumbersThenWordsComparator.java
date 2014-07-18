package com.signalcollect.sna;

import java.util.Comparator;

class NumbersThenWordsComparator implements Comparator<String> {
    private static Integer intValue(String s) {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public int compare(String s1, String s2) {
        Integer i1 = intValue(s1);
        Integer i2 = intValue(s2);
        if (i1 == null && i2 == null) {
            return s1.compareTo(s2);
        } else if (i1 == null) {
            return -1;
        } else if (i2 == null) {
            return 1;
        } else {
            return i1.compareTo(i2);
        }
    }       
}
//
//public void myMethod(Map<String, String[]> originalMap) {
//    TreeMap<String, String[]> t =
//        new TreeMap<String, String[]>(new NumbersThenWordsComparator());
//    t.putAll(originalMap);
//    // now iterate over t, which will produce entries in the desired order
//}
