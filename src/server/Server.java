package server;

import DAO.CategoryDAO;
import DAO.NewsDAO;
import DTO.Category;
import DTO.News;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.List;

public class Server {
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private MessageConsumer consumer;

    public void start() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        try {
            connection = factory.createConnection();
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination queueSend = session.createQueue("toClient");
            Destination queueRec = session.createQueue("fromClient");

            producer = session.createProducer(queueSend);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            consumer = session.createConsumer(queueRec);
            while(processQuery()) {}

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private boolean processQuery() {
        int result = 0;
        String response = "";
        String query = "";
        try {
            Message request = consumer.receive(500);
            if(request == null)
                return true;
            if (request instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) request;
                query = textMessage.getText();
            } else
                return true;

            String[] fields = query.split("#");
            if (fields.length == 0){
                return true;
            } else {
                String action = fields[0];
                Category category;
                News news;

                System.out.println(action);

                switch(action) {
                    case "CategoryFindById":
                        Long id = Long.parseLong(fields[1]);
                        category = CategoryDAO.findById(id);
                        response = category.getName();
                        System.out.println(response);
                        TextMessage message = session.createTextMessage(response);
                        producer.send(message);
                        break;
                    case "NewsFindByCategoryId":
                        id = Long.parseLong(fields[1]);
                        List<News> list = NewsDAO.findByCategoryId(id);
                        StringBuilder str = new StringBuilder();
                        for(News c: list) {
                            str.append(c.getId());
                            str.append("#");
                            str.append(c.getCategoryId());
                            str.append("#");
                            str.append(c.getName());
                            str.append("#");
                            str.append(c.getPublishingHouse());
                            str.append("#");
                        }
                        response = str.toString();
                        System.out.println(response);
                        message = session.createTextMessage(response);
                        producer.send(message);
                        break;
                    case "NewsFindByName":
                        String name = fields[1];
                        news = NewsDAO.findByName(name);
                        response = news.getId().toString()+"#"+news.getCategoryId().toString()+"#"+news.getName()+"#"+news.getPublishingHouse();
                        System.out.println(response);
                        message = session.createTextMessage(response);
                        producer.send(message);
                        break;
                    case "CategoryFindByName":
                        name = fields[1];
                        category = CategoryDAO.findByName(name);
                        response = category.getId().toString();
                        System.out.println(response);
                        message = session.createTextMessage(response);
                        producer.send(message);
                        break;
                    case "NewsUpdate":
                        id = Long.parseLong(fields[1]);
                        Long categoryId = Long.parseLong(fields[2]);
                        name = fields[3];
                        String publishingHouse = fields[4];
                        news = new News(id,categoryId,name,publishingHouse);
                        if(NewsDAO.update(news))
                            response = "true";
                        else
                            response = "false";
                        System.out.println(response);
                        message = session.createTextMessage(response);
                        producer.send(message);
                        break;
                    case "CategoryUpdate":
                        id = Long.parseLong(fields[1]);
                        name = fields[2];
                        category = new Category(id,name);
                        if (CategoryDAO.update(category))
                            response = "true";
                        else
                            response = "false";
                        System.out.println(response);
                        message = session.createTextMessage(response);
                        producer.send(message);
                        break;
                    case "NewsInsert":
                        categoryId = Long.parseLong(fields[1]);
                        name = fields[2];
                        publishingHouse = fields[3];
                        news = new News((long) 0,categoryId,name,publishingHouse);
                        if(NewsDAO.insert(news))
                            response = "true";
                        else
                            response = "false";
                        System.out.println(response);
                        message = session.createTextMessage(response);
                        producer.send(message);
                        break;
                    case "CategoryInsert":
                        name = fields[1];
                        category = new Category();
                        category.setName(name);

                        System.out.println(name);

                        if(CategoryDAO.insert(category))
                            response = "true";
                        else
                            response = "false";
                        System.out.println(response);
                        message = session.createTextMessage(response);
                        producer.send(message);
                        break;
                    case "NewsDelete":
                        id = Long.parseLong(fields[1]);
                        news = new News();
                        news.setId(id);
                        if(NewsDAO.delete(news))
                            response = "true";
                        else
                            response = "false";
                        System.out.println(response);
                        message = session.createTextMessage(response);
                        producer.send(message);
                        break;
                    case "CategoryDelete":
                        id = Long.parseLong(fields[1]);
                        category = new Category();
                        category.setId(id);
                        if(CategoryDAO.delete(category))
                            response = "true";
                        else
                            response = "false";
                        System.out.println(response);
                        message = session.createTextMessage(response);
                        producer.send(message);
                        break;
                    case "NewsAll":
                        List<News> list1 = NewsDAO.findAll();
                        str = new StringBuilder();
                        for(News c: list1) {
                            str.append(c.getId());
                            str.append("#");
                            str.append(c.getCategoryId());
                            str.append("#");
                            str.append(c.getName());
                            str.append("#");
                            str.append(c.getPublishingHouse());
                            str.append("#");
                        }
                        response = str.toString();
                        System.out.println(response);
                        message = session.createTextMessage(response);
                        producer.send(message);
                        break;
                    case "CategoryAll":
                        List<Category> list2 = CategoryDAO.findAll();
                        str = new StringBuilder();
                        for(Category c: list2) {
                            str.append(c.getId());
                            str.append("#");
                            str.append(c.getName());
                            str.append("#");
                        }
                        response = str.toString();
                        System.out.println(response);
                        message = session.createTextMessage(response);
                        producer.send(message);
                        break;
                }
            }

            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    public void close() {
        try {
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}

