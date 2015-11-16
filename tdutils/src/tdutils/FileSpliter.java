package tdutils;

/**
 *  A Java 'FileSplit'ter for Win boxes.
 * This is a program, as name suggests, to split large files into smaller ones.
 * To run the program specify the filename to be split and number of pieces it
 * has to be split into.
 * Along with the pieces that are made, the program writes a batch file
 * (so..it works on Win boxes as of now) which on executing will get back
 * the original file.
 * Getting back the original file by combining all the pieces is done by
 * the dos "COPY" command.
 * You will see that the option specified for the copy command is '/b'
 * asking to treat the pieces binary.
 * The program may be not be efficient while splitting huge files but the
 * purpose is served. If you make changes to the code, please
 * keep me updated at 'x_dim001@yahoo.com'.
 */
public class FileSpliter {
//
//    public static void main(String[] args) {
//
//      try {
//
//        /* A check for the arguments */
//        if (args.length != 2) {
//          System.out.println(
//              "Usage: java FileSplit <cFileName> <no. of nPartsSize>");
//          System.exit(0);
//        }
//
//        String cFileName = args[0]; // The cFileName
//        int nPartsSize = Integer.parseInt(args[1]); // Number of nPartsSize the file to be split
//        StringBuffer com = new StringBuffer("copy /b "); // Command string for the batch file
//        long pos = 0; //current position of the file pointer
//        int i = 1;
//
//        RandomAccessFile ra = new RandomAccessFile(cFileName, "r");
//        double size = ra.length();
//
//        int rem = (int) size % nPartsSize; // The remainder if the size is not exactly divisible by the no. of nPartsSize
//
//        for (; i <= nPartsSize - 1; i++) { // Create n-1 file pieces
//          com.append(cFileName + "." + i + "+");
//          ra.seek(pos);
//          byte[] b = new byte[ (int) (size / nPartsSize)];
//          ra.read(b);
//          pos = ra.getFilePointer();
//          RandomAccessFile ra2 = new RandomAccessFile(cFileName + "." + i, "rw"); //write the piece
//          ra2.write(b);
//          ra2.close();
//
//        }
//
//        com.append(cFileName + "." + i); // write the last piece with 'remainder' if any
//        ra.seek(pos);
//        byte[] b = new byte[ (int) (size / nPartsSize) + rem];
//        ra.read(b);
//        RandomAccessFile ra2 = new RandomAccessFile(cFileName + "." + i, "rw");
//        ra2.write(b);
//        ra2.close();
//
//        com.append(" " + cFileName);
//
//        DataOutputStream out = new DataOutputStream(new FileOutputStream(
//            "combine.bat")); //write the batch file
//        out.writeBytes(com.toString());
//
//        out.close();
//      }
//
//    }
//    catch (Exception ex) {
//      ex.printStackTrace();
//    }
//  }
}
