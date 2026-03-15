package storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import model.Answer;
import model.Memo;
import model.Question;

public class DataStore {

    public static Map<String,Question> questions=new HashMap<>();

    public static List<Answer> answers=new ArrayList<>();

    public static List<Memo> memoList=new ArrayList<>();

    public static Question create(String title,int limit){

        String id=UUID.randomUUID().toString().substring(0,6);

        Question q=new Question(id,title,limit);

        questions.put(id,q);

        return q;

    }

    public static Question get(String id){

        return questions.get(id);

    }

}