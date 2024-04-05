package view;
import business.BookManager;
import business.BrandManager;
import business.CarManager;
import business.ModelManager;
import core.ComboItem;
import core.Helper;
import entity.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.ArrayList;

public class AdminView extends Layout{
    private JPanel container;
    private JLabel lbl_welcome;
    private JPanel pnl_top;
    private JTabbedPane tab_menu;
    private JButton btn_logout;
    private JPanel pnl_brand;
    private JScrollPane scrl_brand;
    private JTable tbl_brand;
    private JPanel pnl_model;
    private JScrollPane scrl_model;
    private JTable tbl_model;
    private JButton btn_search_model;
    private JComboBox cmb_s_model_brand;
    private JComboBox cmb_s_model_type;
    private JComboBox cmb_s_model_fuel;
    private JComboBox cmb_s_model_gear;
    private JButton btn_cncl_model;
    private JPanel pnl_car;
    private JScrollPane scrl_car;
    private JTable tbl_car;
    private JTable tbl_booking;
    private JFormattedTextField fld_strt_date;
    private JFormattedTextField fld_fnsh_date;
    private JComboBox cmb_booking_gear;
    private JComboBox cmb_booking_fuel;
    private JComboBox cmb_booking_type;
    private JButton btn_booking_search;
    private JPanel pnl_booking;
    private JScrollPane scrl_booking;
    private JPanel pnl_booking_search;
    private JButton btn_cncl_booking;
    private JTable tbl_booked;
    private JPanel pnl_booked;
    private JScrollPane scrl_booked;
    private JComboBox cmb_booked_plate;
    private JButton btn_booked_search;
    private JButton btn_booked_clear;
    //Login olan kişi işlem yapabileceği için burada user'a ihtiyacımız var.
    private User user;
    private final DefaultTableModel tmdl_brand = new DefaultTableModel();
    private final DefaultTableModel tmdl_model = new DefaultTableModel();
    private final DefaultTableModel tmdl_car = new DefaultTableModel();
    private final DefaultTableModel tmdl_booking = new DefaultTableModel();
    private final DefaultTableModel tmdl_booked = new DefaultTableModel();


    private final BrandManager brandManager;
    private final ModelManager modelManager;
    private final CarManager carManager;
    private  final BookManager bookManager;
    private JPopupMenu brand_menu;
    private JPopupMenu model_menu;
    private JPopupMenu car_menu;
    private JPopupMenu booking_menu;
    private JPopupMenu booked_menu;
    private Object[] col_model;
    private Object[] col_car;
    private Object[] col_booked_list;

    public AdminView (User user) {
        this.brandManager = new BrandManager();
        this.modelManager = new ModelManager();
        this.carManager = new CarManager();
        this.bookManager = new BookManager();
        this.add(container);
        this.guiInitialize(1000,500);
        this.user = user;
        if (this.user == null) {
            dispose();
        }
        this.lbl_welcome.setText("Hoşgeldiniz " + this.user.getUsername());

        loadComponent();

        loadBrandTable();
        loadBrandComponent();

        loadModelTable(null);
        loadModelComponent();
        loadModelFilter();

        //Car Tab Menu
        loadCarTable();
        loadCarComponent();

        //Booking Tab Menu
        loadBookingTable(null);
        loadBookingComponent();
        loadBookingFilter();

        loadBookingInfoTable(null);
        loadBookedComponent();
        loadBookedFilterPlate(this.bookManager.findAll());


    }

