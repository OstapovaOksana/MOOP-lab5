package client;

import DTO.Category;
import DTO.News;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private MessageConsumer consumer;
    private static final String split = "#";

    public Client(){
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        try {
            connection = factory.createConnection();
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination queueSend = session.createQueue("fromClient");
            Destination queueRec = session.createQueue("toClient");

            producer = session.createProducer(queueSend);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            consumer = session.createConsumer(queueRec);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public Category categoryFindById(Long id) {
        String query = "CategoryFindById"+split+id.toString();
        String response = "";
        try {
            TextMessage mes = session.createTextMessage(query);
            producer.send(mes);
            Message message = consumer.receive(5000);
            if(message == null)
                return null;

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                response = textMessage.getText();
            }
            return new Category(id,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public News newsFindByName(String name) {
        String query = "NewsFindByName"+split+name;
        String response = "";
        try {
            TextMessage mes = session.createTextMessage(query);
            producer.send(mes);
            Message message = consumer.receive(5000);
            if(message == null)
                return null;

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                response = textMessage.getText();
            }
            String[] fields = response.split(split);
            Long id = Long.parseLong(fields[0]);
            Long categoryId = Long.parseLong(fields[1]);
            String publishingHouse = fields[3];
            return new News(id,categoryId,name,publishingHouse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Category categoryFindByName(String name) {
        String query = "CategoryFindByName"+split+name;
        String response = "";
        try {
            TextMessage mes = session.createTextMessage(query);
            producer.send(mes);
            Message message = consumer.receive(5000);
            if(message == null)
                return null;

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                response = textMessage.getText();
            }
            Long responseid = Long.parseLong(response);
            return new Category(responseid,name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean newsUpdate(News news) {
        String query = "NewsUpdate"+split+news.getId().toString()+
                "#"+news.getCategoryId().toString()+"#"+news.getName()
                +"#"+news.getPublishingHouse();
        String response = "";
        try {
            TextMessage mes = session.createTextMessage(query);
            producer.send(mes);
            Message message = consumer.receive(5000);
            if(message == null)
                return false;

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                response = textMessage.getText();
            }
            if ("true".equals(response))
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean categoryUpdate(Category category) {
        String query = "CategoryUpdate"+split+category.getId().toString()+
                "#"+category.getName();
        String response = "";
        try {
            TextMessage mes = session.createTextMessage(query);
            producer.send(mes);
            Message message = consumer.receive(10000000);
            if(message == null)
                return false;

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                response = textMessage.getText();
            }
            if ("true".equals(response))
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean newsInsert(News news) {
        String query = "NewsInsert"+
                "#"+news.getCategoryId().toString()+"#"+news.getName()
                +"#"+news.getPublishingHouse();
        String response = "";
        try {
            TextMessage mes = session.createTextMessage(query);
            producer.send(mes);
            Message message = consumer.receive(5000);
            if(message == null)
                return false;

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                response = textMessage.getText();
            }
            if ("true".equals(response))
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean categoryInsert(Category category) {
        String query = "CategoryInsert"+
                "#"+category.getName();
        String response = "";
        try {
            TextMessage mes = session.createTextMessage(query);
            producer.send(mes);
            Message message = consumer.receive(5000);
            if(message == null)
                return false;

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                response = textMessage.getText();
            }
            if ("true".equals(response))
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean categoryDelete(Category category) {
        String query = "CategoryDelete"+split+category.getId().toString();
        String response = "";
        try {
            TextMessage mes = session.createTextMessage(query);
            producer.send(mes);
            Message message = consumer.receive(5000);
            if(message == null)
                return false;

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                response = textMessage.getText();
            }
            if ("true".equals(response))
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean newsDelete(News news) {
        String query = "NewsDelete"+split+news.getId().toString();
        String response = "";
        try {
            TextMessage mes = session.createTextMessage(query);
            producer.send(mes);
            Message message = consumer.receive(5000);
            if(message == null)
                return false;

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                response = textMessage.getText();
            }
            if ("true".equals(response))
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Category> categoryAll(){
        String query = "CategoryAll";
        ArrayList<Category> list = new ArrayList<Category>();
        String response = "";
        try {
            TextMessage mes = session.createTextMessage(query);
            producer.send(mes);
            Message message = consumer.receive(5000);
            if(message == null)
                return null;

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                response = textMessage.getText();
            }
            String[] fields = response.split(split);
            for(int i=0;i<fields.length; i+=2) {
                Long id = Long.parseLong(fields[i]);
                String name = fields[i+1];
                list.add(new Category(id,name));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<News> newsAll(){
        String query = "NewsAll";
        ArrayList<News> list = new ArrayList<News>();
        String response = "";
        try {
            TextMessage mes = session.createTextMessage(query);
            producer.send(mes);
            Message message = consumer.receive(5000);
            if(message == null)
                return null;

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                response = textMessage.getText();
            }
            String[] fields = response.split(split);
            for(int i=0;i<fields.length; i+=4) {
                Long id = Long.parseLong(fields[i]);
                Long categoryId = Long.parseLong(fields[i+1]);
                String name = fields[i+2];
                String publishingHouse = fields[i+3];
                list.add(new News(id,categoryId,name,publishingHouse));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<News> newsFindByCategoryId(Long idc){
        String query = "NewsFindByCategoryId"+split+idc.toString();
        ArrayList<News> list = new ArrayList<News>();
        String response = "";
        try {
            TextMessage mes = session.createTextMessage(query);
            producer.send(mes);
            Message message = consumer.receive(5000);
            if(message == null)
                return null;

            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                response = textMessage.getText();
            }
            if ("".equals(response))
                return list;
            String[] fields = response.split(split);
            for(int i=0;i<fields.length; i+=4) {
                Long id = Long.parseLong(fields[i]);
                Long categoryId = Long.parseLong(fields[i+1]);
                String name = fields[i+2];
                String publishingHouse = fields[i+3];
                list.add(new News(id,categoryId,name,publishingHouse));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void cleanMessages() {
        try {
            Message message = consumer.receiveNoWait();
            while(message != null)
                message = consumer.receiveNoWait();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        //(new Client()).test("localhost",12345);
    }
}
