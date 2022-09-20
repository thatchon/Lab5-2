package com.example.lab52;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@RestController
public class WordPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    protected Word words;

    public WordPublisher() {
        this.words = new Word();
        this.words.badWords.add("Fuck");
        this.words.badWords.add("olo");
        this.words.goodWords.add("Happy");
        this.words.goodWords.add("Enjoy");
        this.words.goodWords.add("life");
    }

    @RequestMapping(value = "/addBad", method = RequestMethod.POST)
    public ArrayList<String> addBadWord(@RequestParam("word") String s){
        this.words.badWords.add(s);
        return this.words.badWords;
    }

    @RequestMapping(value = "/delBad/{word}", method = RequestMethod.GET)
    public ArrayList<String> deleteBadWord(@PathVariable("word") String s){
        this.words.badWords.remove(s);
        return this.words.badWords;
    }

    @RequestMapping(value = "/addGood", method = RequestMethod.POST)
    public ArrayList<String> addGoodWord(@RequestParam("word") String s){
        this.words.goodWords.add(s);
        return this.words.goodWords;
    }

    @RequestMapping(value = "/delGood/{word}", method = RequestMethod.GET)
    public ArrayList<String> deleteGoodWord(@PathVariable("word") String s){
        this.words.goodWords.remove(s);
        return this.words.goodWords;
    }

    @RequestMapping(value = "/proof", method = RequestMethod.POST)
    public String proofSentence(@RequestParam("sentence") String s){
        boolean found_good = false;
        boolean found_bad = false;
        for (int i = 0; i < this.words.goodWords.size(); i++){
            if(s.contains(this.words.goodWords.get(i))){
                found_good = true;
                break;
            }
        }
        for (int i = 0; i < this.words.badWords.size(); i++){
            if(s.contains(this.words.badWords.get(i))){
                found_bad = true;
                break;
            }
        }
        if(found_good && found_bad){
            rabbitTemplate.convertAndSend("MyFanoutExchange", "", s);
            return "Found Good & Bad Word";
        } else if (found_good) {
            rabbitTemplate.convertAndSend("MyDirectExchange", "good", s);
            return "Found Good Word";
        } else if (found_bad){
            rabbitTemplate.convertAndSend("MyDirectExchange", "bad", s);
            return  "Found Bad Word";
        }
        return "Not Found";
    }
    @RequestMapping(value = "/getSentence", method = RequestMethod.GET)
    public Sentence getSentence(){
        System.out.println("test sen");
        Object sentence = rabbitTemplate.convertSendAndReceive("MyDirectExchange", "sen", "test");
        return  ((Sentence) sentence);
    }

}