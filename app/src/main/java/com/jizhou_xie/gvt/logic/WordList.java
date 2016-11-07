package com.jizhou_xie.gvt.logic;

import android.util.Log;

import com.jizhou_xie.gvt.logic.old.Word;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import static com.jizhou_xie.gvt.logic.Stage.LEARN;
import static com.jizhou_xie.gvt.logic.Stage.TEST;

/**
 * Created by Thomas Hsieh on 2016/10/15.
 */

public class WordList {

    public enum NextFeedback {
        SUCCESS,
        EMPTY_LIST,
        EMPTY_SUBLIST
    }

    public enum NewSublistFeedback {
        SUCCESS,
        ORIGINAL_LIST_END,
        ORIGINAL_LIST_EMPTY,
        FAIL
    }

    private HashMap<Integer,Word> wordMap;
    private HashMap<Stage,LinkedList<Integer>> lists;
    private ListIterator<Integer> iterator;
    private Integer pointer;

    private Stage currentStage = null;

    //BEGIN: variables for sublist control
    private int subListStart = -1;
    private int subListEnd = -1;
    private int subListSize = -1;
    private LinkedList<Integer> sublist = null;
    //END: variables for sublist control

    public WordList() {
        //initiallize the wordMap
        this.wordMap = new HashMap<Integer, Word>(SQLiteAdapter.fetchNewWordsToLearn(0));
        //initiallize the lists and pointers
        this.lists = new HashMap<Stage, LinkedList<Integer>>();
        for (Stage stageItem: Stage.values())
            this.lists.put(stageItem,new LinkedList<Integer>());
        this.lists.get(Stage.LEARN).addAll(this.wordMap.keySet());
        this.iterator = this.lists.get(Stage.LEARN).listIterator();
        this.pointer = null;
        //initiallize the stage
        this.currentStage = Stage.LEARN;
    }

    /*
    public WordList(HashMap<Integer,Word> wordMap, Stage stage) {
        //initiallize the wordMap
        this.wordMap = (HashMap<Integer,Word>) wordMap.clone();
        //initiallize the lists and pointers
        this.lists = new HashMap<Stage, LinkedList<Integer>>();
        for (Stage stageItem: Stage.values()) {
            if(stage == stageItem)
                this.lists.put(stageItem, new LinkedList<Integer>(wordMap.keySet()));
            else
                this.lists.put(stageItem,new LinkedList<Integer>());
            this.iterator = this.lists.get(stageItem).listIterator();
            this.pointer = null;
        }
        //initiallize the stage
        this.currentStage = stage;
    }
    */

    //TODO
    public boolean nextStage() {
        boolean feedback = true;
        switch(this.currentStage) {
            case REVIEW: {
                //operation for old stage
                Integer[] indexes = this.lists.get(Stage.REVIEW).toArray(new Integer[0]);
                Word[] words = new Word[indexes.length];
                for (int i = 0; i < indexes.length; i++)
                    words[i] = this.wordMap.get(indexes[i]);
                SQLiteAdapter.updateWords(words);
                this.lists.get(Stage.REVIEW).clear();
                //operation for new stage
                this.currentStage = LEARN;
                this.subListStart = -1;
                this.subListEnd = -1;
                this.subListSize = -1;
                //-- initiallize the sublist
                LinkedList<Integer> learning_list = this.lists.get(LEARN);

                int number_new_words = SetupDataAdapter.numberOfWordsToLearn() - learning_list.size();
                if (number_new_words > 0) {
                    Map<Integer, Word> new_words = SQLiteAdapter.fetchNewWordsToLearn(1);
                    this.wordMap.putAll(new_words);
                    this.lists.get(LEARN).addAll(new_words.keySet());
                    this.lists.get(TEST).addAll(new_words.keySet());
                }

                Collections.shuffle(this.lists.get(LEARN));
                this.sublist = (LinkedList<Integer>) learning_list.subList(0, learning_list.size());
                this.iterator = this.sublist.listIterator();
            }
            break;

            case LEARN: {
                this.currentStage = TEST;
                this.lists.get(Stage.LEARN).clear();
                this.iterator = this.lists.get(TEST).listIterator();
            }
            break;

            case TEST: {
                this.currentStage = Stage.STOP;
                Integer[] indexes = this.lists.get(Stage.TEST).toArray(new Integer[0]);
                Word[] words = new Word[indexes.length];
                for (int i = 0; i < indexes.length; i++)
                    words[i] = this.wordMap.get(indexes[i]);
                SQLiteAdapter.updateWords(words);
                this.lists.get(Stage.TEST).clear();
            }
            break;
            default:
                feedback = false;
                break;
        }
        return feedback;
    }

