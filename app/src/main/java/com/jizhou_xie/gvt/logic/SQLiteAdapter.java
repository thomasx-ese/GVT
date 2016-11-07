package com.jizhou_xie.gvt.logic;

import com.jizhou_xie.gvt.logic.old.Word;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thomas Hsieh on 2016/10/24.
 */

public class SQLiteAdapter {

    //TODO
    static public boolean updateWords(Word[] words) {
        return false;
    }
    //TODO
    static public Map<Integer, Word> fetchNewWordsToLearn(int i) {
        Map<Integer, Word> map = new HashMap<Integer, Word>();

        Word[] words;
        if(i==0) {
            words = new Word[10];

            words[0] = new Word(Word.WordType.NOUN);
            words[0].setGender(Word.WordGender.DAS);
            words[0].setSpelling("Haus");
            words[0].setMeaning("房子");
            words[0].setPlural(Word.WordPlural.UER);

            words[1] = new Word(Word.WordType.ADJ);
            words[1].setSpelling("hungrig");
            words[1].setMeaning("饥饿的");

            words[2] = new Word(Word.WordType.VERB);
            words[2].setSpelling("essen");
            words[2].setMeaning("吃");

            words[3] = new Word(Word.WordType.VERB);
            words[3].setSpelling("sehen");
            words[3].setMeaning("看");

            words[4] = new Word(Word.WordType.ADV);
            words[4].setSpelling("rot");
            words[4].setMeaning("红");

            words[5] = new Word(Word.WordType.VERB);
            words[5].setSpelling("laufen");
            words[5].setMeaning("跑");

            words[6] = new Word(Word.WordType.VERB);
            words[6].setSpelling("schlafen");
            words[6].setMeaning("睡");

            words[7] = new Word(Word.WordType.NOUN);
            words[7].setSpelling("Auto");
            words[7].setGender(Word.WordGender.DAS);
            words[7].setPlural(Word.WordPlural.S);
            words[7].setMeaning("汽车");

            words[8] = new Word(Word.WordType.ADJ);
            words[8].setSpelling("klein");
            words[8].setMeaning("小");

            words[9] = new Word(Word.WordType.ADJ);
            words[9].setSpelling("hoch");
            words[9].setMeaning("高");

            for(int j=0; j<words.length; i++) {
                map.put(new Integer(j), words[j]);
            }

        } else {
            words = new Word[3];

            words[0] = new Word(Word.WordType.NOUN);
            words[0].setSpelling("Vater");
            words[0].setGender(Word.WordGender.DER);
            words[0].setPlural(Word.WordPlural.U);
            words[0].setMeaning("爹");

            words[1] = new Word(Word.WordType.ADJ);
            words[1].setSpelling("heiß");
            words[1].setMeaning("热");

            words[2] = new Word(Word.WordType.ADJ);
            words[2].setSpelling("kalt");
            words[2].setMeaning("冷");

            for(int j=0; j<words.length; i++) {
                map.put(new Integer(j+10), words[j]);
            }
        }

        return map;
    }
}
