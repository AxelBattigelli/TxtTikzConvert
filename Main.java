/*
** EPITECH PROJECT, 2023
** main.java
** File description:
** Main file for reader file and converting algo
** Axel BATTIGELLI
*/


import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class main {
    // global
    // List<String> l1 = {"ab", "tb"};

    public static Vector<String> readfile(String filename) throws FileNotFoundException{
        Vector<String> allData = new Vector<>();
        File fileObj = new File(filename);
        Scanner fileReader = new Scanner(fileObj);

        while (fileReader.hasNextLine()) {
            String data = fileReader.nextLine();
            allData.add(data);
        }
        fileReader.close();
        return allData;
    }
