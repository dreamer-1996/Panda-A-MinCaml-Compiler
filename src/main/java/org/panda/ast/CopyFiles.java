package org.panda.ast;

import java.io.*;
import java.util.*;

public class CopyFiles {
    public void copyContent(File a, File b) throws Exception {
        FileInputStream in = new FileInputStream(a);
        FileOutputStream out = new FileOutputStream(b);

        try {
            int n;
            while ((n = in.read()) != -1) {
                out.write(n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
