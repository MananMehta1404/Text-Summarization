import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.LinkedList;
import java.util.Set;


public class text_summarize {

    public static void main(String[] args) throws Exception {

        // Reading the input text file
        BufferedReader reader = new BufferedReader(new FileReader("Hurr1out.txt"));
        StringBuilder stringBuilder = new StringBuilder();
        char[] buffer = new char[10];
        while (reader.read(buffer) != -1) {
            stringBuilder.append(new String(buffer));
            buffer = new char[10];
        }
        reader.close();

        // Reading the stopwords file
        LinkedList<String> stopWords = new LinkedList<String>();
        BufferedReader br = new BufferedReader(new FileReader("stop_words_english.txt"));
        String words = null;
        while( (words = br.readLine()) != null) {
            stopWords.add(words.trim());
            }
        br.close();

        // Displaying the stopwords
        // for(int i = 0; i < stopWords.size(); i++){
        //     System.out.println(stopWords.get(i));
        // }
        // System.out.println(stopWords.size());

        // Converting into a string
        String content = stringBuilder.toString();
        // System.out.println(content);
        
        // Creating Linked Lists for storing objects 
        LinkedList<word> l1 = new LinkedList<word>();
        LinkedList<sentence> l2 = new LinkedList<sentence>();
        
        // Removing the unnecessary elements from the input file
        content = content.replaceAll("([0-9]+,[0-9]+)/[A-Z]* ", "");
        content = content.replaceAll("([0-9]+\\.[0-9]+)/[A-Z]* ", "");
        content = content.replaceAll("([0-9]+)/[A-Z]* ", "");
        content = content.replaceAll("(,/, )|(\\'\\'/\\'\\' )|(``/`` )", "");
        
        // Removing the stop words from the input file
        for(int i = 0; i < stopWords.size(); i++){
            content = content.replaceAll(" " + stopWords.get(i) + "/[A-Z]* ", " ");
        }
        // System.out.println(content);
        
        String[] sentences = content.split(" \\./\\. ");
        int len = sentences.length;
        
        // Displaying each sentence and total no. of sentences.
        // for(int i = 0; i < len; i++){
        //         System.out.println(sentences[i]);
        //     }
        // System.out.println(len);
            
        // Adding objects to linked lists
        for(String s: sentences){
            if(s.length() == 1){
                continue;
            }
            String[] word_str = s.split(" ");
            int no_wds = word_str.length;
            if(no_wds == 1){
                continue;
            }

            // Adding to l2
            sentence s1 = new sentence(word_str, no_wds);
            l2.add(s1);
            
            // Adding to l1
            for(String s2: word_str){
                String[] new_arr = s2.split("/");
                if(new_arr.length != 2){
                    continue;
                }
                word wd = new word(new_arr[0].toLowerCase(), new_arr[1]);
                l1.add(wd);
            }
        }
        
        // Sorting the linked list according to the words
        Collections.sort(l1, new Comparator<word>() {
            @Override
            public int compare(word w1, word w2){
                return (w1.wd).compareTo(w2.wd);
            }
        });

        // Creating the list of unique words by calculating the frequency of the words.
        LinkedList<word> l3 = new LinkedList<word>();
        int temp_freq = 1;
        for(int i = 0; i < l1.size() - 1; i++){
            word w1 = new word(l1.get(i).wd, l1.get(i).POS);
            word w2 = new word(l1.get(i+1).wd, l1.get(i+1).POS);
            if(!w1.wd.equals(w2.wd)){
                w1.freq = temp_freq;
                l3.add(w1);
                temp_freq = 1;
            }
            else{
                temp_freq += 1;
            }
        }  

        // Calculating the term frequency of each word.
        for(int i = 0; i < l3.size(); i++){
            float t_fr = (l3.get(i).freq/(float)l3.size()) * 100;
            l3.get(i).term_freq = t_fr;
        }

        // Including the list of sentences where these unique words appear.
        for(int i = 0; i < l3.size(); i++){
            String str1 = l3.get(i).wd;
            for(int j = 0; j < l2.size(); j++){
                for(int k = 0; k < l2.get(j).lnew.size(); k++){
                    if(str1.equals(l2.get(j).lnew.get(k).toLowerCase())){
                        l3.get(i).num.add(j);
                    } 
                }
            }
        }

        // Sorting the Linked list according to the frequency.
        // Collections.sort(l1, new Comparator<word>() {
        //     @Override
        //     public int compare(word w1, word w2){
        //         return (w1.freq).compareTo(w2.freq);
        //     }
        // });
        
        // Displaying the word objects
        // for(int i = 0; i < l1.size(); i++){
        //     System.out.println(l1.get(i).POS);
        //     System.out.println(l1.get(i).freq);
        //     System.out.println(l1.get(i).wd);
        //     System.out.println(l1.get(i).wd_des);
        // }

        // Displaying the unique word objects
        // for(int i = 0; i < l3.size(); i++){
            // System.out.println(l3.get(i).POS);
            // System.out.println(l3.get(i).freq);
            // System.out.println(l3.get(i).wd);
            // System.out.println(l3.get(i).wd_des);
        // }
        
        // Displaying the sentence objects
        // for(int i = 0; i < l2.size(); i++){
            // for(int j = 0; j < l2.get(i).words_des.length; j++){
            //     System.out.println(l2.get(i).words_des[j]);
            // }
            // System.out.println(l2.get(i).lnew);
            // System.out.println(l2.get(i).no_wds);
        // }

        // Writing into the file
        FileWriter fWriter = new FileWriter("output.txt");

        for(int i = 0; i < l3.size(); i++){
            int j = i + 1;
            StringBuffer sb1Buffer = new StringBuffer();
            sb1Buffer.append("" + j + "  " + l3.get(i).wd + "  " + l3.get(i).POS + "  " + l3.get(i).freq + "  " + l3.get(i).term_freq + "  " + l3.get(i).num);
            fWriter.write(sb1Buffer.toString());
            fWriter.write("\n");
        }

        fWriter.close();
    }  
}

