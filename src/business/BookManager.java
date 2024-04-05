package business;
import core.Helper;
import dao.BookDao;
import entity.Book;
import java.util.ArrayList;

public class BookManager {
    private final BookDao bookDao;

    public BookManager() {
        this.bookDao = new BookDao();
    }

    public boolean save(Book book) {
        return this.bookDao.save(book);
    }

    public ArrayList<Book> findAll() {
        return this.bookDao.findAll();
    }

    public ArrayList<Object[]> getForTable(int size, ArrayList<Book> books) {
        ArrayList<Object[]> bookedList = new ArrayList<>();
        for (Book obj : books) {
            int i = 0;
            Object[] rowObject = new Object[size];
            rowObject[i++] = obj.getId();
            rowObject[i++] = obj.getCar().getModel().getBrand().getName();
            rowObject[i++] = obj.getCar().getModel().getName();
            rowObject[i++] = obj.getCar().getPlate();
            rowObject[i++] = obj.getName();
            rowObject[i++] = obj.getIdno();
            rowObject[i++] = obj.getMpno();
            rowObject[i++] = obj.getStrt_date();
            rowObject[i++] = obj.getFnsh_date();
            rowObject[i++] = obj.getPrc();
            rowObject[i++] = obj.getNote();
            rowObject[i++] = obj.getbCase();
            bookedList.add(rowObject);
        }
        return bookedList;
    }

    public boolean delete(int id) {
        if (this.getById(id) == null) {
            Helper.showMsg(id + "ID kayıtlı rezervasyon bulunamadı");
            return false;
        }
        return this.bookDao.delete(id);
    }

    public Book getById(int id) {
        return this.bookDao.getById(id);
    }
}
