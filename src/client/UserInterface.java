package client;

import DTO.Category;
import DTO.News;
import lombok.SneakyThrows;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class UserInterface extends JFrame{
    private static Client client = null;
    private static Category cCnt = null;
    private static News cCt = null;

    private static boolean editMode = false;
    private static boolean categoryMode = true;

    private static JButton btnAddCategory = new JButton("Add Category");
    private static JButton btnAddNews = new JButton("Add News");
    private static JButton btnEdit= new JButton("Edit Data");
    private static JButton btnCancel= new JButton("Cancel");
    private static JButton btnSave= new JButton("Save");
    private static JButton btnDelete= new JButton("Delete");

    private static Box menuPanel = Box.createVerticalBox();
    private static Box actionPanel = Box.createHorizontalBox();
    private static Box comboPanel = Box.createHorizontalBox();
    private static Box newsPanel = Box.createVerticalBox();
    private static Box categoryPanel = Box.createVerticalBox();

    private static JComboBox comboCategory = new JComboBox();
    private static JComboBox comboNews = new JComboBox();

    private static JTextField tfCategoryName = new JTextField(30);
    private static JTextField tfNewsName = new JTextField(30);
    private static JTextField tfNewsCategoryName = new JTextField(30);
    private static JTextField tfNewsPublishingHouse = new JTextField(30);

    private static JFrame frame;

    UserInterface(){
        super("World Map");
        frame = this;
        frame.setPreferredSize(new Dimension(400, 400));
        frame.setMaximumSize(new Dimension(400, 400));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                frame.dispose();
                client.close();
                System.exit(0);
            }
        });
        Box box = Box.createVerticalBox();
        sizeAllElements();
        frame.setLayout(new FlowLayout());
        client.cleanMessages();

        // Menu
        menuPanel.add(btnAddCategory);
        menuPanel.add(Box.createVerticalStrut(20));
        btnAddCategory.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                editMode = false;
                categoryMode = true;
                menuPanel.setVisible(false);
                comboPanel.setVisible(false);
                categoryPanel.setVisible(true);
                newsPanel.setVisible(false);
                actionPanel.setVisible(true);
                btnDelete.setVisible(false);
                pack();
            }
        });
        menuPanel.add(btnAddNews);
        menuPanel.add(Box.createVerticalStrut(20));
        btnAddNews.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                editMode = false;
                categoryMode = false;
                menuPanel.setVisible(false);
                comboPanel.setVisible(false);
                categoryPanel.setVisible(false);
                newsPanel.setVisible(true);
                actionPanel.setVisible(true);
                btnDelete.setVisible(false);
                pack();
            }
        });
        menuPanel.add(btnEdit);
        menuPanel.add(Box.createVerticalStrut(20));
        btnEdit.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                editMode = true;
                menuPanel.setVisible(false);
                comboPanel.setVisible(true);
                categoryPanel.setVisible(false);
                newsPanel.setVisible(false);
                actionPanel.setVisible(true);
                btnDelete.setVisible(true);
                pack();
            }
        });

        // ComboBoxes
        comboPanel.add(new JLabel("Category:"));
        comboPanel.add(comboCategory);
        comboPanel.add(Box.createVerticalStrut(20));
        comboCategory.addActionListener (new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = (String) comboCategory.getSelectedItem();
                cCnt =client.categoryFindByName((String) comboCategory.getSelectedItem());
                categoryMode = true;
                categoryPanel.setVisible(true);
                newsPanel.setVisible(false);
                fillCategoryFields();
                //setContentPane(box);
                pack();
            }
        });
        comboPanel.add(new JLabel("News:"));
        comboPanel.add(comboNews);
        comboPanel.add(Box.createVerticalStrut(20));
        comboNews.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                String name = (String) comboNews.getSelectedItem();
                cCt = client.newsFindByName((String) comboNews.getSelectedItem());
                categoryMode = false;
                categoryPanel.setVisible(false);
                newsPanel.setVisible(true);
                fillNewsFields();
                //setContentPane(box);
                pack();
            }
        });
        fillComboBoxes();
        comboPanel.setVisible(false);

        // News Fields
        newsPanel.add(new JLabel("Name:"));
        newsPanel.add(tfNewsName);
        newsPanel.add(Box.createVerticalStrut(20));
        newsPanel.add(new JLabel("Category Id:"));
        newsPanel.add(tfNewsCategoryName);
        newsPanel.add(Box.createVerticalStrut(20));
        newsPanel.add(new JLabel("Publishing House:"));
        newsPanel.add(tfNewsPublishingHouse);
        newsPanel.add(Box.createVerticalStrut(20));
        newsPanel.setVisible(false);

        // Category Fields
        categoryPanel.add(new JLabel("Name:"));
        categoryPanel.add(tfCategoryName);
        categoryPanel.add(Box.createVerticalStrut(20));
        categoryPanel.setVisible(false);

        // Action Bar
        actionPanel.add(btnSave);
        btnSave.addMouseListener(new MouseAdapter() {
            @SneakyThrows
            public void mouseClicked(MouseEvent event) {
                save();
            }
        });
        actionPanel.add(Box.createVerticalStrut(20));
        actionPanel.add(btnDelete);
        btnDelete.addMouseListener(new MouseAdapter() {
            @SneakyThrows
            public void mouseClicked(MouseEvent event) {
                delete();
            }
        });
        actionPanel.add(Box.createVerticalStrut(20));
        actionPanel.add(btnCancel);
        actionPanel.add(Box.createVerticalStrut(20));
        btnCancel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                clearFields();
                menuPanel.setVisible(true);
                comboPanel.setVisible(false);
                categoryPanel.setVisible(false);
                newsPanel.setVisible(false);
                actionPanel.setVisible(false);
                pack();
            }
        });
        actionPanel.setVisible(false);

        clearFields();
        box.setPreferredSize(new Dimension(300, 500));
        box.add(menuPanel);
        box.add(comboPanel);
        box.add(categoryPanel);
        box.add(newsPanel);
        box.add(actionPanel);
        setContentPane(box);
        //pack();
    }

    private static void sizeAllElements() {
        Dimension dimension = new Dimension(400, 30);
        btnAddCategory.setMaximumSize(dimension);
        btnAddNews.setMaximumSize(dimension);
        btnEdit.setMaximumSize(dimension);
        btnCancel.setMaximumSize(dimension);
        btnSave.setMaximumSize(dimension);
        btnDelete.setMaximumSize(dimension);

        btnAddCategory.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAddNews.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSave.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEdit.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDelete.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension panelDimension = new Dimension(300, 200);
        menuPanel.setMaximumSize(panelDimension);
        comboPanel.setPreferredSize(panelDimension);
        actionPanel.setPreferredSize(panelDimension);
        newsPanel.setPreferredSize(panelDimension);
        categoryPanel.setPreferredSize(panelDimension);

        comboCategory.setPreferredSize(dimension);
        comboNews.setPreferredSize(dimension);

        tfNewsCategoryName.setMaximumSize(dimension);
        tfNewsName.setMaximumSize(dimension);
        tfNewsPublishingHouse.setMaximumSize(dimension);
        tfCategoryName.setMaximumSize(dimension);
    }

    private static void save() {
        if(editMode) {
            if(categoryMode) {
                cCnt.setName(tfCategoryName.getText());
                if (!client.categoryUpdate(cCnt)) {
                    JOptionPane.showMessageDialog(null, "Error: something went wrong!");
                }
            } else {
                cCt.setName(tfNewsName.getText());
                Category cnt = client.categoryFindByName(tfNewsCategoryName.getText());
                if(cnt == null) {
                    JOptionPane.showMessageDialog(null, "Error: no such category!");
                    return;
                }
                cCt.setCategoryId(cnt.getId());
                cCt.setPublishingHouse(tfNewsPublishingHouse.getText());
                if (!client.newsUpdate(cCt)) {
                    JOptionPane.showMessageDialog(null, "Error: something went wrong!");
                }
            }
        } else {
            if (categoryMode) {
                Category category = new Category();
                category.setName(tfCategoryName.getText());
                if(client.categoryInsert(category)) {
                    comboCategory.addItem(category.getName());
                }
            } else {
                News news = new News();
                news.setName(tfNewsName.getText());
                Category cnt = client.categoryFindById(Long.parseLong(tfNewsCategoryName.getText()));
                if(cnt == null) {
                    JOptionPane.showMessageDialog(null, "Error: no such category!");
                    return;
                }
                news.setCategoryId(cnt.getId());
                news.setPublishingHouse(tfNewsPublishingHouse.getText());
                if(client.newsInsert(news)) {
                    comboNews.addItem(news.getName());
                }
            }
        }
    }

    private static void delete() {
        if(editMode) {
            if(categoryMode) {
                List<News> list = client.newsFindByCategoryId(cCnt.getId());
                for(News c: list) {
                    comboNews.removeItem(c.getName());
                    client.newsDelete(c);
                }
                client.categoryDelete(cCnt);
                comboCategory.removeItem(cCnt.getName());

            } else {
                client.newsDelete(cCt);
                comboNews.removeItem(cCt.getName());
            }
        }
    }

    private void fillComboBoxes() {
        comboCategory.removeAllItems();
        comboNews.removeAllItems();
        java.util.List<Category> categories = client.categoryAll();
        List<News> news = client.newsAll();
        for(Category c: categories) {
            comboCategory.addItem(c.getName());
        }
        for(News c: news) {
            comboNews.addItem(c.getName());
        }
    }

    private static void clearFields() {
        tfCategoryName.setText("");
        tfNewsName.setText("");
        tfNewsCategoryName.setText("");
        tfNewsPublishingHouse.setText("");
        cCnt = null;
        cCt = null;
    }

    private static void fillCategoryFields() {
        if (cCnt == null)
            return;
        tfCategoryName.setText(cCnt.getName());
    }
    private static void fillNewsFields() {
        if(cCt == null)
            return;
        Category cnt = client.categoryFindById(cCt.getCategoryId());
        tfNewsName.setText(cCt.getName());
        tfNewsCategoryName.setText(cnt.getName());
        tfNewsPublishingHouse.setText(cCt.getPublishingHouse());
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        client = new Client();
        JFrame myWindow = new UserInterface();
        myWindow.setVisible(true);
    }
}