    public NextFeedback next() {
        NextFeedback feedback = NextFeedback.SUCCESS;

        if(this.iterator.hasNext()) {
            this.pointer = this.iterator.next();
        } else if(this.lists.get(this.currentStage).isEmpty()) {
            //not list mode, empty
            feedback = NextFeedback.EMPTY_LIST;
        } else {
            feedback = NextFeedback.EMPTY_SUBLIST;
        }

        return feedback;
    }

    public boolean setSublistSize(int size) {
        boolean feedback = false;
        if(this.currentStage == LEARN && this.lists.get(LEARN).size()>=size) {
            this.subListSize = size;
            this.subListStart = 0;
            this.subListEnd = 0;
            feedback = true;
        }
        return feedback;
    }

    public NewSublistFeedback newSublist() {

        NewSublistFeedback feedback = NewSublistFeedback.FAIL;

        if(this.currentStage == LEARN) {
            LinkedList<Integer> learning_list = this.lists.get(LEARN);

            if (this.subListSize == -1) {
                Log.e("Sublist Access Error", "Try to access sublist in no Sublist Mode");
            } else if (learning_list.size() == (this.subListEnd+1)) {
                feedback = NewSublistFeedback.ORIGINAL_LIST_END;
            } else if (learning_list.size() == 0) {
                feedback = NewSublistFeedback.ORIGINAL_LIST_EMPTY;
            } else {
                // the condition to have a new sub list match
                if(this.subListEnd == 0) {
                    this.subListEnd = this.subListSize - 1;
                }else if(this.subListEnd + this.subListSize < learning_list.size()) {
                    // right size
                    this.subListStart = this.subListStart + this.sublist.size();
                    this.subListEnd = this.subListStart + this.subListSize -1;
                } else {
                    // last sublist smaller than deafult size
                    this.subListStart = this.subListStart + this.sublist.size();
                    this.subListEnd = learning_list.size() - 1;
                }

                this.sublist = (LinkedList<Integer>) learning_list.subList(this.subListStart, this.subListEnd+1);
                this.iterator = this.sublist.listIterator();
                feedback = NewSublistFeedback.SUCCESS;
            }
        } else {
            Log.e("Sublist Access Error", "Try to have a new sublist in "+this.currentStage.name()+" Stage");
        }
        return feedback;
    }

    public void refreshSublist(){
        if(this.currentStage != LEARN) {
            Log.e("Sublist Access Error", "Try to have refresh the sublist in "+this.currentStage.name()+" Stage");
        } else if(this.subListSize == -1) {
            Log.e("Sublist Access Error", "Try to access sublist in no Sublist Mode");
        } else if(this.sublist.isEmpty()) {
            Log.e("Sublist Access Error", "Sublist is empty!");
        } else {
            // all condition matched
            Collections.shuffle(this.sublist);
        }
    }

    public Word get() {
        Word word = null;
        if(this.pointer != null)
            word = this.wordMap.get(this.pointer);
        return word;
    }

    public int numberOfWordsInTotal() {
        return this.wordMap.size();
    }

    public void processResult(Result result) {
        switch(this.currentStage) {
            case REVIEW:
            {
                switch(result) {
                    case RIGHT:
                        //Keep the word from the list for SQL update
                        //Update the word
                        this.wordMap.get(this.pointer).processResult(result);
                        break;
                    case WRONG:
                        // remove the word from the learn list for SQL update
                        //move the current word to learn sequence and test sequence
                        this.iterator.remove();
                        this.lists.get(LEARN).add(this.pointer);
                        this.lists.get(TEST).add(this.pointer);
                        break;
                    case DISCARD:
                        // set the next tag to INF
                        this.wordMap.get(this.pointer).processResult(result);
                        break;
                    default:
                        Log.e("Button Design Error",result.name()+" in "+this.currentStage.name());
                }
            }
            break;
            case LEARN:
            {
                switch(result) {
                    case RIGHT:
                        //Remove the Result from
                        this.lists.get(LEARN).remove(this.pointer);
                        this.iterator.remove(); //TODO: MAYBE WE DONT NEED IT
                        break;
                    case NEXT:
                        break;
                    case POSTPONE:
                        this.lists.get(LEARN).remove(this.pointer);
                        this.lists.get(TEST).remove(this.pointer); //TODO: THIS WILL NOT WORK
                        this.iterator.remove();
                        break;
                    default:
                        Log.e("Button Design Error",result.name()+" in "+this.currentStage.name());
                }
            }
            break;
            case TEST:
            {
                switch(result) {
                    case RIGHT:
                        this.wordMap.get(this.pointer).processResult(result);
                        break;
                    case WRONG:
                        this.wordMap.get(this.pointer).processResult(result);
                        break;
                    case DISCARD:
                        this.wordMap.get(this.pointer).processResult(result);
                        break;
                    default:
                        Log.e("Button Design Error",result.name()+" in "+this.currentStage.name());
                }
            }
            break;
            default:
                Log.e("Stage Design Error",this.currentStage.name());
        }
    }
}
