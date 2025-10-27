import step.handlers.javahandler.Keyword;

public class MyKeywords extends MyAbstractKeyword {

    @Keyword
    public void MyKeywordFromAp1() {
        session.put(new CommonSession());
    }
}
