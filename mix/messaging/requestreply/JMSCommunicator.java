package messaging.requestreply;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.Properties;

public class JMSCommunicator {
    private static Connection connection;
    private static Session session;
    private static Message msg = null;

    public static Message request(Serializable message, String channel, Destination destination, MessageListener msgListener) {
        Destination sendDestination;
        MessageConsumer msgConsumer;
        Destination replyDestination;
        MessageProducer msgProducer;

        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

            props.put(("queue." + channel), channel);

            Context jndiContext = new InitialContext(props);
            ActiveMQConnectionFactory connectionFactory = (ActiveMQConnectionFactory) jndiContext.lookup("ConnectionFactory");

            connectionFactory.setTrustAllPackages(true);
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            msgProducer = session.createProducer(destination);
            Message msg = session.createObjectMessage(message);

            if (msgListener != null) {
                connection.start();
                replyDestination = session.createTemporaryQueue();
                msg.setJMSReplyTo(replyDestination);
                msgProducer.send(msg);
                msgConsumer = session.createConsumer(replyDestination);
                createListener(msgListener, msgConsumer);
            } else {
                msgProducer.send(msg);
            }
            return msg;
        }catch(JMSException | NamingException e){
            e.printStackTrace();
        }
        return null;
    }

    public static void reply(Serializable message, String channel, Destination destination){
        request(message, channel, destination, null);
    }

    public static Destination createDestination(String channel, String destination){
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
        props.put("queue." + channel, channel);

        try{
            Context jndiContext = new InitialContext(props);
            return (Destination) jndiContext.lookup(destination);
        }catch(NamingException e){
            e.printStackTrace();
        }

        return null;
    }
    public static void setupReceiver (String channel, String destination, MessageListener msgListener){
    Destination receiveDestination;
    MessageConsumer msgConsumer = null;

        try {
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");

        props.put(("queue." + channel), channel);

        Context jndiContext = new InitialContext(props);
        ActiveMQConnectionFactory connectionFactory = (ActiveMQConnectionFactory) jndiContext.lookup("ConnectionFactory");

        connectionFactory.setTrustAllPackages(true);
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        receiveDestination = (Destination) jndiContext.lookup(destination);
        msgConsumer = session.createConsumer(receiveDestination);

        connection.start();

    } catch (NamingException | JMSException e) {
        e.printStackTrace();
    }

    createListener(msgListener, msgConsumer);
}

    private static void createListener(MessageListener msgListener, MessageConsumer msgConsumer){
        try {
            if (msgListener != null)
                msgConsumer.setMessageListener(msgListener);
            else {
                msgConsumer.setMessageListener(new MessageListener() {

                    @Override
                    public void onMessage(Message msg) {
                        System.out.println("received message: " + msg);
                    }
                });
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    }
