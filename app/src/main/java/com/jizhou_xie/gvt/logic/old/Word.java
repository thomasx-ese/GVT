package com.jizhou_xie.gvt.logic.old;

/**
 * Created by Thomas Hsieh on 2016/10/14.
 */

import com.jizhou_xie.gvt.logic.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.EnumSet;

public class Word implements Serializable {

    private static final long serialVersionUID = 3977692933028484684L;
    private WordType type;
    private EnumSet<WordPurpose> purposes; 	//Purposes of the Word; Fixed Length: 3; Structure: [ZH,DE,GaP]
    private WordPlural plural;		//Plural of Noun; if the Word is not a Noun, then fix its value as "WordPlural.NO_PLURAL"
    private String plural_special;  //Used when the plural is "special"; if so, set plural as "WordPlural.SPECIAL"
    private WordGender gender;		//the Gender of a Noun; if it is not a Noun, set this member as "WordGender.NO_GENDER"
    private String spelling;
    private String meaning;

    private long next;
    private int correct;
    private int repeat;

    private File audio;

    private boolean input_mode = false;

    /**
     * Enums
     */

    public enum WordField {
        TYPE,
        PURPOSE,
        GENDER,
        PLURAL,
        PLURAL_SPECIAL,
        SPELLING,
        MEANING
    }

    public enum WordPurpose implements Serializable {
        ZH, DE, GaP
    }

    public enum WordType implements Serializable {
        NOUN("n."),
        VERB("v."),
        ADJ("adj."),
        ADV("adv."),
        OTHERS("");

        private String output = null;
        private WordType(String _output)
        {
            output = _output;
        }

        public String getOutput()
        {
            return output;
        }
    }

    public enum WordGender implements Serializable {
        DER("der"),
        DIE("die"),
        DAS("das"),
        NO_GENDER(null);

        private String output = null;
        private WordGender(String _output)
        {
            output = _output;
        }

        public String getOutput()
        {
            return output;
        }
    }


    public enum WordPlural implements Serializable {
        GLEICH("-"),
        U("-\u00A8"),
        E("-e"),
        UE("-\u00A8e"),
        ER("-er"),
        UER("-\u00A8er"),
        EN("-en"),
        N("-n"),
        S("-s"),
        NUR_SING("nur Sing."),
        NUR_PL("nur Pl."),
        SPECIAL(null),
        NO_PLURAL(null);

        private String output = null;
        private WordPlural(String _output)
        {
            output = _output;
        }

        public String getOutput()
        {
            return output;
        }
    }

