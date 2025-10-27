import step.handlers.javahandler.Keyword;

public class MyKeywords extends MyAbstractKeyword {

    @Keyword
    public void MyKeywordFromAp2() {
        session.get(CommonSession.class);
    }
}
