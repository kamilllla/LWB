package com.example.lwb;

public class Test {
   private String question;
   private String rightAnswer;


    public Test(String question, String rightAnswer) {
        this.question = question;
        this.rightAnswer = rightAnswer;

    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    }
