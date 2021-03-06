package ru.org.dsr.search;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.org.dsr.config.ConfigLabirint;
import ru.org.dsr.domain.Comment;
import ru.org.dsr.domain.Item;
import ru.org.dsr.domain.ItemID;
import ru.org.dsr.exception.LoadedEmptyBlocksException;
import ru.org.dsr.exception.NoFoundElementsException;
import ru.org.dsr.exception.RequestException;
import ru.org.dsr.exception.RobotException;
import ru.org.dsr.search.factory.TypeItem;

import java.util.*;

public class SearchLabirintJSOUP extends AbstractSearch {
    private static final Logger LOGGER = Logger.getLogger(SearchLabirintJSOUP.class);
    private String urlImg;
    private String urlMainBook;

    private Queue<String> books;
    private Queue<Comment> temp;

    ConfigLabirint cnf = new ConfigLabirint();

    public SearchLabirintJSOUP(ItemID itemID) throws RobotException, RequestException {
        initConfig(cnf);
        String url = buildUrlSearch(itemID);
        books = getUrlBooks(url);
    }

    //for test
    SearchLabirintJSOUP() {
        initConfig(cnf);
    }

    @Override
    public Item getItem() throws RobotException, RequestException {
        return isEmpty() ? null : initBook();
    }

    @Override
    public List<Comment> loadComments(int count) throws RobotException, RequestException {
        LinkedList<Comment> comments = new LinkedList<>();
        if (temp != null)
            while (!temp.isEmpty() && count > 0) {
                comments.add(temp.poll());
                count--;
            }
        while(count > 0 && !books.isEmpty()) {
            temp = getComments(books.poll());
            int i;
            for (i = 0; i < count && !temp.isEmpty(); i++) {
                comments.add(temp.poll());
            }
            count -= i;
        }
        return comments;
    }

    @Override
    public boolean isEmpty() {
        return (books == null || books.isEmpty()) && (temp == null || temp.isEmpty());
    }

    private Item initBook() throws RobotException, RequestException {
        Item item = new Item();
        Document doc = getDoc(urlMainBook);

        try {
            item.setDesc(getText(doc, cnf.SELECT_ITEM_DESC, urlMainBook));
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }

        try {
            item.setFirstName(getText(doc, cnf.SELECT_ITEM_FIRST_NAME, urlMainBook));
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }

        try {
            item.setLastName(getText(doc, cnf.SELECT_ITEM_LAST_NAME, urlMainBook));
        } catch (NoFoundElementsException | LoadedEmptyBlocksException e) {
            LOGGER.warn(e);
        }
        item.setType(TypeItem.BOOK);
        item.setUrlImg(urlImg);
        return item;
    }

    private Queue<Comment> getComments (String urlBook) throws RobotException, RequestException {
        LinkedList<Comment> comments = new LinkedList<>();
        Document docBook;
        docBook = getDoc(urlBook);

        Elements els = docBook.select(cnf.SELECT_COMMENTS);
        if (els == null || els.isEmpty()) return comments;
        Element elementsComment = els.get(0);
        if (elementsComment.text().isEmpty()) return comments;
        try {
            comments = initComments(elementsComment, urlBook);
        } catch (NoFoundElementsException e) {
            LOGGER.warn(e.toString());
        }
        return comments;
    }

    private LinkedList<Comment> initComments(Element column, String url) throws NoFoundElementsException {
        LinkedList<Comment> comments = new LinkedList<>();
        Elements elsAuthor = column.select(cnf.SELECT_COMMENT_AUTHOR);
        if (elsAuthor == null || elsAuthor.isEmpty())
            throw new NoFoundElementsException(url, cnf.SELECT_COMMENT_AUTHOR);
        Elements elsDesc = column.select(cnf.SELECT_COMMENT_DESC);
        if (elsDesc == null || elsDesc.isEmpty())
            throw new NoFoundElementsException(url, cnf.SELECT_COMMENT_DESC);
        Elements elsDate = column.select(cnf.SELECT_COMMENT_DTE);
        if (elsDate == null || elsDate.isEmpty())
            throw new NoFoundElementsException(url, cnf.SELECT_COMMENT_DTE);
        Iterator<String> listAuthor = elementsToText(elsAuthor).iterator();
        Iterator<String> listDesc = elementsToText(elsDesc).iterator();
        Iterator<String> listDate = elementsToText(elsDate).iterator();
        while (listAuthor.hasNext() && listDate.hasNext() && listDesc.hasNext()) {
            comments.add(createComment(listAuthor.next(), "", listDesc.next(), listDate.next(), cnf.SITE));
        }
        return comments;
    }

    private List<String> elementsToText(Elements els) {
        LinkedList<String> list = new LinkedList<>();
        for (Element e :
                els) {
            list.add(elementToText(e));
        }
        return list;
    }

    private String elementToText(Element e) {
        return e.text();
    }

    private LinkedList<String> getUrlBooks(String url) throws RobotException, RequestException {
        Document document;
        document = getDoc(url);
        LinkedList<String> books = new LinkedList<>();
        Elements els = document.select(cnf.SELECT_ITEMS);
        if (els == null || els.isEmpty()) {
            return books;
        }
        String id = els.get(0).attr("href");
        urlMainBook = cnf.SITE + id;
        urlImg = String.format(cnf.URL_IMG_FORM, id.substring(7));
        for (Element e :
                els) {
            String path = e.attr("href");
            if (path.contains("books")) {
                id = path.substring(6, path.length()-1);
                books.add(String.format("%s%s%s", cnf.REVIEWS_COMMENTS, id, "/?onpage=100"));
            }
        }
        return books;
    }
}
