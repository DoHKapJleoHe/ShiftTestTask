import java.util.ArrayList;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        String sortMode = "-a";
        String dataType = "";
        String outputFile = "";
        List<String> inputFiles = new ArrayList<>();

        if (args.length < 2)
        {
            System.out.println("Need more than 3 arguments!");
            System.out.println("Usage example: java CommandLineArguments " +
                    "[-a | -d] [-s | -i] outputfile.txt inputfile1.txt inputfile2.txt ...");
            return;
        }

        int index = 0;
        if (args[index].equalsIgnoreCase("-d") || args[index].equalsIgnoreCase("-a"))
        {
            sortMode = args[index];
            index++;
        }

        dataType = args[index];
        if (!dataType.equalsIgnoreCase("-s") && !dataType.equalsIgnoreCase("-i"))
        {
            System.out.println("Incorrect data type. Valid types: -s (strings) or -i (integers)");
            return;
        }

        index++;
        outputFile = args[index];

        System.out.println("Sorting mode: " + sortMode);
        System.out.println("Data type: " + dataType);
        System.out.println("Output file: " + outputFile);

        if (args.length < 4)
        {
            System.out.println("Need to provide at least 1 input file!");
            return;
        }

        index++;
        while (index < args.length)
        {
            inputFiles.add(args[index]);
            index++;
        }

        System.out.println(inputFiles);

        MergeSort sort = new MergeSort();
        sort.run(sortMode, dataType, outputFile, inputFiles);
    }
}
