package org.zwx;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenData {
    public static final String PATH1 = "datas/odds.txt";
    public static final String PATH2 = "datas/evens.txt";

    public static void main(String[] args) throws IOException {
        GenData.writeNums();
    }

    // 写数据
    public static void writeNums() throws IOException {
        List<Integer> odds = new ArrayList<>();
        List<Integer> evens = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            if (i % 2 == 0) {
                evens.add(i);
            } else {
                odds.add(i);
            }
        }

        FileUtils.writeLines(new File(PATH1), odds);
        FileUtils.writeLines(new File(PATH2), evens);
    }

    // 读数据
    public static List<Integer> readNums(String path) {
        try {
            List<String> strings = FileUtils.readLines(new File(path), StandardCharsets.UTF_8);
            return strings.stream().map(Integer::parseInt).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
}
