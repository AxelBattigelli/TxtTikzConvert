/*
** EPITECH PROJECT, 2023
** main.java
** File description:
** Main file for reader file and converting algo
** Axel BATTIGELLI
*/

/*
 * TO DO
 * 
 * tant que exit & loop
 * si -> fin si
 * 
 * bullet est la fin totale pas la fin d'une condition
 * fin si ou fin tantque ==> tmp
 * remplacer tmp par l'intruction suivante
 * reprendre le draw depuis un tmp
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class Main {

    public static void main(String[] args) {
        try {
            Vector<String> lines = readfile("input.txt");
            StringBuilder algorithmeBuilder = new StringBuilder();

            for (String line : lines) {
                algorithmeBuilder.append(line).append("\n");
            }
            String algorithme = algorithmeBuilder.toString();
            String logigramme = genererLogigramme(algorithme);
            // Afficher le résultat en console
            System.out.println(logigramme);

        } catch (FileNotFoundException e) {
            System.out.println("Fichier introuvable : " + e.getMessage());
        }
    }    

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

    private static String genererLogigramme(String algorithme) {
        // Séparation de l'algorithme par lignes
        String[] lignes = algorithme.split("\n");
        StringBuilder codeTikz = new StringBuilder();

        // En-tête du code Tikz nécessaire pour le logigramme
        codeTikz.append("\\documentclass{article}\n");
        codeTikz.append("\\usepackage[utf8]{inputenc}\n");
        codeTikz.append("\\usepackage{tikz}\n");
        codeTikz.append("\\usetikzlibrary{positioning,shapes.geometric}\n");
        codeTikz.append("\\tikzstyle{carre}=[rectangle,rounded corners,draw=red!80,fill=red!10,\n");
        codeTikz.append("inner ysep=0.2cm,text width=2cm,text centered]\n");
        codeTikz.append("\\tikzstyle{losange}=[diamond,draw=blue!80,fill=blue!10, inner\n");
        codeTikz.append("ysep=0.1cm,text width=1cm,text centered]\n");
        codeTikz.append("\\tikzstyle{cercle}=[draw,circle]\n");
        codeTikz.append("\\begin{document}\n");
        codeTikz.append("\\begin{tikzpicture}\n");

        StringBuilder dernierNoeud = new StringBuilder("(debut)");
        StringBuilder codeTikzgenerate = new StringBuilder();
        int t_value = 1;
        // Génération du logigramme en parcourant chaque ligne de l'algorithme
        for (int i = 1; i < lignes.length; i++) {
            // Analyse de la ligne pour déterminer la structure et générer le code Tikz correspondant
            String codeLigne = genererCodeLigne(lignes[i], t_value, dernierNoeud);
            codeTikzgenerate.append(codeLigne);
            if (!lignes[i].startsWith("Début") && !lignes[i].startsWith("Variable")) {
                t_value++;
            }
            if (lignes[i].startsWith("Fin")) {
                t_value--;
            }
        }
        codeTikzgenerate.append("\\node[cercle] (t").append(t_value).append(") [below = of ").append(dernierNoeud).append("] {\\textbullet};\n");

        StringBuilder auxTikzgenerate = new StringBuilder(genererAuxLigne(codeTikzgenerate));
        StringBuilder drawTikzgenerate = new StringBuilder(genererDrawLigne(codeTikzgenerate, auxTikzgenerate));

        codeTikzgenerate = new StringBuilder(codeTikzgenerate.toString().replace("#finttq#", ""));
        codeTikzgenerate = new StringBuilder(codeTikzgenerate.toString().replace("#finsi#", ""));
        codeTikz.append(codeTikzgenerate);
        codeTikz.append(auxTikzgenerate);
        codeTikz.append(drawTikzgenerate);

        // Fin du code Tikz
        codeTikz.append("\\end{tikzpicture}\n");
        codeTikz.append("\\end{document}\n");

        return codeTikz.toString();
    }

    private static String genererDrawLigne(StringBuilder codeTikzgenerate, StringBuilder auxTikzgenerate) {
        StringBuilder drawTikzgenerate = new StringBuilder();
        String[] lines = codeTikzgenerate.toString().split("\n");
    
        for (int i = 0; i < lines.length - 1; i++) {
            if (!lines[i].contains("\\textbullet") && !(lines[i + 1].contains("#finsi")) && !(lines[i + 1].contains("#finttq"))) {
                drawTikzgenerate.append("\\draw[->] (t").append(i + 1).append(".south) to (t").append(i + 2).append(".north);\n");
            }
        }
        // tant que : exit 
        // \draw [->](t3.east)|-(aux1.center)node[pos=1.3,align=center]{non}|-(aux.center)|-(t6.east);
        // tant que : loop
        // \draw [->](t5.west)|-(aux3.center)|-(aux2.center)|-(t3.west);
        return drawTikzgenerate.toString();
    }
    
    

    private static String genererAuxLigne(StringBuilder codeTikzgenerate) {
        StringBuilder auxTikzgenerate = new StringBuilder();
        String[] lines = codeTikzgenerate.toString().split("\n");
        int index = 0;
    
        // Sorties droites
        for (String line : lines) {
            int startIndex = line.indexOf("(t") + 2;
            int endIndex = line.indexOf(")", startIndex);
            
            if (startIndex != -1 && endIndex != -1) {
                String tracker = line.substring(startIndex, endIndex);
    
                // Vérifier si tracker est une valeur numérique
                if (tracker.matches("\\d+")) {
                    if (line.contains("#fin")) {
                        if (index == 0) {
                            auxTikzgenerate.append("\\node (aux").append(") [right = 4em of t").append(tracker).append("]{};\n");
                            index++;
                        } else {
                            auxTikzgenerate.append("\\node (aux").append(index).append(") [right = 4em of t").append(tracker).append("]{};\n");
                            index++;
                        }
                    }
                }
            }
        }

        // Tant que in/out losange
        for (String line : lines) {
            int startIndex = line.indexOf("(t") + 2;
            int endIndex = line.indexOf(")", startIndex);
            
            if (startIndex != -1 && endIndex != -1) {
                String tracker = line.substring(startIndex, endIndex);
    
                // Vérifier si tracker est une valeur numérique
                if (tracker.matches("\\d+")) {
                    if (line.contains("Tantque")) {
                        // Générer du code TikZ en fonction de la présence de "Tantque" dans la ligne
                        auxTikzgenerate.append("\\node (aux").append(index).append(") [right = 4em of t").append(tracker).append("]{};\n");
                        index++;
                        auxTikzgenerate.append("\\node (aux").append(index).append(") [left = 4em of t").append(tracker).append("]{};\n");
                        index++;
                    }
                }
            }
        }

        // Si in/out losange
        for (String line : lines) {
            int startIndex = line.indexOf("(t") + 2;
            int endIndex = line.indexOf(")", startIndex);
            
            if (startIndex != -1 && endIndex != -1) {
                String tracker = line.substring(startIndex, endIndex);
    
                // Vérifier si tracker est une valeur numérique
                if (tracker.matches("\\d+")) {
                    if (line.contains("Si")) {
                        // Générer du code TikZ en fonction de la présence de "Si" dans la ligne
                        auxTikzgenerate.append("\\node (aux").append(index).append(") [right = 4em of t").append(tracker).append("]{};\n");
                        index++;
                        auxTikzgenerate.append("\\node (aux").append(index).append(") [left = 4em of t").append(tracker).append("]{};\n");
                        index++;
                    }
                }
            }
        }

        // sorties gauches des boucles
        for (String line : lines) {
            int startIndex = line.indexOf("(t") + 2;
            int endIndex = line.indexOf(")", startIndex);
            
            if (startIndex != -1 && endIndex != -1) {
                String tracker = line.substring(startIndex, endIndex);
    
                // Vérifier si tracker est une valeur numérique
                if (tracker.matches("\\d+")) {
                    if (line.contains("#finttq")) {
                        auxTikzgenerate.append("\\node (aux").append(index).append(") [left = 4em of t").append(Integer.parseInt(tracker) - 1).append("]{};\n");
                        index++;
                    }
                    if (line.contains("#finsi")) {
                        auxTikzgenerate.append("\\node (aux").append(index).append(") [left = 4em of t").append(tracker).append("]{};\n");
                        index++;
                    }
                }
            }
        }

        return auxTikzgenerate.toString();
    }

    private static String genererCodeLigne(String ligne, int t_value, StringBuilder dernierePosition) {
        ligne = ligne.trim();
        StringBuilder codeTikz = new StringBuilder();
        String res = ligne;
        res = res.replace(" ", "");

        if (ligne.startsWith("Fin tantque")) {
            codeTikz.append("#finttq#");
        } else if (ligne.startsWith("Fin si")) {
            codeTikz.append("#finsi#");
        } else if (ligne.startsWith("tantque")) {
            res = res.replace("faire", "");
            res = res.replace("tantque", "Tantque ");
            res = res.replace("<", "$<");
            res = res.replace(">", "$>");
            res = res.replace("=", "$=");
            res = res.replace("!", "$!");
            codeTikz.append("\\node[losange] (t").append(t_value).append(") [below = of ").append(dernierePosition).append("] {" + res + "};\n");
            dernierePosition.replace(0, dernierePosition.length(), "t" + t_value + "");
        } else if (ligne.startsWith("si")) {
            res = res.replace("faire", "");
            res = res.replace("si", "Si ");
            res = res.replace("<", "$<");
            res = res.replace(">", "$>");
            res = res.replace("=", "$=");
            res = res.replace("!", "$!");
            codeTikz.append("\\node[losange] (t").append(t_value).append(") [below = of ").append(dernierePosition).append("] {" + res + "};\n");
            dernierePosition.replace(0, dernierePosition.length(), "t" + t_value + "");
        } else {
            if (!ligne.startsWith("Début") && !ligne.startsWith("Variable")) {
                if (dernierePosition.toString().equals("(debut)")) {
                    codeTikz.append("\\node[carre] (t").append(t_value).append(") ").append("{" + res + "};\n");
                } else {
                    codeTikz.append("\\node[carre] (t").append(t_value).append(") [below = of ").append(dernierePosition).append("] {" + res + "};\n");
                }
                dernierePosition.replace(0, dernierePosition.length(), "t" + t_value + "");
            }
        }

        return codeTikz.toString();
    }
}
