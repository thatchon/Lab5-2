package com.example.lab52;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;

@Route(value = "index2")
public class MyView2 extends HorizontalLayout {
    private TextField word, sentence;
    private TextArea goodSentence, badSentence;
    private ComboBox<String> goodWord, badWord;
    private Button addGoodWord, addBadWord, addSentence, showSentence;


    public MyView2(){
        word = new TextField("Add Word");
        sentence = new TextField("Add Sentence");
        goodSentence = new TextArea("Good Sentences");
        badSentence = new TextArea("Bad Sentences");
        addGoodWord = new Button("Add Good Word");
        addBadWord = new Button("Add Bad Word");
        addSentence = new Button("Add Sentence");
        showSentence = new Button("Show Sentence");
        goodWord = new ComboBox<>();
        badWord = new ComboBox<>();

        goodWord.setLabel("Good Words");
        badWord.setLabel("Bad Words");

        VerticalLayout wordView = new VerticalLayout();
        VerticalLayout sentenceView = new VerticalLayout();

        wordView.add(word, addGoodWord, addBadWord, goodWord, badWord);
        sentenceView.add(sentence, addSentence, goodSentence, badSentence, showSentence);

        this.add(wordView, sentenceView);

        addGoodWord.addClickListener(event -> {
            String word_in = this.word.getValue();
            ArrayList out = WebClient.create().post()
                    .uri("http://127.0.0.1:8080/addGood?word="+ word_in)
                    .retrieve().bodyToMono(ArrayList.class).block();
            goodWord.setItems(out);
            Notification noti = Notification.show("Good Word Add : " + word_in);
            noti.setPosition(Notification.Position.BOTTOM_START);
        });

        addBadWord.addClickListener(event -> {
            String word_in = this.word.getValue();
            ArrayList out = WebClient.create().post()
                    .uri("http://127.0.0.1:8080/addBad?word="+ word_in)
                    .retrieve().bodyToMono(ArrayList.class).block();
            badWord.setItems(out);
            Notification noti = Notification.show("Bad Word Add : " + word_in);
            noti.setPosition(Notification.Position.BOTTOM_START);
        });

        addSentence.addClickListener(event -> {
            String sen_in = this.sentence.getValue();
            String out = WebClient.create().post()
                    .uri("http://127.0.0.1:8080/proof?sentence="+ sen_in)
                    .retrieve().bodyToMono(String.class).block();
            Notification noti = Notification.show(out);
            noti.setPosition(Notification.Position.BOTTOM_START);
        });

        showSentence.addClickListener(event -> {
            Sentence out = WebClient.create().get().uri("http://127.0.0.1:8080/getSentence")
                    .retrieve().bodyToMono(Sentence.class).block();
            String list_good = String.join(", ", out.goodSentences);
            goodSentence.setValue(list_good);
            String list_bad = String.join(", ", out.badSentences);
            badSentence.setValue(list_bad);
        });
    }


}
