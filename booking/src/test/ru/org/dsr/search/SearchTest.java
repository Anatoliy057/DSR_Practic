package ru.org.dsr.search;

import junit.framework.Assert;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.AbstractSearch;
import ru.org.dsr.search.SearchLabirintJSOUP;

class SearchTest {
    AbstractSearch search;

    @Test
    void getDoc() {
        Document document = null;
        search = new SearchIviJSOUP();
        try {
            for (int i = 0; i < 300; i++) {
                document = search.getDoc(search.cnf.SITE);
                if (i%10 == 0) System.out.println(i);
            }
            System.out.println(document);
            Assert.fail();
        } catch (RobotException e) {
            e.printStackTrace();
        } catch (RequestException e) {
            Assert.fail();
        }
    }
}