    /*
     * Constructor of the Class Word
     * @param _type the Type of the Word
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public Word(WordType _type)
    {
        type = _type;
        spelling = null;
        meaning = null;
        plural_special = null;
        purposes = null;

        //initialize the plural and gender
        if(type==WordType.NOUN)
        {
            plural = null;
            gender = null;
        }
        else
        {
            plural = WordPlural.NO_PLURAL;
            gender = WordGender.NO_GENDER;
        }
    }

    public Word(WordType _type, boolean input_mode) {
        type = _type;
        spelling = null;
        meaning = null;
        plural_special = null;

        //initialize the purposes to Structure:[ZH,DE,GaP]
        if(input_mode == true)
            purposes = EnumSet.noneOf(WordPurpose.class);
        else
            purposes = null;

        //initialize the plural and gender
        if(type==WordType.NOUN)
        {
            plural = null;
            gender = null;
        }
        else
        {
            plural = WordPlural.NO_PLURAL;
            gender = WordGender.NO_GENDER;
        }

        this.input_mode = true;
    }

    public Word(JSONObject jo) throws JSONException
    {
            this(WordType.valueOf(jo.getString(WordField.TYPE.name())),true);

            JSONArray ja_purposes = jo.getJSONArray(WordField.PURPOSE.name());
            for (int i = 0; i < ja_purposes.length(); i++)
                this.setPurpose(WordPurpose.valueOf(ja_purposes.getString(i)));

            if (type == WordType.NOUN) {
                this.setGender(WordGender.valueOf(jo.getString(WordField.GENDER.name())));

                WordPlural _plural = WordPlural.valueOf(jo.getString(WordField.PLURAL.name()));
                String _special = null;
                if (_plural == WordPlural.SPECIAL)
                    _special = jo.getString(WordField.PLURAL_SPECIAL.name());
                this.setPlural(_plural, _special);
            }

            this.setSpelling(jo.getString(WordField.SPELLING.name()));
            this.setMeaning(jo.getString(WordField.MEANING.name()));
    }

    /*
     * Reset the type of the Word
     * @param _type the New Type of the Word
     * @return Whether the Type is different to the previous one
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public boolean resetType(WordType _type)
    {
        boolean different = false;		// the mark about the difference between the new and previous types
        if (type != _type)				// start the process only if the types are different
        {
            type = _type;
            different = true;
            if(_type == WordType.NOUN)
            {
                plural = null;
                gender = null;
            }
            else
            {
                plural = WordPlural.NO_PLURAL;
                gender = WordGender.NO_GENDER;
                plural_special = null;
                if(input_mode == true)
                    cancelPurpose(WordPurpose.GaP);		// if the word is not a noun, there is no need for the purposes GaP
            }
        }
        return different;
    }

    /*
     * Set the Plural Value for a Noun, when the plural is not "special"
     * @param _pl The Enumeration of Plural Value
     * @return Return TRUE if the new Plural Value is Legal
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public boolean setPlural(WordPlural _pl)
    {
        boolean changable = true;
        //if plural==WordPlural.NO_PLURAL, then it is not a noun, the plural value should be fixed as NO_PLURAL
        //if _pl==WordPlural.SPECIAL, then the method setPlural(WordPlural _pl, String _special) should be implemented
        if(plural==WordPlural.NO_PLURAL || _pl==WordPlural.SPECIAL)
        {
            changable  = false;
        }
        else
        {
            plural = _pl;
            plural_special = null;
        }
        return changable;
    }

    /*
     * Set the Plural Value for a Noun
     * @param _pl The Enumeration of Plural Value, set WordPlural.SPECIAL if the Plural is special
     * @param _special The Value of the "Special" Plural
     * @return Return TRUE if the new Plural Value is Legal
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public boolean setPlural(WordPlural _pl, String _special)
    {
        boolean changable = true;
        if(plural==WordPlural.NO_PLURAL)
        {
            changable  = false;
        }
        else
        {
            plural = _pl;
            if(_pl==WordPlural.SPECIAL)
                plural_special = _special;
        }
        return changable;
    }

    /*
     * Set the Gender of a Noun
     * @param _gender Enumeration Value of the Gender
     * @return Return True if the Word is a Noun
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public boolean setGender(WordGender _gender)
    {
        boolean changable = true;
        if(gender==WordGender.NO_GENDER)
        {
            changable = false;
        }
        else
        {
            gender = _gender;
        }
        return changable;
    }

    /*
     * Set the Spelling of the Word
     * @param _spelling The Spelling of the Word
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public void setSpelling(String _spelling)
    {
        spelling = _spelling;
    }

    /*
     * Set the Meaning of the Word
     * @param _meaning The Spelling of the Word
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public void setMeaning(String _meaning)
    {
        meaning = _meaning;
    }

    /*
     * Set the Study Purpose for the Word
     * Invalid if the Type of Current Word is not a Noun and the Purpose Value is GaP
     * @param _purpose The Enumeration of the Purpose
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public void setPurpose(WordPurpose _purpose)
    {
        if(input_mode == true)
            if(type==WordType.NOUN || _purpose!= WordPurpose.GaP)
                purposes.add(_purpose);
    }
    //TODO
    public void setPurposes(EnumSet<WordPurpose> _purposes)
    {
        if(input_mode == true)
            this.purposes = EnumSet.copyOf(_purposes);
    }

    /*
     * Cancel a Specific Purpose of the Word
     * @_purpose The Purpose to be Canceled
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public void cancelPurpose(WordPurpose _purpose)
    {
        if(input_mode == true)
            purposes.remove(_purpose);
    }

    /*
     * Get the Type of the Word
     * @return The Type of the Word
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public WordType getType()
    {
        return type;
    }

    /*
     * Get the Enumeration of the Plural Value
     * @return The Enumeration of the Plural Value
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public WordPlural getPlural()
    {
        return plural;
    }

    /*
     * Return All the Purpose as a Array
     * @return Return All the Purpose as a Array
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public WordPurpose[] getPurposes()
    {
        if(input_mode == true)
            return purposes.toArray(new WordPurpose[0]);
        else
            return null;
    }

    /*
     * Check Whether the Specific Purpose is Included
     * @param _purpose The Enumeration of the Purpose to be Checked
     * @return Return TRUE if the Purpose is set
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public boolean containPurpose(WordPurpose _purpose)
    {
        if(input_mode == true)
            return purposes.contains(_purpose);
        else
            return false;
    }

    /*
     * Get the Special Form of Gender
     * @return The Special Form of the Gender
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public String getPluralSpecial()
    {
        String result = null;
        if(plural!=WordPlural.NO_PLURAL)
        {
            result = plural_special;
        }
        return result;
    }

    /*
     * Translate the Enumeration of the Plural into a Human Readable String
     * @return the String
     * @author Thomas Hsieh
     * @Time 2016/10/14
     */
    public String getPluralString()
    {
        String pluralString = null;
        if(this.plural != WordPlural.NO_PLURAL) {
            if(this.plural == WordPlural.SPECIAL)
                pluralString = this.getPluralSpecial();
            else
                pluralString = this.plural.getOutput();
        }
        return pluralString;
    }

