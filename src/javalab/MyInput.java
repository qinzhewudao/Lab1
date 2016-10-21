package javalab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class MyInput {
	private MyInput(){
		
	}
    public static String readString() throws IOException {
        final BufferedReader brrrrrr = new BufferedReader(new InputStreamReader(System.in), 1);
        return brrrrrr.readLine();
    }

    public static int readInt() throws NumberFormatException, IOException {
        return Integer.parseInt(readString());
    }
}
