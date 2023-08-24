import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MergeSort
{
    private static final int BLOCK_SIZE = 5;
    private String sortMode;
    private String dataType;
    private String outputFile;
    private List<String> inputFiles;
    private BufferedWriter writer;

    public void run (String sortMode, String dataType, String outputFile, List<String> inputFiles)
    {
        this.sortMode = sortMode;
        this.dataType = dataType;
        this.outputFile = outputFile;
        this.inputFiles = inputFiles;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile)))
        {
            this.writer = writer;
            switch (dataType)
            {
                case "-i" -> {
                    sortInt();
                }

                case "-s" -> {
                    sortStr();
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("An error occurred during creating output writer!");
        }
    }

    private <T> void writeOutput(List<T> data)
    {
        try
        {
            for (T d : data)
            {
                this.writer.write(d.toString());
                this.writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("An error occurred during writing in: " + outputFile);
        }
    }

    private void sortInt()
    {
        List<Integer> nums = new ArrayList<>();
        String num = " ";

        for (String input : inputFiles)
        {
            try (BufferedReader reader = new BufferedReader(new FileReader(input)))
            {
                while ((num = reader.readLine()) != null)
                {
                    nums.add(Integer.parseInt(num));
                    if(nums.size() >= BLOCK_SIZE)
                    {
                        mergeSort(nums, 0, nums.size()-1);
                        writeOutput(nums);
                        nums.clear();
                    }
                }
            }
            catch (NumberFormatException nfe)
            {
                System.out.println("Input contains incorrect data: " + num + " In file: " + input);
            }
            catch (IOException e)
            {
                System.out.println("An error occurred during reading input files!");
            }
        }

        if (nums.size() > 0)
        {
            mergeSort(nums, 0, nums.size()-1);
            writeOutput(nums);
        }
    }

    private boolean isNumeric(String str)
    {
        try
        {
            Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    private void sortStr()
    {
        List<String> strs = new ArrayList<>();
        String str;

        for (String input : inputFiles)
        {
            try (BufferedReader reader = new BufferedReader(new FileReader(input)))
            {
                while ((str = reader.readLine()) != null)
                {
                    if (isNumeric(str))
                    {
                        System.out.println("Input contains incorrect data: " + str + " In file: " + input);
                    }
                    strs.add(str);

                    if (strs.size() >= BLOCK_SIZE)
                    {
                        mergeSort(strs, 0, strs.size() - 1);
                        writeOutput(strs);
                        strs.clear();
                    }
                }
            }
            catch (IOException e)
            {
                System.out.println("An error occurred during reading input files!");
            }
        }

        if (strs.size() > 0)
        {
            mergeSort(strs, 0, strs.size() - 1);
            writeOutput(strs);
        }
    }

    private <T extends Comparable<T>> void mergeSort(List<T> arr, int left, int right)
    {
        if (left < right)
        {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid+1, right);
            merge(arr, left, mid, right);
        }
    }

    private <T extends Comparable<T>> void merge(List<T> arr, int left, int mid, int right)
    {
        List<T> temp = new ArrayList<>();
        int i = left;
        int j = mid + 1;

        while (i <= mid && j <= right)
        {
            if (sortMode.equalsIgnoreCase("-a"))
            {
                // Integer and String implement Comparable
                // integers are compared as usual
                // strings are compared lexicographically
                if (arr.get(i).compareTo(arr.get(j)) <= 0)
                {
                    temp.add(arr.get(i));
                    i++;
                }
                else
                {
                    temp.add(arr.get(j));
                    j++;
                }
            }
            else
            {
                if (arr.get(i).compareTo(arr.get(j)) >= 0)
                {
                    temp.add(arr.get(i));
                    i++;
                }
                else
                {
                    temp.add(arr.get(j));
                    j++;
                }
            }
        }

        while (i <= mid)
        {
            temp.add(arr.get(i));
            i++;
        }

        while (j <= right)
        {
            temp.add(arr.get(j));
            j++;
        }

        for (int k = 0; k < temp.size(); k++)
        {
            arr.set(left + k, temp.get(k));
        }
    }
}
