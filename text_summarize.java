// Start of the program

import java.io.*;
import java.util.*;

public class text_summarize {

    // Function for finding the maximum value key from a Hashtable.
    public static int findMax(Hashtable<Integer, Integer> ht){
        int max = 0;
        int key = 0;

        for(Integer key1: ht.keySet()){
            if(max < ht.get(key1)){
                max = ht.get(key1);
                key = key1;
            }
        }

        return key;
    }

    // Function for getting the most important sentences from the input file.
    public static List<Integer> getSummary(List<Integer> ls){

        Hashtable<Integer, Integer> ht1 = new Hashtable<>();
        for(int i = 0; i < ls.size(); i++){
            if(ht1.containsKey(ls.get(i))){
                ht1.put(ls.get(i), ht1.get(ls.get(i)) + 1);
                continue;
            }
            ht1.put(ls.get(i), 1);
        }

        List<Integer> lst1 = new ArrayList<>();
        for(int i = 0; i < ls.size(); i++){
            if(!lst1.contains(ls.get(i))){
                lst1.add(ls.get(i));
            }
        }

        // System.out.println(ht1.toString());

        List<Integer> lst2 = new ArrayList<>();
        while(lst2.size() < lst1.size()){
            int imp = findMax(ht1);
            lst2.add(imp);
            ht1.remove(imp);
        }

        // List<Integer> return_lst = new ArrayList<>();
        // for(int i = 0; i < lines; i++){
        //     return_lst.add(lst2.get(i));
        // }

        return lst2;
    }

