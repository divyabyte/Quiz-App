package com.example.triviaapp.model;

public class Question{

    private String ans;
    private boolean answerTrue;

    public Question() {

    }

    public Question(String ans, boolean answerTrue) {
        this.ans = ans;
        this.answerTrue = answerTrue;
    }

    public String getAns() {
        return ans;
    }

    public boolean isAnswerTrue() {
        return answerTrue;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public void setAnswerTrue(boolean answerTrue) {
        this.answerTrue = answerTrue;
    }

    @Override
    public String toString() {
        return "Question{" +
                "ans='" + ans + '\'' +
                ", answerTrue=" + answerTrue +
                '}';
    }
}