class word{
    String wd_des;
    String wd;
    String POS;
    int freq;
    float term_freq;
    LinkedList<Integer> num;

    word(String wd, String POS){
        this.wd = wd;
        this.POS = POS;
        this.wd_des = wd + "/" + POS;
        this.freq = 1;
        this.term_freq = 1;
        this.num = new LinkedList<Integer>();
    }

}

class sentence{
    String[] words_des;
    int no_wds;
    LinkedList<String> lnew;

    sentence(String[] words_des, int no_wds){
        this.words_des = words_des;
        this.no_wds = no_wds;
        this.lnew = new LinkedList<String>();
        for(int i = 0; i < words_des.length; i++){
            String[] new1 = words_des[i].split("/");
            lnew.add(new1[0]);
        }
    }

}


// End of File


// Tried Logic
    // word arr[][] = new word[len][];
        // int i = 0;
         
        // for(String s: sentences){
            // System.out.println(s);
            // String new_s = s.replaceAll(",/, ", "");
            // String[] new_arr = new_s.split(" ");

            // int j = 0;
            // arr[i] = new word[s.length()]; 
            // for(String s1: new_arr){
                // System.out.println(s1);
                // String[] new_arr1 = s1.split("/");
                // System.out.println(new_arr1[0]);
                // System.out.println(new_arr1[1]);
                // word wd = new word(new_arr1[0], new_arr1[1]);
                // wd.wd_des = s1;
                // wd.wd = new_arr1[0];
                // wd.POS = new_arr1[1];
                // wd.freq = 1;
                // arr[i][j] = wd;
        //         j += 1;  
        //     }
        //     i += 1;
        // }

        // System.out.println(arr[2][1].POS);
        // System.out.println(arr[2][1].wd);

// BufferedReader reader1 = new BufferedReader(new FileReader("stop_words_english.txt"));
// StringBuilder stringBuilder1 = new StringBuilder();
// char[] buffer1 = new char[10];
// while (reader1.read(buffer1) != -1) {
//     stringBuilder1.append(new String(buffer1));
//     buffer1 = new char[10];
// }
// reader1.close();

// String content1 = stringBuilder1.toString();
// content1 = content1.replaceAll("/n", "");
// // System.out.println(content1);
// // System.out.println(stringBuilder1);
// String[] stop_arr = content1.split("/n");
// System.out.println(stop_arr[0]);
// int length1 = stop_arr.length;
// System.out.println(length1);