    public static void main(String[] args) throws Exception {

        // Reading the input text file
        BufferedReader reader = new BufferedReader(new FileReader("Input3.txt"));
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

        // Converting the text file data to a string
        String content = stringBuilder.toString();
        // System.out.println(content);

        // Creating an array which can store the original sentences.
        String[] original_sen = content.split(" \\./\\. ");
        // Displaying original sentences.
        // for(int i = 0; i < original_sen.length; i++){
        //     System.out.println(original_sen[i]);
        // }
        
        // Creating Linked Lists for storing objects 
        LinkedList<word> l1 = new LinkedList<word>();
        LinkedList<sentence> l2 = new LinkedList<sentence>();
        LinkedList<originalSentence> l4 = new LinkedList<originalSentence>();

        // Adding original sentences to a linked list of originalSentence objects.
        for(String s: original_sen){
            if(s.length() == 1){
                continue;
            }
            String[] word_str = s.split(" ");
            int no_wds = word_str.length;
            if(no_wds == 1){
                continue;
            }

            // Adding to l3
            originalSentence s1 = new originalSentence(word_str, no_wds);
            l4.add(s1);
        }
        
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
        
        // Sorting the linked list according to the words.
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
        Collections.sort(l3, new Comparator<word>() {
            @Override
            public int compare(word w1, word w2){
                return Integer.compare(w2.freq, w1.freq);
            }
        });
        
        // Displaying the word objects
        // for(int i = 0; i < l1.size(); i++){
        //     System.out.println(l1.get(i).POS);
        //     System.out.println(l1.get(i).freq);
        //     System.out.println(l1.get(i).wd);
        //     System.out.println(l1.get(i).wd_des);
        // }

        // Displaying the unique word objects
        // for(int i = 0; i < l3.size(); i++){
        //     System.out.println(l3.get(i).POS);
        //     System.out.println(l3.get(i).freq);
        //     System.out.println(l3.get(i).wd);
        //     System.out.println(l3.get(i).wd_des);
        // }
        
        // Displaying the sentence objects
        // for(int i = 0; i < l2.size(); i++){
            // for(int j = 0; j < l2.get(i).words_des.length; j++){
            //     System.out.println(l2.get(i).words_des[j]);
            // }
            // System.out.println(l2.get(i).lnew);
            // System.out.println(l2.get(i).no_wds);
        // }

        // Displaying the originalSentence objects
        // for(int i = 0; i < l4.size(); i++){
        //     for(int j = 0; j < l4.get(i).words_des.length; j++){
        //         System.out.println(l4.get(i).words_des[j]);
        //     }
        //     System.out.println(l4.get(i).lnew);
        //     System.out.println(l4.get(i).no_wds);
        // }

        // Adding most important sentences available according to the important words.
        int threshold_freq = 3;                 // Threshold Frequency
        List<Integer> l5 = new ArrayList<>();
        for(int i = 0; i < l3.size(); i++){
            if(l3.get(i).freq >= threshold_freq && l3.get(i).POS.charAt(0) == 'N' && l3.get(i).POS.charAt(1) == 'N'){
                for(int k = 0; k < l3.get(i).num.size(); k++){
                    l5.add(l3.get(i).num.get(k));
                }
            }
        }

        int sum_per = 40;                       // How much percentage of summary you want?
        int no_lines = (len * sum_per)/100;     // Calculating no. of lines according to the given percentage.
        List<Integer> lst1 = getSummary(l5);    // Getting the important sentences present in the input file.

        // Getting list of most important lines according to the required percentage.
        List<Integer> output_lst = new ArrayList<>();
        for(int i = 0; i < no_lines; i++){
            output_lst.add(lst1.get(i));
        }
        System.out.println(output_lst);


        // Writing into the file

        // Creating intermediate output file as output.txt
        FileWriter fWriter = new FileWriter("output.txt");

        StringBuffer sb2 = new StringBuffer();
        sb2.append("Sr. No.      Word          POS    Frequency    Term_Freq     Sentence");
        fWriter.write(sb2.toString());
        fWriter.write("\n");
        fWriter.write("\n");

        for(int i = 0; i < l3.size(); i++){
            int j = i + 1;
            StringBuffer srno = new StringBuffer();
            StringBuffer u_word = new StringBuffer();
            StringBuffer pos = new StringBuffer();
            StringBuffer w_freq = new StringBuffer();
            StringBuffer t_freq = new StringBuffer();
            StringBuffer sent = new StringBuffer();

            // Formatting the text
            srno.append("   " + j);
            while(srno.length() != 12) srno.append(" ");
            u_word.append(l3.get(i).wd);
            while(u_word.length() != 16) u_word.append(" ");
            pos.append(l3.get(i).POS);
            while(pos.length() != 11) pos.append(" ");
            w_freq.append(l3.get(i).freq);
            while(w_freq.length() != 9) w_freq.append(" ");
            t_freq.append(l3.get(i).term_freq);
            while(t_freq.length() != 13) t_freq.append(" ");
            sent.append(l3.get(i).num);
            fWriter.write(srno.toString() + u_word.toString() + pos.toString() + w_freq.toString() + t_freq.toString() + sent.toString());
            fWriter.write("\n");
        }

        fWriter.close();

        // Creating final summary file as summary.txt
        FileWriter fWriter1 = new FileWriter("summary.txt");

        for(int i = 0; i < l4.size(); i++){
            if(output_lst.contains(i)){
                StringBuffer sb1Buffer = new StringBuffer();
                for(int k = 0; k < l4.get(i).lnew.size(); k++){
                    if(k == l4.get(i).lnew.size() - 1){
                        sb1Buffer.append("" + l4.get(i).lnew.get(k) + "");
                        continue;
                    }
                    if(l4.get(i).lnew.get(k+1).charAt(0) == ','){
                        sb1Buffer.append("" + l4.get(i).lnew.get(k));
                        continue;
                    }
                    if(l4.get(i).lnew.get(k).equals("''") || l4.get(i).lnew.get(k).equals("``")){
                        continue;
                    }
                    sb1Buffer.append("" + l4.get(i).lnew.get(k) + " ");
                }
                fWriter1.write(sb1Buffer.toString());
                fWriter1.write(". ");
            }
        }

        fWriter1.close();
    }  
}

// word Class for storing the description of the important words.
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

// sentence class for storing the information of the sentence. 
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

// originalSentence class for storing the original sentences from the input file.
class originalSentence{
    String[] words_des;
    int no_wds;
    LinkedList<String> lnew;

    originalSentence(String[] words_des, int no_wds){
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