    private void loadComponent () {
        this.btn_logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginView loginView = new LoginView();
            }
        });
    }

    private void loadBookingComponent(){
        tableRowSelect(this.tbl_booking);
        this.booking_menu = new JPopupMenu();
        this.booking_menu.add("Rezervasyon Yap").addActionListener(e -> {
            int selectedCarId = this.getTableSelectedRow(this.tbl_booking, 0);
            BookingView bookingView = new BookingView(
                    this.carManager.getById(selectedCarId),
                    this.fld_strt_date.getText(),
                    this.fld_fnsh_date.getText()
            );
            bookingView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBookingTable(null);
                    loadBookingFilter();
                    loadBookingInfoTable(null);
                }
            });
        });
        this.tbl_booking.setComponentPopupMenu(booking_menu);


        this.btn_booking_search.addActionListener(e -> {
            ArrayList<Car> carList = this.carManager.searchForBooking(
                    this.fld_strt_date.getText(),
                    this.fld_fnsh_date.getText(),
                    (Model.Type) this.cmb_booking_type.getSelectedItem(),
                    (Model.Gear) this.cmb_booking_gear.getSelectedItem(),
                    (Model.Fuel) this.cmb_booking_fuel.getSelectedItem()
            );
            ArrayList<Object[]> carBookingRow = this.carManager.getForTable(col_car.length, carList);
            loadBookingTable(carBookingRow);
        });

        btn_cncl_booking.addActionListener(e -> {
            loadBookingFilter();
        });
    }

    public void loadBookingTable(ArrayList<Object[]> carList){
        Object[] col_booking_list = {"ID", "Marka", "Model", "Plaka", "Renk", "Km", "Yıl", "Tip", "Yakıt Türü", "Vites"};
        this.createTable(this.tmdl_booking, this.tbl_booking, col_booking_list, carList);
    }

    public void loadBookingFilter() {
        this.cmb_booking_type.setModel(new DefaultComboBoxModel<>(Model.Type.values()));
        this.cmb_booking_type.setSelectedItem(null);
        this.cmb_booking_gear.setModel(new DefaultComboBoxModel<>(Model.Gear.values()));
        this.cmb_booking_gear.setSelectedItem(null);
        this.cmb_booking_fuel.setModel(new DefaultComboBoxModel<>(Model.Fuel.values()));
        this.cmb_booking_fuel.setSelectedItem(null);
    }

    private void loadCarComponent() {
        tableRowSelect(this.tbl_car);

        this.car_menu = new JPopupMenu();
        this.car_menu.add("Yeni").addActionListener(e -> {
            CarView carView = new CarView(new Car());
            carView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCarTable();

                }
            });
        });

        this.car_menu.add("Güncelle").addActionListener(e -> {
            int selectCarId = this.getTableSelectedRow(tbl_car,0);
            CarView carView = new CarView(this.carManager.getById(selectCarId));
            carView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCarTable();

                }
            });
        });
        this.car_menu.add("Sil").addActionListener(e -> {
            if (Helper.confirm("sure")){
                int selectCartId = this.getTableSelectedRow(tbl_car,0);
                if (this.carManager.delete(selectCartId)){
                    Helper.showMsg("done");
                    loadCarTable();
                }else{
                    Helper.showMsg("error");
                }
            }
        });
        this.tbl_car.setComponentPopupMenu(car_menu);
    }

    public void loadBrandTable(){
        Object[] col_brand = {"Marka ID", "Marka Adı"};
        ArrayList<Object[]> brandList = this.brandManager.getForTable(col_brand.length);
        this.createTable(this.tmdl_brand, this.tbl_brand, col_brand, brandList);
    }


    public void loadBrandComponent() {
        tableRowSelect(this.tbl_brand);

        this.brand_menu = new JPopupMenu();
        this.brand_menu.add("Yeni").addActionListener(e -> {
            //Ekleme yapıldığı için BrandView'a null değeri verilir.
            BrandView brandView = new BrandView(null);
            brandView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();
                }
            });
        });

        this.brand_menu.add("Güncelle").addActionListener(e -> {
            int selectBrandtId = this.getTableSelectedRow(tbl_brand,0);
            BrandView brandView = new BrandView(this.brandManager.getById(selectBrandtId));
            brandView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();
                    loadCarTable();
                    loadBookingInfoTable(null);
                }
            });
        });
        this.brand_menu.add("Sil").addActionListener(e -> {
            if (Helper.confirm("sure")){
                int selectBrandtId = this.getTableSelectedRow(tbl_brand,0);
                if (this.brandManager.delete(selectBrandtId)){
                    Helper.showMsg("done");
                    loadBrandTable();
                    loadModelTable(null);
                    loadModelFilterBrand();
                    loadCarTable();
                    loadBookingInfoTable(null);
                }else{
                    Helper.showMsg("error");
                }
            }
        });
        this.tbl_brand.setComponentPopupMenu(brand_menu);
    }

    public void loadModelTable(ArrayList<Object[]> modelList){
        this.col_model = new Object[]{"Model ID", "Marka", "Model Adı", "Tip", "Yıl", "Yakıt Türü", "Vites"};
        if (modelList == null) {
            modelList = this.modelManager.getForTable(col_model.length, this.modelManager.findAll());
        }
        this.createTable(this.tmdl_model, this.tbl_model, col_model, modelList);
    }

    public void loadModelFilter() {
        this.cmb_s_model_type.setModel(new DefaultComboBoxModel<>(Model.Type.values()));
        this.cmb_s_model_type.setSelectedItem(null);
        this.cmb_s_model_gear.setModel(new DefaultComboBoxModel<>(Model.Gear.values()));
        this.cmb_s_model_gear.setSelectedItem(null);
        this.cmb_s_model_fuel.setModel(new DefaultComboBoxModel<>(Model.Fuel.values()));
        this.cmb_s_model_fuel.setSelectedItem(null);
        loadModelFilterBrand();
    }

    public void loadModelFilterBrand() {
        this.cmb_s_model_brand.removeAllItems();
        for (Brand obj : brandManager.findAll()) {
            this.cmb_s_model_brand.addItem(new ComboItem(obj.getId(), obj.getName()));
        }
        this.cmb_s_model_brand.setSelectedItem(null);
    }
    public void loadBookedFilterPlate(ArrayList<Book> books) {
        this.cmb_booked_plate.removeAllItems();
        ArrayList<String> plateList = new ArrayList<>();
        for (Book obj :books) {
            String plate = obj.getCar().getPlate();
            if(!plateList.contains(plate)){
                this.cmb_booked_plate.addItem(new ComboItem(obj.getId(), obj.getCar().getPlate()));
                plateList.add(plate);
            }
        }
        this.cmb_booked_plate.setSelectedItem(null);
    }

    public void loadCarTable() {
        this.col_car = new Object[]{"ID", "Marka", "Model", "Plaka", "Renk", "Km", "Yıl", "Tip", "Yakıt Türü", "Vites"};
        ArrayList<Object[]> carList = this.carManager.getForTable(col_car.length, this.carManager.findAll());
        createTable(this.tmdl_car, this.tbl_car, col_car,carList);
    }

    public void loadBookingInfoTable (ArrayList<Object[]> bookingInfoList ) {
        col_booked_list = new Object[]{"ID", "Marka", "Model", "Plaka", "Müşteri Ad Soyad", "T.C Kimlik No", "Telefon", "Başlama Tarihi", "Bitiş Tarihi", "Kiralama Bedeli", "Not", "Durum"};

        if (bookingInfoList == null) {
            bookingInfoList = this.bookManager.getForTable(col_booked_list.length,this.bookManager.findAll());
        }
        this.createTable(this.tmdl_booked, this.tbl_booked, col_booked_list, bookingInfoList);
    }

    public void loadBookedComponent() {
        tableRowSelect(this.tbl_booked);

        this.booked_menu = new JPopupMenu();

        this.booked_menu.add("Sil").addActionListener(e -> {
            if (Helper.confirm("sure")){
                int selectBooktId = this.getTableSelectedRow(tbl_booked,0);
                if (this.bookManager.delete(selectBooktId)){
                    loadBookingInfoTable(null);
                }else{
                    Helper.showMsg("error");
                }
            }
        });
        this.tbl_booked.setComponentPopupMenu(booked_menu);

        this.btn_booked_search.addActionListener(e -> {
            ComboItem selectedPlate = (ComboItem) this.cmb_booked_plate.getSelectedItem();
            ArrayList<Book> filteredByPlateBooks = new ArrayList<>();
            if (selectedPlate != null) {
                for (Book book : this.bookManager.findAll()) {
                    if (book.getCar().getPlate().equals(selectedPlate.getValue())) {
                        filteredByPlateBooks.add(book);
                    }
                }
            }

            ArrayList<Object[]> bookRowListBySearch = this.bookManager.getForTable(this.col_booked_list.length, filteredByPlateBooks);
            loadBookingInfoTable(bookRowListBySearch);
        });

        btn_booked_clear.addActionListener(e -> {
            this.cmb_booked_plate.setSelectedItem(null);
            loadBookingInfoTable(null);
        });
    }


    public void loadModelComponent() {
        tableRowSelect(this.tbl_model);
        this.model_menu = new JPopupMenu();
        this.model_menu.add("Yeni").addActionListener(e -> {
            ModelView modelView = new ModelView(new Model());
            modelView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadModelTable(null);
                }
            });
        });
        this.model_menu.add("Güncelle").addActionListener(e -> {
            int selectModeltId = this.getTableSelectedRow(tbl_model,0);
            ModelView modelView = new ModelView(this.modelManager.getById(selectModeltId));
            modelView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadModelTable(null);
                    loadBookingInfoTable(null);
                    loadCarTable();
                }
            });
        });
        this.model_menu.add("Sil").addActionListener(e -> {
            if (Helper.confirm("sure")){
                int selectModelId = this.getTableSelectedRow(tbl_model,0);
                if (this.modelManager.delete(selectModelId)){
                    Helper.showMsg("done");
                    loadModelTable(null);
                    loadBookingInfoTable(null);
                }else{
                    Helper.showMsg("error");
                }
            }
        });
        this.tbl_model.setComponentPopupMenu(model_menu);

        this.btn_search_model.addActionListener(e -> {
            ComboItem selectedBrand = (ComboItem) this.cmb_s_model_brand.getSelectedItem();
            int brandId = 0;
            if (selectedBrand != null){
                brandId = selectedBrand.getKey();
            }
            ArrayList<Model> modelListBySearch = this.modelManager.searchForTable(
                    brandId,
                    (Model.Fuel) cmb_s_model_fuel.getSelectedItem(),
                    (Model.Gear) cmb_s_model_gear.getSelectedItem(),
                    (Model.Type) cmb_s_model_type.getSelectedItem()
            );

            ArrayList<Object[]> modelRowListBySearch = this.modelManager.getForTable(this.col_model.length, modelListBySearch);
            loadModelTable(modelRowListBySearch);

        });

        this.btn_cncl_model.addActionListener(e -> {
            this.cmb_s_model_type.setSelectedItem(null);
            this.cmb_s_model_gear.setSelectedItem(null);
            this.cmb_s_model_fuel.setSelectedItem(null);
            this.cmb_s_model_brand.setSelectedItem(null);
            loadModelTable(null);
        });
    }

    private void createUIComponents() throws ParseException {
        this.fld_strt_date = new JFormattedTextField(new MaskFormatter("##/##/####"));
        this.fld_strt_date.setText("01/01/2024");
        this.fld_fnsh_date = new JFormattedTextField(new MaskFormatter("##/##/####"));
        this.fld_fnsh_date.setText("02/01/2024");
    }
}