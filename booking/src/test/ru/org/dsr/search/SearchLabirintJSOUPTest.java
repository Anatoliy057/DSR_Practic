package ru.org.dsr.search;

import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.SearchLabirintJSOUP;
import ru.org.dsr.search.factory.TypeItem;
import ru.org.dsr.search.factory.TypeResource;

import java.util.LinkedList;
import java.util.List;

class SearchLabirintJSOUPTest {

    SearchLabirintJSOUP search;
    ItemID itemID;

    @Test
    void getItem() {
        itemID = new ItemID("Автостопом по Галактике", "", TypeItem.BOOK);
        try {
            try {
                search = new SearchLabirintJSOUP(itemID);
                Item item = search.getItem();
                Assert.assertNotNull(item);
                Assert.assertTrue(item.getItemID()!=null &&
                        item.getItemID().getFirstName() != null &&
                        item.getItemID().getLastName() != null &&
                        item.getDesc() != null &&
                        item.getUrlImg() != null);
            } catch (RequestException e) {
                e.printStackTrace();
            }
        } catch (RobotException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void loadCommentsFull() {
        try {
            try {
                ItemID itemID = new ItemID("Автостопом по галактике", "", TypeItem.BOOK);
                search = new SearchLabirintJSOUP(itemID);
                List<Comment> comments = search.loadComments(100);
                int n = comments.size();
                Assert.assertTrue((100 == n || search.isEmpty()) && 100 >= n);
            } catch (RequestException e) {
                e.printStackTrace();
            }
        } catch (RobotException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void loadCommentsParts() {
        try {
            try {
                ItemID itemID = new ItemID("Автостопом по галактике", "", TypeItem.BOOK);
                search = new SearchLabirintJSOUP(itemID);
                List<Comment> comments = new LinkedList<>();
                int n, part = 10;
                for (int i = 0; i < part*10; i+=part) {
                    comments.addAll(search.loadComments(part));
                     n = comments.size();
                    Assert.assertTrue((i+10 == n || search.isEmpty()) && n > 0);
                }
            } catch (RequestException e) {
                e.printStackTrace();
            }
        } catch (RobotException e) {
            e.printStackTrace();
        }
    }

    @Test
    void isEmpty() {
        search = new SearchLabirintJSOUP();
        Assert.assertTrue(search.isEmpty());
    }

    @Test
    void getTypeResource() {
        search = new SearchLabirintJSOUP();
        Assert.assertTrue(search.getTypeResource() == TypeResource.LABIRINT);
    }
}