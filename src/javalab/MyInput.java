package javalab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyInput {
    public static String read_string() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in), 1);
        String string = "";
        try {
            string = br.readLine();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return string;
    }

    public static int read_int() {
        return Integer.parseInt(read_string());
    }
}
