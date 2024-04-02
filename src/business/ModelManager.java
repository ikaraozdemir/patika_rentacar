package business;
import core.Helper;
import dao.ModelDao;
import entity.Model;
import java.util.ArrayList;

public class ModelManager {
    private final ModelDao modelDao;

    public ModelManager() {
        this.modelDao = new ModelDao();
    }

    public ArrayList<Object[]> getForTable (int size) {
        ArrayList<Object[]> modelObjList = new ArrayList<>();
        for (Model model : this.findAll()){
            int i = 0;
            Object[] rowObject = new Object[size];
            rowObject[i++] = model.getId();
            rowObject[i++] = model.getBrand().getName();
            rowObject[i++] = model.getName();
            rowObject[i++] = model.getType();
            rowObject[i++] = model.getYear();
            rowObject[i++] = model.getFuel();
            rowObject[i++] = model.getGear();
            modelObjList.add(rowObject);
        }
        return modelObjList;
    }
    public ArrayList <Model> findAll() {
        return this.modelDao.findAll();
    }

    public boolean save (Model model) {
        if(this.getById(model.getId()) != null){
            Helper.showMsg("error");
            return false;
        }
        return this.modelDao.save(model);
    }
    public Model getById (int id){
        return this.modelDao.getById(id);
    }


    public boolean update(Model model){
        if(this.getById(model.getId()) == null){
            Helper.showMsg(model.getId() + "ID kayıtlı model bulunamadı");
            return false;
        }
        return this.modelDao.update(model);
    }

    public boolean delete (int id){
        if(this.getById(id) == null) {
            Helper.showMsg(id +"ID kayıtlı model bulunamadı");
            return false;
        }
        return this.modelDao.delete(id);
    }



}
