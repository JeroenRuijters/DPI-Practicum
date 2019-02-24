import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class MessageReceiverGateway {

    /*public static void setupReceiver (String channel, String destination, MessageListener msgListener){
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
    }*/
}
