package vn.poly.hailt.project1.model;

public class Vocabulary {
    public int id;
    public String english;
    public int linkImage;
    public String vietnameses;
    public String answerA;
    public String answerB;
    public String answerC;
    public String answerD;
    public int trueAnswer;

    public Vocabulary(int linkImage) {
        this.linkImage = linkImage;
    }
}
