package ex5.main;

import java.io.File;
import java.io.IOException;

/**
 * The class that contains the main.
 */
public class Sjavac {

    /**
     * The main function.
     * @param args the arguments (which are the name of the file).
     */
    public static void main(String[] args) {
        try {

            if (args.length != 1) {
                throw new IOException("ERROR: Illegal number of arguments.");
            }

            String filePath = args[0];
            File file = new File(filePath);

            if (!file.exists()) {
                throw new IOException("ERROR: File not found: " + filePath);
            }
            if (!filePath.endsWith(".sjava")) {
                throw new IOException("ERROR: Wrong file format. Must be .sjava");
            }

            Verifier verifier = new Verifier(filePath);
            verifier.verify();

            System.out.println(0);

        } catch (IOException e) {
            System.out.println(2);
            System.err.println(e.getMessage());
        } catch (SjavacException e) {
            System.out.println(1);
            System.err.println(e.getMessage());
        } 
    }

}
