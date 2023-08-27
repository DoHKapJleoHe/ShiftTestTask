import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MergeSort
{
    private static final int BLOCK_SIZE = 1000;
    private String sortMode;
    private String dataType;
    private String outputFile;
    private List<String> inputFiles; // files, containing data for the program
    private List<File> helpingFiles = new ArrayList<>(); // files, tht will contain промежуточные вычисления
    private BufferedWriter writer; // this writer writes in output file
    private BufferedWriter helpFileWriter; // this writer writes in helping file
    private int newfileNum = 1; // helping files names will be: 1 2 3 ...

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
                case "-i" -> sortInt();
                case "-s" -> sortStr();
            }
        }
        catch (IOException e)
        {
            System.out.println("An error occurred during creating output writer!");
        }
    }

    private void writeOutput()
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFiles.get(0))))
        {
            String str = " ";
            while ((str = reader.readLine()) != null)
            {
                this.writer.write(str);
                this.writer.newLine();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void sortInt()
    {
        while (inputFiles.size() > 1)
        {
            for(int i = 0; i < inputFiles.size()-1; i++)
            {
                if (inputFiles.get(i+1) == null)
                    break;

                try(BufferedReader reader1 = new BufferedReader(new FileReader(inputFiles.get(i)));
                    BufferedReader reader2 = new BufferedReader(new FileReader(inputFiles.get(i+1))))
                {
                    List<Integer> nums1 = new ArrayList<>();
                    List<Integer> nums2 = new ArrayList<>();
                    String num1 = reader1.readLine();
                    String num2 = reader2.readLine();
                    File newFile = new File(String.valueOf(newfileNum));
                    newFile.createNewFile();
                    // saving helping file to have an opportunity in future to delete it
                    helpingFiles.add(newFile);
                    helpFileWriter = new BufferedWriter(new FileWriter(String.valueOf(newfileNum)));

                    while (num1 != null || num2 != null)
                    {
                        if(num1 != null)
                            nums1.add(Integer.parseInt(num1));
                        if (num2 != null)
                            nums2.add(Integer.parseInt(num2));

                        if (nums1.size() >= BLOCK_SIZE || nums2.size() >= BLOCK_SIZE)
                        {
                            var res = merge(nums1, nums2);
                            writeHelpFile(res);
                        }

                        num1 = reader1.readLine();
                        num2 = reader2.readLine();
                    }

                    // check remaining
                    // if two buffers contain data then sort&merge
                    if (nums1.size() > 0 && nums2.size() > 0)
                    {
                        var res = merge(nums1, nums2);
                        writeHelpFile(res);
                    }

                    // if only one buffer contain data then just write it into help file
                    if (nums1.size() > 0)
                        writeHelpFile(nums1);
                    if (nums2.size() > 0)
                        writeHelpFile(nums2);

                    helpFileWriter.close();
                }
                catch (NumberFormatException nfe)
                {
                    System.out.println("Input contains incorrect data in files: "+
                            inputFiles.get(i)+"|"+inputFiles.get(i+1));
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }

                inputFiles.remove(i+1);
                inputFiles.set(i, String.valueOf(newfileNum));
                newfileNum++;
            }
        }

        writeOutput();
        deleteHelpingFiles();
    }

    private void sortStr()
    {
        while (inputFiles.size() > 1)
        {
            for(int i = 0; i < inputFiles.size()-1; i++)
            {
                if (inputFiles.get(i+1) == null)
                    break;

                try(BufferedReader reader1 = new BufferedReader(new FileReader(inputFiles.get(i)));
                    BufferedReader reader2 = new BufferedReader(new FileReader(inputFiles.get(i+1))))
                {
                    List<String> strs1 = new ArrayList<>();
                    List<String> strs2 = new ArrayList<>();
                    String str1 = reader1.readLine();
                    String str2 = reader2.readLine();
                    File newFile = new File(String.valueOf(newfileNum));
                    newFile.createNewFile();
                    helpingFiles.add(newFile);
                    helpFileWriter = new BufferedWriter(new FileWriter(String.valueOf(newfileNum)));

                    while (str1 != null || str2 != null)
                    {
                        // checking if there are spaces in strings
                        if(str1 != null)
                        {
                            str1 = str1.replaceAll(" ", "");
                            strs1.add(str1);
                        }

                        if (str2 != null)
                        {
                            str2 = str2.replaceAll(" ", "");
                            strs2.add(str2);
                        }

                        // if block filled, then start sorting
                        if (strs1.size() >= BLOCK_SIZE || strs2.size() >= BLOCK_SIZE)
                        {
                            var res = merge(strs1, strs2);
                            writeHelpFile(res);
                        }

                        str1 = reader1.readLine();
                        str2 = reader2.readLine();
                    }

                    if (strs1.size() > 0 && strs2.size() > 0)
                    {
                        var res = merge(strs1, strs2);
                        writeHelpFile(res);
                    }

                    if (strs1.size() > 0)
                        writeHelpFile(strs1);
                    if (strs2.size() > 0)
                        writeHelpFile(strs2);

                    helpFileWriter.close();
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }

                inputFiles.remove(i+1);
                inputFiles.set(i, String.valueOf(newfileNum));
                newfileNum++;
            }
        }

        writeOutput();
        deleteHelpingFiles();
    }

    private void deleteHelpingFiles()
    {
        for (File file : helpingFiles)
        {
            file.delete();
        }
    }

    private <T> void writeHelpFile(List<T> res)
    {
        try
        {
            System.out.println("Writing to helping file");
            for (T d : res)
            {
                this.helpFileWriter.write(d.toString());
                this.helpFileWriter.newLine();
            }
        }
        catch (IOException e)
        {
            System.out.println("An error occurred during writing in helping file #: " + newfileNum);
        }
    }

    private <T extends Comparable<T>> List<T> merge(List<T> arr, List<T> arr2)
    {
        List<T> result = new ArrayList<>();
        int i = 0, j = 0;
        int size1 = arr.size();
        int size2 = arr2.size();

        while (i < arr.size() && j < arr2.size())
        {
            if (sortMode.equalsIgnoreCase("-a"))
            {
                if (arr.get(i).compareTo(arr2.get(j)) < 0)
                {
                    result.add(arr.get(i));
                    i++;
                } else
                {
                    result.add(arr2.get(j));
                    j++;
                }
            }
            else
            {
                if (arr.get(i).compareTo(arr2.get(j)) > 0)
                {
                    result.add(arr.get(i));
                    i++;
                } else
                {
                    result.add(arr2.get(j));
                    j++;
                }
            }
        }

        if(i < arr.size() || j < arr2.size())
        {
            var remainingList = i < arr.size() ? arr : arr2;
            int remainingIndex = i < arr.size() ? i : j;

            // adding remaining elements to the beginning of the list
            for (int k = remainingIndex; k < remainingList.size(); k++)
                remainingList.set(k - remainingIndex, remainingList.get(k));

            //int newSize = remainingList.size() - remainingIndex;
            for (int k = 0; k < remainingIndex; k++)
                remainingList.remove(remainingList.size()-1);

            if (i < size1)
                arr2.clear();
            else
                arr.clear();

            return result;
        }

        arr.clear();
        arr2.clear();

        return result;
    }
}