    /*
     * Get the Gender of the Word
     * @return The Enumeration of the Gender of the Word
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public WordGender getGender()
    {
        return gender;
    }

    /*
     * Get the Spelling of the Word
     * @return The Spelling of the Word
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public String getSpelling()
    {
        return spelling;
    }

    /*
     * Get the Meaning of the Word
     * @return The Meaning of the Word
     * @author Thomas Hsieh
     * @Time 2014/8/11
     */
    public String getMeaning()
    {
        return meaning;
    }

    /*
    public StringBuilder toJSONString()
    {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\""+WordField.TYPE.name()+"\":\""+type.name()+"\",");

        JSONArray ja_purpose = new JSONArray();
        WordPurpose[] purposes = getPurposes();
        for(WordPurpose purpose : purposes)
            ja_purpose.put(purpose);
        String purpose_json = ja_purpose.toString();
        sb.append("\""+WordField.PURPOSE.name()+"\":"+purpose_json+",");
        sb.append("\""+WordField.SPELLING.name()+"\":\""+urlEncode(spelling)+"\",");
        if(type==WordType.NOUN)
        {
            sb.append("\""+WordField.GENDER.name()+"\":\""+gender.name()+"\",");
            sb.append("\""+WordField.PLURAL.name()+"\":\""+plural.name()+"\",");
            if(plural==WordPlural.SPECIAL)
                sb.append("\""+WordField.PLURAL_SPECIAL.name()+"\":\""+urlEncode(plural_special)+"\",");
        }
        sb.append("\""+WordField.MEANING.name()+"\":\""+urlEncode(meaning)+"\"}");
        return sb;
    }
    */

    private String urlEncode(String str)
    {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(str,"UTF8");
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
        return encoded;
    }

    /**
     * Update the correct and repeat fields according to the given result
     * Update also the next tag
     * @param result
     */
    public void processResult(Result result) {
        //TODO
    }
}

